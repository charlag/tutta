package com.charlag.tuta

import com.charlag.tuta.posix.check
import com.charlag.tuta.posix.errorMessage
import com.charlag.tuta.posix.isZero
import kotlinx.cinterop.*
import org.sqlite.*
import platform.posix.stat

class SqliteDb(path: String) {
    private val arena = Arena()
    private val db = arena.alloc<CPointerVar<sqlite3>>()

    init {
        sqlite3_open(path, db.ptr).check(::isZero) {
            throw DbException("Could not open dv ${errorMessage()}")
        }
    }

    fun exec(sql: String) {
        this.execSqliteStatement(this.db, sql)
    }

    fun insert(sql: String, vararg args: Any) {
        memScoped {
            val statement = allocStatement(db, sql).apply { bindArgs(args) }
            sqlite3_step(statement.statement)
                .check({ it == SQLITE_OK || it == SQLITE_DONE }) {
                    throw DbException("step of $sql failed: $it ${errorMessage()}")
                }
        }
    }

    fun close() {
        sqlite3_close(db.value)
    }

    private fun execSqliteStatement(db: CPointerVar<sqlite3>, statement: String) {
        memScoped {
            val errMessagePointer = allocPointerTo<ByteVar>()
            println("db: $statement")
            sqlite3_exec(
                db.value,
                statement,
                null,
                null,
                errMessagePointer.ptr
            ).check({ it == SQLITE_OK || it == SQLITE_DONE }) {
                val msg = errMessagePointer.value!!.toKString()
                println("Sqlite error: $msg")
                sqlite3_free(errMessagePointer.value)
            }
        }
    }

    fun <T> queryMultiple(query: String, vararg args: Any, transformer: Cursor.() -> T): List<T> {
        return memScoped {
            val statement = allocStatement(db, query).apply { bindArgs(args) }
            val cursor = Cursor(statement.statement)
            val results = mutableListOf<T>()
            cursor.iterate {
                results += transformer(this)
                true
            }
            results
        }
    }


    fun <T> querySingle(query: String, vararg args: Any, transformer: Cursor.() -> T): T? {
        return memScoped {
            val statement = allocStatement(db, query).apply { bindArgs(args) }
            val cursor = Cursor(statement.statement)
            var result: T? = null
            cursor.iterate {
                result = transformer(this)
                false
            }
            result
        }
    }


    private inline fun Int.sqlOk(op: String) = check({ it == SQLITE_OK }) {
        val sqlMessage = errorMessage()
        throw DbException("$op failed: $it, ${sqlMessage}")
    }

    private fun errorMessage() = sqlite3_errmsg(db.value)?.toKString()


    fun SqlStatement.bindInt(at: Int, value: Int) = sqlite3_bind_int(statement, at, value)
        .sqlOk("bindInt")

    fun SqlStatement.bindBlob(at: Int, value: ByteArray): Int {
        // SQLITE_TRANSIENT tells SQLite to make a copy of the data immediately which is something
        // that we would like to do to avoid memory corruption.
        return sqlite3_bind_blob(statement, at, value.refTo(0), value.size, SQLITE_TRANSIENT)
            .sqlOk("bindBlob")
    }

    fun SqlStatement.bindText(at: Int, value: String): Int {
        // SQLITE_TRANSIENT tells SQLite to make a copy of the data immediately which is something
        // that we would like to do to avoid memory corruption.
        // Fourth argument can be either negative and then text is read up to the first null terminator
        // or it can be offset where null terminator would occur.
        return sqlite3_bind_text(statement, at, value, value.toBytes().size, SQLITE_TRANSIENT)
            .sqlOk("bindText")
    }

    private fun MemScope.allocStatement(db: CPointerVar<sqlite3>, sql: String): SqlStatement {
        val statementHandle = allocPointerTo<sqlite3_stmt>()
        sqlite3_prepare_v2(db.value, sql, sql.toBytes().size, statementHandle.ptr, null)
            .sqlOk("Preparing")
        defer { sqlite3_finalize(statementHandle.value) }
        return SqlStatement(statementHandle.value!!)
    }

    fun SqlStatement.bindArgs(args: Array<out Any>) {
        args.forEachIndexed { index, value ->
            // "The leftmost SQL parameter has an index of 1."
            val statementIndex = index + 1
            when (value) {
                is Int -> bindInt(statementIndex, value)
                is ByteArray -> bindBlob(statementIndex, value)
                is String -> bindText(statementIndex, value)
                else -> throw IllegalArgumentException("Invalid type to bind: ${value::class}")
            }
        }
    }
}

inline class Cursor(val statement: CPointer<sqlite3_stmt>)

private inline fun Cursor.iterate(block: Cursor.() -> Boolean) {
    while (true) {
        when (val code = sqlite3_step(statement)) {
            SQLITE_DONE -> break
            SQLITE_ROW -> {
            }
            else -> throw DbException("Step failed: $code ${errorMessage()}")
        }
        if (!block(this)) {
            break
        }
    }
}

fun Cursor.readInt(at: Int): Int = sqlite3_column_int(statement, at)

fun Cursor.readBlob(at: Int): ByteArray {
    val bytesPtr = sqlite3_column_blob(statement, at)
    val dataSize = sqlite3_column_bytes(statement, at)
    return bytesPtr!!.readBytes(dataSize)
}

fun Cursor.readString(at: Int): String {
    val ptr: CPointer<ByteVar> = sqlite3_column_text(statement, at)!!.reinterpret()
    return ptr.toKString()
}

inline class SqlStatement(val statement: CPointer<sqlite3_stmt>)

class DbException(message: String) : Exception(message)
