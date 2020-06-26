package com.flutterwave.raveandroid.rave_presentation.di;


import com.flutterwave.raveandroid.rave_core.di.DeviceIdGetterModule;
import com.flutterwave.raveandroid.rave_core.models.DeviceIdGetter;
import com.flutterwave.raveandroid.rave_logger.EventLogger;
import com.flutterwave.raveandroid.rave_logger.di.EventLoggerModule;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadEncryptor;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJson;
import com.flutterwave.raveandroid.rave_presentation.data.PayloadToJsonConverter;
import com.flutterwave.raveandroid.rave_presentation.data.validators.CardNoValidator;
import com.flutterwave.raveandroid.rave_presentation.data.validators.TransactionStatusChecker;
import com.flutterwave.raveandroid.rave_presentation.data.validators.UrlValidator;
import com.flutterwave.raveandroid.rave_presentation.di.account.AccountComponent;
import com.flutterwave.raveandroid.rave_presentation.di.account.AccountModule;
import com.flutterwave.raveandroid.rave_presentation.di.ach.AchComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ach.AchModule;
import com.flutterwave.raveandroid.rave_presentation.di.banktransfer.BankTransferComponent;
import com.flutterwave.raveandroid.rave_presentation.di.banktransfer.BankTransferModule;
import com.flutterwave.raveandroid.rave_presentation.di.barter.BarterComponent;
import com.flutterwave.raveandroid.rave_presentation.di.barter.BarterModule;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardComponent;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardModule;
import com.flutterwave.raveandroid.rave_presentation.di.francmobilemoney.FrancophoneComponent;
import com.flutterwave.raveandroid.rave_presentation.di.francmobilemoney.FrancophoneModule;
import com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney.GhMobileMoneyComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ghmobilemoney.GhMobileMoneyModule;
import com.flutterwave.raveandroid.rave_presentation.di.mpesa.MpesaComponent;
import com.flutterwave.raveandroid.rave_presentation.di.mpesa.MpesaModule;
import com.flutterwave.raveandroid.rave_presentation.di.rwfmobilemoney.RwfComponent;
import com.flutterwave.raveandroid.rave_presentation.di.rwfmobilemoney.RwfModule;
import com.flutterwave.raveandroid.rave_presentation.di.sabank.SaBankComponent;
import com.flutterwave.raveandroid.rave_presentation.di.sabank.SaBankModule;
import com.flutterwave.raveandroid.rave_presentation.di.ugmomo.UgComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ugmomo.UgModule;
import com.flutterwave.raveandroid.rave_presentation.di.uk.UkComponent;
import com.flutterwave.raveandroid.rave_presentation.di.uk.UkModule;
import com.flutterwave.raveandroid.rave_presentation.di.ussd.UssdComponent;
import com.flutterwave.raveandroid.rave_presentation.di.ussd.UssdModule;
import com.flutterwave.raveandroid.rave_presentation.di.zm.ZmComponent;
import com.flutterwave.raveandroid.rave_presentation.di.zm.ZmModule;
import com.flutterwave.raveandroid.rave_remote.RemoteRepository;
import com.flutterwave.raveandroid.rave_remote.di.RemoteModule;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {RemoteModule.class, EventLoggerModule.class, DeviceIdGetterModule.class})
public interface RaveComponent {

    RemoteRepository networkImpl();

    EventLogger eventLogger();

    DeviceIdGetter deviceIdGetter();

    UrlValidator urlValidator();

    CardNoValidator cardNoValidator();

    PayloadToJsonConverter payloadToJsonConverter();

    TransactionStatusChecker transactionStatusChecker();

    PayloadEncryptor payloadEncryptor();

    PayloadToJson payloadToJson();

    Gson gson();

    CardComponent plus(CardModule cardModule);

    AccountComponent plus(AccountModule module);

    AchComponent plus(AchModule module);

    BankTransferComponent plus(BankTransferModule module);

    BarterComponent plus(BarterModule module);

    FrancophoneComponent plus(FrancophoneModule module);

    GhMobileMoneyComponent plus(GhMobileMoneyModule module);

    MpesaComponent plus(MpesaModule module);

    RwfComponent plus(RwfModule module);

    SaBankComponent plus(SaBankModule module);

    UgComponent plus(UgModule module);

    UkComponent plus(UkModule module);

    UssdComponent plus(UssdModule module);

    ZmComponent plus(ZmModule module);
}

