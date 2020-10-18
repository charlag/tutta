package com.charlag.tuta.libsecret

import com.charlag.tuta.setAt
import kotlinx.cinterop.*
import org.libsecret.*

fun MemScope.SecretSchema(
    schemaName: String,
    flags: SecretSchemaFlags,
    attributes: Map<String, SecretSchemaAttributeType>
): SecretSchema {
    val nullAttr = cValue<SecretSchemaAttribute> {
        name = "NULL".cstr.ptr
        type = SecretSchemaAttributeType.byValue(0)
    }
    val libAttrs = attributes.map { (name, type) ->
        cValue<SecretSchemaAttribute> {
            this.name = name.cstr.ptr
            this.type = type
        }
    } + nullAttr

    return alloc {
        this.name = schemaName.cstr.ptr
        this.flags = flags
        libAttrs.forEachIndexed { index, attr ->
            this.attributes.setAt(index, attr)
        }
    }
}

fun storeSecretPasswordSync(
    schema: SecretSchema,
    collection: String,
    label: String,
    password: String,
    attrs: Map<String, Any>
) {
    memScoped {
        val error = allocPointerTo<GError>()
        secret_password_storev_sync(
            schema.ptr,
            prepareAttributesHash(attrs),
            collection,
            label,
            password,
            cancellable = null,
            error.ptr,
        )
        val errorValue = error.pointed
        if (errorValue != null) {
            val message = errorValue.message?.toKStringFromUtf8() ?: "unknown error"
            g_error_free(error.value)
            error(message)
        }
    }
}

fun lookupSecretPasswordSync(schema: SecretSchema, attrs: Map<String, Any>): String? {
    memScoped {
        val error = allocPointerTo<GError>()
        val attributes = prepareAttributesHash(attrs)
        val pw = secret_password_lookupv_sync(
            schema.ptr,
            cancellable = null,
            attributes = attributes,
            error = error.ptr,
        )
        error.pointed?.let {
            val message = it.message?.toKString()
            g_error_free(error.value)
            error("Error looking up pw $message")
        }
        return pw?.toKStringFromUtf8()?.also { secret_password_free(pw) }
    }
}

private fun MemScope.prepareAttributesHash(attrs: Map<String, Any>): CPointer<GHashTable> {
    val table = g_hash_table_new(staticCFunction(::g_str_hash), staticCFunction(::g_str_equal))!!
    defer { g_hash_table_destroy(table) }
    for ((k, v) in attrs.entries) {
        // We are allocating vars and throwing them away but it's okay because they will stay
        // until the end of scope so their pointers are safe.
        val preparedValue = when (v) {
            is Int -> alloc<IntVar> { value = v }.ptr
            is Boolean -> alloc<BooleanVar> { value = v }.ptr
            is String -> v.cstr.ptr
            else -> throw IllegalArgumentException("Attribute of invalid type: ${v::class}")
        }
        g_hash_table_insert(table, k.cstr.ptr, preparedValue)
    }
    return table
}
