package com.flutterwave.raveandroid.rave_presentation;

import com.flutterwave.raveandroid.rave_core.di.DeviceIdGetterModule;
import com.flutterwave.raveandroid.rave_core.models.RavePaymentMethods;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentCallback;
import com.flutterwave.raveandroid.rave_presentation.card.RaveChargeCardPayload;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.DaggerRaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.LIVE_URL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.STAGING_URL;

public class RaveNonUIManager extends RavePayManager {
    private String uniqueDeviceID;

    private CardPayManager cardPayManager;

    private RaveNonUICallback raveNonUICallback;

    public RaveNonUIManager(RaveNonUICallback raveNonUICallback) {
        super();
        this.raveNonUICallback = raveNonUICallback;
    }

    public RaveNonUIManager setUniqueDeviceId(String uniqueDeviceID){
        this.uniqueDeviceID = uniqueDeviceID;
        return this;
    }

    public void chargeCard(RaveChargeCardPayload raveChargeCardPayload){

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(amount))
                .setCardno(raveChargeCardPayload.getPan())
                .setCountry(country)
                .setCurrency(currency)
                .setCvv(raveChargeCardPayload.getCvv())
                .setEmail(email)
                .setFirstname(fName)
                .setLastname(lName)
                .setIP(uniqueDeviceID).setTxRef(txRef)
                .setExpiryyear(raveChargeCardPayload.getExpiryDate().substring(3, 5))
                .setExpirymonth(raveChargeCardPayload.getExpiryDate().substring(0, 2))
                .setMeta(meta)
                .setSubAccount(subAccounts)
                .setIsPreAuth(isPreAuth)
                .setPBFPubKey(publicKey)
                .setDevice_fingerprint(uniqueDeviceID);

        cardPayManager.chargeCard(builder.createPayload(), encryptionKey);
    }

    public RaveNonUIManager initializeNonUI() {
        cardPayManager = new CardPayManager(setUpGraph(), new CardPaymentCallback() {
            @Override
            public void collectCardPin(Payload payload) {
                raveNonUICallback.collectCardPin(RavePaymentMethods.CARD);
            }

            @Override
            public void showProgressIndicator(boolean active) {
                raveNonUICallback.showProgressIndicator(active);
            }

            @Override
            public void collectOtp(String flwRef, String message) {
                raveNonUICallback.collectOtp(RavePaymentMethods.CARD, message);
            }

            @Override
            public void onError(String flwRef, String errorMessage) {
                raveNonUICallback.onError(RavePaymentMethods.CARD, "Transaction with Flutterwave Reference of " + flwRef + " failed with error message - " + errorMessage);
            }

            @Override
            public void onError(String errorMessage) {
                raveNonUICallback.onError(RavePaymentMethods.CARD, errorMessage);
            }

            @Override
            public void onSuccessful(String flwRef) {
                raveNonUICallback.onSuccessful(flwRef);
            }
        });


        return this;
    }

    public void initializeUI() {
        throw new IllegalArgumentException("Cannot initialize Rave UI with RaveNonUIManager use RaveUIManager instead");
    }

    private RaveComponent setUpGraph() {
        String baseUrl;

        if (staging) {
            baseUrl = STAGING_URL;
        } else {
            baseUrl = LIVE_URL;
        }
        return DaggerRaveComponent.builder()
                .deviceIdGetterModule(new DeviceIdGetterModule(uniqueDeviceID))
                .remoteModule(new RemoteModule(baseUrl))
                .build();
    }
}
