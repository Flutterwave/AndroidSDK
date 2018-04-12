package com.flutterwave.raveandroid.card;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.data.Callbacks;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.View.GONE;
import static com.flutterwave.raveandroid.RaveConstants.AVS_VBVSECURECODE;
import static com.flutterwave.raveandroid.RaveConstants.PIN;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardFragment extends Fragment implements View.OnClickListener, CardContract.View {

    private static final String RAVEPAY = "ravepay";
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
    TextInputLayout otpTil;
    TextInputEditText otpEt;
    Button otpButton;
    SwitchCompat saveCardSwitch;
    Button payButton;
    private ProgressDialog progessDialog ;
    CardPresenter presenter;
    LinearLayout otpLayout;
    BottomSheetBehavior bottomSheetBehaviorOTP;
    BottomSheetBehavior bottomSheetBehaviorVBV;
    private String flwRef;
    private FrameLayout vbvLayout;
    RavePayInitializer ravePayInitializer;
    WebView webView;
    String initialUrl = null;
    private TextView pcidss_tv;
    private AlertDialog dialog;
    FrameLayout progressContainer;
    View v;
    Button savedCardBtn;
    String cardFirst6;
    TextView otpInstructionsTv;
    String cardLast4;
    boolean shouldISaveThisCard = false;

    public CardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        presenter = new CardPresenter(getActivity(), this);
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_card, container, false);
        otpTil = (TextInputLayout) v.findViewById(R.id.rave_otpTil);
        otpEt = (TextInputEditText) v.findViewById(R.id.rave_otpEv);
        otpButton = (Button) v.findViewById(R.id.rave_otpButton);
        savedCardBtn = (Button) v.findViewById(R.id.rave_savedCardButton);
        amountEt = (TextInputEditText) v.findViewById(R.id.rave_amountTV);
        emailEt = (TextInputEditText) v.findViewById(R.id.rave_emailTv);
        cardNoTv = (TextInputEditText) v.findViewById(R.id.rave_cardNoTv);
        cardExpiryTv = (TextInputEditText) v.findViewById(R.id.rave_cardExpiryTv);
        cvvTv = (TextInputEditText) v.findViewById(R.id.rave_cvvTv);
        payButton = (Button) v.findViewById(R.id.rave_payButton);
        saveCardSwitch = (SwitchCompat) v.findViewById(R.id.rave_saveCardSwitch);
        amountTil = (TextInputLayout) v.findViewById(R.id.rave_amountTil);
        emailTil = (TextInputLayout) v.findViewById(R.id.rave_emailTil);
        cardNoTil = (TextInputLayout) v.findViewById(R.id.rave_cardNoTil);
        cardExpiryTil = (TextInputLayout) v.findViewById(R.id.rave_cardExpiryTil);
        cvvTil = (TextInputLayout) v.findViewById(R.id.rave_cvvTil);
        webView = (WebView) v.findViewById(R.id.rave_webview);
        pcidss_tv = (TextView) v.findViewById(R.id.rave_pcidss_compliant_tv);
        progressContainer = (FrameLayout) v.findViewById(R.id.rave_progressContainer);
        otpInstructionsTv = (TextView) v.findViewById(R.id.otp_instructions_tv);

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        TransformFilter filter = new TransformFilter() {
            public final String transformUrl(final Matcher match, String url) {
                return "";
            }
        };

        Pattern pattern = Pattern.compile("()PCI-DSS COMPLIANT");
        Linkify.addLinks(pcidss_tv, pattern, "https://www.pcisecuritystandards.org/pci_security/", null, filter);

        otpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = otpEt.getText().toString();

                otpTil.setError(null);
                otpTil.setErrorEnabled(false);

                if (otp.length() < 1) {
                    otpTil.setError("Enter a valid one time password");
                }
                else {
                    presenter.validateCardCharge(flwRef, otp, ravePayInitializer.getPublicKey());
                }
            }
        });

        presenter.checkForSavedCards(ravePayInitializer.getEmail());

        savedCardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSavedCardsClicked(ravePayInitializer.getEmail());
            }
        });

        cardExpiryTv.addTextChangedListener(new ExpiryWatcher());


        payButton.setOnClickListener(this);

        otpLayout = (LinearLayout) v.findViewById(R.id.rave_OTPButtomSheet);
        vbvLayout = (FrameLayout) v.findViewById(R.id.rave_VBVBottomSheet);
        bottomSheetBehaviorOTP = BottomSheetBehavior.from(otpLayout);
        bottomSheetBehaviorVBV = BottomSheetBehavior.from(vbvLayout);


        if (Utils.isEmailValid(ravePayInitializer.getEmail())) {
            emailTil.setVisibility(GONE);
            emailEt.setText(ravePayInitializer.getEmail());
        }

        double amountToPay = ravePayInitializer.getAmount();

        if (amountToPay > 0) {
            amountTil.setVisibility(GONE);
            amountEt.setText(String.valueOf(amountToPay));
        }

        if (!ravePayInitializer.isAllowSaveCard()) {
            saveCardSwitch.setVisibility(GONE);
        }

        return v;
    }

    /**
     * Closes all open bottom sheets and returns true is bottom sheet is showing, else return false
     * @return
     */
    public boolean closeBottomSheetsIfOpen() {

        boolean showing = false;
        if (bottomSheetBehaviorOTP.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            showing = true;
            bottomSheetBehaviorOTP.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        if (bottomSheetBehaviorVBV.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            showing = true;
            bottomSheetBehaviorVBV.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        return showing;
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.rave_payButton) {
            validateDetails();
        }

    }

    @Override
    public void onNoAuthInternationalSuggested(final Payload payload) {

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View v = inflater.inflate( R.layout.avsvbv_layout, null, false);

        final TextInputEditText addressEt = (TextInputEditText) v.findViewById(R.id.rave_billAddressEt);
        final TextInputEditText stateEt = (TextInputEditText) v.findViewById(R.id.rave_billStateEt);
        final TextInputEditText cityEt = (TextInputEditText) v.findViewById(R.id.rave_billCityEt);
        final TextInputEditText zipCodeEt = (TextInputEditText) v.findViewById(R.id.rave_zipEt);
        final TextInputEditText countryEt = (TextInputEditText) v.findViewById(R.id.rave_countryEt);
        final TextInputLayout addressTil = (TextInputLayout) v.findViewById(R.id.rave_billAddressTil);
        final TextInputLayout stateTil = (TextInputLayout) v.findViewById(R.id.rave_billStateTil);
        final TextInputLayout cityTil = (TextInputLayout) v.findViewById(R.id.rave_billCityTil);
        final TextInputLayout zipCodeTil = (TextInputLayout) v.findViewById(R.id.rave_zipTil);
        final TextInputLayout countryTil = (TextInputLayout) v.findViewById(R.id.rave_countryTil);

        Button zipBtn = (Button) v.findViewById(R.id.rave_zipButton);

        zipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                String address = addressEt.getText().toString();
                String state = stateEt.getText().toString();
                String city = cityEt.getText().toString();
                String zipCode = zipCodeEt.getText().toString();
                String country = countryEt.getText().toString();

                addressTil.setError(null);
                stateTil.setError(null);
                cityTil.setError(null);
                zipCodeTil.setError(null);
                countryTil.setError(null);

                if (address.length() == 0) {
                    valid = false;
                    addressTil.setError("Enter a valid address");
                }

                if (state.length() == 0) {
                    valid = false;
                    stateTil.setError("Enter a valid state");
                }

                if (city.length() == 0) {
                    valid = false;
                    cityTil.setError("Enter a valid city");
                }

                if (zipCode.length() == 0) {
                    valid = false;
                    zipCodeTil.setError("Enter a valid zip code");
                }

                if (country.length() == 0) {
                    valid = false;
                    countryTil.setError("Enter a valid country");
                }

                if (valid) {
                    bottomSheetDialog.dismiss();
                    presenter.chargeCardWithAVSModel(payload, address, city, zipCode, country, state,
                            RaveConstants.NOAUTH_INTERNATIONAL, ravePayInitializer.getSecretKey());
                }

            }
        });


        bottomSheetDialog.setContentView(v);
        bottomSheetDialog.show();

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
        bottomSheetBehaviorVBV.setState(BottomSheetBehavior.STATE_COLLAPSED);

        presenter.requeryTx(flwRef, ravePayInitializer.getSecretKey(), false);

    }

    /**
     *  Validate card details and get the fee if available
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



        try {
            double amnt = Double.parseDouble(amount);

            if (amnt <= 0) {
                valid = false;
                amountTil.setError("Enter a valid amount");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            valid = false;
            amountTil.setError("Enter a valid amount");
        }

        if (!Utils.isEmailValid(email)) {
            valid = false;
            emailTil.setError("Enter a valid email");
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

        if (cardNoStripped.length() < 12 ) {
            valid = false;
            cardNoTil.setError("Enter a valid credit card number");
        }
        else {
            try {
                Long parsed = Long.parseLong(cardNoStripped);
            }
            catch (Exception e) {
                e.printStackTrace();
                valid = false;
                cardNoTil.setError("Enter a valid credit card number");
            }
        }

        if (valid) {

            ravePayInitializer.setAmount(Double.parseDouble(amount));

            if (saveCardSwitch.isChecked()) {
                int cardLen = cardNoStripped.length();
                cardFirst6 = cardNoStripped.substring(0, 6);
                cardLast4 = cardNoStripped.substring(cardLen - 4, cardLen);
                shouldISaveThisCard = true;
                presenter.savePotentialCardDets(cardFirst6, cardLast4);
            }

            //make request
            String txRef = ravePayInitializer.getTxRef();
            Log.d("txRef", txRef);
            PayloadBuilder builder = new PayloadBuilder();
            builder.setAmount(ravePayInitializer.getAmount() + "").setCardno(cardNoStripped)
                .setCountry(ravePayInitializer.getCountry()).setCurrency(ravePayInitializer.getCurrency())
                .setCvv(cvv).setEmail(email).setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName()).setIP(Utils.getDeviceImei(getActivity())).setTxRef(ravePayInitializer.getTxRef())
                .setExpiryyear(expiryDate.substring(3,5)).setExpirymonth(expiryDate.substring(0,2))
                .setMeta(ravePayInitializer.getMeta())
                .setPBFPubKey(ravePayInitializer.getPublicKey()).setDevice_fingerprint(Utils.getDeviceImei(getActivity()));

            Payload body = builder.createPayload();

            presenter.fetchFee(body, RaveConstants.MANUAL_CARD_CHARGE);

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
     * @param active = status of progress indicator
     */
    @Override
    public void showProgressIndicator(boolean active) {

        if (getActivity().isFinishing()) { return; }
        if(progessDialog == null) {
            progessDialog = new ProgressDialog(getActivity());
            progessDialog.setMessage("Please wait...");
        }

        if (active && !progessDialog.isShowing()) {
            progessDialog.show();
        }
        else {
            progessDialog.dismiss();
        }
    }

    /**
     * Called when there's a non fatal error in payment. Shows a toast with the error message
     * @param message = response message to display
     */
    @Override
    public void onPaymentError(String message) {
        dismissDialog();
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    /**
     *  Called when a pin suggested auth model is required.
     *  It shows a dialog that receives the pin and sends the payment payload
     * @param payload = Contains card payment details
     */
    @Override
    public void onPinAuthModelSuggested(final Payload payload) {

//        bottomSheetDialog = new BottomSheetDialog(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.pin_layout, null, false);

        Button pinBtn = (Button) v.findViewById(R.id.rave_pinButton);
        final TextInputEditText pinEv = (TextInputEditText) v.findViewById(R.id.rave_pinEv);
        final TextInputLayout pinTil = (TextInputLayout) v.findViewById(R.id.rave_pinTil);

        pinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pin = pinEv.getText().toString();

                pinTil.setError(null);
                pinTil.setErrorEnabled(false);

                if (pin.length() != 4) {
                    pinTil.setError("Enter a valid pin");
                }
                else {
                    presenter.chargeCardWithSuggestedAuthModel(payload, pin, PIN, ravePayInitializer.getSecretKey());
                }
            }
        });

        builder.setView(v);
        dialog = builder.show();
    }

    /**
     * Displays a toast with the message parameter
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
     * @param flwRef
     * @param chargeResponseMessage
     */
    @Override
    public void showOTPLayout(String flwRef, String chargeResponseMessage) {
        this.flwRef = flwRef;
        dismissDialog();
        otpInstructionsTv.setText(chargeResponseMessage);
        bottomSheetBehaviorOTP.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    /**
     * Called when a payment that requires validation has been completed
     * @param status = status of the payment (success)
     * @param responseAsJSONString = full json response from the payment
     */
    @Override
    public void onValidateSuccessful(String status, String responseAsJSONString) {

        closeBottomSheetsIfOpen();

        presenter.requeryTx(flwRef, ravePayInitializer.getSecretKey(), shouldISaveThisCard);

    }



    /**
     * Called when a validation error is received. Shows a toast
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
     * @param flwRef = reference of the payment transaction
     */
    @Override
    public void onVBVAuthModelUsed(String authUrlCrude, String flwRef) {

        this.flwRef = flwRef;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        webView.loadUrl(authUrlCrude);
        bottomSheetBehaviorVBV.setState(BottomSheetBehavior.STATE_EXPANDED);

    }

    @Override
    public void onRequerySuccessful(RequeryResponse response, String responseAsJSONString, String flwRef) {
        presenter.verifyRequeryResponse(response, responseAsJSONString, ravePayInitializer, flwRef);
    }

    /**
     *
     * Called after a successful transaction occurs. It closes all open dialogs
     * and bottomsheets if any and send back the result of payment to the calling activity
     *
     * @param status = status of the transaction
     * @param flwRef = reference of the payment transaction
     * @param responseAsJSONString = full json response from the payment transaction
     */
    @Override
    public void onPaymentSuccessful(String status, String flwRef, String responseAsJSONString) {
        dismissDialog();
        closeBottomSheetsIfOpen();

        if (shouldISaveThisCard && flwRef != null) {
            presenter.saveThisCard(ravePayInitializer.getEmail(), flwRef, RavePayActivity.getSecretKey());
        }

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
            getActivity().finish();
        }
    }

    /**
     *  Called after a fatal failure in a transaction. It closes all open dialogs
     * and bottomsheets if any and send back the result of payment to the calling activity
     * @param status = status of the transaction
     * @param responseAsJSONString = full json response from the payment transaction
     */
    @Override
    public void onPaymentFailed(String status, String responseAsJSONString) {
        dismissDialog();
        bottomSheetBehaviorVBV.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);
        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
            getActivity().finish();
        }
    }

    /**
     *  Hides/shows a progress indicator that covers the entire view. It is only used with
     *  webview (in the bottomsheets)
     * @param active = status of progress indicator
     */
    @Override
    public void showFullProgressIndicator(boolean active) {

        if (progressContainer == null) {
            progressContainer = (FrameLayout) v.findViewById(R.id.rave_progressContainer);
        }

        if (active) {
            progressContainer.setVisibility(View.VISIBLE);
        }
        else {
            progressContainer.setVisibility(GONE);
        }


    }

    /**
     *
     *  Displays a list of user saved cards and displays them in a bottom sheet
     *  It also attaches a listener to the list of displayed cards to detect clicks
     *  and sends the card details to the presenter for further processing of payment
      * @param cards = List of saved cards
     */
    @Override
    public void showSavedCards(List<SavedCard> cards) {


        if (cards.size() > 0) {
            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View v = inflater.inflate(R.layout.pick_saved_card_layout, null, false);
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rave_recycler);

            SavedCardRecyclerAdapter adapter = new SavedCardRecyclerAdapter();
            adapter.set(cards);

            adapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
                @Override
                public void onCardSelected(SavedCard savedCard) {
                    bottomSheetDialog.dismiss();
                    String ref = Utils.decryptRef(RavePayActivity.getSecretKey(), savedCard.getFlwRef());

                    presenter.requeryTxForToken(ref, RavePayActivity.getSecretKey());

                }
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            bottomSheetDialog.setContentView(v);
            bottomSheetDialog.show();
        }
        else {
            showToast("You have no saved cards");
        }

    }

    /**
     * Called when a payment token is received. It creates the payment details object and
     * performs a token charge
     *
     * @param flwRef = reference of the payment transaction
     * @param cardBIN = First 6 numbers of the card
     * @param token = Auth token for cards
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
        body.setSECKEY(RavePayActivity.getSecretKey());
        body.setCardBIN(cardBIN);
        body.setPBFPubKey(ravePayInitializer.getPublicKey());
        presenter.fetchFee(body, RaveConstants.TOKEN_CHARGE);
    }

    /**
     * Called when an error occurs while token was being received. Closes any open bottom sheets
     * then shows a toast
     * @param s = error message
     */
    @Override
    public void onTokenRetrievalError(String s) {
        closeBottomSheetsIfOpen();
        showToast(s);
    }

    /**
     *
     * @param charge_amount = Total amount to be charged (transaction fees incuded)
     * @param payload = Object that contains the payment info (Contains card payment details)
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
                    presenter.chargeCard(payload, ravePayInitializer.getSecretKey());
                }
                else if (why == RaveConstants.TOKEN_CHARGE) {
                    presenter.chargeToken(payload);
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
     * @param s = error message
     */
    @Override
    public void showFetchFeeFailed(String s) {
        showToast(s);
    }

    @Override
    public void hideSavedCardsButton() {
        savedCardBtn.setVisibility(GONE);
    }

    @Override
    public void onChargeTokenComplete(ChargeResponse response) {

        presenter
                .requeryTx(response.getData().getFlwRef(), ravePayInitializer.getSecretKey(), false);
    }

    @Override
    public void onChargeCardSuccessful(ChargeResponse response) {
        presenter
                .requeryTx(response.getData().getFlwRef(),
                        ravePayInitializer.getSecretKey(),
                        shouldISaveThisCard);
    }

    @Override
    public void onAVS_VBVSECURECODEModelSuggested(final Payload payload) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View v = inflater.inflate(R.layout.avsvbv_layout, null, false);


        final TextInputEditText addressEt = (TextInputEditText) v.findViewById(R.id.rave_billAddressEt);
        final TextInputEditText stateEt = (TextInputEditText) v.findViewById(R.id.rave_billStateEt);
        final TextInputEditText cityEt = (TextInputEditText) v.findViewById(R.id.rave_billCityEt);
        final TextInputEditText zipCodeEt = (TextInputEditText) v.findViewById(R.id.rave_zipEt);
        final TextInputEditText countryEt = (TextInputEditText) v.findViewById(R.id.rave_countryEt);
        final TextInputEditText addressTil = (TextInputEditText) v.findViewById(R.id.rave_billAddressTil);
        final TextInputEditText stateTil = (TextInputEditText) v.findViewById(R.id.rave_billStateTil);
        final TextInputEditText cityTil = (TextInputEditText) v.findViewById(R.id.rave_billCityTil);
        final TextInputEditText zipCodeTil = (TextInputEditText) v.findViewById(R.id.rave_zipTil);
        final TextInputEditText countryTil = (TextInputEditText) v.findViewById(R.id.rave_countryTil);

        Button zipBtn = (Button) v.findViewById(R.id.rave_zipButton);

        zipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;

                String address = addressEt.getText().toString();
                String state = stateEt.getText().toString();
                String city = cityEt.getText().toString();
                String zipCode = zipCodeEt.getText().toString();
                String country = countryEt.getText().toString();

                addressTil.setError(null);
                stateTil.setError(null);
                cityTil.setError(null);
                zipCodeTil.setError(null);
                countryTil.setError(null);

                if (address.length() == 0) {
                    valid = false;
                    addressTil.setError("Enter a valid address");
                }

                if (state.length() == 0) {
                    valid = false;
                    stateTil.setError("Enter a valid state");
                }

                if (city.length() == 0) {
                    valid = false;
                    cityTil.setError("Enter a valid city");
                }

                if (zipCode.length() == 0) {
                    valid = false;
                    zipCodeTil.setError("Enter a valid zip code");
                }

                if (country.length() == 0) {
                    valid = false;
                    countryTil.setError("Enter a valid country");
                }

                if (valid) {
                    dialog.dismiss();
                    presenter.chargeCardWithAVSModel(payload, address, city, zipCode, country, state,
                            AVS_VBVSECURECODE, ravePayInitializer.getSecretKey());
                }

            }
        });

        builder.setView(v);
        dialog = builder.show();

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

        this.flwRef = flwRef;
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new MyBrowser());
        // Load the initial URL
        webView.loadUrl(authurl);
        bottomSheetBehaviorVBV.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    // Manages the behavior when URLs are loaded
    private class MyBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.loadUrl(request.getUrl().toString());
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showFullProgressIndicator(true);

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            showFullProgressIndicator(false);

            if (initialUrl == null) {
                initialUrl = url;
            }
            else {
                if (url.contains("/complete") || url.contains("submitting_mock_form")) {
                    presenter.requeryTx(flwRef, ravePayInitializer.getSecretKey(), shouldISaveThisCard); // requery transaction when a url with /complete or /submit...
                    //is hit
                }
            }
            Log.d("URLS", url);

        }
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
                        cardExpiryTv.setText(cardExpiryTv.getText().toString().substring(0,1));
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    } else {
                        cardExpiryTv.setText("12");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                } else if (editable.length() == 1) {
                    int month = Integer.parseInt(input);
                    if (month > 1) {
                        cardExpiryTv.setText("0" + cardExpiryTv.getText().toString()  + "/");
                        cardExpiryTv.setSelection(cardExpiryTv.getText().toString().length());
                    }
                }

                lastInput = cardExpiryTv.getText().toString();
            }
        }
    }

}
