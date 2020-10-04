import com.charlag.tuta.*
import com.charlag.tuta.entities.GENERATED_MAX_ID
import com.charlag.tuta.entities.GENERATED_MIN_ID
import com.charlag.tuta.entities.GeneratedId
import io.ktor.utils.io.core.toByteArray
import kotlin.math.pow
import kotlin.test.Ignore
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

    @Test
    fun testBase64ExtToBase64() {
        val hexPaddedGeneratedId = "4fc6fbb10000000000"
        assertEquals("IwQvgF------", base64ToBase64Ext(hexToBase64(hexPaddedGeneratedId)))
        assertEquals(hexToBase64(hexPaddedGeneratedId), base64ExtToBase64("IwQvgF------"))


        assertEquals(hexToBase64(hexPaddedGeneratedId), base64ExtToBase64("IwQvgF------"))

        // roundtrips
        assertEquals("AA==", base64ExtToBase64(base64ToBase64Ext("AA==")))
        assertEquals("qq8=", base64ExtToBase64(base64ToBase64Ext("qq8=")))
        assertEquals("qqqv", base64ExtToBase64(base64ToBase64Ext("qqqv")))
    }

    @Test
    @Ignore
    fun testGeneratedIdToTimestamp() {
        val i: Long = 1370563200000
        assertEquals(i, generatedIdToTimestamp(GeneratedId("IwQvgF------")))
    }

    @Test
    @Ignore
    fun testGeneratedIdToTimestamp_maxId() {
        val maxTimestamp = (2.toDouble().pow(42) - 1).toLong()
        assertEquals(maxTimestamp, generatedIdToTimestamp(GENERATED_MAX_ID))
    }

    @Test
    @Ignore
    fun testGeneratedIdToTimestamp_minId() {
        assertEquals(0, generatedIdToTimestamp(GENERATED_MIN_ID))
    }
}

fun assertArrayEquals(left: ByteArray, right: ByteArray) {
    assertTrue(left.contentEquals(right), "Expected $left but got $right")
}