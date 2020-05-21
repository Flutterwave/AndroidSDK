package com.flutterwave.raveandroid.rave_presentation.account;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.account.AccountModule;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NG;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NGN;

public class AccountPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public AccountHandler paymentHandler;
    AccountInteractorImpl interactor;

    public AccountPaymentManager(RaveNonUIManager manager, AccountPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void chargeAccount(BankAccount Account) {
        Payload payload = createPayload(Account);

        paymentHandler.chargeAccount(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(BankAccount account) {
        // Todo: check/enforce bank-specific parameters like bvn and date of birth
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(manager.getAmount()))
                .setEmail(manager.getEmail())
                .setCountry(NG)
                .setCurrency(NGN)
                .setPBFPubKey(manager.getPublicKey())
                .setDevice_fingerprint(manager.getUniqueDeviceID())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setAccountbank(account.bank.getBankcode())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setBVN(account.getBvn())
                .setAccountnumber(account.getAccountNumber());


        Payload body = builder.createBankPayload();
        body.setPasscode(account.getDateOfBirth());
        body.setPhonenumber(manager.getPhoneNumber());

        return body;
    }

    public void submitOtp(String otp) {
        paymentHandler.authenticateAccountCharge(interactor.getFlwRef(), otp, manager.getPublicKey());
    }

    public void onWebpageAuthenticationComplete() {
        paymentHandler.requeryTx(interactor.getFlwRef(), manager.getPublicKey());
    }

    public void fetchTransactionFee(FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        Payload feePayload = new PayloadBuilder().createBankPayload();
        feePayload.setPBFPubKey(manager.getPublicKey());
        feePayload.setAmount("" + manager.getAmount());
        paymentHandler.fetchFee(feePayload);
    }

    private void injectFields(RaveComponent component, AccountPaymentCallback callback) {
        interactor = new AccountInteractorImpl(callback);

        component.plus(new AccountModule(interactor))
                .inject(this);

    }
}
