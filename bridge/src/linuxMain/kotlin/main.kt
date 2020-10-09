import com.charlag.tryToLoadCredentials
import com.charlag.tuta.*
import com.charlag.tuta.entities.sys.User
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailBox
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.entities.tutanota.MailboxGroupRoot
import com.charlag.tuta.imap.ImapServer
import com.charlag.tuta.imap.MailLoaderImpl
import com.charlag.tuta.network.API
import com.charlag.tuta.network.SessionKeyResolver
import com.charlag.tuta.network.UserSessionDataProvider
import com.charlag.tuta.network.makeHttpClient
import com.charlag.tuta.network.mapping.InstanceMapper
import com.charlag.tuta.posix.Path
import com.charlag.tuta.posix.exists
import com.charlag.tuta.util.timestmpToGeneratedId
import com.charlag.writeCredentials
import io.ktor.client.features.logging.*
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main() {
    Platform.isMemoryLeakCheckerActive = false

    val dependencyDump = DependencyDump()

    runBlocking {
        val pwKey = login(dependencyDump)
        println("Logged in!")

        val dbBath = "mail.db"
        val dbExists = Path(dbBath).exists()
        val db = Db(dbBath)
        val mailDb = MailDb(db, dependencyDump.instanceMapper, pwKey)
        if (!dbExists) {
            println("Doing initial sync")
            val size = initialSync(pwKey, mailDb, dependencyDump)
            println("Finished initial sync with $size mails")
        }

        val mailLoader = MailLoaderImpl(dependencyDump.api, dependencyDump.userController, mailDb)
//        val mailLoader = FakeMailLoader()
        runBridgeServer { ImapServer(mailLoader) }
    }
}

private suspend fun initialSync(key: ByteArray, db: MailDb, dependencyDump: DependencyDump): Int {
    val inbox = getInbox(dependencyDump)
    val dateStart = Clock.System.now().minus(14.toDuration(DurationUnit.DAYS))
    println("Downloading emails since $dateStart")
    val startMs = dateStart.toEpochMilliseconds()
    val startId = timestmpToGeneratedId(startMs, 0)
    val mails = dependencyDump.api.loadRangeInBetween(Mail::class, inbox.mails, startId)
    println("Downloaded mails ${mails.size}")
    for (mail in mails) {
        val uid = (mail.receivedDate.millis / 1000).toInt()
        db.writeSingle(uid, mail)
    }
    return mails.size
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
    val password = readLine() ?: ""

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
}

class UserController {
    var user: User? = null
}

private suspend fun getInbox(dependencyDump: DependencyDump): MailFolder {
    val user = dependencyDump.userController.user ?: error("Not logged in!")
    val mailMembership = user.memberships.first { it.groupType == GroupType.Mail.value }

    val groupRoot = dependencyDump.api
        .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
    val mailbox = dependencyDump.api.loadElementEntity<MailBox>(groupRoot.mailbox)
    val folders = dependencyDump.api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
    val inbox = folders.first { it.folderType == MailFolderType.INBOX.value }
    return inbox
}

private const val REST_PATH = "https://mail.tutanota.com/rest/"
private const val WS_PATH = "https://mail.tutanota.com/event"