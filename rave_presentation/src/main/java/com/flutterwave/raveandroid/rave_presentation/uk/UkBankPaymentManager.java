package com.flutterwave.raveandroid.rave_presentation.uk;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.uk.UkModule;

import javax.inject.Inject;

public class UkBankPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public UkHandler paymentHandler;
    UkInteractorImpl interactor;

    public UkBankPaymentManager(RaveNonUIManager manager, UkBankPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeUk(payload, manager.getEncryptionKey());
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

        return builder.createUKPayload();

    }

    public void checkTransactionStatus() {
        paymentHandler.requeryTx(interactor.getFlwRef(), interactor.getTxRef(), manager.getPublicKey());
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

    private void injectFields(RaveComponent component, UkBankPaymentCallback callback) {
        interactor = new UkInteractorImpl(callback);

        component.plus(new UkModule(interactor))
                .inject(this);

    }
}
