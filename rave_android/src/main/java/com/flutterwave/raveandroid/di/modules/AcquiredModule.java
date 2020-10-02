package com.flutterwave.raveandroid.di.modules;

import com.flutterwave.raveandroid.acquireddotcom.AcquiredUiContract;
import javax.inject.Inject;
import dagger.Module;
import dagger.Provides;


@Module
public class AcquiredModule {
    private AcquiredUiContract.View view;

    @Inject
    public AcquiredModule(AcquiredUiContract.View view) {
        this.view = view;
    }

    @Provides
    public AcquiredUiContract.View providesContract() {
        return view;
    }
}
