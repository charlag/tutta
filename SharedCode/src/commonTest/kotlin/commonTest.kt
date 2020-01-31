import com.charlag.tuta.base64ToBytes
import com.charlag.tuta.toBase64
import io.ktor.utils.io.core.toByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class Base64Test {
    @Test
    fun testBase64ToBytes() {
        assertArrayEquals(
            "Kotlin is awesome".toByteArray(),
            base64ToBytes("S290bGluIGlzIGF3ZXNvbWU=")
        )
    }

    @Test
    fun testBytesToBase64() {
        assertEquals("S290bGluIGlzIGF3ZXNvbWU=", "Kotlin is awesome".toByteArray().toBase64())
    }

}

fun assertArrayEquals(left: ByteArray, right: ByteArray) {
    assertTrue(left.contentEquals(right), "Expected $left but got $right")
}