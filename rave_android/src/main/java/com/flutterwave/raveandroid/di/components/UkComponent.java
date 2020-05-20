package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.di.modules.UkModule;
import com.flutterwave.raveandroid.di.scopes.UkScope;
import com.flutterwave.raveandroid.uk.UkFragment;

import dagger.Subcomponent;

@UkScope
@Subcomponent(modules = {UkModule.class})
public interface UkComponent {
    void inject(UkFragment ukFragment);
}
