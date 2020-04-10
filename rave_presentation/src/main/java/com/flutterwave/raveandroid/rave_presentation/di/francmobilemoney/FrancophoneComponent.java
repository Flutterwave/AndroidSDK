package com.flutterwave.raveandroid.rave_presentation.di.francmobilemoney;


import com.flutterwave.raveandroid.rave_presentation.francmobilemoney.FrancophoneMobileMoneyManager;

import dagger.Subcomponent;

@FrancophoneScope
@Subcomponent(modules = {FrancophoneModule.class})
public interface FrancophoneComponent {
    void inject(FrancophoneMobileMoneyManager manager);
}
