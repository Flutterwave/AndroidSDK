package com.flutterwave.raveandroid.card;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.savedcards.SavedCardsActivity;
import com.flutterwave.raveandroid.card.savedcards.SavedCardsFragment;
import com.flutterwave.raveandroid.data.PhoneNumberObfuscator;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.StartTypingEvent;
import com.flutterwave.raveandroid.di.modules.CardUiModule;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.events.ErrorEvent;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveandroid.verification.AVSVBVFragment;
import com.flutterwave.raveandroid.verification.OTPFragment;
import com.flutterwave.raveandroid.verification.PinFragment;
import com.flutterwave.raveandroid.verification.VerificationActivity;
import com.flutterwave.raveandroid.verification.web.WebFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.MANUAL_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.SAVED_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.verification.VerificationActivity.EXTRA_IS_STAGING;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements View.OnClickListener, CardUiContract.View, View.OnFocusChangeListener {

    public static final int FOR_NOAUTH_INTERNATIONAL = 888;
    @Inject
    CardUiPresenter presenter;
    int chargeType = MANUAL_CARD_CHARGE;

    public static final int FOR_PIN = 444;
    public static final int FOR_OTP = 666;
    public static final int FOR_AVBVV = 333;
    @Inject
    PhoneNumberObfuscator phoneNumberObfuscator;
    public static final int FOR_INTERNET_BANKING = 555;
    private static final int FOR_SAVED_CARDS = 777;
    private static final String STATE_PRESENTER_SAVEDCARDS = "presenter_saved_cards";
    public static final String INTENT_SENDER = "cardFrag";
    private static final String RAVEPAY = "ravepay";
    private View v;
    TextView useASavedCardTv;
    TextView useAnotherCardTv;
    private Button payButton;
    private TextView pcidss_tv;
    private AlertDialog dialog;
    private TextInputLayout cvvTil;
    private TextInputEditText cvvTv;
    private TextInputLayout emailTil;
    private TextInputLayout cardNoTil;
    private TextInputEditText emailEt;
    private ProgressDialog progessDialog;
    private TextInputLayout amountTil;
    private TextInputEditText amountEt;

    private String flwRef;
    private Payload payLoad;
    private TextInputEditText cardNoTv;
    private TextInputLayout cardExpiryTil;
    private TextInputEditText cardExpiryTv;
    private SwitchCompat saveCardSwitch;
    private FrameLayout progressContainer;
    private RavePayInitializer ravePayInitializer;
    private boolean shouldISaveThisCard = false;
    Boolean hasSavedCards = false;
    private LinearLayout saveNewCardLayout;
    private EditText saveCardEmailEt;
    private EditText saveCardPhoneNoEt;
    private TextInputLayout saveCardEmailTil;
    private TextInputLayout saveCardPhoneNoTil;
    private String responseAsJsonString;
    private SavedCard selectedSavedCard;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_PRESENTER_SAVEDCARDS)) {
                Type savedCardsListType = new TypeToken<List<SavedCard>>() {
                }.getType();
                presenter.savedCards = (new Gson()).fromJson(savedInstanceState.getString
                                (STATE_PRESENTER_SAVEDCARDS),
                        savedCardsListType);
            }
        }

        injectComponents();

        v = inflater.inflate(R.layout.rave_sdk_fragment_card, container, false);

        initializeViews();

        pcidss_tv.setMovementMethod(LinkMovementMethod.getInstance());

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getRaveUiComponent()
                    .plus(new CardUiModule(this))
                    .inject(this);
        }
    }

    private void initializePresenter() {
        if (getActivity() != null) {
            ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();
            Log.d("okh", ravePayInitializer.isStaging() + " staging");
            presenter.init(ravePayInitializer);
        }
    }

    private void setListeners() {
        cardExpiryTv.addTextChangedListener(new ExpiryWatcher());
        payButton.setOnClickListener(this);
        useASavedCardTv.setOnClickListener(this);

        cardExpiryTv.setOnFocusChangeListener(this);
        cardNoTv.setOnFocusChangeListener(this);
        amountEt.setOnFocusChangeListener(this);
        emailEt.setOnFocusChangeListener(this);
        cvvTv.setOnFocusChangeListener(this);

        saveCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveNewCardLayout.setVisibility(VISIBLE);
                    shouldISaveThisCard = true;
                } else {
                    saveNewCardLayout.setVisibility(View.GONE);
                    shouldISaveThisCard = false;
                }
            }
        });
    }

    private void onSavedCardSelected(SavedCard savedCard) {
        selectedSavedCard = savedCard;
        chargeType = SAVED_CARD_CHARGE;
        presenter.processSavedCardTransaction(savedCard, ravePayInitializer);
    }


    private void initializeViews() {
        progressContainer = v.findViewById(R.id.rave_progressContainer);
        pcidss_tv = v.findViewById(R.id.rave_pcidss_compliant_tv);
        saveCardSwitch = v.findViewById(R.id.rave_saveCardSwitch);
        cardExpiryTil = v.findViewById(R.id.rave_cardExpiryTil);
        cardExpiryTv = v.findViewById(R.id.rave_cardExpiryTv);
        payButton = v.findViewById(R.id.rave_payButton);
        cardNoTil = v.findViewById(R.id.rave_cardNoTil);
        amountTil = v.findViewById(R.id.rave_amountTil);
        emailTil = v.findViewById(R.id.rave_emailTil);
        cardNoTv = v.findViewById(R.id.rave_cardNoTv);
        amountEt = v.findViewById(R.id.rave_amountEt);
        emailEt = v.findViewById(R.id.rave_emailEt);
        cvvTil = v.findViewById(R.id.rave_cvvTil);
        cvvTv = v.findViewById(R.id.rave_cvvTv);
        useAnotherCardTv = (TextView) v.findViewById(R.id.rave_use_new_card_tv);
        useASavedCardTv = (TextView) v.findViewById(R.id.rave_use_saved_card_tv);
        useASavedCardTv.setVisibility(GONE);
        saveCardSwitch = (SwitchCompat) v.findViewById(R.id.rave_saveCardSwitch);
        saveCardPhoneNoEt = (EditText) v.findViewById(R.id.save_card_phoneNoTV);
        saveCardEmailEt = (EditText) v.findViewById(R.id.save_card_emailTv);
        saveCardPhoneNoTil = (TextInputLayout) v.findViewById(R.id.save_card_phoneNoTil);
        saveCardEmailTil = (TextInputLayout) v.findViewById(R.id.save_card_emailTil);
        saveNewCardLayout = (LinearLayout) v.findViewById(R.id.rave_layout_for_saving_card);


    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            collectData();
        }
        if (i == R.id.rave_use_saved_card_tv) {
            if (!hasSavedCards) {
                showToast("You have no saved Cards");
            } else {
                clearErrors();
                collectDataForSavedCardCharge();
            }
        }
    }

    private void collectDataForSavedCardCharge() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldEmail, new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));

        presenter.onDataForSavedCardChargeCollected(dataHashMap, ravePayInitializer);
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldEmail, new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldCvv, new ViewObject(cvvTil.getId(), cvvTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldCardExpiry, new ViewObject(cardExpiryTil.getId(), cardExpiryTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldcardNoStripped, new ViewObject(cardNoTil.getId(), cardNoTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldPhone, new ViewObject(saveCardPhoneNoTil.getId(), saveCardPhoneNoEt.getText().toString(), TextInputLayout.class));

        presenter.onDataCollected(dataHashMap);
    }

    private void clearErrors() {
        cardExpiryTil.setErrorEnabled(false);
        cardNoTil.setErrorEnabled(false);
        amountTil.setErrorEnabled(false);
        emailTil.setErrorEnabled(false);
        cvvTil.setErrorEnabled(false);
        cardExpiryTil.setError(null);
        amountTil.setError(null);
        emailTil.setError(null);
        cardNoTil.setError(null);
        cvvTil.setError(null);
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
        chargeType = MANUAL_CARD_CHARGE;
        presenter.processTransaction(dataHashMap, ravePayInitializer);
    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

        if (viewType == TextInputLayout.class) {
            TextInputLayout view = v.findViewById(viewID);
            view.setError(message);
        } else if (viewType == EditText.class) {
            EditText view = v.findViewById(viewID);
            view.setError(message);
        }

    }

    @Override
    public void onEmailValidated(String emailToSet, int visibility) {
        emailTil.setVisibility(visibility);
        emailEt.setText(emailToSet);
        saveCardEmailEt.setText(emailToSet);
    }


    @Override
    public void onAmountValidated(String amountToSet, int visibility) {
        amountTil.setVisibility(visibility);
        amountEt.setText(amountToSet);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = new CardUiPresenter(this);
        }
        presenter.onAttachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetachView();
        }
    }


    /**
     * Show/Hide a progress dialog (general purpose)
     *
     * @param active = status of progress indicator
     */
    @Override
    public void showProgressIndicator(boolean active) {

        try {
            if (getActivity().isFinishing()) {
                return;
            }
            if (progessDialog == null) {
                progessDialog = new ProgressDialog(getActivity());
                progessDialog.setCanceledOnTouchOutside(false);
                progessDialog.setMessage(getResources().getString(R.string.wait));
            }

            if (active && !progessDialog.isShowing()) {
                progessDialog.show();
            } else {
                progessDialog.dismiss();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when there's a non fatal error in payment. Shows a toast with the error message
     *
     * @param errorMessage = response message to display
     */
    @Override
    public void onPaymentError(String errorMessage) {
        dismissDialog();
        presenter.logEvent(new ErrorEvent(errorMessage).getEvent(), ravePayInitializer.getPublicKey());
        Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * Called when a pin suggested auth model is required.
     * It shows a dialog that receives the pin and sends the payment payload
     *
     * @param payload = Contains card payment details
     */
    @Override
    public void collectCardPin(final Payload payload) {
        this.payLoad = payload;   //added so as to get back in onActivityResult
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "pin");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_PIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
            //just to be sure this v sent the receiving intent
            switch (requestCode) {
                case FOR_PIN:
                    String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                    presenter.chargeCardWithPinAuthModel(payLoad, pin, ravePayInitializer.getEncryptionKey());
                    break;
                case FOR_AVBVV:
                case FOR_NOAUTH_INTERNATIONAL:
                    String streetAddress = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                    String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                    String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                    String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                    String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                    AddressDetails address = new AddressDetails(streetAddress, city, state, zipCode, country);

                    String authModel = null;
                    if (requestCode == FOR_AVBVV) authModel = AVS_VBVSECURECODE;
                    else if (requestCode == FOR_NOAUTH_INTERNATIONAL)
                        authModel = NOAUTH_INTERNATIONAL;

                    presenter.chargeCardWithAddressDetails(payLoad, address, ravePayInitializer.getEncryptionKey(), authModel);
                    break;
                case FOR_INTERNET_BANKING:
                    presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
                    break;
                case FOR_OTP:
                    String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                    if (data.getBooleanExtra(OTPFragment.IS_SAVED_CARD_CHARGE, false)) {
                        payLoad.setOtp(otp);
                        presenter.chargeSavedCard(payLoad, selectedSavedCard, ravePayInitializer.getEncryptionKey());
                    } else
                        presenter.validateCardCharge(flwRef, otp, ravePayInitializer.getPublicKey());
                    break;
                case FOR_SAVED_CARDS:
                    if (data.hasExtra(SavedCardsFragment.EXTRA_SAVED_CARDS)) {
                        SavedCard savedCardToCharge = new Gson().fromJson(
                                data.getStringExtra(SavedCardsFragment.EXTRA_SAVED_CARDS),
                                SavedCard.class);
                        onSavedCardSelected(savedCardToCharge);
                    }
                    presenter.checkForSavedCardsInMemory(ravePayInitializer);
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Type savedCardsListType = new TypeToken<List<SavedCard>>() {
        }.getType();
        outState.putString(STATE_PRESENTER_SAVEDCARDS,
                (new Gson()).toJson(presenter.savedCards, savedCardsListType));
        super.onSaveInstanceState(outState);
    }

    /**
     * Displays a toast with the message parameter
     *
     * @param message = text to display
     */
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hide all dialog if available
     */
    private void dismissDialog() {

        if (dialog != null && !dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * If an OTP is required, this method shows the dialog that receives it
     *
     * @param flwRef
     * @param message
     */
    @Override
    public void collectOtp(String flwRef, String message) {
        this.flwRef = flwRef;
        dismissDialog();
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, message);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_OTP);
    }

    public void showOTPLayoutForSavedCard(Payload payload, String authInstruction) {
        this.payLoad = payload;
        dismissDialog();
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, authInstruction);
        intent.putExtra(OTPFragment.IS_SAVED_CARD_CHARGE, true);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_OTP);
    }

    @Override
    public void showSavedCardsLayout(List<SavedCard> savedCardsList) {
        Intent intent = new Intent(getContext(), SavedCardsActivity.class);
        Type savedCardsListType = new TypeToken<List<SavedCard>>() {
        }.getType();
        intent.putExtra(SavedCardsFragment.EXTRA_SAVED_CARDS,
                (new Gson()).toJson(savedCardsList, savedCardsListType));
        intent.putExtra(SavedCardsActivity.ACTIVITY_MOTIVE, SavedCardsFragment.SAVED_CARD_MOTIVE);
        startActivityForResult(intent, FOR_SAVED_CARDS);
    }

    @Override
    public void setHasSavedCards(boolean b) {
        hasSavedCards = b;
        if (b) useASavedCardTv.setVisibility(VISIBLE);
        else useASavedCardTv.setVisibility(GONE);
    }

    /**
     * Called when the auth model suggested is VBV. It opens a WebView
     * that loads the authURL
     *
     * @param authenticationUrl URL to display in webview
     * @param flwRef  Reference of the payment transaction
     */
    @Override
    public void showWebPage(String authenticationUrl, String flwRef) {

        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authenticationUrl);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_INTERNET_BANKING);

    }

    @Override
    public void onPhoneNumberValidated(String phoneNumber) {
        saveCardPhoneNoEt.setText(phoneNumber);
    }

    @Override
    public void showCardSavingOption(boolean shouldShow) {
        if (shouldShow) saveCardSwitch.setVisibility(VISIBLE);
        else saveCardSwitch.setVisibility(GONE);
    }

    @Override
    public void collectOtpForSaveCardCharge(Payload payload) {
        String authInstruction = "Enter the one time password (OTP) sent to " +
                phoneNumberObfuscator.obfuscatePhoneNumber(payload
                        .getPhonenumber());
        showOTPLayoutForSavedCard(payload, authInstruction);
    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {
        // Perform lookup of saved savedCards and save to phone storage
        presenter.lookupSavedCards(ravePayInitializer.getPublicKey(), phoneNumber);

    }

    @Override
    public void onCardSaveFailed(String message) {

        showToast("Unable to save card");
        presenter.setCardSaveInProgress(false);

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJsonString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }

    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {
        if (cards != null && cards.size() != 0) hasSavedCards = true;
        //Save details to phone
        presenter.saveCardToSharedPreferences(cards, phoneNumber, ravePayInitializer.getPublicKey());
        // Save details in app memory
        presenter.retrieveSavedCardsFromMemory(ravePayInitializer.getPhoneNumber(),
                ravePayInitializer.getPublicKey());

        presenter.setCardSaveInProgress(false);

        if (responseAsJsonString != null) {
            // If this is a lookup after successful charge
            Intent intent = new Intent();
            intent.putExtra("response", responseAsJsonString);

            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }
    }

    @Override
    public void onSavedCardsLookupFailed(String message) {
        Intent intent = new Intent();
        intent.putExtra("response", responseAsJsonString);

        if (responseAsJsonString != null) {// If this is after successful charge
            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }
    }

    /**
     * Called after a fatal failure in a transaction. It closes all open dialogs
     * and bottomsheets if any and send back the result of payment to the calling activity
     *
     * @param status               = status of the transaction
     * @param responseAsJsonString = full json response from the payment transaction
     */
    @Override
    public void onPaymentFailed(String status, String responseAsJsonString) {
        dismissDialog();

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJsonString);
        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    /**
     * Called after a successful transaction occurs. It closes all open dialogs
     * and bottomsheets if any and send back the result of payment to the calling activity
     *
     * @param status               = status of the transaction
     * @param flwRef               = reference of the payment transaction
     * @param responseAsJSONString = full json response from the payment transaction
     */
    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString) {
        this.responseAsJsonString = responseAsJSONString;
        dismissDialog();

        if (shouldISaveThisCard && flwRef != null) {
            presenter.setCardSaveInProgress(true);
            presenter.saveCardToRave(
                    ravePayInitializer.getPhoneNumber(),
                    ravePayInitializer.getEmail(),
                    flwRef,
                    ravePayInitializer.getPublicKey()
            );
        }

        if (!presenter.isCardSaveInProgress()) {
            Intent intent = new Intent();
            intent.putExtra("response", responseAsJSONString);

            if (getActivity() != null) {
                ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }// else, result will be delivered after card save [in onCardSaveSuccessful()]

    }

    /**
     * Displays the error message from a failed fetch fee request
     *
     * @param errorMessage = error message
     */
    @Override
    public void onFetchFeeError(String errorMessage) {
        presenter.logEvent(new ErrorEvent(errorMessage).getEvent(), ravePayInitializer.getPublicKey());
        showToast(errorMessage);
    }

    /**
     * @param chargeAmount = Total amount to be charged (transaction fees incuded)
     * @param payload      = Object that contains the payment info (Contains card payment details)
     */
    @Override
    public void onTransactionFeeFetched(String chargeAmount, final Payload payload) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + " " + chargeAmount + " " + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());

                    if (chargeType == MANUAL_CARD_CHARGE) {
                        presenter.chargeCard(payload, ravePayInitializer.getEncryptionKey());
                    } else if (chargeType == SAVED_CARD_CHARGE) {
                        presenter.chargeSavedCard(payload, selectedSavedCard, ravePayInitializer.getEncryptionKey());
                    }

                }
            }).setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.logEvent(new FeeDisplayResponseEvent(false).getEvent(), ravePayInitializer.getPublicKey());
                }
            });

            builder.show();
        }
    }

    @Override
    public void collectCardAddressDetails(final Payload payload, String authModel) {
        this.payLoad = payload;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
        intent.putExtra("theme", ravePayInitializer.getTheme());

        int requestCode = 0;
        if (authModel.equals(AVS_VBVSECURECODE)) requestCode = FOR_AVBVV;
        else requestCode = FOR_NOAUTH_INTERNATIONAL;
        startActivityForResult(intent, requestCode);
    }

    private class ExpiryWatcher implements TextWatcher {

        private final Calendar calendar;
        private final SimpleDateFormat simpleDateFormat;
        private String lastInput = "";

        public ExpiryWatcher() {
            calendar = Calendar.getInstance();
            simpleDateFormat = new SimpleDateFormat("MM/yy");
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String input = editable.toString();
            String cardExpiryToSet = cardExpiryTv.getText().toString() + "/";

            try {
                calendar.setTime(simpleDateFormat.parse(input));
            } catch (ParseException e) {

                if (editable.length() == 2 && !lastInput.endsWith("/")) {

                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        cardExpiryTv.setText(cardExpiryToSet);
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    } else {
                        cardExpiryTv.setText(getResources().getString(R.string.defaultCardExpiry));
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                } else if (editable.length() == 2 && lastInput.endsWith("/")) {
                    try {
                        int month = Integer.parseInt(input);
                        if (month <= 12) {
                            cardExpiryTv.setText(cardExpiryTv.getText().toString().substring(0, 1));
                            cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                        } else {
                            cardExpiryTv.setText(getResources().getString(R.string.defaultCardExpiry));
                            cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                        }
                    } catch (NumberFormatException ex) {
                        cardExpiryTv.setText(input.replace("/", ""));
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    } catch (Resources.NotFoundException ex) {
                        ex.printStackTrace();
                    }

                } else if (editable.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        cardExpiryTv.setText("0" + cardExpiryTv.getText().toString() + "/");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                }
            }

            lastInput = cardExpiryTv.getText().toString();
        }
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        int i = view.getId();

        String fieldName = "";

        if (i == R.id.rave_cvvTv) {
            fieldName = "CVV";
        } else if (i == R.id.rave_amountEt) {
            fieldName = "Amount";
        } else if (i == R.id.rave_emailEt) {
            fieldName = "Email";
        } else if (i == R.id.rave_cardNoTv) {
            fieldName = "Card Number";
        } else if (i == R.id.rave_cardExpiryTv) {
            fieldName = "Card Expiry";
        }

        if (hasFocus) {
            presenter.logEvent(new StartTypingEvent(fieldName).getEvent(), ravePayInitializer.getPublicKey());
        }
    }


}
