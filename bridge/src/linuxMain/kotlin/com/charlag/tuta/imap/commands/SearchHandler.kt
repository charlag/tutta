package com.charlag.tuta.imap.commands

import com.charlag.tuta.entities.tutanota.Mail
import com.charlag.tuta.entities.tutanota.MailFolder
import com.charlag.tuta.imap.*
import deriveHackyUid
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn

class SearchHandler(private val mailLoader: MailLoader) {
    fun handleUidSearch(folder: MailFolder, args: String, tag: String): List<String> {
        val searchCommand = searchCommandParser.build()(args)
        // Placeholder impl which just
        val mails = searchCommand.map { searchEmails(folder, it).toSet() }
            .reduce { acc, list -> acc.union(list) }
            .sortedByDescending { it.deriveHackyUid() }
        val uids = mails.joinToString(" ") { it.deriveHackyUid().toString() }
        return listOf(
            "* SEARCH $uids",
            successResponse(tag, "search"),
        )
    }

    private fun searchEmails(
        selectedFolder: MailFolder,
        searchCriteria: SearchCriteria,
    ): List<Mail> {
        return when (searchCriteria) {
            is SearchCriteria.Id -> TODO()
            is SearchCriteria.Uid -> mailLoader.loadMailsByUidParam(
                selectedFolder,
                searchCriteria.idSet
            )
            is SearchCriteria.Since -> {
                val date = LocalDate(
                    searchCriteria.date.year,
                    searchCriteria.date.month,
                    searchCriteria.date.day
                )
                val uid = date.atStartOfDayIn(TimeZone.currentSystemDefault())
                    .epochSeconds.toInt()
                this.mailLoader.mailsByUid(selectedFolder, uid, null)
            }
        }
    }
}