package app.vigil

import app.vigil.core.util.DomainNormalizer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DomainNormalizerTest {
    @Test fun plainDomain() = assertEquals("exemplo.com", DomainNormalizer.normalize("exemplo.com"))

    @Test fun stripsSchemePathWwwAndCase() =
        assertEquals("exemplo.com.br", DomainNormalizer.normalize("  HTTPS://WWW.Exemplo.com.br/caminho?x=1#a "))

    @Test fun stripsPortAndUserInfo() =
        assertEquals("exemplo.com", DomainNormalizer.normalize("http://user@exemplo.com:8080/"))

    @Test fun rejectsEmptyAndSingleLabel() {
        assertNull(DomainNormalizer.normalize(""))
        assertNull(DomainNormalizer.normalize("   "))
        assertNull(DomainNormalizer.normalize("localhost"))
    }

    @Test fun rejectsIpAndInvalidChars() {
        assertNull(DomainNormalizer.normalize("192.168.0.1"))
        assertNull(DomainNormalizer.normalize("a b.com"))
        assertNull(DomainNormalizer.normalize("-ruim.com"))
    }
}
