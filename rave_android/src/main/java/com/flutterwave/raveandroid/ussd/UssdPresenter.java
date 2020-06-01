package com.flutterwave.raveandroid.ussd;


import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.ussd.UssdHandler;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

public class UssdPresenter extends UssdHandler implements UssdUiContract.UserActionsListener {
    public UssdUiContract.View mView;

    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    RemoteRepository networkRequest;

    @Inject
    UssdPresenter(UssdUiContract.View mView) {
        super(mView);
        this.mView = mView;
    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(RaveConstants.fieldAmount).getData()));

            String bankCode = null;
            for (Bank bank : RaveConstants.ussdBanksList) {
                if (bank.getBankname().equals(dataHashMap.get(RaveConstants.fieldUssdBank).getData())) {
                    bankCode = bank.getBankcode();
                    break;
                }
            }

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setAccountbank(bankCode)
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceIdGetter.getDeviceId())
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId())
                    .setNarration(ravePayInitializer.getNarration());

            Payload body = builder.createUssdPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                payWithUssd(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }


    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("USSD Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            } else mView.onAmountValidationFailed();

        }
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {
        boolean valid = true;

        int amountID = dataHashMap.get(RaveConstants.fieldAmount).getViewId();
        String amount = dataHashMap.get(RaveConstants.fieldAmount).getData();
        Class amountViewType = dataHashMap.get(RaveConstants.fieldAmount).getViewType();

        int bankID = dataHashMap.get(RaveConstants.fieldUssdBank).getViewId();
        String bankName = dataHashMap.get(RaveConstants.fieldUssdBank).getData();
        Class bankViewType = dataHashMap.get(RaveConstants.fieldUssdBank).getViewType();


        if (!amountValidator.isAmountValid(amount)) {
            valid = false;
            mView.showFieldError(amountID, RaveConstants.validAmountPrompt, amountViewType);
        }

        boolean isValidBank = false;
        for (Bank bank : RaveConstants.ussdBanksList) {
            if (bank.getBankname().equals(bankName)) {
                isValidBank = true;
                break;
            }
        }
        if (bankName == null || !isValidBank) {
            valid = false;
            mView.showFieldError(bankID, "Please select a bank", bankViewType);
        }

        if (valid) {
            mView.onDataValidationSuccessful(dataHashMap);
        }
    }

    @Override
    public void onAttachView(UssdUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullUssdView();
    }
}