package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.CardModule;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;

import javax.inject.Inject;

public class CardPayManager {

    private final RaveNonUIManager manager;
    @Inject
    public CardPaymentHandler paymentHandler;
    CardInteractorImpl interactor;

    public CardPayManager(RaveNonUIManager manager, CardPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback);

    }

    public void chargeCard(Card card) {

        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(manager.getAmount()))
                .setCardno(card.getCardNumber())
                .setCountry(manager.getCountry())
                .setCurrency(manager.getCurrency())
                .setCvv(card.getCvv())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getfName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setExpiryyear(card.getExpiryYear())
                .setExpirymonth(card.getExpiryMonth())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setIsPreAuth(manager.isPreAuth())
                .setPBFPubKey(manager.getPublicKey())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        Payload payload = builder.createPayload();

        paymentHandler.chargeCard(payload, manager.getEncryptionKey());
    }

    public void submitPin(String pin) {
        // Todo: add null checks for ensuring process flow
        paymentHandler.chargeCardWithPinAuthModel(interactor.getPayload(), pin, manager.getEncryptionKey());
    }

    public void submitOtp(String otp) {
        paymentHandler.validateCardCharge(interactor.getFlwRef(), otp, manager.getPublicKey());
    }

    public void submitAddress(AddressDetails addressDetails) {
        paymentHandler.chargeCardWithAddressDetails(
                interactor.getPayload(),
                addressDetails,
                manager.getEncryptionKey(),
                interactor.getAuthModel()
        );
    }

    public void onWebpageAuthenticationComplete() {
        paymentHandler.requeryTx(interactor.getFlwRef(), manager.getPublicKey());
    }

    private void injectFields(RaveComponent component, CardPaymentCallback callback) {
        interactor = new CardInteractorImpl(callback);

        component.plus(new CardModule(interactor))
                .inject(this);

    }
}
