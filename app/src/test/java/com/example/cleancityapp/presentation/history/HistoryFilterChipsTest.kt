package com.example.cleancityapp.presentation.history

import org.junit.Assert.assertEquals
import org.junit.Test

class HistoryFilterChipsTest {

    @Test
    fun emptyReports_noChips() {
        assertEquals(emptyList<String>(), historyFilterChips(emptyList()))
    }

    @Test
    fun twoUniqueStatuses_noAllChip() {
        val chips = historyFilterChips(listOf("PENDING", "approved", "PENDING"))
        assertEquals(listOf("Approved", "Pending"), chips)
    }

    @Test
    fun moreThanTwoUniqueStatuses_includesAllFirst() {
        val chips = historyFilterChips(listOf("PENDING", "RESOLVED", "REJECTED", "OPEN"))
        assertEquals(listOf("All", "Approved", "Pending", "Rejected"), chips)
    }
}
