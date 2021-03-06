package com.charlag.tuta.posix

import com.charlag.tuta.toBytes
import kotlinx.cinterop.*
import platform.posix.*

inline class Path(val value: String)

fun Path.append(component: String): Path = Path(this.value + "/" + component)

fun Path.exists(): Boolean {
    return access(this.value, F_OK) == 0
}

inline class FileHandle(val fd: Int)

inline fun <T> useFile(path: Path, modes: FileOpenMask, lambda: (FileHandle) -> T): T {
    val fd = open(path.value, modes.value, S_IRWXU).check(::isNonNegative) {
        error("Could not open file $path: $it, ${errorMessage()}")
    }
    try {
        return lambda(FileHandle(fd))
    } finally {
        close(fd)
    }
}

inline class FileOpenMask(val value: Int = 0)

operator fun FileOpenMask.plus(other: FileOpenMask) = FileOpenMask(this.value or other.value)

operator fun FileOpenMask.plus(other: Int) = FileOpenMask(this.value or other)

fun readFile(path: Path): String {
    return useFile(path, FileOpenMask(O_RDONLY)) { handle ->
        val BUF_SIZE = 256
        val buf = ByteArray(BUF_SIZE)
        val readBytes = read(handle.fd, buf.refTo(0), BUF_SIZE.toULong()).check(::isNonNegative) {
            error("Could not read file $path: $it, ${errorMessage()}")
        }
        buf.decodeToString(0, readBytes.toInt())
    }
}

fun isZero(v: Long): Boolean = v == 0L

fun isNonNegative(v: Long): Boolean = v >= 0

inline fun Long.check(predicate: (Long) -> Boolean, handler: (Long) -> Unit): Long {
    if (!predicate(this)) {
        handler(this)
    }
    return this
}

fun isZero(v: Int): Boolean = v == 0

fun isNonNegative(v: Int): Boolean = v >= 0

inline fun Int.check(predicate: (Int) -> Boolean, handler: (Int) -> Unit): Int {
    if (!predicate(this)) {
        handler(this)
    }
    return this
}

fun writeFile(path: Path, content: String) {
    useFile(path, FileOpenMask(O_RDWR) + O_CREAT + O_TRUNC) { handle ->
        val buf = content.toBytes()
        write(handle.fd, buf.refTo(0), buf.size.toULong()).check(::isNonNegative) {
            error("Could not write file $path: $it, ${errorMessage()}")
        }
    }
}

inline fun errorMessage() = strerror(errno)?.toKString()

fun ensureDir(path: Path) {
    if (!path.exists()) {
        mkdir(path.value, S_IRWXU.toUInt()).check(::isZero) {
            error("Could not create director ${path.value}: ${errorMessage()}")
        }
    }
}

fun getEnvironmentVariable(name: String): String? {
    return getenv(name)?.toKString()
}

/**
 * Reads password from STDIN with echo (output each typed character) disabled.
 * see [this SO answer](https://stackoverflow.com/questions/1413445/reading-a-password-from-stdcin).
 */
fun readPassword(): String? {
    memScoped {
        // Disable echo
        val tty = alloc<termios>()
        tcgetattr(STDIN_FILENO, tty.ptr)
        tty.c_lflag = tty.c_lflag and ECHO.toUInt().inv()
        tcsetattr(STDIN_FILENO, TCSANOW, tty.ptr)

        val pw = readLine()

        // Enable echo again
        tty.c_lflag = tty.c_lflag or ECHO.toUInt()
        tcsetattr(STDIN_FILENO, TCSANOW, tty.ptr)

        return pw
    }
}