package dev.takaaki.onescreenlauncher.ui

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import dev.takaaki.onescreenlauncher.data.LauncherApp

private object IconCache {
    private val cache = LruCache<String, Bitmap>(80)

    fun get(app: LauncherApp): Bitmap {
        cache.get(app.key)?.let { return it }
        val size = (56 * Resources.getSystem().displayMetrics.density).toInt().coerceAtLeast(1)
        val bitmap = app.info.getBadgedIcon(Resources.getSystem().displayMetrics.densityDpi)
            .toBitmap(size, size, Bitmap.Config.ARGB_8888)
        cache.put(app.key, bitmap)
        return bitmap
    }
}

@Composable
fun AppIcon(app: LauncherApp, modifier: Modifier = Modifier) {
    val bitmap = remember(app.key) { IconCache.get(app).asImageBitmap() }
    Image(bitmap = bitmap, contentDescription = app.label, modifier = modifier)
}
