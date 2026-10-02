package dev.takaaki.onescreenlauncher

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed interface UpdateCheckResult {
    data class Available(
        val versionName: String,
        val apkUrl: String,
        val notes: String?,
    ) : UpdateCheckResult

    data object UpToDate : UpdateCheckResult
    data object Failed : UpdateCheckResult
}

object UpdateChecker {
    private const val LATEST_RELEASE_URL =
        "https://api.github.com/repos/taka-0223/one-screen-launcher/releases/latest"
    private const val APK_ASSET_NAME = "one-screen-launcher.apk"

    suspend fun check(currentVersionName: String): UpdateCheckResult = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(LATEST_RELEASE_URL).openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 6_000
                readTimeout = 6_000
                setRequestProperty("Accept", "application/vnd.github+json")
                setRequestProperty("User-Agent", "One-Screen-Launcher")
            }

            connection.use {
                if (it.responseCode != HttpURLConnection.HTTP_OK) {
                    return@runCatching UpdateCheckResult.Failed
                }

                val json = JSONObject(it.inputStream.bufferedReader().use { reader -> reader.readText() })
                val latestVersion = json.optString("tag_name").removePrefix("v")
                if (latestVersion.isBlank() || compareVersions(latestVersion, currentVersionName) <= 0) {
                    return@runCatching UpdateCheckResult.UpToDate
                }

                val assets = json.optJSONArray("assets")
                var apkUrl: String? = null
                if (assets != null) {
                    for (index in 0 until assets.length()) {
                        val asset = assets.optJSONObject(index) ?: continue
                        if (asset.optString("name") == APK_ASSET_NAME) {
                            apkUrl = asset.optString("browser_download_url").takeIf(String::isNotBlank)
                            break
                        }
                    }
                }

                if (apkUrl == null) {
                    UpdateCheckResult.Failed
                } else {
                    UpdateCheckResult.Available(
                        versionName = latestVersion,
                        apkUrl = apkUrl,
                        notes = json.optString("body").takeIf(String::isNotBlank),
                    )
                }
            }
        }.getOrElse { UpdateCheckResult.Failed }
    }

    private fun compareVersions(left: String, right: String): Int {
        val a = left.split('.').map { it.toIntOrNull() ?: 0 }
        val b = right.split('.').map { it.toIntOrNull() ?: 0 }
        val size = maxOf(a.size, b.size)
        for (index in 0 until size) {
            val av = a.getOrElse(index) { 0 }
            val bv = b.getOrElse(index) { 0 }
            if (av != bv) return av.compareTo(bv)
        }
        return 0
    }

    private inline fun <T> HttpURLConnection.use(block: (HttpURLConnection) -> T): T =
        try {
            block(this)
        } finally {
            disconnect()
        }
}
