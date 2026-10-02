package dev.takaaki.onescreenlauncher.data

import android.content.Context

class HomeLayoutStore(context: Context) {
    private val prefs = context.getSharedPreferences("home_layout", Context.MODE_PRIVATE)

    var columns: Int
        get() = 4
        set(@Suppress("UNUSED_PARAMETER") value) {
            prefs.edit().putInt(KEY_COLUMNS, 4).apply()
        }

    fun slotCount(@Suppress("UNUSED_PARAMETER") columns: Int): Int = 24

    fun loadSlots(columns: Int, apps: List<LauncherApp>): List<String?> {
        val count = slotCount(columns)
        val key = slotsKey(columns)
        val raw = prefs.getString(key, null)

        if (raw == null) {
            val seeded = seed(apps, count)
            saveSlots(columns, seeded)
            return seeded
        }

        return decode(raw).let { existing ->
            when {
                existing.size == count -> existing
                existing.size < count -> existing + List(count - existing.size) { null }
                else -> existing.take(count)
            }
        }
    }

    fun saveSlots(columns: Int, slots: List<String?>) {
        prefs.edit().putString(slotsKey(columns), encode(slots)).apply()
    }

    private fun seed(apps: List<LauncherApp>, count: Int): List<String?> {
        val preferredPackages = listOf(
            "com.android.vending",
            "com.google.android.play.games",
            "com.google.android.apps.photos",
            "com.facebook.katana",
            "com.facebook.orca",
            "com.spotify.music",
            "jp.ecstudio.chatworkandroid",
            "com.logseq.app",
            "works.jubilee.timetree",
            "com.openai.chatgpt",
            "jp.naver.line.android",
            "com.google.android.apps.maps",
            "com.android.chrome",
            "com.google.android.gm",
            "com.google.android.GoogleCamera",
            "com.android.settings",
        )

        val rank = preferredPackages.withIndex().associate { it.value to it.index }
        val ordered = apps.sortedWith(
            compareBy<LauncherApp> { rank[it.packageName] ?: Int.MAX_VALUE }
                .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label },
        )
        return ordered.take(count).map { it.key } + List((count - ordered.size).coerceAtLeast(0)) { null }
    }

    private fun encode(slots: List<String?>): String = slots.joinToString("\n") { it ?: EMPTY }
    private fun decode(raw: String): List<String?> = raw.split("\n").map { if (it == EMPTY) null else it }
    private fun slotsKey(columns: Int) = "slots_$columns"

    private companion object {
        const val KEY_COLUMNS = "columns"
        const val EMPTY = "-"
    }
}
