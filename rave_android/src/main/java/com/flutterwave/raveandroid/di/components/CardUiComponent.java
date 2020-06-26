package com.flutterwave.raveandroid.di.components;


import com.flutterwave.raveandroid.card.CardFragment;
import com.flutterwave.raveandroid.di.modules.CardUiModule;
import com.flutterwave.raveandroid.rave_presentation.di.card.CardScope;

import dagger.Subcomponent;

@CardScope
@Subcomponent(modules = {CardUiModule.class})
public interface CardUiComponent {
    void inject(CardFragment cardFragment);
}
