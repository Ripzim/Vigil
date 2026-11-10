package app.vigil.core.util

import java.net.IDN

/** Normaliza e valida entradas de domínio digitadas pelo usuário. */
object DomainNormalizer {
    private val labelRegex = Regex("^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?$")

    /**
     * Aceita "exemplo.com", "https://www.Exemplo.com/caminho?x=1" etc.
     * Retorna o domínio em minúsculas, sem esquema, caminho, porta nem "www.",
     * ou null se a entrada não for um domínio válido.
     */
    fun normalize(raw: String): String? {
        var s = raw.trim().lowercase()
        if (s.isEmpty()) return null
        s = s.substringAfter("://")
        s = s.substringBefore('/').substringBefore('?').substringBefore('#')
        s = s.substringAfter('@')
        s = s.substringBefore(':')
        s = s.trimEnd('.')
        if (s.startsWith("www.")) s = s.removePrefix("www.")
        if (s.isEmpty()) return null

        val ascii = try {
            IDN.toASCII(s)
        } catch (e: IllegalArgumentException) {
            return null
        }
        if (ascii.length > 253) return null

        val labels = ascii.split('.')
        if (labels.size < 2) return null
        if (!labels.all { labelRegex.matches(it) }) return null
        val tld = labels.last()
        if (tld.length < 2 || tld.all { it.isDigit() }) return null
        return ascii
    }
}
