package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.card.CardFragment;
import com.flutterwave.raveandroid.di.modules.CardModule;
import com.flutterwave.raveandroid.di.scopes.CardScope;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {CardModule.class})
public interface CardComponent {
    void inject(CardFragment cardFragment);
}
