package com.flutterwave.raveandroid.card;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.SwipeToDeleteCallback;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.card.savedcards.SavedCardRecyclerAdapter;
import com.flutterwave.raveandroid.card.savedcards.SavedCardsActivity;
import com.flutterwave.raveandroid.card.savedcards.SavedCardsFragment;
import com.flutterwave.raveandroid.data.EmailObfuscator;
import com.flutterwave.raveandroid.data.PhoneNumberObfuscator;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.di.modules.CardUiModule;
import com.flutterwave.raveandroid.rave_core.models.SavedCard;
import com.flutterwave.raveandroid.rave_java_commons.Payload;
import com.flutterwave.raveandroid.rave_logger.events.StartTypingEvent;
import com.flutterwave.raveandroid.rave_presentation.data.AddressDetails;
import com.flutterwave.raveandroid.rave_presentation.data.events.ErrorEvent;
import com.flutterwave.raveandroid.rave_remote.Callbacks;
import com.flutterwave.raveandroid.rave_remote.responses.SaveCardResponse;
import com.flutterwave.raveutils.verification.AVSVBVFragment;
import com.flutterwave.raveutils.verification.OTPFragment;
import com.flutterwave.raveutils.verification.PinFragment;
import com.flutterwave.raveutils.verification.RaveVerificationUtils;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.ADDRESS_DETAILS_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.MANUAL_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.OTP_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.PIN_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.SAVED_CARD_CHARGE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.WEB_VERIFICATION_REQUEST_CODE;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldPhone;
import static com.flutterwave.raveandroid.rave_java_commons.RaveConstants.fieldcardNoStripped;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements View.OnClickListener, CardUiContract.View, View.OnFocusChangeListener {

    @Inject
    CardUiPresenter presenter;
    int chargeType = MANUAL_CARD_CHARGE;

    @Inject
    PhoneNumberObfuscator phoneNumberObfuscator;
    @Inject
    EmailObfuscator emailObfuscator;
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
    private String authModel;
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
    private ScrollView newCardOverallLay;
    private NestedScrollView savedCardOverallLay;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_PRESENTER_SAVEDCARDS)) {
                Type savedCardsListType = new TypeToken<List<SavedCard>>() {
                }.getType();
                presenter.savedCards = (new Gson()).fromJson(savedInstanceState.getString
                                (STATE_PRESENTER_SAVEDCARDS),
                        savedCardsListType);
            }
        }

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
        useAnotherCardTv.setOnClickListener(this);

        cardExpiryTv.setOnFocusChangeListener(this);
        cardNoTv.setOnFocusChangeListener(this);
        amountEt.setOnFocusChangeListener(this);
        emailEt.setOnFocusChangeListener(this);
        cvvTv.setOnFocusChangeListener(this);

        saveCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    presenter.onSavedCardSwitchSwitchedOn(ravePayInitializer);
                    shouldISaveThisCard = true;
                } else {
                    saveNewCardLayout.setVisibility(View.GONE);
                    shouldISaveThisCard = false;
                }
            }
        });
    }

    @Override
    public void setSavedCardsLayoutVisibility(boolean showPhoneEmailFields) {
        if (showPhoneEmailFields) {
            saveNewCardLayout.setVisibility(VISIBLE);
        } else {
            saveNewCardLayout.setVisibility(View.GONE);
        }
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
        newCardOverallLay = (ScrollView) v.findViewById(R.id.new_card_overall_lay);
        savedCardOverallLay = (NestedScrollView) v.findViewById(R.id.saved_card_overall_lay);
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
        if(i == R.id.rave_use_new_card_tv){
            switchToSaveCards(false);
        }
    }

    private void switchToSaveCards(boolean switchToSaveCards){
        if(switchToSaveCards){
            savedCardOverallLay.setVisibility(VISIBLE);
            newCardOverallLay.setVisibility(GONE);
        } else {
            savedCardOverallLay.setVisibility(GONE);
            newCardOverallLay.setVisibility(VISIBLE);
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
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showPinScreen();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
            //just to be sure this v sent the receiving intent
            switch (requestCode) {
                case PIN_REQUEST_CODE:
                    String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                    presenter.chargeCardWithPinAuthModel(payLoad, pin, ravePayInitializer.getEncryptionKey());
                    break;
                case ADDRESS_DETAILS_REQUEST_CODE:
                    String streetAddress = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                    String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                    String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                    String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                    String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                    AddressDetails address = new AddressDetails(streetAddress, city, state, zipCode, country);

                    presenter.chargeCardWithAddressDetails(payLoad, address, ravePayInitializer.getEncryptionKey(), authModel);
                    break;
                case WEB_VERIFICATION_REQUEST_CODE:
                    presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
                    break;
                case OTP_REQUEST_CODE:
                    String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                    if (data.getBooleanExtra(OTPFragment.IS_SAVED_CARD_CHARGE, false)) {
                        payLoad.setOtp(otp);
                        presenter.chargeSavedCard(payLoad, ravePayInitializer.getEncryptionKey());
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
        Toast.makeText(requireContext(), message+"", Toast.LENGTH_SHORT).show();
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
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showOtpScreen(message);
    }

    public void showOTPLayoutForSavedCard(Payload payload, String authInstruction) {
        this.payLoad = payload;
        dismissDialog();
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showOtpScreenForSavedCard(authInstruction);
    }

    @Override
    public void showSavedCardsLayout(List<SavedCard> savedCardsList) {
        switchToSaveCards(true);
    }

    @Override
    public void setHasSavedCards(boolean b, List<SavedCard> savedCards) {
        hasSavedCards = b;
        switchToSaveCards(b);
        if (b) {
            useASavedCardTv.setVisibility(VISIBLE);
            setUpSavedCardsAdapter(savedCards);
        } else {
            useASavedCardTv.setVisibility(GONE);
            if(savedCards == null) savedCards = new ArrayList();
            setUpSavedCardsAdapter(savedCards);
        }
    }

    private void setUpSavedCardsAdapter(final List<SavedCard> savedCards){
        final SavedCardRecyclerAdapter adapter = new SavedCardRecyclerAdapter();
        adapter.set(savedCards);
        adapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
            @Override
            public void onCardSelected(SavedCard savedCard) {
                onSavedCardSelected(savedCard);
            }
        });
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rave_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(isVisible()){
            SwipeToDeleteCallback swipeHandler = new SwipeToDeleteCallback(getActivity()) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    SavedCard savedCard = adapter.getCards().get(viewHolder.getAdapterPosition());
                    presenter.deleteASavedCard(savedCard.getCardHash(), ravePayInitializer.getPhoneNumber(), ravePayInitializer.getPublicKey());
                }
            };
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeHandler);
            itemTouchHelper.attachToRecyclerView(recyclerView);
        }

        recyclerView.setAdapter(adapter);
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
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showWebpageVerificationScreen(authenticationUrl);
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
                        .getPhonenumber()) + " or " + emailObfuscator.obfuscateEmail(payload
                .getEmail());
        showOTPLayoutForSavedCard(payload, authInstruction);
    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String phoneNumber) {
        // Perform lookup of saved savedCards and save to phone storage
        presenter.lookupSavedCards(ravePayInitializer.getPublicKey(), phoneNumber, false);

    }

    @Override
    public void onCardSaveFailed(String message) {

        showToast("Unable to save card:" + message);
        presenter.setCardSaveInProgress(false);

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJsonString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }

    }

    @Override
    public void onSavedCardRemoveSuccessful() {
        presenter.lookupSavedCards(ravePayInitializer.getPublicKey(), ravePayInitializer.getPhoneNumber(), true);
    }

    @Override
    public void onSavedCardRemoveFailed(String message) {
        ((RecyclerView) v.findViewById(R.id.rave_recycler)).getAdapter().notifyDataSetChanged();
        showToast(message);
    }

    @Override
    public void onSavedCardsLookupSuccessful(List<SavedCard> cards, String phoneNumber) {
        if (cards != null && cards.size() != 0) hasSavedCards = true;
        //Save details to phone
        presenter.saveCardToSharedPreferences(cards, phoneNumber, ravePayInitializer.getPublicKey());
        // Save details in app memory
        presenter.checkForSavedCardsInMemory(ravePayInitializer);

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
        setHasSavedCards(false, new ArrayList<SavedCard>());
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
    public void onTransactionFeeFetched(String chargeAmount, final Payload payload, String fee) {
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
                        payload.setSavedCardDetails(selectedSavedCard);
                        presenter.chargeSavedCard(payload, ravePayInitializer.getEncryptionKey());
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
        this.authModel = authModel;
        new RaveVerificationUtils(this, ravePayInitializer.isStaging(), ravePayInitializer.getPublicKey(), ravePayInitializer.getTheme())
                .showAddressScreen();
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
