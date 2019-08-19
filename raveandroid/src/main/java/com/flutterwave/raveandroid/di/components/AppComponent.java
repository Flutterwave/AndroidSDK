package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.RaveApp;
import com.flutterwave.raveandroid.di.modules.AccountModule;
import com.flutterwave.raveandroid.di.modules.AchModule;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.BankTransferModule;
import com.flutterwave.raveandroid.di.modules.CardModule;
import com.flutterwave.raveandroid.di.modules.GhanaModule;
import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.di.modules.UgandaModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, NetworkModule.class})
public interface AppComponent {

    void inject(RaveApp raveApp);

    MpesaComponent plus(MpesaModule mpesaModule);
    UgandaComponent plus(UgandaModule ugandaModule);
    GhanaComponent plus(GhanaModule ghanaModule);
    CardComponent plus(CardModule cardModule);
    BankTransferComponent plus(BankTransferModule bankTransferModule);
    AccountComponent plus(AccountModule accountModule);
    AchComponent plus(AchModule achModule);
//    ActivityComponent plus(NetworkModule networkModule);
}

