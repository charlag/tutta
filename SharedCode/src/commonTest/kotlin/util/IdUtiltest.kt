package util

import com.charlag.tuta.entities.GeneratedId
import com.charlag.tuta.util.timestampToHexGeneratedId
import com.charlag.tuta.util.timestmpToGeneratedId
import kotlin.test.Test
import kotlin.test.assertEquals

class IdUtiltest {
    @Test
    fun timestampToHexGeneratedId() {
        assertEquals("4fc6fbb10000000000", timestampToHexGeneratedId(1370563200000L, 0))
    }

    @Test
    fun `timestampToHexGeneratedId serverId 1`() {
        assertEquals("4fc6fbb10000000001", timestampToHexGeneratedId(1370563200000, 1))
    }

    @Test
    fun timestampToGeneratedId() {
        assertEquals(GeneratedId("IwQvgF------"), timestmpToGeneratedId(1370563200000L, 0))
    }
}