/*
 * Copyright (C) 2021-2026 The FlorisBoard Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.patrickgold.florisboard.app.settings.theme

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness2
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.patrickgold.florisboard.R
import dev.patrickgold.florisboard.app.FlorisPreferenceStore
import dev.patrickgold.florisboard.app.LocalNavController
import dev.patrickgold.florisboard.app.Routes
import dev.patrickgold.florisboard.app.apptheme.ScreenHorizontalPadding
import dev.patrickgold.florisboard.app.components.SegmentedListTitle
import dev.patrickgold.florisboard.app.enumDisplayEntriesOf
import dev.patrickgold.florisboard.ime.extension.ExtensionComponentName
import dev.patrickgold.florisboard.ime.theme.LocalThemeController
import dev.patrickgold.florisboard.ime.theme.PreferredThemeMode
import dev.patrickgold.florisboard.ime.theme.ThemeExtension
import dev.patrickgold.florisboard.lib.compose.FlorisScreenNg
import dev.patrickgold.jetpref.datastore.model.collectAsState
import dev.patrickgold.jetpref.datastore.ui.ColorPickerPreference
import dev.patrickgold.jetpref.datastore.ui.ListPreference
import dev.patrickgold.jetpref.datastore.ui.LocalTimePickerPreference
import dev.patrickgold.jetpref.datastore.ui.Preference
import dev.patrickgold.jetpref.datastore.ui.isMaterialYou
import org.florisboard.lib.compose.stringRes
import org.florisboard.lib.snygg.color.ColorMappings

@Composable
fun ThemeScreen() {
    val prefs by FlorisPreferenceStore
    val context = LocalContext.current
    val navController = LocalNavController.current
    val themeController = LocalThemeController.current

    val themeIndex by themeController.effectiveThemeIndex.collectAsState()
    val mode by prefs.theme.mode.collectAsState()
    val dayThemeId by prefs.theme.dayThemeId.collectAsState()
    val nightThemeId by prefs.theme.nightThemeId.collectAsState()

    @Composable
    fun rememberThemeName(themeId: ExtensionComponentName): String {
        return remember(themeId, themeIndex) {
            themeIndex.extensions[themeId.extensionId]?.manifest?.themes
                ?.first { it.id == themeId.componentId }
                ?.name
                ?: themeId.toString()
        }
    }

    FlorisScreenNg(
        title = stringRes(R.string.settings__theme__title),
        previewFieldVisible = true,
    ) { contentPadding ->
//        context.launchUrl("https://${BuildConfig.FLADDONS_STORE_URL}/")
//        Icons.Filled.Add,
//        Icons.Default.Shop,
//        Icons.AutoMirrored.Filled.Input,
        LazyColumn(
            modifier = Modifier.padding(horizontal = ScreenHorizontalPadding),
            contentPadding = contentPadding,
        ) {
            val countOffset = if (mode == PreferredThemeMode.FOLLOW_TIME) 2 else 0
            val staticCount = 4 + countOffset
            item {
                ListPreference(
                    prefs.theme.mode,
                    shapes = ListItemDefaults.segmentedShapes(0, staticCount),
                    icon = Icons.Default.BrightnessAuto,
                    title = stringRes(R.string.pref__theme__mode__label),
                    entries = enumDisplayEntriesOf(PreferredThemeMode::class),
                )
            }
            if (mode == PreferredThemeMode.FOLLOW_TIME) {
                item {
                    LocalTimePickerPreference(
                        prefs.theme.sunriseTime,
                        modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap),
                        shapes = ListItemDefaults.segmentedShapes(1, staticCount),
                        title = stringRes(R.string.pref__theme__sunrise_time__label),
                        icon = Icons.Default.WbTwilight,
                    )
                }
                item {
                    LocalTimePickerPreference(
                        prefs.theme.sunsetTime,
                        modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap),
                        shapes = ListItemDefaults.segmentedShapes(2, staticCount),
                        title = stringRes(R.string.pref__theme__sunset_time__label),
                        icon = Icons.Default.Brightness2,
                    )
                }
            }
            item {
                Preference(
                    modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap),
                    shapes = ListItemDefaults.segmentedShapes(1 + countOffset, staticCount),
                    icon = Icons.Default.LightMode,
                    title = stringRes(R.string.pref__theme__day),
                    summary = rememberThemeName(dayThemeId),
                    enabledIf = { prefs.theme.mode isNotEqualTo PreferredThemeMode.ALWAYS_NIGHT },
                    onClick = {
                        navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_DAY))
                    },
                )
            }
            item {
                Preference(
                    modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap),
                    shapes = ListItemDefaults.segmentedShapes(2 + countOffset, staticCount),
                    icon = Icons.Default.DarkMode,
                    title = stringRes(R.string.pref__theme__night),
                    summary = rememberThemeName(nightThemeId),
                    enabledIf = { prefs.theme.mode isNotEqualTo PreferredThemeMode.ALWAYS_DAY },
                    onClick = {
                        navController.navigate(Routes.Settings.ThemeManager(ThemeManagerScreenAction.SELECT_NIGHT))
                    },
                )
            }
            item {
                ColorPickerPreference(
                    pref = prefs.theme.accentColor,
                    modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap, bottom = 16.dp),
                    shapes = ListItemDefaults.segmentedShapes(3 + countOffset, staticCount),
                    title = stringRes(R.string.pref__theme__theme_accent_color__label),
                    defaultValueLabel = stringRes(R.string.action__default),
                    icon = Icons.Default.ColorLens,
                    defaultColors = ColorMappings.colors,
                    showAlphaSlider = false,
                    enableAdvancedLayout = true,
                    colorOverride = {
                        if (it.isMaterialYou(context)) {
                            Color.Unspecified
                        } else {
                            it
                        }
                    },
                )
            }

            val extensions = themeIndex.extensions.values.toList()
            for (extension in extensions) {
                segmentedExtensionItems(extension)
            }
        }
    }
}

private fun LazyListScope.segmentedExtensionItems(
    extension: ThemeExtension,
) {
    val components = extension.manifest.themes.sortedBy { it.name }
    val count = components.size + 1
    item {
        SegmentedListTitle(extension.manifest.meta.id)
    }
    item {
        SegmentedListItem(
            shapes = ListItemDefaults.segmentedShapes(0, count),
            content = { Text(extension.manifest.meta.title) },
            supportingContent = extension.manifest.meta.description?.let { { Text(it) } },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        )
    }
    itemsIndexed(components) { index, component ->
        SegmentedListItem(
            modifier = Modifier.padding(top = ListItemDefaults.SegmentedGap),
            shapes = ListItemDefaults.segmentedShapes(index + 1, count),
            leadingContent = {
                Icon(
                    imageVector = if (component.isNightTheme) {
                        Icons.Default.DarkMode
                    } else {
                        Icons.Default.LightMode
                    },
                    contentDescription = null,
                )
            },
            content = { Text(component.name) },
            trailingContent = { Icon(Icons.Default.ChevronRight, contentDescription = null) },
        )
    }
    item {
        Spacer(Modifier.height(16.dp))
    }
}
