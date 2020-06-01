package com.flutterwave.raveutils.di;

import com.flutterwave.raveutils.verification.web.WebFragment;

import dagger.Subcomponent;


@WebScope
@Subcomponent(modules = {WebModule.class})
public interface WebComponent {
    void inject(WebFragment webFragment);
}