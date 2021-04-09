package com.flutterwave.raveandroid.rave_presentation.francmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.francmobilemoney.FrancophoneModule;

import javax.inject.Inject;

public class FrancophoneMobileMoneyPaymentManager {

    private final RaveNonUIManager manager;
    private final String country;
    @Inject
    public FrancMobileMoneyHandler paymentHandler;
    FrancMobileMoneyInteractorImpl interactor;

    public FrancophoneMobileMoneyPaymentManager(RaveNonUIManager manager, String country, FrancophoneMobileMoneyPaymentCallback callback) {
        this.manager = manager;
        this.country = country;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeFranc(payload, manager.getEncryptionKey());
    }

    private Payload createPayload() {
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(manager.getAmount()))
                .setCountry(country)
                .setCurrency(manager.getCurrency())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getlName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setPhonenumber(manager.getPhoneNumber())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createFrancPayload();
    }

    public void fetchTransactionFee(FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        Payload feePayload = new PayloadBuilder()
                .setPBFPubKey(manager.getPublicKey())
                .setAmount("" + manager.getAmount())
                .setCurrency(manager.getCurrency())
                .createPayload();
        paymentHandler.fetchFee(feePayload);
    }

    private void injectFields(RaveComponent component, FrancophoneMobileMoneyPaymentCallback callback) {
        interactor = new FrancMobileMoneyInteractorImpl(callback);

        component.plus(new FrancophoneModule(interactor))
                .inject(this);

    }
}
