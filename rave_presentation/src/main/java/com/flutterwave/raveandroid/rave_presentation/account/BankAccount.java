package com.flutterwave.raveandroid.rave_presentation.account;

import com.flutterwave.raveandroid.rave_core.models.Bank;

public class BankAccount {
    Bank bank;
    private String bvn;
    private String accountNumber;
    private String dateOfBirth;

    public BankAccount(Bank bank, String accountNumber) {
        this.bank = bank;
        this.accountNumber = accountNumber;
    }

    public String getBvn() {
        return bvn;
    }

    public void setBvn(String bvn) {
        this.bvn = bvn;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
