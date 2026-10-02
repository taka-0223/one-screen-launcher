package dev.takaaki.onescreenlauncher.data

object LayoutOps {
    fun swap(slots: List<String?>, from: Int, to: Int): List<String?> {
        if (from !in slots.indices || to !in slots.indices || from == to) return slots
        return slots.toMutableList().also {
            val tmp = it[from]
            it[from] = it[to]
            it[to] = tmp
        }
    }

    fun replace(slots: List<String?>, index: Int, componentKey: String?): List<String?> {
        if (index !in slots.indices) return slots
        val result = slots.toMutableList()
        if (componentKey != null) {
            val existing = result.indexOf(componentKey)
            if (existing >= 0 && existing != index) {
                result[existing] = result[index]
            }
        }
        result[index] = componentKey
        return result
    }
}
