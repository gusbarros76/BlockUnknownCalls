package com.gusbarros.blockcalls.data.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PhoneNumberNormalizerTest {

    @Test
    fun `normalize removes +55 country code and formatting`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("+55 11 98765-4321"))
    }

    @Test
    fun `normalize removes parentheses and hyphens`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("(11) 98765-4321"))
    }

    @Test
    fun `normalize leaves already clean number unchanged`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("11987654321"))
    }

    @Test
    fun `normalize removes 55 prefix from long number`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("5511987654321"))
    }

    @Test
    fun `normalize handles empty string`() {
        assertEquals("", PhoneNumberNormalizer.normalize(""))
    }

    @Test
    fun `normalize handles number without country code but with spaces`() {
        assertEquals("11987654321", PhoneNumberNormalizer.normalize("11 98765-4321"))
    }
}
