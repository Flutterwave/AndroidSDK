package com.flutterwave.raveandroid.card;

import android.view.View;

import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.DeviceIdGetter;
import com.flutterwave.raveandroid.data.PhoneNumberObfuscator;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.events.ScreenLaunchEvent;
import com.flutterwave.raveandroid.rave_presentation.card.CardPaymentHandler;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadBuilder;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validAmountPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validCreditCardPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validCvvPrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validExpiryDatePrompt;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.validPhonePrompt;

/**
 * Created by hamzafetuga on 18/07/2017.
 */

public class CardUiPresenter extends CardPaymentHandler implements CardUiContract.UserActionsListener {

    @Inject
    EventLogger eventLogger;
    @Inject
    RemoteRepository networkRequest;
    @Inject
    AmountValidator amountValidator;
    @Inject
    CvvValidator cvvValidator;
    @Inject
    EmailValidator emailValidator;
    @Inject
    CardExpiryValidator cardExpiryValidator;
    @Inject
    CardNoValidator cardNoValidator;
    @Inject
    DeviceIdGetter deviceIdGetter;
    @Inject
    PhoneNumberObfuscator phoneNumberObfuscator;
    @Inject
    TransactionStatusChecker transactionStatusChecker;
    @Inject
    PayloadEncryptor payloadEncryptor;
    @Inject
    SharedPrefsRepo sharedManager;
    @Inject
    Gson gson;
    List<SavedCard> savedCards;
    private CardUiContract.View mView;
    private boolean cardSaveInProgress = false;
    private String requeryInstruction = "Transaction is under processing, please use transaction requery to check status";

    @Inject
    public CardUiPresenter(CardUiContract.View mView) {
        super(mView);
        this.mView = mView;
    }

    public boolean isCardSaveInProgress() {
        return cardSaveInProgress;
    }

    public void setCardSaveInProgress(boolean cardSaveInProgress) {
        this.cardSaveInProgress = cardSaveInProgress;
    }

    @Override
    public void onDataCollected(HashMap<String, ViewObject> dataHashMap) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        int emailID = dataHashMap.get(fieldEmail).getViewId();
        String email = dataHashMap.get(fieldEmail).getData();
        Class emailViewType = dataHashMap.get(fieldEmail).getViewType();

        int cvvID = dataHashMap.get(fieldCvv).getViewId();
        String cvv = dataHashMap.get(fieldCvv).getData();
        Class cvvViewType = dataHashMap.get(fieldCvv).getViewType();

        int cardExpiryID = dataHashMap.get(fieldCardExpiry).getViewId();
        String cardExpiry = dataHashMap.get(fieldCardExpiry).getData();
        Class cardExpiryViewType = dataHashMap.get(fieldCardExpiry).getViewType();

        int cardNoStrippedID = dataHashMap.get(fieldcardNoStripped).getViewId();
        String cardNoStripped = dataHashMap.get(fieldcardNoStripped).getData().replaceAll(" ", "");
        dataHashMap.get(fieldcardNoStripped).setData(cardNoStripped);

        Class cardNoStrippedViewType = dataHashMap.get(fieldcardNoStripped).getViewType();

        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isEmailValid = emailValidator.isEmailValid(email);
        boolean isCVVValid = cvvValidator.isCvvValid(cvv);
        boolean isCardExpiryValid = cardExpiryValidator.isCardExpiryValid(cardExpiry);
        boolean isCardNoValid = cardNoValidator.isCardNoStrippedValid(cardNoStripped);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (!isEmailValid) {
            valid = false;
            mView.showFieldError(emailID, validPhonePrompt, emailViewType);
        }

        if (!isCVVValid) {
            valid = false;
            mView.showFieldError(cvvID, validCvvPrompt, cvvViewType);
        }

        if (!isCardExpiryValid) {
            valid = false;
            mView.showFieldError(cardExpiryID, validExpiryDatePrompt, cardExpiryViewType);
        }

