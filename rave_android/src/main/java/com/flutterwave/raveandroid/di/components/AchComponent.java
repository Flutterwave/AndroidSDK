package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.ach.AchFragment;
import com.flutterwave.raveandroid.di.modules.AchModule;
import com.flutterwave.raveandroid.di.scopes.AchScope;

import dagger.Subcomponent;

@AchScope
@Subcomponent(modules = {AchModule.class})
public interface AchComponent {
    void inject(AchFragment achFragment);
}
