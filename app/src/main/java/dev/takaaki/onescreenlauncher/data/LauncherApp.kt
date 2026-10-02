package dev.takaaki.onescreenlauncher.data

import android.content.ComponentName
import android.content.pm.LauncherActivityInfo

data class LauncherApp(
    val info: LauncherActivityInfo,
) {
    val component: ComponentName get() = info.componentName
    val key: String get() = component.flattenToString()
    val label: String get() = info.label?.toString().orEmpty().ifBlank { component.packageName }
    val packageName: String get() = component.packageName
}