        if (!isCardNoValid) {
            valid = false;
            mView.showFieldError(cardNoStrippedID, validCreditCardPrompt, cardNoStrippedViewType);
        }

        if (valid) {
            mView.onValidationSuccessful(dataHashMap);
        }

    }

    @Override
    public void onDataForSavedCardChargeCollected(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        boolean valid = true;

        int amountID = dataHashMap.get(fieldAmount).getViewId();
        String amount = dataHashMap.get(fieldAmount).getData();
        Class amountViewType = dataHashMap.get(fieldAmount).getViewType();

        int emailID = dataHashMap.get(fieldEmail).getViewId();
        String email = dataHashMap.get(fieldEmail).getData();
        Class emailViewType = dataHashMap.get(fieldEmail).getViewType();


        boolean isAmountValid = amountValidator.isAmountValid(amount);
        boolean isEmailValid = emailValidator.isEmailValid(email);

        if (!isAmountValid) {
            valid = false;
            mView.showFieldError(amountID, validAmountPrompt, amountViewType);
        }

        if (!isEmailValid) {
            valid = false;
            mView.showFieldError(emailID, validPhonePrompt, emailViewType);
        }


        if (valid) {
            ravePayInitializer.setAmount(Double.parseDouble(amount));
            ravePayInitializer.setEmail(email);

            if (savedCards == null)
                checkForSavedCardsInMemory(ravePayInitializer);
            mView.showSavedCardsLayout(savedCards);

        }
    }

    @Override
    public void processTransaction(HashMap<String, ViewObject> dataHashMap, RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {

            ravePayInitializer.setAmount(Double.parseDouble(dataHashMap.get(fieldAmount).getData()));

            if (dataHashMap.containsKey(fieldPhone)) {
                String phoneNumber = dataHashMap.get(fieldPhone).getData();
                if (!phoneNumber.isEmpty()) ravePayInitializer.setPhoneNumber(phoneNumber);
            }

            String deviceID = deviceIdGetter.getDeviceId();

            String cardFirstSix = "";
            if (dataHashMap.get(fieldcardNoStripped).getData().length() > 6) {
                cardFirstSix = dataHashMap.get(fieldcardNoStripped).getData().substring(0, 6);
            }

            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCardno(dataHashMap.get(fieldcardNoStripped).getData())
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setCvv(dataHashMap.get(fieldCvv).getData())
                    .setEmail(dataHashMap.get(fieldEmail).getData())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID).setTxRef(ravePayInitializer.getTxRef())
                    .setExpiryyear(dataHashMap.get(fieldCardExpiry).getData().substring(3, 5))
                    .setExpirymonth(dataHashMap.get(fieldCardExpiry).getData().substring(0, 2))
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceID);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createPayload();

            if (ravePayInitializer.getNarration().equalsIgnoreCase("barterRavePay")){
                checkCard(cardFirstSix, body, ravePayInitializer.getIsDisplayFee(), ravePayInitializer.getEncryptionKey(), ravePayInitializer.getBarterCountry());
            }else{
                if (ravePayInitializer.getIsDisplayFee()) {
                    fetchFee(body);
                } else {
                    chargeCard(body, ravePayInitializer.getEncryptionKey());
                }
            }

        }
    }

    @Override
    public void processSavedCardTransaction(SavedCard savedCard, RavePayInitializer ravePayInitializer) {
        if (ravePayInitializer != null) {

            String deviceID = deviceIdGetter.getDeviceId();


            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                    .setCountry(ravePayInitializer.getCountry())
                    .setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(ravePayInitializer.getEmail())
                    .setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName())
                    .setIP(deviceID)
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey())
                    .setDevice_fingerprint(deviceID)
                    .setIs_saved_card_charge(true)
                    .setSavedCard(savedCard)
                    .setPhonenumber(ravePayInitializer.getPhoneNumber());

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createSavedCardChargePayload();

            String cardFirstSix = "";
            if (savedCard.getMasked_pan().length() >= 6) {
                cardFirstSix = savedCard.getMasked_pan().substring(0, 6);
            }

            if (ravePayInitializer.getBarterCountry()!= null){
                checkCard(cardFirstSix, body, ravePayInitializer.getIsDisplayFee(), ravePayInitializer.getEncryptionKey(), ravePayInitializer.getBarterCountry());
            }else{
                if (ravePayInitializer.getIsDisplayFee()) {
                    fetchFee(body);
                } else {
                    chargeCard(body, ravePayInitializer.getEncryptionKey());
                }
            }
        }
    }

    @Override
    public void saveCardToSharedPreferences(List<SavedCard> cards, String phoneNumber, String publicKey) {
        sharedManager.saveCardToSharedPreference(cards, phoneNumber, publicKey);
    }

    @Override
    public void retrieveSavedCardsFromMemory(String phoneNumber, String publicKey) {
        if (phoneNumber != null && !phoneNumber.isEmpty())
            savedCards = sharedManager.getSavedCards(phoneNumber, publicKey);
    }

    @Override
    public void checkForSavedCardsInMemory(RavePayInitializer ravePayInitializer) {
        if (savedCards == null) {
            savedCards = new ArrayList<>();
        }

        if (ravePayInitializer.getPhoneNumber().equals(sharedManager.fetchPhoneNumber())) {
            retrieveSavedCardsFromMemory(ravePayInitializer.getPhoneNumber(), ravePayInitializer.getPublicKey());
        }

        mView.setHasSavedCards(!savedCards.isEmpty(), savedCards);
    }

    @Override
    public void onDetachView() {
        if (!this.cardSaveInProgress)
            this.mView = new NullUiCardView();
    }

    @Override
    public void onAttachView(CardUiContract.View view) {
        this.mView = view;
    }


    @Override
    public void onSavedCardSwitchSwitchedOn(RavePayInitializer ravePayInitializer) {
        boolean shouldHideSavedCardsLayout = ravePayInitializer.isUsePhoneAndEmailSuppliedToSaveCards() &&
                emailValidator.isEmailValid(ravePayInitializer.getEmail()) &&
                (ravePayInitializer.getPhoneNumber() != null && !ravePayInitializer.getPhoneNumber().isEmpty());
        mView.setSavedCardsLayoutVisibility(!shouldHideSavedCardsLayout);
    }

    @Override
    public void init(RavePayInitializer ravePayInitializer) {

        if (ravePayInitializer != null) {
            logEvent(new ScreenLaunchEvent("Card Fragment").getEvent(),
                    ravePayInitializer.getPublicKey());

            if (ravePayInitializer.isSaveCardFeatureAllowed()) {
                mView.showCardSavingOption(true);
            }

            checkForSavedCardsInMemory(ravePayInitializer);

            // Check for saved cards on Rave server
            if (ravePayInitializer.getPhoneNumber() != null) {
                if (ravePayInitializer.getPhoneNumber().length() > 0) {
                    lookupSavedCards(ravePayInitializer.getPublicKey(),
                            ravePayInitializer.getPhoneNumber(), false);
                    mView.onPhoneNumberValidated(ravePayInitializer.getPhoneNumber());
                }
            }


            boolean isEmailValid = emailValidator.isEmailValid(ravePayInitializer.getEmail());
            boolean isAmountValid = amountValidator.isAmountValid(ravePayInitializer.getAmount());

            if (isEmailValid) {
                mView.onEmailValidated(ravePayInitializer.getEmail(), View.GONE);
            } else {
                mView.onEmailValidated("", View.VISIBLE);
            }
            if (isAmountValid) {
                mView.onAmountValidated(String.valueOf(ravePayInitializer.getAmount()), View.GONE);
            } else {
                mView.onAmountValidated("", View.VISIBLE);
            }
        }
    }
}
