package com.flutterwave.raveandroid.rave_presentation.di.acquireddotcom;

import com.flutterwave.raveandroid.rave_presentation.acquireddotcom.AcquiredManager;
import dagger.Subcomponent;

@AcquiredScope
@Subcomponent(modules = {AcquiredModule.class})
public interface AcquiredComponent {
    void inject(AcquiredManager manager);
}
