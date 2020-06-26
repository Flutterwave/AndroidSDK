package com.flutterwave.raveandroid.rave_presentation.zmmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_java_commons.RaveConstants;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.zm.ZmModule;

import javax.inject.Inject;

public class ZambiaMobileMoneyPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public ZmMobileMoneyHandler paymentHandler;
    private ZmInteractorImpl interactor;

    public ZambiaMobileMoneyPaymentManager(RaveNonUIManager manager, ZambiaMobileMoneyPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void chargeMtn() {
        charge(RaveConstants.mtn);
    }

    public void charge(String network) {
        Payload payload = createPayload(network);

        paymentHandler.chargeZmMobileMoney(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(String network) {

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
                .setNetwork(network)
                .setPhonenumber(manager.getPhoneNumber())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createZmMobileMoneyPayload();
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

    public void onWebpageAuthenticationComplete() {
        paymentHandler.requeryTx(manager.getPublicKey());
    }


    private void injectFields(RaveComponent component, ZambiaMobileMoneyPaymentCallback callback) {
        interactor = new ZmInteractorImpl(callback);

        component.plus(new ZmModule(interactor))
                .inject(this);

    }
}
