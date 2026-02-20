package com.gusbarros.blockcalls.data.util

/**
 * Normaliza números de telefone brasileiros para comparação com ContentProvider.
 *
 * Remove caracteres especiais e código de país (+55 ou 55 no início).
 *
 * @param phoneNumber Número no formato original (ex: "+55 11 98765-4321")
 * @return Número normalizado (ex: "11987654321")
 *
 * @sample
 * ```
 * normalize("+55 11 98765-4321") // "11987654321"
 * normalize("(11) 98765-4321")   // "11987654321"
 * ```
 */
object PhoneNumberNormalizer {

    fun normalize(phoneNumber: String): String {
        // Remove todos os caracteres não-numéricos
        val digitsOnly = phoneNumber.replace(Regex("[^0-9]"), "")

        // Remove código de país brasileiro (+55 ou 55 no início)
        // ContentProvider do Android compara números sem código de país
        return when {
            digitsOnly.startsWith("55") && digitsOnly.length > 11 -> {
                // Remove "55" do início
                digitsOnly.substring(2)
            }
            else -> digitsOnly
        }
    }
}
