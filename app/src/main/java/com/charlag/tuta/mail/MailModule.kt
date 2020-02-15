package com.charlag.tuta.mail

import androidx.lifecycle.ViewModel
import com.charlag.tuta.MailFacade
import com.charlag.tuta.MainActivity
import com.charlag.tuta.UserController
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.di.UserBound
import com.charlag.tuta.di.ViewModelBuilder
import com.charlag.tuta.di.ViewModelKey
import com.charlag.tuta.network.API
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class MailModule {
    @ContributesAndroidInjector
    abstract fun mainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun mailListFragment(): MailListFragment

    @ContributesAndroidInjector(modules = [ViewModelBuilder::class])
    abstract fun mailViewerFragment(): MailViewerFragment

    @Binds
    @IntoMap
    @ViewModelKey(MailViewModel::class)
    abstract fun bindsViewModel(viewModel: MailViewModel): ViewModel

    companion object {
        @Provides
        fun mailRepository(
            @UserBound api: API,
            db: AppDatabase,
            userController: UserController,
            mailFacade: MailFacade
        ) =
            MailRepository(api, db, userController, mailFacade)
    }
}