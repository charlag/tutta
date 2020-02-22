import com.charlag.tuta.network.mapping.NestedMapper
import com.charlag.tuta.entities.ByteArraySerializer
import com.charlag.tuta.entities.Entity
import kotlinx.serialization.Serializable
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class SaltTest(
    val somethingElse: Int,
    @Serializable(with = ByteArraySerializer::class)
    val salt: ByteArray,
    val someList: List<Int>? = null
) : Entity()

@Serializable
data class NestedContainer(
    val saltTest: SaltTest
)

@Ignore // failing because deserialization is buggy with inheritance
class NestedMapperTest {
    val mapper = NestedMapper()

    @Test
    fun testMapInput() {
        val salt = byteArrayOf(1, 2, 3)
        val map = mapOf("somethingElse" to 1, "salt" to salt)
        val result = mapper.unmap(SaltTest.serializer(), map)
        assertEquals(SaltTest(somethingElse = 1, salt = salt), result)
    }

    @Test
    fun testMapOutput() {
        val salt = byteArrayOf(1, 2, 3)
        val testObject = SaltTest(somethingElse = 1, salt = salt)
        val result = mapper.map(SaltTest.serializer(), testObject)
        assertEquals(mapOf("somethingElse" to 1, "salt" to salt, "someList" to null), result)
    }

    @Test
    fun testNested() {
        val salt = byteArrayOf(1, 2, 3)
        val value = NestedContainer(SaltTest(1, salt, listOf(1, 2, 3)))
        val serialized = mapper.map(NestedContainer.serializer(), value)
        assertEquals(
            mapOf(
                "saltTest" to mapOf(
                    "somethingElse" to 1,
                    "salt" to salt,
                    "someList" to listOf(1, 2, 3)
                )
            ), serialized
        )
        val deserialized: NestedContainer = mapper.unmap(NestedContainer.serializer(), serialized)
        assertEquals(deserialized, value)
    }
}