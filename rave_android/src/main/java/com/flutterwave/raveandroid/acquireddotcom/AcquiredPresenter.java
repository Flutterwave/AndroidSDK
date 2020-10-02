package com.flutterwave.raveandroid.acquireddotcom;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.acquireddotcom.AcquiredHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.validators.AmountValidator;

import java.util.HashMap;

import javax.inject.Inject;


public class AcquiredPresenter extends AcquiredHandler implements AcquiredUiContract.UserActionsListener {
    @Inject
    AmountValidator amountValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;

    private AcquiredUiContract.View mView;

    @Inject
    public AcquiredPresenter(AcquiredUiContract.View mView) {
        super(mView);
        this.mView = mView;
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Acquired.com Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());
            if (isAmountValid) {
                mView.onAmountValidationSuccessful(String.valueOf(ravePayInitializer.getAmount()),
                        String.valueOf(ravePayInitializer.getCurrency()));
            } else {
                mView.onAmountValidationFailed();
            }
        }
    }

    @Override
    public void processTransaction(RavePayInitializer ravePayInitializer, boolean appIsInDarkMode) {
        if (ravePayInitializer != null) {

            String deviceID = deviceIdGetter.getDeviceId();

            PayloadBuilder builder = new PayloadBuilder();

            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCountry("GB")
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
                    .setPhonenumber(ravePayInitializer.getPhoneNumber())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createAcquiredDotComPayload(appIsInDarkMode);

            chargeAcquired(body, ravePayInitializer.getEncryptionKey());
        }
    }

    @Override
    public void onAttachView(AcquiredUiContract.View view) {
        this.mView = view;
    }

    @Override
    public void onDetachView() {
        this.mView = new NullAcquiredView();
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

    }

}
