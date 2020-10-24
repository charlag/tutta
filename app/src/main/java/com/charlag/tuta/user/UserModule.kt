package com.charlag.tuta.user

import android.content.Context
import androidx.room.Room
import com.charlag.tuta.*
import com.charlag.tuta.compose.ComposeModule
import com.charlag.tuta.contacts.ContactsModule
import com.charlag.tuta.contacts.ContactsRepository
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.di.RestPath
import com.charlag.tuta.di.UserBound
import com.charlag.tuta.di.UserScoped
import com.charlag.tuta.di.WsPath
import com.charlag.tuta.entities.Id
import com.charlag.tuta.events.EntityEventListener
import com.charlag.tuta.files.FileHandler
import com.charlag.tuta.mail.InboxRuleHandler
import com.charlag.tuta.mail.MailModule
import com.charlag.tuta.mail.RealInboxRuleHandler
import com.charlag.tuta.network.*
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.notifications.PushNotificationsManager
import com.charlag.tuta.settings.SettingsModule
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.android.DispatchingAndroidInjector
import io.ktor.client.*
import kotlinx.coroutines.Deferred
import net.sqlcipher.database.SupportFactory

@Module
class UserModule(
    private val userId: Id,
    private val loginDeferred: Deferred<SessionData>,
    dbPassword: ByteArray
) {
    private val dbFactory = SupportFactory(dbPassword)

    @Provides
    @UserBound
    @UserScoped
    fun api(
        httpClient: HttpClient,
        cryptor: Cryptor,
        instanceMapper: InstanceMapper,
        @UserBound sessionDataProvider: SessionDataProvider,
        @UserBound keyResolver: SessionKeyResolver,
        @RestPath restPath: String,
        @WsPath wsPath: String
    ): API =
        API(
            httpClient,
            restPath,
            cryptor,
            instanceMapper,
            sessionDataProvider,
            keyResolver,
            wsPath
        )

    @Provides
    @UserScoped
    fun userController(@UserBound api: API): UserController =
        RealUserController(userId, api, loginDeferred)

    @Provides
    @UserBound
    @UserScoped
    fun providesUserGroupKeysCache(cryptor: Cryptor): SessionDataProvider =
        UserSessionDataProvider(cryptor)

    @Provides
    @UserScoped
    fun fileHandler(
        fileFacade: FileFacade,
        userController: UserController,
        context: Context
    ): FileHandler = FileHandler(fileFacade, userController, context)

    @Provides
    @UserScoped
    fun providesAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "tuta-db-$userId")
            .openHelperFactory(dbFactory)
            .fallbackToDestructiveMigration()
            .build()
    }


    @Provides
    @UserBound
    @UserScoped
    fun sessionKeyResolver(
        cryptor: Cryptor, @UserBound sessionDataProvider: SessionDataProvider
    ): SessionKeyResolver =
        SessionKeyResolver(cryptor, sessionDataProvider)


    @Provides
    @UserScoped
    fun fileFacade(
        @UserBound api: API,
        cryptor: Cryptor,
        @UserBound keyResolver: SessionKeyResolver
    ): FileFacade =
        FileFacade(api, cryptor, keyResolver)

    @Provides
    @UserScoped
    fun mailFacade(
        @UserBound api: API,
        cryptor: Cryptor,
        @UserBound keyResolver: SessionKeyResolver
    ) = MailFacade(api, cryptor, keyResolver)

    @Provides
    @UserScoped
    fun contactsRepository(
        @UserBound api: API,
        db: AppDatabase,
        userController: UserController
    ): ContactsRepository = ContactsRepository(api, db, userController)

    @Provides
    @UserBound
    @UserScoped
    fun loginFacade(
        cryptor: Cryptor,
        @UserBound api: API
    ): LoginFacade = LoginFacade(cryptor, api)

    @Provides
    @UserScoped
    fun inboxRuleHandler(
        userController: UserController,
        mailFacade: MailFacade
    ): InboxRuleHandler = RealInboxRuleHandler(userController, mailFacade)
}


@UserScoped
@Subcomponent(
    modules = [
        UserModule::class,
        MailModule::class,
        ComposeModule::class,
        SettingsModule::class,
        ContactsModule::class
    ]
)
interface UserComponent {
    fun userController(): UserController
    fun androidInjector(): DispatchingAndroidInjector<Any>
    @UserBound
    @UserScoped
    fun api(): API

    @UserBound
    @UserScoped
    fun groupKeysCache(): SessionDataProvider

    @UserScoped
    fun pushNotificationsManger(): PushNotificationsManager

    @UserBound
    @UserScoped
    fun loginFacade(): LoginFacade

    fun entityEventListener(): EntityEventListener
    @UserScoped
    fun db(): AppDatabase
}