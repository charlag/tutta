package com.charlag.tuta

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.charlag.tuta.compose.MailSender
import com.charlag.tuta.contacts.ContactsRepository
import com.charlag.tuta.data.AppDatabase
import com.charlag.tuta.events.EntityEventListener
import com.charlag.tuta.files.FileHandler
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.util.KtorExperimentalAPI
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object DependencyDump {
    var credentials: Credentials? = null
    val cryptor = Cryptor()
    val groupKeysCache = GroupKeysCache(cryptor)
    @UseExperimental(KtorExperimentalAPI::class)
    val httpClient = makeHttpClient {
        install(Logging) {
            level = LogLevel.INFO
            logger = object : Logger {
                override fun log(message: String) {
                    Log.d("HTTP", message)
                }
            }
        }
        install(WebSockets)
    }
    val compressor = Compressor()

    val api = API(
        httpClient, "https://mail.tutanota.com/rest/",
        cryptor,
        typemodelMap,
        compressor,
        groupKeysCache,
        accessToken = null,
        wsUrl = "wss://mail.tutanota.com/event"
    )
    val loginFacade = LoginFacade(cryptor, api, groupKeysCache)
    val mailFacade = MailFacade(api, cryptor)
    val fileFacade = FileFacade(api, cryptor)
    lateinit var db: AppDatabase
    lateinit var contactRepository: ContactsRepository
    private lateinit var eventListener: EntityEventListener
    lateinit var fileHandler: FileHandler
    lateinit var mailSender: MailSender

    private var _hasLoggedin = false
    val hasLoggedIn: Boolean
        get() = _hasLoggedin

    fun ignite(dbPassword: String, applicationContext: Context) {
        val factory = SupportFactory(SQLiteDatabase.getBytes(dbPassword.toCharArray()))
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "tuta-db")
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
        contactRepository = ContactsRepository(api, db, loginFacade)
        eventListener =
            EntityEventListener(loginFacade, api, db, contactRepository, applicationContext)
        fileHandler = FileHandler(fileFacade, loginFacade, applicationContext)
        val notificationManager = LocalNotificationManager(applicationContext)
        mailSender = MailSender(mailFacade, fileHandler, notificationManager, db)
        _hasLoggedin = true
    }
}