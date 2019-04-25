package com.flutterwave.raveandroid.card;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.util.Linkify;
import android.text.util.Linkify.TransformFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.SavedCardsFragment;
import com.flutterwave.raveandroid.MultipurposeActivity;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.AVSVBVFragment;
import com.flutterwave.raveandroid.OTPFragment;
import com.flutterwave.raveandroid.PinFragment;
import com.flutterwave.raveandroid.WebFragment;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.LookupSavedCardsResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.responses.SaveCardResponse;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.PIN;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements View.OnClickListener, CardContract.View {

    private static final String RAVEPAY = "ravepay";
    public static final String INTENT_SENDER = "cardFrag";
    public static final int FOR_AVBVV = 333;
    public static final int FOR_PIN = 444;
    public static final int FOR_INTERNET_BANKING = 555;
    public static final int FOR_OTP = 666;
    private static final int FOR_SAVED_CARDS = 777;
    private static final String STATE_PRESENTER_SAVEDCARDS = "presenter_saved_cards";
    Button useASavedCardButton;
    TextView useAnotherCardTv;
    TextInputEditText amountEt;
    TextInputEditText emailEt;
    TextInputEditText cardNoTv;
    TextInputEditText cardExpiryTv;
    TextInputEditText cvvTv;
    TextInputLayout amountTil;
    TextInputLayout emailTil;
    TextInputLayout cardNoTil;
    TextInputLayout cardExpiryTil;
    TextInputLayout cvvTil;
    SwitchCompat saveCardSwitch;
    Button payButton;
    private ProgressDialog progessDialog;
    CardPresenter presenter;
    private String flwRef;
    RavePayInitializer ravePayInitializer;
    private TextView pcidss_tv;
    private Payload payLoad;
    private AlertDialog dialog;
    FrameLayout progressContainer;
    View v;

    boolean shouldISaveThisCard = false;
    Boolean hasSavedCards = false;
    private LinearLayout saveNewCardLayout;
    private EditText saveCardEmailEt;
    private EditText saveCardPhoneNoEt;
    private String emailForSavingCard;
    private String phoneNumber;


    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        presenter = new CardPresenter(getActivity(), this);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_PRESENTER_SAVEDCARDS)) {
                Type savedCardsListType = new TypeToken<List<SavedCard>>() {
                }.getType();
                presenter.savedCards = (new Gson()).fromJson(savedInstanceState.getString
                                (STATE_PRESENTER_SAVEDCARDS),
                        savedCardsListType);
            }
        }

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_card, container, false);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        emailEt = (TextInputEditText) v.findViewById(R.id.rave_emailTv);
        cardNoTv = (TextInputEditText) v.findViewById(R.id.rave_cardNoTv);
        cardExpiryTv = (TextInputEditText) v.findViewById(R.id.rave_cardExpiryTv);
        cvvTv = (TextInputEditText) v.findViewById(R.id.rave_cvvTv);
        payButton = (Button) v.findViewById(R.id.rave_payButton);
        saveCardSwitch = (SwitchCompat) v.findViewById(R.id.rave_saveCardSwitch);
        saveCardPhoneNoEt = (EditText) v.findViewById(R.id.save_card_phoneNoTV);
        saveCardEmailEt = (EditText) v.findViewById(R.id.save_card_emailTv);
        saveNewCardLayout = (LinearLayout) v.findViewById(R.id.rave_layout_for_saving_card);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        emailTil = (TextInputLayout) v.findViewById(R.id.rave_emailTil);
        cardNoTil = (TextInputLayout) v.findViewById(R.id.rave_cardNoTil);
        cardExpiryTil = (TextInputLayout) v.findViewById(R.id.rave_cardExpiryTil);
        cvvTil = (TextInputLayout) v.findViewById(R.id.rave_cvvTil);
        pcidss_tv = (TextView) v.findViewById(R.id.rave_pcidss_compliant_tv);
        progressContainer = (FrameLayout) v.findViewById(R.id.rave_progressContainer);
        useAnotherCardTv = (TextView) v.findViewById(R.id.rave_use_new_card_tv);
        useASavedCardButton = (Button) v.findViewById(R.id.rave_use_saved_card_button);
        useASavedCardButton.setVisibility(GONE);

        presenter.checkForSavedCardsInMemory(ravePayInitializer.getPublicKey());

        useASavedCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hasSavedCards) {
                    showToast("You have no saved Cards");
                } else {
                    if (presenter.savedCards == null) presenter.checkForSavedCardsInMemory
                            (ravePayInitializer.getPublicKey());
                    showSavedCardsLayout(presenter.savedCards);
                }
            }
        });

        // Check for saved cards on Rave server
        if (ravePayInitializer.getPhoneNumber().length() > 0) {
            presenter.lookupSavedCards(ravePayInitializer.getPublicKey(),
                    ravePayInitializer.getPhoneNumber(), "");
        }

        saveCardSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    saveNewCardLayout.setVisibility(View.VISIBLE);
                    shouldISaveThisCard = true;
                } else {
                    saveNewCardLayout.setVisibility(View.GONE);
                    shouldISaveThisCard = false;
                }
            }
        });

        TransformFilter filter = new TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern = Pattern.compile("()PCI-DSS COMPLIANT");
        Linkify.addLinks(pcidss_tv, pattern, "https://www.pcisecuritystandards.org/pci_security/", null, filter);


        cardExpiryTv.addTextChangedListener(new ExpiryWatcher());

        payButton.setOnClickListener(this);


        if (Utils.isEmailValid(ravePayInitializer.getEmail())) {
            emailTil.setVisibility(GONE);
            emailEt.setText(ravePayInitializer.getEmail());
            saveCardEmailEt.setText(ravePayInitializer.getEmail());
        }

        if (ravePayInitializer.getPhoneNumber().length() > 0) {
            saveCardPhoneNoEt.setText(ravePayInitializer.getPhoneNumber());
        }
        double amountToPay = ravePayInitializer.getAmount();

        if (amountToPay > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(amountToPay));
        }

        return v;
    }


    private void chargeSavedCard(SavedCard savedCard) {
        clearErrors();
        Utils.hide_keyboard(getActivity());

        boolean valid = true;

        String amount = amountEt.getText().toString();
        String email = emailEt.getText().toString();

        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                amountTil.setError("Enter a valid amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
            valid = false;
            amountTil.setError("Enter a valid amount");
        }

        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailTil.setError("Enter a valid email");
        }


        if (valid) {
            ravePayInitializer.setAmount(Double.parseDouble(amount));

            //make request
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "")
                    .setCountry(ravePayInitializer.getCountry()).setCurrency(ravePayInitializer.getCurrency())
                    .setEmail(email).setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName()).setIP(Utils.getDeviceImei(getActivity()))
                    .setTxRef(ravePayInitializer.getTxRef())
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey()).setDevice_fingerprint(Utils
                    .getDeviceImei(getActivity())).setSavedCard(savedCard)
                    .setIs_saved_card_charge(true).setPhoneNumber(phoneNumber);

            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createSavedCardChargePayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                presenter.fetchFee(body, RaveConstants.SAVED_CARD_CHARGE);
            } else {
                presenter.chargeSavedCard(body, ravePayInitializer.getEncryptionKey());
            }

        }
    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.rave_payButton) {
            validateDetails();
        }
    }

    @Override
    public void onNoAuthUsed(String flwRef, String publicKey) {
        presenter.requeryTx(flwRef, publicKey);
    }

    @Override
    public void onNoAuthInternationalSuggested(final Payload payload) {
        this.payLoad = payload;

        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "avsvbv");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_AVBVV);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter != null) {
            presenter = new CardPresenter(getActivity(), this);
        }
        assert presenter != null;
        presenter.onAttachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (presenter != null) {
            presenter.onDetachView();
        }
    }

    @Override
    public void onValidateCardChargeFailed(String flwRef, String responseAsJSON) {

        dismissDialog();

        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());

    }

    /**
     * Validate card details and get the fee if available
     */
    private void validateDetails() {

        clearErrors();
        Utils.hide_keyboard(getActivity());

        boolean valid = true;

        String amount = amountEt.getText().toString();
        String email = emailEt.getText().toString();
        String cvv = cvvTv.getText().toString();
        String expiryDate = cardExpiryTv.getText().toString();
        String cardNo = cardNoTv.getText().toString();
        phoneNumber = saveCardPhoneNoEt.getText().toString();
        phoneNumber = phoneNumber.replaceAll("\\s", "");
        emailForSavingCard = saveCardEmailEt.getText().toString();


        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                amountTil.setError("Enter a valid amount");
            }
        } catch (Exception e) {
            e.printStackTrace();
            valid = false;
            amountTil.setError("Enter a valid amount");
        }

        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailTil.setError("Enter a valid email");
        }

        if (shouldISaveThisCard && !Utils.isEmailValid(emailForSavingCard)) {
            valid = false;
            saveCardEmailEt.setError("Enter a valid email");
        }

        if (shouldISaveThisCard && phoneNumber.length() < 1) {
            valid = false;
            saveCardPhoneNoEt.setError("Enter a Phone Number");
        }

        if (cvv.length() < 3) {
            valid = false;
            cvvTil.setError("Enter a valid cvv");
        }

        if (expiryDate.length() != 5) {
            cardExpiryTil.setError("Enter a valid expiry date");
            valid = false;
        }

        String cardNoStripped = cardNo.replaceAll("\\s", "");

        if (cardNoStripped.length() < 12 | !Utils.isValidLuhnNumber(cardNoStripped)) {
            valid = false;
            cardNoTil.setError("Enter a valid credit card number");
        }

        if (valid) {

            ravePayInitializer.setAmount(Double.parseDouble(amount));


            //make request
            String txRef = ravePayInitializer.getTxRef();
            Log.d("txRef", txRef);
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "").setCardno(cardNoStripped)
                    .setCountry(ravePayInitializer.getCountry()).setCurrency(ravePayInitializer.getCurrency())
                    .setCvv(cvv).setEmail(email).setFirstname(ravePayInitializer.getfName())
                    .setLastname(ravePayInitializer.getlName()).setIP(Utils.getDeviceImei(getActivity())).setTxRef(ravePayInitializer.getTxRef())
                    .setExpiryyear(expiryDate.substring(3, 5)).setExpirymonth(expiryDate.substring(0, 2))
                    .setMeta(ravePayInitializer.getMeta())
                    .setSubAccount(ravePayInitializer.getSubAccount())
                    .setIsPreAuth(ravePayInitializer.getIsPreAuth())
                    .setPBFPubKey(ravePayInitializer.getPublicKey()).setDevice_fingerprint(Utils
                    .getDeviceImei(getActivity()));


            if (ravePayInitializer.getPayment_plan() != null) {
                builder.setPaymentPlan(ravePayInitializer.getPayment_plan());
            }

            Payload body = builder.createPayload();

            if (ravePayInitializer.getIsDisplayFee()) {
                presenter.fetchFee(body, RaveConstants.MANUAL_CARD_CHARGE);
            } else {
                presenter.chargeCard(body, ravePayInitializer.getEncryptionKey());
            }

        }
    }

    /**
     * Remove all errors from the input fields
     */
    private void clearErrors() {
        amountTil.setError(null);
        emailTil.setError(null);
        cvvTil.setError(null);
        cardExpiryTil.setError(null);
        cardNoTil.setError(null);

        amountTil.setErrorEnabled(false);
        emailTil.setErrorEnabled(false);
        cvvTil.setErrorEnabled(false);
        cardExpiryTil.setErrorEnabled(false);
        cardNoTil.setErrorEnabled(false);

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
                progessDialog.setMessage("Please wait...");
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
     * @param message = response message to display
     */
    @Override
    public void onPaymentError(String message) {
        dismissDialog();
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Called when a pin suggested auth model is required.
     * It shows a dialog that receives the pin and sends the payment payload
     *
     * @param payload = Contains card payment details
     */
    @Override
    public void onPinAuthModelSuggested(final Payload payload) {
        this.payLoad = payload;   //added so as to get back in onActivityResult
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "pin");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_PIN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RavePayActivity.RESULT_SUCCESS) {
            //just to be sure this fragment sent the receiving intent
            if (requestCode == FOR_PIN) {
                String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                presenter.chargeCardWithSuggestedAuthModel(payLoad, pin, PIN, ravePayInitializer.getEncryptionKey());
            } else if (requestCode == FOR_AVBVV) {
                String address = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                presenter.chargeCardWithAVSModel(payLoad, address, city, zipCode, country, state,
                        RaveConstants.NOAUTH_INTERNATIONAL, ravePayInitializer.getEncryptionKey());
            } else if (requestCode == FOR_INTERNET_BANKING) {
                presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());
            } else if (requestCode == FOR_OTP) {
                String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                if (data.getBooleanExtra(OTPFragment.IS_SAVED_CARD_CHARGE, false)) {
                    payLoad.setOtp(otp);
                    presenter.chargeSavedCard(payLoad, ravePayInitializer.getEncryptionKey());
                } else presenter.validateCardCharge(flwRef, otp, ravePayInitializer.getPublicKey());
            } else if (requestCode == FOR_SAVED_CARDS) {
                if (data.hasExtra(SavedCardsFragment.EXTRA_SAVED_CARDS)) {
                    SavedCard savedCardToCharge = new Gson().fromJson(
                            data.getStringExtra(SavedCardsFragment.EXTRA_SAVED_CARDS),
                            SavedCard.class);
                    chargeSavedCard(savedCardToCharge);
                }
                presenter.checkForSavedCardsInMemory(ravePayInitializer.getPublicKey());
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
    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hide all dialog if available
     */
    private void dismissDialog() {

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * If an OTP is required, this method shows the dialog that receives it
     *
     * @param flwRef
     * @param chargeResponseMessage
     */
    @Override
    public void showOTPLayout(String flwRef, String chargeResponseMessage) {
        this.flwRef = flwRef;
        dismissDialog();
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, chargeResponseMessage);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_OTP);
    }

    @Override
    public void showOTPLayoutForSavedCard(Payload payload, String authInstruction) {
        this.payLoad = payload;
        dismissDialog();
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, authInstruction);
        intent.putExtra(OTPFragment.IS_SAVED_CARD_CHARGE, true);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_OTP);
    }

    @Override
    public void onSendRaveOtpFailed(String message, String responseAsJSONString) {
        showToast(message);
    }

    @Override
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public void showSavedCardsLayout(List<SavedCard> savedCardsList) {
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        Type savedCardsListType = new TypeToken<List<SavedCard>>() {
        }.getType();
        intent.putExtra(SavedCardsFragment.EXTRA_SAVED_CARDS,
                (new Gson()).toJson(savedCardsList, savedCardsListType));
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, SavedCardsFragment.SAVED_CARD_MOTIVE);
        startActivityForResult(intent, FOR_SAVED_CARDS);
    }

    @Override
    public void setHasSavedCards(boolean b) {
        hasSavedCards = b;
        if (b) useASavedCardButton.setVisibility(View.VISIBLE);
        else useASavedCardButton.setVisibility(GONE);
    }

    /**
     * Called when a payment that requires validation has been completed
     *
     * @param status               = status of the payment (success)
     * @param responseAsJSONString = full json response from the payment
     */
    @Override
    public void onValidateSuccessful(String status, String responseAsJSONString) {

        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey());

    }


    /**
     * Called when a validation error is received. Shows a toast
     *
     * @param message
     */
    @Override
    public void onValidateError(String message) {
        showToast(message);
    }

    /**
     * Called when the auth model suggested is VBV. It opens a webview
     * that loads the authURL
     *
     * @param authUrlCrude = URL to display in webview
     * @param flwRef       = reference of the payment transaction
     */
    @Override
    public void onVBVAuthModelUsed(String authUrlCrude, String flwRef) {

        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authUrlCrude);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_INTERNET_BANKING);

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef) {
        presenter.verifyRequeryResponse(response, responseAsJSONString, ravePayInitializer, flwRef);
    }

    @Override
    public void onCardSaveSuccessful(SaveCardResponse response, String verifyResponseAsJSONString) {
        showToast("Card Saved Successfully");


        // Perform lookup of saved savedCards and save to phone storage
        presenter.lookupSavedCards(ravePayInitializer.getPublicKey(), phoneNumber, verifyResponseAsJSONString);

    }

    @Override
    public void onCardSaveFailed(String message, String verifyResponseAsJSONString) {

        showToast("Unable to save card");
        presenter.setCardSaveInProgress(false);

        Intent intent = new Intent();
        intent.putExtra("response", verifyResponseAsJSONString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }

    }

    @Override
    public void onLookupSavedCardsSuccessful(LookupSavedCardsResponse response, String responseAsJSONString, String verifyResponseAsJSONString) {
        //Save details to phone
        presenter.saveCardToSharedPreferences(response, ravePayInitializer.getPublicKey());
        // Save details in app memory
        presenter.retrieveSavedCardsFromMemory(ravePayInitializer.getPhoneNumber(),
                ravePayInitializer.getPublicKey());

        presenter.setCardSaveInProgress(false);

        if (!verifyResponseAsJSONString.equalsIgnoreCase("")) {
            // If this is a lookup after successful charge
            Intent intent = new Intent();
            intent.putExtra("response", verifyResponseAsJSONString);

            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }
    }

    @Override
    public void onLookupSavedCardsFailed(String message, String responseAsJSONString, String verifyResponseAsJSONString) {
        Intent intent = new Intent();
        intent.putExtra("response", verifyResponseAsJSONString);

        if (!verifyResponseAsJSONString.equalsIgnoreCase("")) {
            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
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
        dismissDialog();

        if (shouldISaveThisCard && flwRef != null) {
            presenter.setCardSaveInProgress(true);
            presenter.saveCardToRave(
                    phoneNumber,
                    emailForSavingCard,
                    flwRef,
                    ravePayInitializer.getPublicKey(),
                    Utils.getDeviceImei(getActivity()),
                    responseAsJSONString);
        }

        if (!presenter.isCardSaveInProgress()) {
            Intent intent = new Intent();
            intent.putExtra("response", responseAsJSONString);

            if (getActivity() != null) {
                getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
                getActivity().finish();
            }
        }// else, result will be delivered after card save [in onCardSaveSuccessful()]
    }

    /**
     * Called after a fatal failure in a transaction. It closes all open dialogs
     * and bottomsheets if any and send back the result of payment to the calling activity
     *
     * @param status               = status of the transaction
     * @param responseAsJSONString = full json response from the payment transaction
     */
    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {
        dismissDialog();

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    /**
     * Called when a payment token is received. It creates the payment details object and
     * performs a token charge
     *
     * @param flwRef  = reference of the payment transaction
     * @param cardBIN = First 6 numbers of the card
     * @param token   = Auth token for savedCards
     */
    @Override
    public void onTokenRetrieved(String flwRef, String cardBIN, String token) {


        String txRef = ravePayInitializer.getTxRef();
        Log.d("txRef", txRef);
        PayloadBuilder builder = new PayloadBuilder();
        builder.setAmount(String.valueOf(ravePayInitializer.getAmount()))
                .setCountry(ravePayInitializer.getCountry())
                .setCurrency(ravePayInitializer.getCurrency())
                .setEmail(ravePayInitializer.getEmail())
                .setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName())
                .setIP(Utils.getDeviceImei(getActivity()))
                .setTxRef(ravePayInitializer.getTxRef())
                .setDevice_fingerprint(Utils.getDeviceImei(getActivity()))
                .setMeta(ravePayInitializer.getMeta());

        Payload body = builder.createPayload();
        body.setToken(token);
        body.setCardBIN(cardBIN);
        body.setPBFPubKey(ravePayInitializer.getPublicKey());
        presenter.fetchFee(body, RaveConstants.TOKEN_CHARGE);
    }

    /**
     * Called when an error occurs while token was being received. Closes any open bottom sheets
     * then shows a toast
     *
     * @param s = error message
     */
    @Override
    public void onTokenRetrievalError(String s) {
        showToast(s);
    }

    /**
     * @param charge_amount = Total amount to be charged (transaction fees incuded)
     * @param payload       = Object that contains the payment info (Contains card payment details)
     * @param why
     */
    @Override
    public void displayFee(String charge_amount, final Payload payload, final int why) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("You will be charged a total of " + charge_amount + ravePayInitializer.getCurrency() + ". Do you want to continue?");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (why == RaveConstants.MANUAL_CARD_CHARGE) {
                    presenter.chargeCard(payload, ravePayInitializer.getEncryptionKey());
                } else if (why == RaveConstants.TOKEN_CHARGE) {
                    presenter.chargeToken(payload);
                } else if (why == RaveConstants.SAVED_CARD_CHARGE) {
                    presenter.chargeSavedCard(payload, ravePayInitializer.getEncryptionKey());
                }

            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    /**
     * Displays the error message from a failed fetch fee request
     *
     * @param s = error message
     */
    @Override
    public void showFetchFeeFailed(String s) {
        showToast(s);
    }

    @Override
    public void onChargeTokenComplete(ChargeResponse response) {

        presenter
                .requeryTx(response.getData().getFlwRef(), ravePayInitializer.getPublicKey());
    }

    @Override
    public void onChargeCardSuccessful(ChargeResponse response) {
        presenter
                .requeryTx(response.getData().getFlwRef(),
                        ravePayInitializer.getPublicKey()
                );
    }

    @Override
    public void onAVS_VBVSECURECODEModelSuggested(final Payload payload) {
        this.payLoad = payload;
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "avsvbv");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_AVBVV);
    }

    /**
     * Called when the auth model suggested is AVS_VBVSecureCode. It opens a webview
     * that loads the authURL
     *
     * @param authurl = URL to display in webview
     * @param flwRef = reference of the payment transaction
     */
    @Override
    public void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef) {
        Intent intent = new Intent(getContext(), MultipurposeActivity.class);
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
        intent.putExtra(MultipurposeActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_INTERNET_BANKING);
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

            try {
                calendar.setTime(simpleDateFormat.parse(input));
            } catch (ParseException e) {
                if (editable.length() == 2 && !lastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        cardExpiryTv.setText(cardExpiryTv.getText().toString() + "/");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    } else {
                        cardExpiryTv.setText("12");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                } else if (editable.length() == 2 && lastInput.endsWith("/")) {
                    int month = Integer.parseInt(input);
                    if (month <= 12) {
                        cardExpiryTv.setText(cardExpiryTv.getText().toString().substring(0, 1));
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    } else {
                        cardExpiryTv.setText("12");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                } else if (editable.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        cardExpiryTv.setText("0" + cardExpiryTv.getText().toString() + "/");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                }

                lastInput = cardExpiryTv.getText().toString();
            }
        }
    }


    @Override
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
