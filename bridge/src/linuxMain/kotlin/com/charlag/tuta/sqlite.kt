package com.charlag.tuta

import com.charlag.tuta.posix.check
import com.charlag.tuta.posix.isZero
import kotlinx.cinterop.*
import org.sqlite.*

// TODO; decouple from any concrete type/table.
class Db(path: String) {
    private val arena = Arena()
    private val db = arena.alloc<CPointerVar<sqlite3>>()

    init {
        sqlite3_open(path, db.ptr).check(::isZero) {
            error("Could not open dv")
        }
        execSqliteStatement(
            db,
            "CREATE TABLE IF NOT EXISTS Mail(uid INTEGER PRIMARY KEY, data BLOB);"
        )
    }

    fun write(uid: Int, bytes: ByteArray) {
        memScoped {
            val zSql = "INSERT INTO Mail(uid, data) VALUES (?, ?)"
            val statementHandle = allocPointerTo<sqlite3_stmt>()
            sqlite3_prepare_v2(db.value, zSql, zSql.toBytes().size, statementHandle.ptr, null)
                .sqlOk("Preparing")

            sqlite3_bind_int(statementHandle.value, 1, uid)
                .sqlOk("bind 1")

            bytes.usePinned {
                sqlite3_bind_blob(statementHandle.value, 2, bytes.refTo(0), bytes.size, null)
                    .sqlOk("bind 2")
            }

            sqlite3_step(statementHandle.value)
                .check({ it == SQLITE_OK || it == SQLITE_DONE }) {
                    error("step of $zSql faile: $it")
                }
            sqlite3_finalize(statementHandle.value)
        }
    }

    fun readSingle(uid: Int): ByteArray? {
        memScoped {
            val zSql = "SELECT uid, data FROM Mail WHERE uid = ? LIMIT 1"
            val statementHandle = allocPointerTo<sqlite3_stmt>()
            defer { sqlite3_finalize(statementHandle.value) }

            sqlite3_prepare_v2(db.value, zSql, zSql.toBytes().size, statementHandle.ptr, null)
                .sqlOk("Preparing SELECT")
            sqlite3_bind_int(statementHandle.value, 1, uid).sqlOk("bind UID")

            when (val code = sqlite3_step(statementHandle.value)) {
                SQLITE_DONE -> return null
                SQLITE_ROW -> {
                }
                else -> error("Step of '$zSql' failed: $code")
            }
            val bytesPtr = sqlite3_column_blob(statementHandle.value, 1)
            val dataSize = sqlite3_column_bytes(statementHandle.value, 1)
            return bytesPtr?.readBytes(dataSize)
        }
    }


    fun readMultiple(fromUid: Int, toId: Int?): List<ByteArray> {
        memScoped {
            val statementHandle: CPointerVarOf<CPointer<sqlite3_stmt>>
            if (toId != null) {
                val zSql = "SELECT uid, data FROM Mail WHERE UID >= ? AND UID <= ?"
                statementHandle = allocPointerTo<sqlite3_stmt>()
                sqlite3_prepare_v2(db.value, zSql, zSql.toBytes().size, statementHandle.ptr, null)
                    .sqlOk("Preparing")
                sqlite3_bind_int(statementHandle.value, 1, fromUid)
                sqlite3_bind_int(statementHandle.value, 2, toId)

            } else {
                val zSql = "SELECT uid, data FROM Mail WHERE UID >= ?"
                statementHandle = allocPointerTo<sqlite3_stmt>()
                sqlite3_prepare_v2(db.value, zSql, zSql.toBytes().size, statementHandle.ptr, null)
                    .sqlOk("Preparing")
                sqlite3_bind_int(statementHandle.value, 1, fromUid)
            }
            val expandedSql = sqlite3_expanded_sql(statementHandle.value)
            println("DB: Multiple: ${expandedSql?.toKString()}")
            sqlite3_free(expandedSql)
            defer { sqlite3_finalize(statementHandle.value) }
            sqlite3_bind_int(statementHandle.value, 1, fromUid)

            val result = mutableListOf<ByteArray>()
            while (true) {
                when (val code = sqlite3_step(statementHandle.value)) {
                    SQLITE_DONE -> break
                    SQLITE_ROW -> {
                    }
                    else -> error("Step failed: $code")
                }
                val bytesPtr = sqlite3_column_blob(statementHandle.value, 1)
                val dataSize = sqlite3_column_bytes(statementHandle.value, 1)
                val readBytes = bytesPtr!!.readBytes(dataSize)
                result += readBytes
            }
            return result
        }
    }

    fun count(): Int {
        memScoped {
            val zSql = "SELECT COUNT (uid) FROM Mail"
            val statementHandle = allocPointerTo<sqlite3_stmt>()
            sqlite3_prepare_v2(db.value, zSql, zSql.toBytes().size, statementHandle.ptr, null)
                .sqlOk("Preparing")
            defer { sqlite3_finalize(statementHandle.value) }

            when (val code = sqlite3_step(statementHandle.value)) {
                SQLITE_ROW, SQLITE_DONE -> {
                }
                else -> error("Step of '$zSql' failed: $code")
            }
            return sqlite3_column_int(statementHandle.value, 0)
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
}

private inline fun Int.sqlOk(op: String) = check({ it == SQLITE_OK }) { error("$op failed: $it") }