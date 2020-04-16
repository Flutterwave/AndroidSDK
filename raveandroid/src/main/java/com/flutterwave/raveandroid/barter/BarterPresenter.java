package com.flutterwave.raveandroid.barter;


import android.content.Context;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.data.Utils;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.barter.BarterHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;

public class BarterPresenter extends BarterHandler implements BarterUiContract.UserActionsListener {

    @Inject
    RemoteRepository networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PayloadEncryptor payloadEncryptor;
    private Context context;
    private BarterUiContract.View mView;

    @Inject
    public BarterPresenter(Context context, BarterUiContract.View mView) {
        super(mView);
        this.context = context;
        this.mView = mView;
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()));
            }
        }
    }


    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        if (amountValidator == null) {
            amountValidator = new AmountValidator();
        }

        boolean isAmountValid = amountValidator.isAmountValid(amount);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }


    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

            String deviceID = deviceIdGetter.getDeviceId();
            if (deviceID == null) {
                deviceID = Utils.getDeviceId(context);
            }

            PayloadBuilder builder = new PayloadBuilder();

            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID)
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createBarterPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                fetchFee(body);
            } else {
                chargeBarter(body, ravePayInitializer.getEncryptionKey());
            }
        }
    }

    @Override
    public void onAttachView(BarterUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullBarterView();
    }


}
