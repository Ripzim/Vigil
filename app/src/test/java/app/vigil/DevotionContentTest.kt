package app.vigil

import app.vigil.feature.devotion.DevotionContent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DevotionContentTest {
    @Test fun keysAreUnique() {
        val keys = DevotionContent.readings.map { it.key }
        assertEquals(keys.size, keys.toSet().size)
    }

    @Test fun everyReadingIsComplete() {
        DevotionContent.readings.forEach {
            assertTrue(it.key, it.reference.isNotBlank())
            assertTrue(it.key, it.context.isNotBlank())
            assertTrue(it.key, it.reflection.isNotBlank())
            assertTrue(it.key, it.prayer.isNotBlank())
        }
    }

    @Test fun lookupByKey() {
        assertEquals("Salmo 51", DevotionContent.byKey("sl51")?.reference)
    }
}
