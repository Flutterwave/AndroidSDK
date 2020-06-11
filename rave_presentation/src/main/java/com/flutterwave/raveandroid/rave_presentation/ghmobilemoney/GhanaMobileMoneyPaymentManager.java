package com.flutterwave.raveandroid.rave_presentation.ghmobilemoney;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney.GhMobileMoneyModule;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.mtn;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.tigo;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.vodafone;

public class GhanaMobileMoneyPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public GhMobileMoneyHandler paymentHandler;
    GhMobileMoneyInteractorImpl interactor;

    public GhanaMobileMoneyPaymentManager(RaveNonUIManager manager, GhanaMobileMoneyPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void chargeMtn() {
        charge(mtn, null);
    }

    public void chargeTigo() {
        charge(tigo, null);
    }

    public void chargeVodafone(String voucher) {
        charge(vodafone, voucher);
    }

    public void onWebpageAuthenticationComplete() {
        paymentHandler.requeryTx(manager.getPublicKey());
    }

    private void charge(String network, String voucher) {
        Payload payload = createPayload(network, voucher);

        paymentHandler.chargeGhMobileMoney(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(String network, String voucher) {
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

        if (voucher != null) {
            builder.setVoucher(voucher);
        }

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }

        return builder.createGhMobileMoneyPayload();
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

    private void injectFields(RaveComponent component, GhanaMobileMoneyPaymentCallback callback) {
        interactor = new GhMobileMoneyInteractorImpl(callback);

        component.plus(new GhMobileMoneyModule(interactor))
                .inject(this);

    }
}
