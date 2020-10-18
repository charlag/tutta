import com.charlag.getAppConfigDir
import com.charlag.tryToLoadCredentials
import com.charlag.tuta.*
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.imap.ImapServer
import com.charlag.tuta.imap.MailLoaderImpl
import com.charlag.tuta.imap.SmtpServer
import com.charlag.tuta.imap.commands.FetchHandler
import com.charlag.tuta.imap.commands.SearchHandler
import com.charlag.tuta.network.API
import com.charlag.tuta.network.SessionKeyResolver
import com.charlag.tuta.network.UserSessionDataProvider
import com.charlag.tuta.network.makeHttpClient
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.posix.append
import com.charlag.tuta.posix.exists
import com.charlag.tuta.posix.readPassword
import com.charlag.writeCredentials
import io.ktor.client.features.logging.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.freeze
import kotlin.time.minutes

fun main() {
    Platform.isMemoryLeakCheckerActive = false

    val dependencyDump = DependencyDump()

    runBlocking {
        val pwKey = login(dependencyDump)
        println("Logged in!")

        val dbPath = getAppConfigDir().append("mail.db")
        val dbExists = dbPath.exists() // Must be checked before creating db!
        val db = SqliteDb(dbPath.value)
        val mailDb = MailDb(db, dependencyDump.instanceMapper, pwKey)
        val syncHandler = SyncHandler(dependencyDump.api, mailDb, dependencyDump.userController)
        if (!dbExists) syncHandler.initialSync() else syncHandler.resync()

        val mailLoader = MailLoaderImpl(dependencyDump.api, mailDb, dependencyDump.fileFacade)
        val fetchHandler = FetchHandler(mailLoader)
        val searchHandler = SearchHandler(mailLoader)
//        val mailLoader = FakeMailLoader()
        runBridgeServer(
            imapServerFactory = {
                ImapServer(
                    mailLoader,
                    syncHandler,
                    fetchHandler,
                    searchHandler
                )
            },
            smtpServerFactory = {
                SmtpServer(dependencyDump.mailFacade, dependencyDump.userController)
            }
        )
        while (true) {
            delay(5.minutes)
            syncHandler.resync()
        }
    }
}


/**
 * @return passphraseKey
 */
suspend fun login(dependencyDump: DependencyDump): ByteArray {
    val createSessionResult = tryToLoadCredentials() ?: newSession(dependencyDump)
    dependencyDump.sessionDataProvider.setAccessToken(createSessionResult.accessToken)
    val user = dependencyDump.api.loadElementEntity<User>(createSessionResult.userId)
    val userGroupKey =
        dependencyDump.loginFacade.getUserGroupKey(user, createSessionResult.passphraseKey)
    dependencyDump.sessionDataProvider.setSessionData(
        SessionData(
            user,
            createSessionResult.accessToken,
            userGroupKey
        )
    )
    dependencyDump.userController.user = user
    return createSessionResult.passphraseKey
}

suspend fun newSession(dependencyDump: DependencyDump): CreateSessionResult {
    print("Email: ")
    val email = readLine() ?: ""
    print("Password: ")
    val password = readPassword() ?: ""

    val credentials = dependencyDump.loginFacade.createSession(
        email,
        password,
        onSecondFactorPending = { error("Does not support 2FA yet") }
    )
    writeCredentials(credentials)
    return credentials
}

class DependencyDump {
    val htpClient = makeHttpClient {
        install(Logging) {
            level = LogLevel.INFO
        }
        // TODO: websockets?
        // Current ooptions for native are CIO and curl. CIO doesn't support SSL connections.
        // ktor doesn't handle updates. libcurl doesn't handle 'ws://' scheme.
    }
    val cryptor = Cryptor()

    val instanceMapper = InstanceMapper(cryptor, Compressor(), typemodelMap)
    val sessionDataProvider = UserSessionDataProvider(cryptor)
    val keyResolver = SessionKeyResolver(cryptor, sessionDataProvider)
    val api = API(
        htpClient,
        REST_PATH,
        cryptor,
        instanceMapper,
        sessionDataProvider,
        keyResolver,
        WS_PATH
    )
    val loginFacade = LoginFacade(cryptor, api)
    val mailFacade = MailFacade(api, cryptor, keyResolver)
    val userController = UserController()
    val fileFacade = FileFacade(api, cryptor, keyResolver)
}

class UserController {
    private val _user: AtomicReference<User?> = AtomicReference(null)
    var user: User?
        get() = _user.value
        set(value) {
            _user.value = value.freeze()
        }
}

private const val REST_PATH = "https://mail.tutanota.com/rest/"
private const val WS_PATH = "https://mail.tutanota.com/event"

fun Mail.deriveHackyUid() = (receivedDate.millis / 1000).toInt()