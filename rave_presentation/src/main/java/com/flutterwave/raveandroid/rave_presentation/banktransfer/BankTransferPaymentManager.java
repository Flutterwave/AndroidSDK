package com.flutterwave.raveandroid.rave_presentation.banktransfer;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.banktransfer.BankTransferModule;

import javax.inject.Inject;

public class BankTransferPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public BankTransferHandler paymentHandler;
    BankTransferInteractorImpl interactor;

    public BankTransferPaymentManager(RaveNonUIManager manager, BankTransferPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {

        Payload payload = createPayload();

        paymentHandler.payWithBankTransfer(payload, manager.getEncryptionKey());
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
                .setSubAccount(manager.getSubAccounts())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID())
                .setNarration(manager.getNarration())
                .setfrequency(manager.getFrequency())
                .setDuration(manager.getDuration())
                .setIsPermanent(manager.isPermanent());

        Payload body = builder.createBankTransferPayload();

        return body;
    }

    public void fetchTransactionFee(FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        paymentHandler.fetchFee(createPayload());
    }

    public void checkTransactionStatus(int timeoutInSeconds) {
        paymentHandler.startPaymentVerification(timeoutInSeconds);
    }

    public void cancelPolling() {
        paymentHandler.cancelPolling();
    }

    private void injectFields(RaveComponent component, BankTransferPaymentCallback callback) {
        interactor = new BankTransferInteractorImpl(callback);

        component.plus(new BankTransferModule(interactor))
                .inject(this);

    }
}
