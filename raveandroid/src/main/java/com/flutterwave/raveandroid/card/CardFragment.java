package com.flutterwave.raveandroid.card;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import com.codetroopers.betterpickers.expirationpicker.ExpirationPickerBuilder;
import com.codetroopers.betterpickers.expirationpicker.ExpirationPickerDialogFragment;
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
        otpTil = (TextInputLayout) v.findViewById(R.id.otpTil);
        otpEt = (TextInputEditText) v.findViewById(R.id.otpEv);
        otpButton = (Button) v.findViewById(R.id.otpButton);
        savedCardBtn = (Button) v.findViewById(R.id.savedCardButton);
        amountEt = (TextInputEditText) v.findViewById(R.id.amountTV);
        emailEt = (TextInputEditText) v.findViewById(R.id.emailTv);
        cardNoTv = (TextInputEditText) v.findViewById(R.id.cardNoTv);
        cardExpiryTv = (TextInputEditText) v.findViewById(R.id.cardExpiryTv);
        cvvTv = (TextInputEditText) v.findViewById(R.id.cvvTv);
        payButton = (Button) v.findViewById(R.id.payButton);
        saveCardSwitch = (SwitchCompat) v.findViewById(R.id.saveCardSwitch);
        amountTil = (TextInputLayout) v.findViewById(R.id.amountTil);
        emailTil = (TextInputLayout) v.findViewById(R.id.emailTil);
        cardNoTil = (TextInputLayout) v.findViewById(R.id.cardNoTil);
        cardExpiryTil = (TextInputLayout) v.findViewById(R.id.cardExpiryTil);
        cvvTil = (TextInputLayout) v.findViewById(R.id.cvvTil);
        webView = (WebView) v.findViewById(R.id.webview);
        pcidss_tv = (TextView) v.findViewById(R.id.pcidss_compliant_tv);
        progressContainer = (FrameLayout) v.findViewById(R.id.progressContainer);

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

        cardExpiryTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpirationPickerBuilder epb = new ExpirationPickerBuilder()
                        .setFragmentManager(getActivity().getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickersDialogFragment)
                        .setMinYear(2000);
                epb.show();

                epb.addExpirationPickerDialogHandler(new ExpirationPickerDialogFragment.ExpirationPickerDialogHandler() {
                    @Override
                    public void onDialogExpirationSet(int reference, int year, int monthOfYear) {
                        String month = String.valueOf(monthOfYear);
                        if (month.length() != 2) {
                            month = "0" + month;
                        }

                        String year_str = String.valueOf(year).substring(2, 4);
                        cardExpiryTv.setText(month + "/" + year_str);
                    }
                });
            }
        });


        payButton.setOnClickListener(this);

        otpLayout = (LinearLayout) v.findViewById(R.id.OTPButtomSheet);
        vbvLayout = (FrameLayout) v.findViewById(R.id.VBVBottomSheet);
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
        if (i == R.id.payButton) {
            validateDetails();
        }

    }

    private void checkRunTimePermission() {
        PackageManager pm = getActivity().getPackageManager();
        if (pm.checkPermission(Manifest.permission.READ_PHONE_STATE, getActivity().getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            // do something
        } else {
            Toast.makeText(getContext(), "Added read READ_PHONE permissions ", Toast.LENGTH_SHORT).show();
            // do something
        }
    }

    /**
     *  Validate card details and get the fee if available
     */
    private void validateDetails() {
        checkRunTimePermission();

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
            builder.setAmount(amount).setCardno(cardNoStripped)
                .setCountry(ravePayInitializer.getCountry()).setCurrency(ravePayInitializer.getCurrency())
                .setCvv(cvv).setEmail(email).setFirstname(ravePayInitializer.getfName())
                .setLastname(ravePayInitializer.getlName()).setIP(Utils.getDeviceImei(getActivity())).setTxRef(ravePayInitializer.getTxRef())
                .setExpiryyear(expiryDate.substring(3,5)).setExpirymonth(expiryDate.substring(0,2))
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

        Button pinBtn = (Button) v.findViewById(R.id.pinButton);
        final TextInputEditText pinEv = (TextInputEditText) v.findViewById(R.id.pinEv);
        final TextInputLayout pinTil = (TextInputLayout) v.findViewById(R.id.pinTil);

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
                    presenter.chargeCardWithSuggestedAuthModel(payload, pin, PIN);
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
     */
    @Override
    public void showOTPLayout(String flwRef) {
        this.flwRef = flwRef;
        dismissDialog();
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
            progressContainer = (FrameLayout) v.findViewById(R.id.progressContainer);
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
            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler);

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
                .setDevice_fingerprint(Utils.getDeviceImei(getActivity()));

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
                    presenter.chargeCard(payload);
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

        Button zipBtn = (Button) v.findViewById(R.id.zipButton);
        final TextInputEditText zipEt = (TextInputEditText) v.findViewById(R.id.zipEt);
        final TextInputLayout zipTil = (TextInputLayout) v.findViewById(R.id.zipTil);

        zipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zip = zipEt.getText().toString();

                zipTil.setError(null);
                zipTil.setErrorEnabled(false);

                if (zip.length() == 0) {
                    zipTil.setError("Enter a valid pin");
                }
                else {
                    dialog.dismiss();
                    presenter.chargeCardWithSuggestedAuthModel(payload, zip, AVS_VBVSECURECODE);
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

}
