package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.RavePayActivity;
import com.flutterwave.raveandroid.data.NetworkRequestImpl;
import com.flutterwave.raveandroid.di.modules.AccountModule;
import com.flutterwave.raveandroid.di.modules.AchModule;
import com.flutterwave.raveandroid.di.modules.AndroidModule;
import com.flutterwave.raveandroid.di.modules.BankTransferModule;
import com.flutterwave.raveandroid.di.modules.CardModule;
import com.flutterwave.raveandroid.di.modules.FrancModule;
import com.flutterwave.raveandroid.di.modules.GhanaModule;
import com.flutterwave.raveandroid.di.modules.MpesaModule;
import com.flutterwave.raveandroid.di.modules.NetworkModule;
import com.flutterwave.raveandroid.di.modules.RwandaModule;
import com.flutterwave.raveandroid.di.modules.UgandaModule;
import com.flutterwave.raveandroid.di.modules.UkModule;
import com.flutterwave.raveandroid.di.modules.ZambiaModule;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AndroidModule.class, NetworkModule.class})
public interface AppComponent {

    NetworkRequestImpl networkImpl();

    void inject(RavePayActivity ravePayActivity);

    MpesaComponent plus(MpesaModule mpesaModule);

    UgandaComponent plus(UgandaModule ugandaModule);

    RwandaComponent plus(RwandaModule rwandaModule);

    GhanaComponent plus(GhanaModule ghanaModule);

    ZambiaComponent plus(ZambiaModule zambiaModule);

    CardComponent plus(CardModule cardModule);

    BankTransferComponent plus(BankTransferModule bankTransferModule);

    AccountComponent plus(AccountModule accountModule);

    AchComponent plus(AchModule achModule);

    UkComponent plus(UkModule ukModule);

    FrancComponent plus(FrancModule francModule);
}

