package com.flutterwave.raveandroid.banktransfer;

import android.os.Bundle;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.banktransfer.BankTransferHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

/**
 * Created by hfetuga on 27/06/2018.
 */


public class BankTransferPresenter extends BankTransferHandler implements BankTransferUiContract.UserActionsListener {
    private static final String ACCOUNT_NUMBER = "account_number";
    private static final String BANK_NAME = "bank_name";
    private static final String BENEFICIARY_NAME = "benef_name";
    private static final String AMOUNT = "amount";
    private static final String TX_REF = "txref";
    private static final String FLW_REF = "flwref";
    private static final String PUBLIC_KEY = "pbfkey";

    @Inject
    EventLogger eventLogger;
    @Inject
    AmountValidator amountValidator;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadToJson payloadToJson;
    @Inject
    PayloadEncryptor payloadEncryptor;

    public boolean pollingCancelled = false;
    public boolean hasTransferDetails = false;
    BankTransferUiContract.View mView;

    @Inject
    public BankTransferPresenter(BankTransferUiContract.View mView) {
        super(mView);
        this.mView = mView;
    }

    @Override
    public Bundle getState() {
        if (hasTransferDetails) {
            Bundle state = new Bundle();
            state.putString(ACCOUNT_NUMBER, accountNumber);
            state.putString(BANK_NAME, bankName);
            state.putString(BENEFICIARY_NAME, beneficiaryName);
            state.putString(AMOUNT, amount);
            state.putString(TX_REF, txRef);
            state.putString(FLW_REF, flwRef);
            state.putString(PUBLIC_KEY, publicKey);
            return state;
        } else return null;
    }

    @Override
    public void restoreState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            hasTransferDetails = true;
            accountNumber = savedInstanceState.getString(ACCOUNT_NUMBER);
            bankName = savedInstanceState.getString(BANK_NAME);
            beneficiaryName = savedInstanceState.getString(BENEFICIARY_NAME);
            amount = savedInstanceState.getString(AMOUNT);
            txRef = savedInstanceState.getString(TX_REF);
            flwRef = savedInstanceState.getString(FLW_REF);
            publicKey = savedInstanceState.getString(PUBLIC_KEY);

            mView.onTransferDetailsReceived(amount, accountNumber, bankName, beneficiaryName);
        }

    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Bank Transfer Fragment").getEvent(),
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


        if (!amountValidator.isAmountValid(amount)) {
            valid = false;
            mView.showFieldError(amountID, RaveConstants.validAmountPrompt, amountViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {


        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(RaveConstants.fieldAmount).getData()));

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
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
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(deviceIdGetter.getDeviceId())
                    .setNarration(ravePayInitializer.getNarration())
                    .setfrequency(ravePayInitializer.getFrequency())
                    .setDuration(ravePayInitializer.getDuration())
                    .setIsPermanent(ravePayInitializer.getIsPermanent());

            Payload body = builder.createBankTransferPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                payWithBankTransfer(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }

    @Override
    public void onAttachView(BankTransferUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullBankTransferView();
    }
}
