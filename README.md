<p align="center">
    <img title="Flutterwave" height="200" src="https://flutterwave.com/images/logo/full.svg" width="50%"/>
</p>

# Flutterwave Android SDK

Flutterwave's Android SDK allows you to integrate the Flutterwave payment gateway into your Android application. It comes with a ready-made Drop In UI as well as a non-UI module, depending on your preference.

Currently, the payment methods supported are: 

Cards, 

USSD, 

Mpesa, 

GH Mobile Money, 

UG Mobile Money, 

ZM Mobile Money, 

Rwanda Mobile Money, 

Franc Mobile Money, 

US ACH, 

UK Bank, 

SA Bank,

Nigeria Bank Account, 

Nigeria Bank Transfer, 

Barter Mobile Wallet.

<img alt="Screenshot of Drop-In" src="https://i.imgur.com/UZZkC6e.png" width="900"/>

## Before you begin
- Ensure you have your test and live API keys. You can find more information about retrieving your keys [here](https://flutterwave.com/gh/support/my-account/getting-your-api-keys).
- By using this SDK, you confirm that you have signed up on Flutterwave and accepted our [terms and conditions](https://flutterwave.com/us/terms) as well as our [privacy policy](https://flutterwave.com/us/privacy-policy).

## Requirements
- The minimum supported SDK version is 15.
- Kindly note that Rave Android SDK version 1.0.50 and above only supports projects that have been migrated to [androidx](https://developer.android.com/jetpack/androidx/). For more information, please refer to Google's [migration guide](https://developer.android.com/jetpack/androidx/migrate).

## Adding it to your project


**Step 1.**  Add the JitPack Repository:
Include the JitPack repository in your root `build.gradle` file by adding the following lines at the end of the `repositories` section:

```groovy
    allprojects {
		repositories {
			//...
			maven { url 'https://jitpack.io' }
		}
	}
```
**Step 2.** Include the dependency

To use the default Drop In UI, add the `rave-android` module dependency.
```groovy
    dependencies {
	     implementation 'com.github.flutterwave.rave-android:rave_android:2.2.1'
	}
```

if you prefer to use your own custom UI and only want to interact with our core sdk, use the `rave_presentation` module instead.

```groovy
    dependencies {
	     implementation 'com.github.Flutterwave.rave-android:rave_presentation:2.2.1'
	}
```
**Step 3.** Add the  `INTERNET` permission.
Make sure to include the `INTERNET` permission to your Android manifest by adding the following line:

     <uses-permission android:name="android.permission.INTERNET" /> 


> The steps outlined below guide you on how to utilize the Flutterwave Android SDK as a Drop-in UI, where all the views for the payment process are handled by the SDK. If you wish to implement your own custom UI, please refer to the additional instructions provided [here](CustomUiImplementation.md).

## Usage
### For using the default UI
###  1. Create a `RaveUiManager` instance
Configure your Public and Encryption key, along with other required parameters. The `RaveUiManager` requires a mandatory instance of the calling `Activity`, or a `Fragment` associated with a parent activity.

        new RaveUiManager(activity).setAmount(amount)
                        .setCurrency(currency)
                        .setEmail(email)
                        .setfName(fName)
                        .setlName(lName)
                        .setNarration(narration)
                        .setPublicKey(publicKey)
                        .setEncryptionKey(encryptionKey)
                        .setTxRef(txRef)
                        .setPhoneNumber(phoneNumber, boolean)
                        .acceptAccountPayments(boolean)
                        .acceptCardPayments(boolean)
                        .acceptMpesaPayments(boolean)
                        .acceptAchPayments(boolean)
                        .acceptGHMobileMoneyPayments(boolean)
                        .acceptUgMobileMoneyPayments(boolean)
                        .acceptZmMobileMoneyPayments(boolean)
                        .acceptRwfMobileMoneyPayments(boolean)
                        .acceptSaBankPayments(boolean)
                        .acceptUkPayments(boolean)
                        .acceptBankTransferPayments(boolean)
                        .acceptUssdPayments(boolean)
                        .acceptBarterPayments(boolean)
                        .acceptFrancMobileMoneyPayments(boolean)
                        .allowSaveCardFeature(boolean)
                        .onStagingEnv(boolean)
                        .setMeta(List<Meta>)
                        .withTheme(styleId)
                        .isPreAuth(boolean)
                        .setSubAccounts(List<SubAccount>)
                        .shouldDisplayFee(boolean)
                        .showStagingLabel(boolean)
                        .initialize();


<details>
    <summary>Function Definitions</summary>


| Function        | Parameter           | Type | Required  |
| ------------- |:-------------:| -----:| -----:|
| setAmount(amount)      |  This sets the amount to be charged from the customer. | `double` | Required
| setCurrency(currency) | This specifies the currency to charge the customer in. | `String` | Required
| setfName(fName) | This sets the first name of the customer.  | `String` | Required
| setlName(lName) | This sets the last name of the the customer. | `String` | Required
| setEmail(email) | This sets the email address of the customer. | `String` | Required
| setNarration(narration) | This adds a custom description provided by the merchant. For `Bank Transfer` payments, this  will be shown to the customer as the account name when the accoun number is resolved. | `String` | Not Required
| setPublicKey(publicKey) | This sets the Merchant's public key. Get your Live and Test keys from your [dashboard](http://dashboard.flutterwave.com/).| `String` | Required
| setEncryptionKey(encryptionKey) |This sets the Merchant's encryption key. Get your Live and Test keys from your [dashboard](http://dashboard.flutterwave.com/). | `String` | Required
| setTxRef(txRef) | This sets the unique reference, for the specific transaction being carried out. It is generated by the merchant for each transaction. | `String` | Required
| setPhoneNumber(phoneNumber) | This sets the customer's phone number. This functions has an overload to specify whether the customer can edit their phone number. `setPhoneNumber(phoneNumber,false)`. When set to false, the customer is unable to change the phone number provided.| `String`<br/><br/>Optional overloads:<br/>`String`, `boolean` | Not Required
| acceptAccountPayments(boolean) | Set to `true` if you want to accept payments via bank accounts; otherwsie set to `false`. | `boolean` | Not Required
| acceptCardPayments(boolean) | Set to `true` if you want to accept payments via cards, otherwise set to `false`. | `boolean` | Not Required |
| acceptMpesaPayments(boolean) | Set to `true` if you want to accept Mpesa payments; otherwise set to `false`. To use  using this payment option, you must set the country to `KE` and the currency to `KES`. | `boolean` | Not Required |
| acceptGHMobileMoneyPayments(boolean) | Set to `true` if you want to accept Ghana mobile money payments; otherwise set  set to `false` . To use  using this payment option, you must set the country to `GH` and the currency to `GHS`. | `boolean` | Not Required |
| acceptUgMobileMoneyPayments(boolean) | Set to `true` if you want to accept Uganda mobile money payments; otherwise set to `false` . To use this payment option, set the country to `UG` and the currency to `UGX`.| `boolean` | Not Required |
| acceptZmMobileMoneyPayments(boolean) | Set to `true` if you want to accept Zambia mobile money payments;otherwise set to `false` . To use this payment option, set the country to `NG` and the currency to `ZMW`. `MTN` is the only available network at the moment.| `boolean` | Not Required |
| acceptRwfMobileMoneyPayments(boolean) | Set to `true` if you want to accept Rwanda mobile money payments; otherwise set  to `false` . To use this payment option, you should set the country to `NG` and the currency to `RWF`.| `boolean` | Not Required |
| acceptSaBankPayments(boolean) | Set to `true` if you want to accept South African direct bank account payments; otherwsie set to `false` . To use this payment option, you should set the country to `ZA` and the currency to `ZAR`.| `boolean` | Not Required |
| acceptUkPayments(boolean) | Set to `true` if you want to accept UK Bank Account payments; otherwise set to `false` . To use this payment option, you should set the country to `NG` and the currency to `GBP`, and provide the bank account information including `accountBank` (String), `accountName` (String), and `accountNumber` (String). Additionally, set `is_uk_bank_charge` to `true` and `payment_type` to `account`. `Please ensure you use your live credentials for this`. | `boolean` | Not Required |
| acceptAchPayments(boolean) | Set to `true` if you want to accept US ACH charges from your customers; otherwise set to `false` . To use this payment option, you should set the country to `US` and the currency to `USD`. You also need to set `acceptAccountPayments(true)` to true.| `boolean` | Not Required |
| acceptBankTransferPayments(boolean) | Set to `true` if you want to accept payments via bank transfer from your customers; otherwise set to `false`. This payment option is currently only available for the NGN currency. <br/><br/><strong>Note:</strong> By default, the account numbers generated are dynamic. This method has been overloaded with additional options as shown below:<br><ul><li>To generate static (permanent) accounts instead, pass in `true` as a second parameter. E.g. <br/>```acceptBankTransferPayments(true, true)```</li><li>To generate dynamic accounts that expire at a certain date, or after a set number of payments, provide integer values for `duration` and `frequency`, like so: <br/>```acceptBankTransferPayments(true, duration, frequency)``` </li></ul>| `boolean`<br/><br/>Optional overloads:<br/>`boolean`, `boolean`<br/><br/>`boolean`, `int`, `int` .| Not Required |
| acceptUssdPayments(boolean) | Set to `true` if you want to accept payments via USSD transfer from your customers, otherwise set to `false` . This payment option is currently only available for the NGN currency.| `boolean` | Not Required |
| acceptBarterPayments(boolean) | Set to `true` if you want to accept payments via Barter from your customers; otherwise set to `false`.| `boolean` | Not Required |
| acceptFrancMobileMoneyPayments(boolean) | Set to `true` if you want to accept Francophone mobile money payments from your customers; otherwise set to `false` . To use this payment option, you should set the country to `NG` and the currency to `XOF` for West African CFA franc like `Ivory Coast` OR `XAF` for Central African CFA franc like `Cameroon`.| `boolean` | Not Required |
| allowSaveCardFeature(boolean) | Set to `true` if you wish to give the customer the option of saving their cards for future use. This option helps them avoid retyping their card details for every transaction. By default, this is set to `true`.| `boolean` | Not Required |
| onStagingEnv(boolean) | Set to `true` if you want perform test transactions in the test environment; otherwise set to `false`. The Default value to false.  | `boolean` | Not Required
| setMeta(`List<Meta>`) | Pass in any other custom data you wish to pass. It takes in a `List` of `Meta` objects. | List<Meta> | Not Required
| setSubAccounts(`List<SubAccount>`) | Pass in a `List` of `SubAccount`,if you want to split transaction fee with other people. Subaccounts are your vendors' accounts that you want to settle per transaction. To initialize a `SubAccount` class, do `SubAccount(String subAccountId,String transactionSplitRatio)` or `SubAccount(String subAccountId,String transactionSplitRatio,String transactionChargeType, String transactionCharge)` to also charge the subaccount a fee. [Learn more about split payments and subaccounts](https://flutterwave.com/eg/support/payments/split-payments-with-sub-accounts).| `List<SubAccount>`| Not Required
| setIsPreAuth(boolean) | Set to `true` to preauthorise the transaction amount. [Learn more about preauthourization](https://flutterwave.com/ci/blog/a-developers-guide-to-implementing-card-pre-authorization-charges-with-flutterwave). | `int` | Not Required
| withTheme(styleId) | Sets the theme of the UI. | `int` | Not Required
| setPaymentPlan(payment_plan) | If you want to set up recurring payments, the ID set here is the payment plan ID that will be used for the recurring charges. You can learn how to create payment plans [here](https://flutterwave.com/gb/support/my-account/creating-and-cancelling-payment-plans). Recurring payments is only available for card payments. | `String` | Not Required
| shouldDisplayFee(boolean) | Set to `false` to disable the dialog box for confirming the total amount charged by Flutterwave, including the charge fee. By default this is set to `true`. | `boolean` | Not Required
| showStagingLabel(boolean) | Set to `false` to disable the staging label displayed in the test environment. By default this is set to `true`. | `boolean` | Not Required
| initialize() | Launch the Flutterwave Payment UI when using the UI module.   |  N/A | Required

> <strong>Note:</strong> The order in which you call the methods for accepting different payment types determines the display order in the UI.

>  To see a more practical way of using the SDK, head to our sample app in the repository [here](https://github.com/Flutterwave/rave-android/tree/master/app)
</details>


###  2. Handle the response
In the calling activity, override the `onActivityResult` method to receive the payment response, as shown below:
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
         *  We advise you to do a further verification of transaction's details on your server to be
         *  sure everything checks out before providing service or goods.
        */
        if (requestCode == RaveConstants.RAVE_REQUEST_CODE && data != null) {
            String message = data.getStringExtra("response");
            if (resultCode == RavePayActivity.RESULT_SUCCESS) {
                Toast.makeText(this, "SUCCESS " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_ERROR) {
                Toast.makeText(this, "ERROR " + message, Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RavePayActivity.RESULT_CANCELLED) {
                Toast.makeText(this, "CANCELLED " + message, Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    
```

The intent's `message` object contains the raw JSON response from Flutterwave's API. This can be parsed to retrieve any additional payment information needed. A Typical success response can be found [here](https://gist.github.com/bolaware/305ef5a6df7744694d9c35787580a2d2) and a failed response found [here](https://gist.github.com/bolaware/afa972cbca782bbb942984ddec9f5262).

> **PLEASE NOTE**
>  We strongly advise you to verify transaction's details on your server to ensure accuracy before providing goods or services.

###  3. Customize the look
You can customize the UI by changing the color of certain elements to reflect your brand colors.

First specify the theme in your `styles.xml` file. In this theme, you can edit the styles for each of the elements you'd like to modify, such as the pay button, OTP button, etc.
```XML
    <style name="MyCustomTheme" parent="RaveAppTheme.NoActionBar">
        <item name="colorPrimary">@color/colorPrimary</item>
        <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
        <item name="colorAccent">@color/colorAccent</item>
        <item name="OTPButtonStyle">@style/myOtpBtnStyle</item>
        <item name="PayButtonStyle">@style/myBtnStyle</item>
	<item name="PinButtonStyle">@style/myPinButtonStyle</item>
        <item name="OTPHeaderStyle">@style/myOtpHeaderStyle</item>
        <item name="TabLayoutStyle">@style/myTabLayoutStyle</item>
        <item name="PinHeaderStyle">@style/myPinHeaderStyle</item>
        <item name="PaymentTileStyle">@style/myPaymentTileStyle</item>
        <item name="PaymentTileTextStyle">@style/myPaymentTileTextStyle</item>
        <item name="PaymentTileDividerStyle">@style/myPaymentTileDividerStyle</item>
    </style>
```

 Then in your RavePayManager setup, add `.withTheme(<Reference to your style>)` anywhere before calling the `initialize()` function. 
 For example:
 ```java
  new RavePayManager(activity).setAmount(amount)
                    //...
                    //...
                    .withTheme(R.Style.MyCustomTheme)
                    .initialize();
```
> Be aware that there is a limit to how much you can customize the drop-in UI. For further customizations, refer to our Custom UI implemetation guide [here](CustomUiImplementation.md).


## Configuring Proguard
To configure Proguard, add the following lines to your proguard configuration file. These lines will keep the files related to this SDK.
```
keepclasseswithmembers public class com.flutterwave.raveandroid.** { *; }
dontwarn com.flutterwave.raveandroid.card.CardFragment
```


##  Help
* Have issues integrating? Join our [Slack community](https://join.slack.com/t/flutterwavedevelopers/shared_invite/zt-7mdxu82t-~WwizwN3eWKjUXLY275AzQ) for support
* Find a bug? [Open an issue](https://github.com/Flutterwave/rave-android/issues)
* Want to contribute? [Check out contributing guidelines]() and [submit a pull request](https://help.github.com/articles/creating-a-pull-request).

## Want to contribute?

Feel free to create issues and pull requests. The more concise the pull requests the better :)


## License

```
Rave's Android SDK
MIT License

Copyright (c) 2020

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
