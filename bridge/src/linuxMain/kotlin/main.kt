import com.charlag.tryToLoadCredentials
import com.charlag.tuta.*
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.sys.Group
import com.charlag.tuta.entities.sys.GroupInfo
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
import com.charlag.writeCredentials
import io.ktor.client.features.logging.*
import kotlinx.coroutines.runBlocking

fun main() {
    Platform.isMemoryLeakCheckerActive = false

    val dependencyDump = DependencyDump()

    runBlocking {
        val user = login(dependencyDump)
        println("Logged in!")

        val mailLoader = MailLoaderImpl(dependencyDump.api, dependencyDump.userController)
//        val mailLoader = FakeMailLoader()
        val server = ImapServer(mailLoader)

        loadMails(dependencyDump)
        runBridgeServer(server)
    }
}


suspend fun login(dependencyDump: DependencyDump): User {
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
    return user
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

private suspend fun loadMails(dependencyDump: DependencyDump) {
    val user = dependencyDump.userController.user ?: error("Not logged in!")
    val mailMembership = user.memberships.first { it.groupType == GroupType.Mail.value }
    val mailGroup = dependencyDump.api.loadElementEntity<Group>(mailMembership.group)
    val userGroupInfo =
        dependencyDump.api.loadListElementEntity<GroupInfo>(user.userGroup.groupInfo)

    val mailAddresses =
        dependencyDump.mailFacade.getEnabledMailAddresses(user, userGroupInfo, mailGroup)
    println("Mail addresses: $mailAddresses")

    val groupRoot = dependencyDump.api
        .loadElementEntity<MailboxGroupRoot>(mailMembership.group)
    val mailbox = dependencyDump.api.loadElementEntity<MailBox>(groupRoot.mailbox)
    val folders = dependencyDump.api.loadAll(MailFolder::class, mailbox.systemFolders!!.folders)
    val inbox = folders.first { it.folderType == MailFolderType.INBOX.value }
    val mails = dependencyDump.api.loadRange(Mail::class, inbox.mails, GENERATED_MAX_ID, 40, true)
    mails.forEach {
        println("${it.sender.name} ${it.sender.address} ${it.subject}")
    }
}

private const val REST_PATH = "https://mail.tutanota.com/rest/"
private const val WS_PATH = "https://mail.tutanota.com/event"