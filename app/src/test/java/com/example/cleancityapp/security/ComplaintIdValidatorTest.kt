package com.example.cleancityapp.security

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ComplaintIdValidatorTest {

    @Test
    fun acceptsAlphanumericIds() {
        assertEquals("abc-123_XYZ", ComplaintIdValidator.sanitize("abc-123_XYZ"))
    }

    @Test
    fun rejectsBlankAndNull() {
        assertNull(ComplaintIdValidator.sanitize(null))
        assertNull(ComplaintIdValidator.sanitize(""))
        assertNull(ComplaintIdValidator.sanitize("   "))
    }

    @Test
    fun rejectsPathTraversalAndInjection() {
        assertNull(ComplaintIdValidator.sanitize("../etc/passwd"))
        assertNull(ComplaintIdValidator.sanitize("id;drop"))
        assertNull(ComplaintIdValidator.sanitize("a/b"))
        assertNull(ComplaintIdValidator.sanitize("id with spaces"))
    }

    @Test
    fun rejectsOversizedIds() {
        assertNull(ComplaintIdValidator.sanitize("a".repeat(65)))
    }
}
