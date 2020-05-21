package com.flutterwave.raveandroid.rave_presentation.barter;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.barter.BarterModule;

import javax.inject.Inject;

public class BarterPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public BarterHandler paymentHandler;
    BarterInteractorImpl interactor;

    public BarterPaymentManager(RaveNonUIManager manager, BarterPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {

        Payload payload = createPayload();

        paymentHandler.setAutomaticRequery(true);

        paymentHandler.chargeBarter(payload, manager.getEncryptionKey());
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

        return builder.createBarterPayload();
    }

    public void fetchTransactionFee(FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        paymentHandler.fetchFee(createPayload());
    }

    public void cancelPolling() {
        paymentHandler.cancelPolling();
    }

    private void injectFields(RaveComponent component, BarterPaymentCallback callback) {
        interactor = new BarterInteractorImpl(callback);

        component.plus(new BarterModule(interactor))
                .inject(this);

    }
}
