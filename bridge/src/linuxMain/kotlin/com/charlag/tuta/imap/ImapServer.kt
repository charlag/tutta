package com.charlag.tuta.imap

import com.charlag.tuta.MailFolderType
import com.charlag.tuta.SyncHandler
import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.imap.commands.FetchHandler
import com.charlag.tuta.imap.commands.SearchHandler
import com.charlag.tuta.toBytes
import deriveHackyUid
import kotlinx.coroutines.runBlocking

private data class ReadingState(val tag: String, var toRead: Int)

typealias FetchRange = Pair<Int, Int>

class ImapServer(
    private val mailLoader: MailLoader,
    private val syncHandler: SyncHandler,
    private val fetchHandler: FetchHandler,
    private val searchHandler: SearchHandler,
) {
    private var currentFolder: MailFolder? = null
    private var readingState: ReadingState? = null

    fun newConnection(): List<String> {
        return listOf("* OK IMAP4rev1 Service Ready")
    }

    fun respondTo(message: String): List<String> {
        val readingState = this.readingState
        if (readingState != null) {
            return handleReading(message, readingState)
        }

        return message.split("\r\n").flatMap { line ->
            // If we are not reading body then empty line shouldn't matter to us
            if (line.isEmpty()) return@flatMap emptyList()

            val messageParts = line.split(' ', limit = 3)
            if (messageParts.size < 2) {
                return listOf("* BAD")
            }
            val (tag, commandLower) = messageParts
            val command = commandLower.toUpperCase()
            val args = messageParts.getOrElse(2) { "" }
            respondToCommand(tag, command, args)
        }
    }

    private fun handleReading(message: String, readingState: ReadingState): List<String> {
        val size = message.toBytes().size
        val isEmpty = message == ""
        // After data is over, there's an empty line
        return if (readingState.toRead > 0) {
            readingState.toRead -= size
            listOf()
        } else if (isEmpty && readingState.toRead == 0) {
            this.readingState = null
            listOf(successResponse(readingState.tag, "APPEND"))
        } else {
            listOf("* BAD")
        }
    }

    private fun respondToCommand(tag: String, command: String, args: String): List<String> {
        return when (command) {
            "CAPABILITY" -> {
                listOf("* CAPABILITY IMAP4rev1", successResponse(tag, command))
            }
            "LOGIN" -> {
                listOf(successResponse(tag, command))
            }
            "LIST", "LSUB" -> {
                handleList(args, tag, command)
            }
            "SELECT", "EXAMINE" -> {
                handleSelect(args, tag, command)
            }
            "NOOP" -> listOf(successResponse(tag, command))
            "FETCH" -> {
                requireFolder(tag) { folder ->
                    fetchHandler.handleFetch(folder, args, tag, command)
                }
            }
            "CREATE" -> listOf(successResponse(tag, command))
            "UID" -> {
                // TODO: subcommand parsing is a mess, leave it to the parser

                // for UID FETCH and UID SEARCH, same as normal commands but with UID instead of
                // sequence numbers
                return when {
                    args.startsWith("FETCH ", ignoreCase = true) -> {
                        requireFolder(tag) { folder ->
                            fetchHandler.handleUidFetch(folder, args, tag)
                        }
                    }
                    args.startsWith("search ", ignoreCase = true) -> {
                        requireFolder(tag) { folder ->
                            searchHandler.handleUidSearch(
                                folder,
                                args.substring("search ".length),
                                tag
                            )
                        }
                    }
                    args.startsWith("store ", ignoreCase = true) -> {
                        val effectiveArgs = args.substring("store ".length)
                        handleUidStore(effectiveArgs, tag)
                    }
                    else -> {
                        listOf("$tag BAD $command $args")
                    }
                }
            }
            "LOGOUT" -> listOf(successResponse(tag, command))
            "STATUS" -> {
                handleStatus(args, tag, command)
            }
            // TODO
            "APPEND" -> {
                val appendCommand = appendParser.build()(args)
                this.readingState = ReadingState(tag, appendCommand.literalSize)
                listOf("+")
            }
            "CHECK" -> {
                runBlocking {
                    syncHandler.resync()
                }
                listOf(successResponse(tag, command))
            }
            "CLOSE" -> {
                // Noop for now
                listOf(successResponse(tag, command))
            }
            else -> {
                println("# unknown command '$command'")
                listOf("$tag BAD IDK WHAT THIS MEANS $command")
            }
        }
    }

    private fun handleList(args: String, tag: String, command: String): List<String> {
        // In some cases should return hierarchy delimiter
        val (delimiter, pattern) = this.parseList(args)
        return if (delimiter == "") {
            if (pattern == "") {
                //      An empty ("" string) mailbox name argument is a special request to
                //      return the hierarchy delimiter and the root name of the name given
                //      in the reference.  The value returned as the root MAY be the empty
                //      string if the reference is non-rooted or is an empty string.  In
                //      all cases, a hierarchy delimiter (or NIL if there is no hierarchy)
                //      is returned.  This permits a client to get the hierarchy delimiter
                //      (or find out that the mailbox names are flat) even when no
                //      mailboxes by that name currently exist.
                //
                //      https://tools.ietf.org/html/rfc3501#section-6.3.8
                listOf("""* LIST (\Noselect) "/" """"", successResponse(tag, command))
            } else {
                val folders = mailLoader.folders()
                val folderResponses = folders.mapNotNull { folder ->
                    val name = folder.imapName
                    if (pattern == "*" ||
                        pattern == "%" || // should not match subfolders
                        name.contains(pattern, ignoreCase = true)
                    ) {
                        val flags = mutableListOf("\\HasNoChildren")
                        folder.specialUse?.let { flags.add(it) }
                        "* LIST (${flags.joinToString(" ")}) \"/\" \"$name\""
                    } else {
                        null
                    }
                }
                folderResponses + successResponse(tag, command)
            }
        } else {
            listOf(successResponse(tag, command))
        }
    }

    private fun handleSelect(args: String, tag: String, command: String): List<String> {
        // SELECT and EXAMINE do the same thing - show info about mailbox - but EXAMINE does
        // not reset RECENT flag nor does any other changes
        if (args.isEmpty()) {
            return listOf("$tag BAD NO ARGS FOR SELECT")
        }
        val folder = this.getFolderByImapName(args) ?: return listOf("$tag NO such folder")
        this.currentFolder = folder
        val nextUid = mailLoader.nextUid(folder)
        return listOf(
            """* ${mailLoader.numberOfMailsFor(folder)} EXISTS""",
            """* 0 RECENT""",
            //                            """* OK [UNSEEN 1] Message 1 is first unseen""",
            // we user valid UIDs always
            """* OK [UIDVALIDITY 1] UIDs valid""",
            """* OK [UIDNEXT ${nextUid}] Predicted next UID""",
            """* FLAGS (\Answered \Flagged \Deleted \Seen)""",
            """* OK [PERMANENTFLAGS (\Deleted \Seen \*)] Limited""",
            """$tag OK [READ-WRITE] $command completed""",
        )
    }

    private fun handleStatus(args: String, tag: String, command: String): List<String> {
        val statusCommand = parseStatus(args)
        val folder = getFolderByImapName(statusCommand.folder)
            ?: return listOf("$tag NO ${statusCommand.folder}")
        val attrs = statusCommand.attributes.mapNotNull { attr ->
            val value = when (attr) {
                "MESSAGES" -> this.mailLoader.numberOfMailsFor(folder)
                "UNSEEN" -> this.mailLoader.numberOfMailsFor(folder)
                // Placeholder
                "RECENT" -> 0
                "UIDNEXT" -> return@mapNotNull null
                else -> return@mapNotNull null
            }
            "$attr $value"
        }
        return listOf(
            "* STATUS ${statusCommand.folder} (${attrs.joinToString(" ")})",
            successResponse(tag, command)
        )
    }

    private fun handleUidStore(args: String, tag: String): List<String> {
        val storeCommand = storeCommandParser.build()(args)
        val selectedFolder = currentFolder ?: return listOf("$tag NO INBOX SELECTED")
        if (storeCommand.flags.size != 1 ||
            !storeCommand.flags[0].equals("\\seen", ignoreCase = true)
        ) {
            return listOf("$tag NO not allowed to change flags")
        }
        val mails = when (val id = storeCommand.id) {
            is IdParam.IdSet ->
                id.ids.mapNotNull { mailLoader.mailByUid(selectedFolder, it) }
            is IdParam.ClosedRange ->
                mailLoader.mailsByUid(selectedFolder, id.startId, id.endId)
            is IdParam.OpenRange ->
                return listOf("$tag NO not allowed to store with open range")
        }
        val unread = storeCommand.operation == FlagOperation.REMOVE
        val unreadFlag = if (unread) "" else "\\SEEN"
        val responses = mails.mapIndexed { index, mail ->
            mailLoader.markUnread(mail, unread)
            // TODO: more flags?
            "* ${index + 1} FETCH (UID ${mail.deriveHackyUid()} FLAGS (${unreadFlag}))"
        }
        return responses + successResponse(tag, "store")
    }

    private fun getFolderByImapName(name: String): MailFolder? {
        val canonicalName = name.trim(' ', '"').toUpperCase()
        return this.mailLoader.folders().find { it.imapName == canonicalName }
    }

    private val parseList: (String) -> ListCommand = listCommandParser.build()
    private val parseStatus: (String) -> StatusCommand = statusParser.build()

    private inline fun requireFolder(
        tag: String,
        block: (MailFolder) -> List<String>
    ): List<String> {
        val currentFolder = this.currentFolder
        return if (currentFolder == null) {
            listOf("$tag NO FOLDER SELECTED")
        } else {
            block(currentFolder)
        }
    }
}

