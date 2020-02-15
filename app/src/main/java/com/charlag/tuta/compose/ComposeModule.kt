package com.charlag.tuta.compose

import androidx.lifecycle.ViewModel
import com.charlag.tuta.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
interface ComposeModule {
    @ContributesAndroidInjector
    fun composeAcitivity(): ComposeActivity

    @Binds
    @IntoMap
    @ViewModelKey(ComposeViewModel::class)
    fun bindsViewModel(viewModel: ComposeViewModel): ViewModel
}

