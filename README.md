# Unnamed Tutanota client

This is unofficial mail client for encrypted service Tutanota.

## Current state

Currently it's alpha-quality so use at your own risk. It may eat your glue and steal your dog.
There is no support from Tutanota team and no one guarantees your safety when using this.

The project started as a Kotlin Multiplatform experiment but quickly focused on Android client.
A lot of important code (auth, api, parsing/mapping) is still multiplatform and theoretically it's
possible to develop another platform further, also some code which resides in Android part currently
can be extracted into the common part.

JS/web project is currently abandoned because of the poor support on the Kotlin side: bundling
is not automatic, hooking up sourcemaps is nontrivial. It might be that I did some things wrong but
examples are few and between. Quality of generated JS is also quite poor so until promised compiler 
backend rewrite comes it's not feasible to touch it.

## Goals

**Goals**:
 - see how far we can get with low effort
 - be a nice-looking, well-behaved native app
 - be secure and try to avoid executing Javascript
 - have offline caching
 
**Non-goals**:
 - implement all the Tutanota web client features
 - implement all apps inside Tutanota (e.g. Calendar)

Current state proves that with limited feature set it is feasible to build a native client.

## TODO
 - [x] Login
 - [x] Login with TOTP
 - [x] Saving password with biometrics
 - [x] Viewing mails
 - [x] External content blocking
 - [x] Downloading files
 - [x] Viewing mails offline
 - [x] Simple mail search
 - [x] Moving/deleting mails
 - [x] Marking mails as read/unread, manually and automatically
 - [x] Contact list
 - [x] Writing emails
 - [x] Sending files
 - [x] Replying to emails
 - [x] Replying including content
 - [x] Sending mails in background
 - [x] Saving drafts
 - [x] Contact autocomplete from Tutanota
 - [x] Mail counters
 - [x] Some real-time updates (mail/counter/contact/folder)
 - [] Notifications
 - [] Forwarding mails
 - [] Login experience improvements
 - [] Editing drafts
 - [] Viewing inline images
 - [] Icon and name
 - [] Settings
 - [] Displaying contact details
 - [] Contact autocomplete from system
 - [] Remembering external content exceptions
 - [] Permanently deleting mails
 - [] Mail swipe actions
 - [] Additional mail details (recipients, sender, date)
 - [] Warnings for technical sender
 - [] Updating permissions/mail body session key cache
 - [] Websocket connection follows screen activity
 - [] Dependency injection
 - [] Multi-user support
 - [] Mail threads
 - [] Saving password without biometrics
 - [] Full-text search
 - [] Sharing contacts with system?

## License
GPL-3.0-or-later of course