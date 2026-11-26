package com.dante2194.okalastores.util

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {

    @Test
    fun persianDigitsAreConverted() {
        assertEquals("۱۲۳", "123".toPersianDigits())
        assertEquals("۴۰", 40.toPersianDigits())
    }

    @Test
    fun distanceIsFormattedInPersian() {
        assertEquals("—", formatDistance(null))
        assertEquals("۵۰۰ متر", formatDistance(0.5))
        assertEquals("۱۲.۵ کیلومتر", formatDistance(12.5))
    }

    @Test
    fun rialIsConvertedToToman() {
        assertEquals("رایگان", formatToman(0.0, freeLabel = true))
        assertEquals("—", formatToman(null))
    }

    @Test
    fun ratingHandlesMissingValues() {
        assertEquals("جدید", formatRating(0.0))
        assertEquals("۴.۵", formatRating(4.5))
    }

    @Test
    fun etaSortKeyHandlesPersianDigitsAndClosedStores() {
        assertEquals(12, etaSortKey("۱۲ دقیقه"))
        assertEquals(Int.MAX_VALUE, etaSortKey("بسته است"))
        assertEquals(Int.MAX_VALUE, etaSortKey(null))
    }
}
