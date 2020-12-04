package com.flutterwave.raveandroid.rave_presentation.card;

import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.FeeCheckListener;
import com.flutterwave.raveandroid.rave_presentation.RaveNonUIManager;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardModule;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.MANUAL_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.SAVED_CARD_CHARGE;

public class CardPaymentManager {

    private final RaveNonUIManager manager;
    @Inject
    public CardPaymentHandler paymentHandler;
    CardInteractorImpl interactor;
    private int chargeType;

    public CardPaymentManager(RaveNonUIManager manager, CardPaymentCallback callback) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback, null);

    }

    public CardPaymentManager(RaveNonUIManager manager, CardPaymentCallback callback, SavedCardsListener savedCardsListener) {
        this.manager = manager;

        injectFields(manager.getRaveComponent(), callback, savedCardsListener);

    }

    public void chargeCard(Card card) {
        chargeType = MANUAL_CARD_CHARGE;

        Payload payload = createPayload(card);

        paymentHandler.chargeCard(payload, manager.getEncryptionKey());
    }

    private Payload createPayload(Card card) {
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(manager.getAmount()))
                .setCardno(card.getCardNumber())
                .setCountry(manager.getCountry())
                .setCurrency(manager.getCurrency())
                .setCvv(card.getCvv())
                .setEmail(manager.getEmail())
                .setFirstname(manager.getfName())
                .setLastname(manager.getlName())
                .setIP(manager.getUniqueDeviceID())
                .setTxRef(manager.getTxRef())
                .setExpiryyear(card.getExpiryYear())
                .setExpirymonth(card.getExpiryMonth())
                .setMeta(manager.getMeta())
                .setSubAccount(manager.getSubAccounts())
                .setIsPreAuth(manager.isPreAuth())
                .setPBFPubKey(manager.getPublicKey())
                .setDevice_fingerprint(manager.getUniqueDeviceID());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }
        return builder.createPayload();
    }

    public void submitPin(String pin) {
        // Todo: add null checks for ensuring process flow
        paymentHandler.chargeCardWithPinAuthModel(interactor.getPayload(), pin, manager.getEncryptionKey());
    }

    public void submitOtp(String otp) {
        if (chargeType == MANUAL_CARD_CHARGE)
            paymentHandler.validateCardCharge(interactor.getFlwRef(), otp, manager.getPublicKey());
        else if (chargeType == SAVED_CARD_CHARGE) {
            interactor.getPayload().setOtp(otp);
            paymentHandler.chargeSavedCard(interactor.getPayload(), manager.getEncryptionKey());
        }
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

    public void setSavedCardsListener(SavedCardsListener listener) {
        interactor.setSavedCardsListener(listener);
    }

    /**
     * Fetch the saved cards associated with the user phone number (if available)
     *
     * @param showLoader If true, {@link CardPaymentCallback#showProgressIndicator(boolean)} will be used to indicate progress. Otherwise, request will be done silently
     */
    public void fetchSavedCards(boolean showLoader) {
        paymentHandler.lookupSavedCards(manager.getPublicKey(), manager.getPhoneNumber(), showLoader);
    }

    /**
     * Delete a user's saved card.
     *
     * @param cardhash The {@link SavedCard#getCardHash() card hash} of the saved card to delete.
     */
    public void deleteSavedCard(String cardhash) {
        paymentHandler.deleteASavedCard(cardhash, manager.getPhoneNumber(), manager.getPublicKey());
    }

    public void fetchTransactionFee(Card card, FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        paymentHandler.fetchFee(createPayload(card));
    }

    public void fetchTransactionFee(SavedCard card, FeeCheckListener feeCheckListener) {
        interactor.setFeeCheckListener(feeCheckListener);
        paymentHandler.fetchFee(createPayload(card));
    }

    public void chargeSavedCard(SavedCard card) {
        chargeType = SAVED_CARD_CHARGE;

        Payload body = createPayload(card);
        paymentHandler.chargeSavedCard(body, manager.getEncryptionKey());
    }

    private Payload createPayload(SavedCard card) {
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
                .setIsPreAuth(manager.isPreAuth())
                .setPBFPubKey(manager.getPublicKey())
                .setDevice_fingerprint(manager.getUniqueDeviceID())
                .setIs_saved_card_charge(true)
                .setSavedCard(card)
                .setPhonenumber(manager.getPhoneNumber());

        if (manager.getPayment_plan() != null) {
            builder.setPaymentPlan(manager.getPayment_plan());
        }
        return builder.createSavedCardChargePayload();
    }

    public void saveCard() {
        paymentHandler.saveCardToRave(manager.getPhoneNumber(), manager.getEmail(), interactor.getFlwRef(), manager.getPublicKey());
    }

    private void injectFields(RaveComponent component, CardPaymentCallback callback, SavedCardsListener listener) {
        interactor = new CardInteractorImpl(callback, listener);

        component.plus(new CardModule(interactor))
                .inject(this);

    }
}
