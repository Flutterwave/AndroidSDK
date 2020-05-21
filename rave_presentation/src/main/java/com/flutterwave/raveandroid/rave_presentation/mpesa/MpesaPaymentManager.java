package com.flutterwave.raveandroid.rave_presentation.mpesa;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.mpesa.MpesaModule;

import javax.inject.Inject;

public class MpesaPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public MpesaHandler paymentHandler;
    MpesaInteractorImpl interactor;

    public MpesaPaymentManager(RaveNonUIManager manager, MpesaPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeMpesa(payload, manager.getEncryptionKey());
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
                .setPhonenumber(manager.getPhoneNumber())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createMpesaPayload();
    }

    public void cancelPolling() {
        paymentHandler.cancelPolling();
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

    private void injectFields(RaveComponent component, MpesaPaymentCallback callback) {
        interactor = new MpesaInteractorImpl(callback);

        component.plus(new MpesaModule(interactor))
                .inject(this);

    }
}
