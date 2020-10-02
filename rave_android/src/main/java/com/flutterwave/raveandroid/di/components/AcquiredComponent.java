package com.flutterwave.raveandroid.di.components;

import com.flutterwave.raveandroid.acquireddotcom.AcquiredFragment;
import com.flutterwave.raveandroid.di.modules.AcquiredModule;
import com.flutterwave.raveandroid.di.scopes.AcquiredScope;

import dagger.Subcomponent;

@AcquiredScope
@Subcomponent(modules = {AcquiredModule.class})
public interface AcquiredComponent {
    void inject(AcquiredFragment acquiredFragment);
}

