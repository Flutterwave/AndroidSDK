package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.di.modules.WebModule;
import com.flutterwave.raveandroid.di.scopes.WebScope;
import com.flutterwave.raveandroid.verification.web.WebFragment;

import dagger.Subcomponent;


@WebScope
@Subcomponent(modules = {WebModule.class})
public interface WebComponent {
    void inject(WebFragment webFragment);
}