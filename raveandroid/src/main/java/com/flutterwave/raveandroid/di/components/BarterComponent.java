package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.barter.BarterFragment;
import com.flutterwave.raveandroid.di.modules.BarterModule;
import com.flutterwave.raveandroid.di.scopes.BarterScope;

import dagger.Subcomponent;


@BarterScope
@Subcomponent(modules = {BarterModule.class})
public interface BarterComponent {
    void inject(BarterFragment barterFragment);
}