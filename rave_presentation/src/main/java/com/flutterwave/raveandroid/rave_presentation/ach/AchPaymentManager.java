package com.flutterwave.raveandroid.rave_presentation.ach;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ach.AchModule;

import javax.inject.Inject;

public class AchPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public AchHandler paymentHandler;
    AchInteractorImpl interactor;

    public AchPaymentManager(RaveNonUIManager manager, AchPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeAccount(payload, manager.getEncryptionKey());
    }

    private Payload createPayload() {

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(manager.getAmount() + "")
                .setCountry(manager.getCountry())
                .setCurrency(manager.getCurrency())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getlName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setMeta(manager.getMeta())
                .setPBFPubKey(manager.getPublicKey())
                .setIsUsBankCharge(true)
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        Payload body = builder.createBankPayload();

        return body;
    }

    public void onWebpageAuthenticationComplete() {
        paymentHandler.requeryTx(interactor.getFlwRef(), manager.getPublicKey());
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

    private void injectFields(RaveComponent component, AchPaymentCallback callback) {
        interactor = new AchInteractorImpl(callback);

        component.plus(new AchModule(interactor))
                .inject(this);

    }
}
