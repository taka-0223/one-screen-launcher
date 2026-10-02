package dev.takaaki.onescreenlauncher.data

import org.junit.Assert.assertEquals
import org.junit.Test

class LayoutOpsTest {
    @Test
    fun swap_keepsEmptySlotsAndPositionsStable() {
        val slots = listOf("A", null, "C")
        assertEquals(listOf(null, "A", "C"), LayoutOps.swap(slots, 0, 1))
    }

    @Test
    fun replace_existingApp_swapsInsteadOfDuplicating() {
        val slots = listOf("A", "B", "C")
        assertEquals(listOf("B", "A", "C"), LayoutOps.replace(slots, 0, "B"))
    }
}
