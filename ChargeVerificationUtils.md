# Charge Verification Utils
This module helps you handle charge verification when not using the default drop-in UI provided by Flutterwave's android SDK.

**Step 1.** Add this in your root build.gradle at the end of repositories:

    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

**Step 2.** Add the dependency for the utils library

    dependencies {
	     implementation 'com.github.Flutterwave.rave-android:rave_utils:2.1.20'
	}

**Step 2.**  In your payment activity or fragment, create an instance of the `RaveVerificationUtils` class

     RaveVerificationUtils verificationUtils = new RaveVerificationUtils(contextProvider, isStaging, publicKey, theme);

##### Parameter definitions
| Parameter Name      | Description           | Type | Required  |
| ------------- |:-------------:| -----:| -----:|
| contextProvider     |  This is the application or fragment class where you're handling the charge verification. | `Activity` or `Fragment` | Required
| isStaging     |  Specifies whether it's the staging or live environment. | `Boolean` | Required
| publicKey     |  Your Flutterwave account's public key. | `String` | Required
| theme     |  Reference to your custom style. | `int` | Not required

**Step 3** You can call the verification class for these scenarios:

```
// For PIN collection:
verificationUtils.showPinScreen();
        
// For OTP collection
verificationUtils.showOtpScreen(instructionToBeDisplayed); // instruction parameter is optional
        
// For Address collection
verificationUtils.showAddressScreen();
        
// For Authentication webpage display
verificationUtils.showWebpageVerificationScreen(authUrl);
```

**Step 4** Handle the result in `onActivityResult`:

```
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RaveConstants.RESULT_SUCCESS) {
            switch (requestCode) {
                case RaveConstants.PIN_REQUEST_CODE:
                    String pin = data.getStringExtra(PinFragment.EXTRA_PIN);
                    // Use the collected PIN
                    cardPayManager.submitPin(pin);
                    break;
                case RaveConstants.ADDRESS_DETAILS_REQUEST_CODE:
                    String streetAddress = data.getStringExtra(AVSVBVFragment.EXTRA_ADDRESS);
                    String state = data.getStringExtra(AVSVBVFragment.EXTRA_STATE);
                    String city = data.getStringExtra(AVSVBVFragment.EXTRA_CITY);
                    String zipCode = data.getStringExtra(AVSVBVFragment.EXTRA_ZIPCODE);
                    String country = data.getStringExtra(AVSVBVFragment.EXTRA_COUNTRY);
                    AddressDetails address = new AddressDetails(streetAddress, city, state, zipCode, country);

                    // Use the address details
                    cardPayManager.submitAddress(address);
                    break;
                case RaveConstants.WEB_VERIFICATION_REQUEST_CODE:
                    // Web authentication complete, proceed
                    cardPayManager.onWebpageAuthenticationComplete();
                    break;
                case RaveConstants.OTP_REQUEST_CODE:
                    String otp = data.getStringExtra(OTPFragment.EXTRA_OTP);
                    // Use OTP
                    cardPayManager.submitOtp(otp);
                    break;
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
```
