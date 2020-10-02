package com.flutterwave.raveandroid.rave_presentation.acquireddotcom;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.acquireddotcom.AcquiredModule;

import javax.inject.Inject;

public class AcquiredManager {

    private final RaveNonUIManager manager;
    @Inject
    public AcquiredHandler paymentHandler;
    AcquiredInteractorImpl interactor;

    public AcquiredManager(RaveNonUIManager manager, AcquiredCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge(boolean appIsInDarkMode) {
        Payload payload = createPayload(appIsInDarkMode);

        paymentHandler.chargeAcquired(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(boolean appIsInDarkMode) {
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

        return builder.createAcquiredDotComPayload(appIsInDarkMode);
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

    private void injectFields(RaveComponent component, AcquiredCallback callback) {
        interactor = new AcquiredInteractorImpl(callback);

        component.plus(new AcquiredModule(interactor))
                .inject(this);

    }
}