package com.flutterwave.raveandroid.rave_presentation.banktransferdemo;

public interface BankTransferDemoContract {

    interface UserActionsListener {
        void onComplete(String accountNo, String accountCode);
    }

    interface View {
        void showProgressIndicator(boolean active);
        void showMessage(String message);
    }
}
