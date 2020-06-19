package com.flutterwave.raveandroid.rave_presentation.ugmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ugmomo.UgModule;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NG;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.UGX;

public class UgandaMobileMoneyPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public UgMobileMoneyHandler paymentHandler;
    UgInteractorImpl interactor;

    public UgandaMobileMoneyPaymentManager(RaveNonUIManager manager, UgandaMobileMoneyPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void charge() {
        Payload payload = createPayload();

        paymentHandler.chargeUgMobileMoney(payload, manager.getEncryptionKey());
    }

    private Payload createPayload() {
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(manager.getAmount() + "")
//                    .setCountry(manager.getCountry())
                .setCountry(NG) //Country has to be set to NG for UGX payments (as at 10/12/2018)
                .setCurrency(manager.getCurrency())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getlName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setNetwork(UGX)
                .setPhonenumber(manager.getPhoneNumber())
                .setPBFPubKey(manager.getPublicKey())
                .setIsPreAuth(manager.isPreAuth())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createUgMobileMoneyPayload();
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


    private void injectFields(RaveComponent component, UgandaMobileMoneyPaymentCallback callback) {
        interactor = new UgInteractorImpl(callback);

        component.plus(new UgModule(interactor))
                .inject(this);

    }
}
