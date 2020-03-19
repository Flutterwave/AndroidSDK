package com.flutterwave.raveandroid.rave_presentation.banktransferdemo;

import android.content.Context;

import com.flutterwave.raveandroid.rave_core.ChargeRequestBody;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;

import javax.inject.Inject;

public class BankTransferDemoPresenter implements BankTransferDemoContract.UserActionsListener {

    private final RemoteRepository repository;
    private BankTransferDemoContract.View mView;

    @Inject
    public BankTransferDemoPresenter(BankTransferDemoContract.View mView,
                                     RemoteRepository repository) {
        this.mView = mView;
        this.repository = repository;
    }

    @Override
    public void onComplete(String accountNo, String accountCode) {

        mView.showProgressIndicator(true);

        repository.charge(new ChargeRequestBody(), new ResultCallback() {
            @Override
            public void onResult(boolean status, String response) {
                mView.showProgressIndicator(false);

                if (status) {
                    mView.showMessage("Yaay! " + response);
                }
                else {
                    mView.showMessage("nah! " + response);
                }
            }
        });
    }
}
