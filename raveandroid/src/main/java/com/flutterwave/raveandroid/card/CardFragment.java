package com.flutterwave.raveandroid.card;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.flutterwave.raveandroid.AVSVBVFragment;
import com.flutterwave.raveandroid.OTPFragment;
import com.flutterwave.raveandroid.Payload;
import com.flutterwave.raveandroid.PayloadBuilder;
import com.flutterwave.raveandroid.PinFragment;
import com.flutterwave.raveandroid.R;
import com.flutterwave.raveandroid.RaveConstants;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.RavePayInitializer;
import com.flutterwave.raveandroid.Utils;
import com.flutterwave.raveandroid.VerificationActivity;
import com.flutterwave.raveandroid.ViewObject;
import com.flutterwave.raveandroid.WebFragment;
import com.flutterwave.raveandroid.data.SavedCard;
import com.flutterwave.raveandroid.responses.ChargeResponse;
import com.flutterwave.raveandroid.responses.RequeryResponse;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

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

        // Inflate the layout for this v
        v = inflater.inflate(R.layout.fragment_card, container, false);

        initializeViews();

        ravePayInitializer = ((RavePayActivity) getActivity()).getRavePayInitializer();

        pcidss_tv.setMovementMethod(LinkMovementMethod.getInstance());

        presenter.checkForSavedCards(ravePayInitializer.getEmail());

        cardExpiryTv.addTextChangedListener(new ExpiryWatcher());

        savedCardBtn.setOnClickListener(this);

        payButton.setOnClickListener(this);

        presenter.init(ravePayInitializer);

        return v;
    }

    private void initializeViews() {
        progressContainer =  v.findViewById(R.id.rave_progressContainer);
        pcidss_tv =  v.findViewById(R.id.rave_pcidss_compliant_tv);
        saveCardSwitch =  v.findViewById(R.id.rave_saveCardSwitch);
        cardExpiryTil =  v.findViewById(R.id.rave_cardExpiryTil);
        savedCardBtn =  v.findViewById(R.id.rave_savedCardButton);
        cardExpiryTv =  v.findViewById(R.id.rave_cardExpiryTv);
        payButton =  v.findViewById(R.id.rave_payButton);
        cardNoTil =  v.findViewById(R.id.rave_cardNoTil);
        amountTil =  v.findViewById(R.id.rave_amountTil);
        emailTil =  v.findViewById(R.id.rave_emailTil);
        cardNoTv =  v.findViewById(R.id.rave_cardNoTv);
        amountEt =  v.findViewById(R.id.rave_amountTV);
        emailEt =  v.findViewById(R.id.rave_emailTv);
        cvvTil =  v.findViewById(R.id.rave_cvvTil);
        cvvTv =  v.findViewById(R.id.rave_cvvTv);

    }


    @Override
    public void onClick(View v) {

        int i = v.getId();
        if (i == R.id.rave_payButton) {
            clearErrors();
            collectData();
        }
        else if (i == R.id.rave_savedCardButton){
            presenter.onSavedCardsClicked(ravePayInitializer.getEmail());
        }
    }

    private void collectData() {

        HashMap<String, ViewObject> dataHashMap = new HashMap<>();

        dataHashMap.put(getResources().getString(R.string.fieldAmount), new ViewObject(amountTil.getId(), amountEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldEmail), new ViewObject(emailTil.getId(), emailEt.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldCvv), new ViewObject(cvvTil.getId(), cvvTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldCardExpiry), new ViewObject(cardExpiryTil.getId(), cardExpiryTv.getText().toString(), TextInputLayout.class));
        dataHashMap.put(getResources().getString(R.string.fieldcardNoStripped), new ViewObject(cardNoTil.getId(), cardNoTv.getText().toString().replaceAll("\\s", ""), TextInputLayout.class));

        presenter.validate(dataHashMap);
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

            String cardNoStripped = dataHashMap.get(getResources().getString(R.string.cardNoStripped)).getData();
            CheckSaveCard(cardNoStripped);

            presenter.processTransaction(dataHashMap, ravePayInitializer);

    }

    private void CheckSaveCard(String cardNoStripped) {
        if (saveCardSwitch.isChecked()) {
            int cardLen = cardNoStripped.length();
            cardFirst6 = cardNoStripped.substring(0, 6);
            cardLast4 = cardNoStripped.substring(cardLen - 4, cardLen);
            shouldISaveThisCard = true;
            presenter.savePotentialCardDets(cardFirst6, cardLast4);
        }
    }

    @Override
    public void showFieldError(int viewID, String message, Class<?> viewType) {

        if (viewType == TextInputLayout.class){
            TextInputLayout view  =  v.findViewById(viewID);
            view.setError(message);
        }
        else if (viewType == EditText.class){
            EditText view  =  v.findViewById(viewID);
            view.setError(message);
        }

    }

    @Override
    public void onEmailValidationSuccessful() {
        emailTil.setVisibility(GONE);
        emailEt.setText(ravePayInitializer.getEmail());
    }

    @Override
    public void onAmountValidationSuccessful(String amountToPay) {
        amountTil.setVisibility(GONE);
        amountEt.setText(amountToPay);
    }

    @Override
    public void onNoAuthInternationalSuggested(final Payload payload) {
        this.payLoad = payload;

        Intent intent = new Intent(getContext(), VerificationActivity.class);
        intent.putExtra(VerificationActivity.ACTIVITY_MOTIVE, "avsvbv");
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

        presenter.requeryTx(flwRef, ravePayInitializer.getPublicKey(), false);

    }

    /**
     * Validate card details and get the fee if available
     */

    /**
     * Remove all errors from the input fields
     */


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
        Intent intent = new Intent(getContext(), VerificationActivity.class);
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
                        RaveConstants.NOAUTH_INTERNATIONAL, ravePayInitializer.getEncryptionKey());
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

