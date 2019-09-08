
@JsModule("bCrypt")
@JsNonModule
external class Bcrypt {
    fun crypt_raw(password: Array<Byte>, salt: Array<Byte>, logRounds: Int): Array<Byte>
}
