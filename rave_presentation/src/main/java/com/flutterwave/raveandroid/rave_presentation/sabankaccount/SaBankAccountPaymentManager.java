package com.flutterwave.raveandroid.rave_presentation.sabankaccount;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.sabank.SaBankModule;

import javax.inject.Inject;

public class SaBankAccountPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public SaBankAccountHandler paymentHandler;
    SaBankInteractorImpl interactor;

    public SaBankAccountPaymentManager(RaveNonUIManager manager, SaBankAccountPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeSaBankAccount(payload, manager.getEncryptionKey());
    }

    private Payload createPayload() {
        PayloadBuilder builder = new PayloadBuilder();

        builder.setAmount(String.valueOf(manager.getAmount()))
                .setCountry(manager.getCountry())
                .setCurrency(manager.getCurrency())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getlName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createSaBankAccountPayload();
    }

    public void checkStatus() {
        paymentHandler.requeryTx(manager.getPublicKey(), interactor.getFlwRef());
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

    private void injectFields(RaveComponent component, SaBankAccountPaymentCallback callback) {
        interactor = new SaBankInteractorImpl(callback);

        component.plus(new SaBankModule(interactor))
                .inject(this);

    }
}
