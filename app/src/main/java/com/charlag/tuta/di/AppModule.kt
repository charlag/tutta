package com.charlag.tuta.di

import android.content.Context
import com.charlag.tuta.*
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.files.FileHandler
import com.charlag.tuta.network.API
import com.charlag.tuta.network.GroupKeysCache
import com.charlag.tuta.network.InstanceMapper
import com.charlag.tuta.network.SessionKeyResolver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @Provides
    fun preferencesFacade(context: Context): PreferenceFacade =
        SharedPreferencesPreferenceFacade(context)

    @Provides
    @Singleton
    fun cryptor(): Cryptor = Cryptor()

    @Provides
    @Singleton
    fun instanceMapper(cryptor: Cryptor, compressor: Compressor): InstanceMapper {
        return InstanceMapper(cryptor, compressor, typemodelMap)
    }

    @Provides
    @Singleton
    fun compressor() = Compressor()

    @Provides
    @Singleton
    fun providesApi(): API = DependencyDump.api

    @Provides
    @Singleton
    fun providesAppDatabase(): AppDatabase = DependencyDump.db

    @Provides
    @Singleton
    fun userController(api: API, loginFacade: LoginFacade): UserController =
        // TODO
//        UserController(api, loginFacade)
        DependencyDump.userController

    @Provides
    @Singleton
    fun groupKeysCache(cryptor: Cryptor): GroupKeysCache =
        // TODO
//        GroupKeysCache(cryptor)
        DependencyDump.groupKeysCache

    @Provides
    @Singleton
    fun loginFacade(cryptor: Cryptor, api: API, groupKeysCache: GroupKeysCache): LoginFacade =
        LoginFacade(cryptor, api, groupKeysCache)

    @Provides
    @Singleton
    fun fileFacade(api: API, cryptor: Cryptor, keyResolver: SessionKeyResolver): FileFacade =
        FileFacade(api, cryptor, keyResolver)

    @Provides
    @Singleton
    fun fileHandler(
        fileFacade: FileFacade,
        loginFacade: LoginFacade,
        context: Context
    ): FileHandler = FileHandler(fileFacade, loginFacade, context)

    @Provides
    @Singleton
    fun sessionKeyResolver(cryptor: Cryptor, groupKeysCache: GroupKeysCache): SessionKeyResolver =
        SessionKeyResolver(cryptor, groupKeysCache)
}