//        if (shouldISaveThisCard && flwRef != null) {
//            presenter.saveThisCard(ravePayInitializer.getEmail(), flwRef, ravePayInitializer.getSecretKey());
//        }

        Intent intent = new Intent();
        intent.putExtra("response", responseAsJSONString);

        if (getActivity() != null) {
            getActivity().setResult(RavePayActivity.RESULT_SUCCESS, intent);
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
            getActivity().setResult(RavePayActivity.RESULT_ERROR, intent);
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


//        if (cards.size() > 0) {
//            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
//            LayoutInflater inflater = LayoutInflater.from(getActivity());
//            View v = inflater.inflate(R.layout.pick_saved_card_layout, null, false);
//            RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.rave_recycler);
//
//            SavedCardRecyclerAdapter adapter = new SavedCardRecyclerAdapter();
//            adapter.set(cards);
//
//            adapter.setSavedCardSelectedListener(new Callbacks.SavedCardSelectedListener() {
//                @Override
//                public void onCardSelected(SavedCard savedCard) {
//                    bottomSheetDialog.dismiss();
//                    String ref = Utils.decryptRef(RavePayActivity.getSecretKey(), savedCard.getFlwRef());
//
//                    presenter.requeryTxForToken(ref, RavePayActivity.getSecretKey());
//
//                }
//            });
//
//            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//            recyclerView.setAdapter(adapter);
//            bottomSheetDialog.setContentView(v);
//            bottomSheetDialog.show();
//        }
//        else {
//            showToast("You have no saved cards");
//        }

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
                .setIP(Utils.getDeviceImei(getActivity()))
                .setTxRef(ravePayInitializer.getTxRef())
                .setDevice_fingerprint(Utils.getDeviceImei(getActivity()))
                .setMeta(ravePayInitializer.getMeta());

        Payload body = builder.createPayload();
        body.setToken(token);
//        body.setSECKEY(RavePayActivity.getSecretKey());
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
        builder.setMessage(getResources().getString(R.string.charge) + charge_amount + ravePayInitializer.getCurrency() + getResources().getString(R.string.askToContinue));
        builder.setPositiveButton(getResources().getString(R.string.yes) , new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

                if (why == RaveConstants.MANUAL_CARD_CHARGE) {
                    presenter.chargeCard(payload, ravePayInitializer.getEncryptionKey());
                } else if (why == RaveConstants.TOKEN_CHARGE) {
                    presenter.chargeToken(payload);
                }

            }
        }).setNegativeButton(getResources().getString(R.string.no) , new DialogInterface.OnClickListener() {
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
    public void hideSavedCardsButton() {
        savedCardBtn.setVisibility(GONE);
    }

    @Override
    public void onChargeTokenComplete(ChargeResponse response) {

        presenter
                .requeryTx(response.getData().getFlwRef(), ravePayInitializer.getPublicKey(), false);
    }

    @Override
    public void onChargeCardSuccessful(ChargeResponse response) {
        presenter
                .requeryTx(response.getData().getFlwRef(),
                        ravePayInitializer.getPublicKey(),
                        shouldISaveThisCard);
    }

    @Override
    public void onAVS_VBVSECURECODEModelSuggested(final Payload payload) {
        this.payLoad = payload;
        Intent intent = new Intent(getContext(), VerificationActivity.class);
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

}
