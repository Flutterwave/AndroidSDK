package com.flutterwave.raveandroid.rave_presentation.ussd;

import com.flutterwave.raveandroid.rave_core.models.Bank;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ussd.UssdModule;

import javax.inject.Inject;

public class UssdPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public UssdHandler paymentHandler;
    UssdInteractorImpl interactor;

    public UssdPaymentManager(RaveNonUIManager manager, UssdPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge(Bank bank) {

        Payload payload = createPayload(bank);

        paymentHandler.payWithUssd(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(Bank bank) {

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(manager.getAmount() + "")
                .setAccountbank(bank.getBankcode())
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
                .setDevice_fingerprint(manager.getUniqueDeviceID())
                .setNarration(manager.getNarration());

        return builder.createUssdPayload();
    }

    public void fetchTransactionFee(FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        paymentHandler.fetchFee(createPayload(new Bank("", "")));
    }

    public void checkTransactionStatus(int timeoutInSeconds) {
        paymentHandler.startPaymentVerification(timeoutInSeconds);
    }

    public void cancelPolling() {
        paymentHandler.cancelPolling();
    }

    private void injectFields(RaveComponent component, UssdPaymentCallback callback) {
        interactor = new UssdInteractorImpl(callback);

        component.plus(new UssdModule(interactor))
                .inject(this);

    }
}
