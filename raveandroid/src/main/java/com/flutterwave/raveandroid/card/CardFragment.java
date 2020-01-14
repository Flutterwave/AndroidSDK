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
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.data.events.ErrorEvent;
import com.flutterwave.raveandroid.data.events.FeeDisplayResponseEvent;
import com.flutterwave.raveandroid.data.events.StartTypingEvent;
import com.flutterwave.raveandroid.di.modules.CardModule;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.verification.AVSVBVFragment;
import com.flutterwave.raveandroid.verification.OTPFragment;
import com.flutterwave.raveandroid.verification.PinFragment;
import com.flutterwave.raveandroid.verification.VerificationActivity;
import com.flutterwave.raveandroid.verification.web.WebFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import static com.flutterwave.raveandroid.RaveConstants.MANUAL_CARD_CHARGE;
import static com.flutterwave.raveandroid.RaveConstants.NOAUTH_INTERNATIONAL;
import static com.flutterwave.raveandroid.RaveConstants.PIN;
import static com.flutterwave.raveandroid.RaveConstants.TOKEN_CHARGE;
import static com.flutterwave.raveandroid.RaveConstants.fieldAmount;
import static com.flutterwave.raveandroid.RaveConstants.fieldCardExpiry;
import static com.flutterwave.raveandroid.RaveConstants.fieldCvv;
import static com.flutterwave.raveandroid.RaveConstants.fieldEmail;
import static com.flutterwave.raveandroid.RaveConstants.fieldcardNoStripped;
import static com.flutterwave.raveandroid.verification.VerificationActivity.EXTRA_IS_STAGING;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements View.OnClickListener, CardContract.View, View.OnFocusChangeListener {

    @Inject
    CardPresenter presenter;

    public static final int FOR_PIN = 444;
    public static final int FOR_OTP = 666;
    public static final int FOR_AVBVV = 333;
    public static final int FOR_INTERNET_BANKING = 555;
    public static final String INTENT_SENDER = "cardFrag";
    private static final String RAVEPAY = "ravepay";
    private View v;
    private Button payButton;
    private TextView pcidss_tv;
    private AlertDialog dialog;
    private Button savedCardBtn;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        injectComponents();

        v = inflater.inflate(R.layout.fragment_card, container, false);

        initializeViews();

        pcidss_tv.setMovementMethod(LinkMovementMethod.getInstance());

        setListeners();

        initializePresenter();

        return v;
    }

    private void injectComponents() {

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).getAppComponent()
                    .plus(new CardModule(this))
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
        savedCardBtn.setOnClickListener(this);
        payButton.setOnClickListener(this);

        cardExpiryTv.setOnFocusChangeListener(this);
        cardNoTv.setOnFocusChangeListener(this);
        amountEt.setOnFocusChangeListener(this);
        emailEt.setOnFocusChangeListener(this);
        cvvTv.setOnFocusChangeListener(this);
    }

    private void initializeViews() {
        progressContainer = v.findViewById(R.id.rave_progressContainer);
        pcidss_tv = v.findViewById(R.id.rave_pcidss_compliant_tv);
        saveCardSwitch = v.findViewById(R.id.rave_saveCardSwitch);
        cardExpiryTil = v.findViewById(R.id.rave_cardExpiryTil);
        savedCardBtn = v.findViewById(R.id.rave_savedCardButton);
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

    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            collectData();
        }
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(fieldAmount, new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldEmail, new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldCvv, new ViewObject(cvvTil.getId(), cvvTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldCardExpiry, new ViewObject(cardExpiryTil.getId(), cardExpiryTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(fieldcardNoStripped, new ViewObject(cardNoTil.getId(), cardNoTv.getText().toString(), TextInputLayout.class));

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
    public void onNoAuthUsed(String flwRef, String publicKey) {
        presenter.requeryTx(flwRef, publicKey, shouldISaveThisCard);
    }

    @Override
    public void onValidationSuccessful(HashMap<String, ViewObject> dataHashMap) {
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
    }


    @Override
    public void onAmountValidated(String amountToSet, int visibility) {
        amountTil.setVisibility(visibility);
        amountEt.setText(amountToSet);
    }

    @Override
    public void onNoAuthInternationalSuggested(final Payload payload) {
        this.payLoad = payload;

        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_AVBVV);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (presenter == null) {
            presenter = new CardPresenter(getActivity(), this);
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

    @Override
    public void onValidateCardChargeFailed(String flwRef, String responseAsJSON) {

        dismissDialog();

        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey(), false);

    }


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
                if (active && !progessDialog.isShowing()) {
                    progessDialog.show();
                } else {
                    progessDialog.dismiss();
                }
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
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
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
                        NOAUTH_INTERNATIONAL, ravePayInitializer.getEncryptionKey());
            } else if (requestCode == FOR_INTERNET_BANKING) {
                presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey(), shouldISaveThisCard);
            } else if (requestCode == FOR_OTP) {
                String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                presenter.validateCardCharge(flwRef, otp, ravePayInitializer.getPublicKey());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
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
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(OTPFragment.EXTRA_CHARGE_MESSAGE, chargeResponseMessage);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "otp");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_OTP);
    }

    /**
     * Called when a payment that requires validation has been completed
     *
     * @param status               = status of the payment (success)
     * @param responseAsJSONString = full json response from the payment
     */
    @Override
    public void onValidateSuccessful(String status, String responseAsJSONString) {
        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey(), shouldISaveThisCard);

    }

    /**
     * Called when a validation error is received. Shows a toast
     *
     * @param message
     */
    @Override
    public void onValidateError(String message) {
        presenter.logEvent(new ErrorEvent(message).getEvent(), ravePayInitializer.getPublicKey());
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
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authUrlCrude);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_INTERNET_BANKING);

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef) {
        presenter.verifyRequeryResponse(response, responseAsJSONString, ravePayInitializer, flwRef);
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

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
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
            ((RavePayActivity) getActivity()).setRavePayResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }


    /**
     * Displays a list of user saved cards and displays them in a bottom sheet
     * It also attaches a listener to the list of displayed cards to detect clicks
     * and sends the card details to the presenter for further processing of payment
     *
     * @param cards = List of saved cards
     */
    @Override
    public void showSavedCards(List<SavedCard> cards) {

    }

    /**
     * Called when a payment token is received. It creates the payment details object and
     * performs a token charge
     *
     * @param flwRef  = reference of the payment transaction
     * @param cardBIN = First 6 numbers of the card
     * @param token   = Auth token for cards
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
                .setIP(Utils.getDeviceId(getActivity()))
                .setTxRef(ravePayInitializer.getTxRef())
                .setDevice_fingerprint(Utils.getDeviceId(getActivity()))
                .setMeta(ravePayInitializer.getMeta());

        Payload body = builder.createPayload();
        body.setToken(token);
//        body.setSECKEY(RavePayActivity.getSecretKey());
        body.setCardBIN(cardBIN);
        body.setPBFPubKey(ravePayInitializer.getPublicKey());
        presenter.fetchFee(body, TOKEN_CHARGE);
    }

    /**
     * Called when an error occurs while token was being received. Closes any open bottom sheets
     * then shows a toast
     *
     * @param s = error message
     */
    @Override
    public void onTokenRetrievalError(String s) {
        presenter.logEvent(new ErrorEvent(s).getEvent(), ravePayInitializer.getPublicKey());
        showToast(s);
    }

    /**
     * @param charge_amount = Total amount to be charged (transaction fees incuded)
     * @param payload       = Object that contains the payment info (Contains card payment details)
     * @param why
     */
    @Override
    public void displayFee(String charge_amount, final Payload payload, final int why) {
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(getResources().getString(R.string.charge) + " " + charge_amount + " " + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
            builder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    presenter.logEvent(new FeeDisplayResponseEvent(true).getEvent(), ravePayInitializer.getPublicKey());

                    if (why == MANUAL_CARD_CHARGE) {
                        presenter.chargeCard(payload, ravePayInitializer.getEncryptionKey());
                    } else if (why == TOKEN_CHARGE) {
                        presenter.chargeToken(payload);
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

    /**
     * Displays the error message from a failed fetch fee request
     *
     * @param s = error message
     */
    @Override
    public void showFetchFeeFailed(String s) {
        presenter.logEvent(new ErrorEvent(s).getEvent(), ravePayInitializer.getPublicKey());
        showToast(s);
    }

    @Override
    public void onChargeTokenComplete(ChargeResponse response) {

        presenter.requeryTx(response.getData().getFlwRef(), ravePayInitializer.getPublicKey(), false);
    }

    @Override
    public void onChargeCardSuccessful(ChargeResponse response) {
        presenter.requeryTx(response.getData().getFlwRef(),
                ravePayInitializer.getPublicKey(),
                shouldISaveThisCard);
    }

    @Override
    public void onAVS_VBVSECURECODEModelSuggested(final Payload payload) {
        this.payLoad = payload;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
        intent.putExtra("theme", ravePayInitializer.getTheme());
        startActivityForResult(intent, FOR_AVBVV);
    }

    /**
     * Called when the auth model suggested is AVS_VBVSecureCode. It opens a webview
     * that loads the authURL
     *
     * @param authurl = URL to display in webview
     * @param flwRef  = reference of the payment transaction
     */
    @Override
    public void onAVSVBVSecureCodeModelUsed(String authurl, String flwRef) {
        this.flwRef = flwRef;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(EXTRA_IS_STAGING, ravePayInitializer.isStaging());
        intent.putExtra(VerificationActivity.PUBLIC_KEY_EXTRA, ravePayInitializer.getPublicKey());
        intent.putExtra(WebFragment.EXTRA_AUTH_URL, authurl);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "web");
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
