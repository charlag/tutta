# Unnamed Tutanota client(s)

This is a number unofficial mail client for encrypted service Tutanota.

 * Android client
 * Desktop translator/bridge from Tutanota API to IMAP/SMTP

## Current state

Currently it's alpha-quality so use at your own risk. It may eat your glue and steal your dog.
There is no support from Tutanota team and no one guarantees your safety when using this.

The project started as a Kotlin Multiplatform experiment but quickly focused on Android client
initially. JS/web project was  abandoned because of the poor tooling. It was fixed since then and
it's possible to start with it again.

Bridge has some basic viewing of emails and sending of plain emails. No support for attachments or
operations (like move, read) yet. Has some kind of offline cache.

## Goals for Android

**Goals**:
 - see how far we can get with low effort
 - be a nice-looking, well-behaved native app
 - be secure and try to avoid executing Javascript
 - have offline caching
 
**Non-goals**:
 - implement all the Tutanota web client features
 - implement all apps inside Tutanota (e.g. Calendar)

Current state proves that with limited feature set it is feasible to build a native client.

## TODO for Android
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
 - [x] Forwarding mails
 - [x] Notifications
 - [x] Login experience improvements
 - [x] Icon and name
 - [x] Editing drafts
 - [x] Permanently deleting mails
 - [x] Remembering external content exceptions
 - [x] Mail swipe actions
 - [x] Displaying contact details
 - [x] Additional mail details (recipients, sender, date)
 - [x] Warnings for technical sender
 - [x] Updating permissions
 - [x] Websocket connection follows screen activity
 - [x] Dependency injection
 - [x] Multi-user support
 - [x] Saving password without biometrics
 - [x] Prevent loading unnecessary events on initial sync
 - [x] Inbox rules
 - [ ] Contact autocomplete from system
 - [ ] Viewing inline images
 - [ ] "Secure external" mails
 - [ ] Replying inline
 - [ ] Mail intent handling
 - [ ] Settings
 - [ ] Mail threads
 - [ ] Full-text search
 - [ ] Sharing contacts with system?
 - [ ] UI can be reused with different backends?
 
## TODO for bridge
 - [x] Support for Linux
 - [ ] Support for macOS
 - [ ] Support for Windows
 - [x] Loading of emails and bodies
 - [x] Loading files
 - [x] Sending files
 - [x] Different folders
 - [ ] Subfolders
 - [ ] Creating folders
 - [x] Sync (initial, after startup, scheduled)
 - [ ] Realtime sync via websockets (blocked by ws support on native)
 - [x] Sending encrypted
 - [x] Sending unencrypted
 - [ ] All recipient fields
 - [x] Sending attachments
 - [x] Marking as read/unread
 - [ ] Moving mails
 - [x] Storing credentials in keychain
 - [ ] Reading/writing to sockets async

## License
GPL-3.0-or-later of course