private val MailFolder.imapName
    get() = when (this.folderType) {
        MailFolderType.INBOX.value -> "INBOX"
        MailFolderType.SENT.value -> "SENT"
        MailFolderType.DRAFT.value -> "DRAFTS"
        MailFolderType.ARCHIVE.value -> "ARCHIVE"
        MailFolderType.SPAM.value -> "JUNK"
        MailFolderType.TRASH.value -> "TRASH"
        else -> error("Unknown folder type ${this.folderType}")
    }

private val MailFolder.specialUse: String?
    get() = when (this.folderType) {
        MailFolderType.INBOX.value -> null
        MailFolderType.SENT.value -> "\\Sent"
        MailFolderType.DRAFT.value -> "\\Drafts"
        MailFolderType.ARCHIVE.value -> "\\Archive"
        MailFolderType.SPAM.value -> "\\Junk"
        MailFolderType.TRASH.value -> "\\Trash"
        else -> error("Unknown folder type ${this.folderType}")
    }

fun MailLoader.loadMailsByUidParam(
    selectedFolder: MailFolder,
    uidParam: IdParam
): List<Mail> {
    return when (uidParam) {
        is IdParam.IdSet -> {
            uidParam.ids.mapNotNull {
                mailByUid(selectedFolder, it)
            }
        }
        is IdParam.OpenRange -> mailsByUid(selectedFolder, uidParam.startId, null)
        is IdParam.ClosedRange -> mailsByUid(selectedFolder, uidParam.startId, uidParam.endId)
    }
}


fun successResponse(tag: String, command: String) = "$tag OK $command COMPLETED"