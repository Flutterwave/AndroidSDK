package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.DeviceIdGetter;
import com.flutterwave.raveandroid.PhoneNumberObfuscator;
import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.WebFragment;
import com.flutterwave.raveandroid.di.modules.AccountModule;
import com.flutterwave.raveandroid.di.modules.AchModule;
import com.flutterwave.raveandroid.di.modules.BankTransferModule;
import com.flutterwave.raveandroid.di.modules.BarterModule;
import com.flutterwave.raveandroid.di.modules.CardModule;
import com.flutterwave.raveandroid.di.modules.FrancModule;
import com.flutterwave.raveandroid.di.modules.GhanaModule;
import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.RwandaModule;
import com.flutterwave.raveandroid.di.modules.SaBankModule;
import com.flutterwave.raveandroid.di.modules.UgandaModule;
import com.flutterwave.raveandroid.di.modules.UkModule;
import com.flutterwave.raveandroid.di.modules.UssdModule;
import com.flutterwave.raveandroid.di.modules.WebModule;
import com.flutterwave.raveandroid.di.modules.ZambiaModule;
import com.flutterwave.raveandroid.di.scopes.UiScope;
import com.flutterwave.raveandroid.rave_cache.SharedPrefsRepo;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_presentation.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.di.RaveComponent;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.validators.AccountNoValidator;
import com.flutterwave.raveandroid.validators.AmountValidator;
import com.flutterwave.raveandroid.validators.BankCodeValidator;
import com.flutterwave.raveandroid.validators.BanksMinimum100AccountPaymentValidator;
import com.flutterwave.raveandroid.validators.BvnValidator;
import com.flutterwave.raveandroid.validators.CardExpiryValidator;
import com.flutterwave.raveandroid.validators.CardNoValidator;
import com.flutterwave.raveandroid.validators.CvvValidator;
import com.flutterwave.raveandroid.validators.DateOfBirthValidator;
import com.flutterwave.raveandroid.validators.EmailValidator;
import com.flutterwave.raveandroid.validators.NetworkValidator;
import com.flutterwave.raveandroid.validators.PhoneValidator;
import com.flutterwave.raveandroid.verification.AVSVBVFragment;
import com.flutterwave.raveandroid.verification.OTPFragment;
import com.flutterwave.raveandroid.verification.PinFragment;
import com.google.gson.Gson;

import dagger.Component;

@UiScope
@Component(dependencies = {RaveComponent.class})
public interface RaveUiComponent {

    RemoteRepository networkImpl();

    EventLogger eventLogger();

    AmountValidator amountValidator();

    CvvValidator cvvValidator();

    EmailValidator emailValidator();

    DateOfBirthValidator dateOfBirthValidator();

    BvnValidator bvnValidator();

    PhoneValidator phoneValidator();

    CardExpiryValidator cardExpiryValidator();

    CardNoValidator cardNoValidator();

    DeviceIdGetter deviceIdGetter();

    BankCodeValidator bankCodeValidator();

    AccountNoValidator accountNoValidator();

    BanksMinimum100AccountPaymentValidator minimum100AccountPaymentValidator();

    PhoneNumberObfuscator phoneNumberObfuscator();

    PayloadToJsonConverter payloadToJsonConverter();

    TransactionStatusChecker transactionStatusChecker();

    PayloadEncryptor payloadEncryptor();

    PayloadToJson payloadToJson();

    SharedPrefsRepo sharedManager();

    NetworkValidator networkValidator();

    Gson gson();

    void inject(RavePayActivity ravePayActivity);

    void inject(AVSVBVFragment avsvbvFragment);

    void inject(OTPFragment otpFragment);

    void inject(PinFragment pinFragment);

    void inject(WebFragment webFragment);

    MpesaComponent plus(MpesaModule mpesaModule);

    UgandaComponent plus(UgandaModule ugandaModule);

    RwandaComponent plus(RwandaModule rwandaModule);

    GhanaComponent plus(GhanaModule ghanaModule);

    ZambiaComponent plus(ZambiaModule zambiaModule);

    CardComponent plus(CardModule cardModule);

    BankTransferComponent plus(BankTransferModule bankTransferModule);

    UssdComponent plus(UssdModule ussdModule);

    AccountComponent plus(AccountModule accountModule);

    AchComponent plus(AchModule achModule);

    UkComponent plus(UkModule ukModule);

    BarterComponent plus(BarterModule barterModule);

    WebComponent plus(WebModule webModule);

    FrancComponent plus(FrancModule francModule);

    SaBankComponent plus(SaBankModule saBankModule);
}

