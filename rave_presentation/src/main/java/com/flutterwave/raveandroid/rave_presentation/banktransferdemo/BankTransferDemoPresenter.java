package com.flutterwave.raveandroid.rave_presentation.banktransferdemo;

import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.ResultCallback;
import com.flutterwave.raveandroid.rave_remote.requests.ChargeRequestBody;

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

        repository.charge(new ChargeRequestBody(), new ResultCallback<String>() {
            @Override
            public void onSuccess(String response) {
                mView.showProgressIndicator(false);
                mView.showMessage("Yaay! " + response);
            }

            @Override
            public void onError(String message) {
                mView.showProgressIndicator(false);
                mView.showMessage("nah! " + message);

            }
        });
    }
}
