package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import moe.forpleuvoir.ibukigourd.ui.sokitsu.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SurfaceDefaults
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.resolve
import moe.forpleuvoir.ibukigourd.ui.sokitsu.FlatButtonDefaults

@Composable
fun CustomRadialMenuManagerUI(modifier: Modifier = Modifier) {
    var selectedMenuKey by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(CustomRadialMenuManager.customRadialMenus) {
        val menus = CustomRadialMenuManager.customRadialMenus
        if (selectedMenuKey != null && selectedMenuKey !in menus) {
            selectedMenuKey = menus.keys.firstOrNull()
        } else if (selectedMenuKey == null && menus.isNotEmpty()) {
            selectedMenuKey = menus.keys.first()
        }
    }


    var showCreateDialog by remember { mutableStateOf(false) }

    var showDeleteConfirm by remember { mutableStateOf<String?>(null) }

    var showRenameDialog by remember { mutableStateOf<String?>(null) }

    var showSettingDialog by remember { mutableStateOf<String?>(null) }


    val selectedMenu = selectedMenuKey?.let { CustomRadialMenuManager.customRadialMenus[it] }

    // 两块内嵌面板共用一档底色（与配置页同款）
    val panelColor = Color.Unspecified.resolve(ConfigManagerDefaults.PanelTone)

    Row(
        modifier = modifier.fillMaxSize().padding(ConfigManagerDefaults.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(ConfigManagerDefaults.ColumnSpacing),
    ) {
        Surface(
            modifier = Modifier
                .widthIn(
                    min = ConfigManagerDefaults.GroupListMinWidth,
                    max = ConfigManagerDefaults.GroupListMaxWidth,
                )
                .width(IntrinsicSize.Max)
                .fillMaxHeight(),
            color = panelColor,
            sprite = SurfaceDefaults.embeddedPanel,
        ) {
            RadialMenuListPane(
                selectedMenuKey = selectedMenuKey,
                onSelectMenu = { selectedMenuKey = it },
                onNewMenu = { showCreateDialog = true },
                onEditSetting = { showSettingDialog = it },
                onRename = { showRenameDialog = it },
                onDeleteMenu = { showDeleteConfirm = it },
                modifier = Modifier.fillMaxSize().padding(ConfigManagerDefaults.EmbedContentPadding),
            )
        }

        Surface(
            modifier = Modifier.weight(1f).fillMaxHeight(),
            color = panelColor,
            sprite = SurfaceDefaults.embeddedPanel,
        ) {
            AnimatedContent(
                targetState = selectedMenuKey,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "RadialMenuTaskPane",
            ) { menuKey ->
                RadialMenuTaskPane(
                    menu = selectedMenu,
                    menuKey = menuKey,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }


    if (showCreateDialog) {
        CreateRadialMenuDialog(
            onDismissRequest = { showCreateDialog = false },
            onConfirm = { name, menu ->
                CustomRadialMenuManager.add(name, menu).onSuccess {
                    menu.load()
                    selectedMenuKey = name
                    CustomRadialMenuManager.save()
                }
                showCreateDialog = false
            }
        )
    }

    showDeleteConfirm?.let { key ->
        SimpleAlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            onConfirmRequest = { true },
            title = { Text(component = HSLang.CustomRadialMenu.deleteConfirm) },
            confirmButton = {
                TextButton(
                    onClick = {
                        CustomRadialMenuManager.remove(key).onSuccess {
                            if (key == selectedMenuKey) {
                                val remaining = CustomRadialMenuManager.customRadialMenus.keys.toList()
                                selectedMenuKey = remaining.firstOrNull()
                            }
                        }
                        showDeleteConfirm = null
                    },
                    colors = FlatButtonDefaults.colors(
                        color = SokitsuTheme.colorScheme.error,
                        contentColor = SokitsuTheme.colorScheme.onError,
                    )
                ) {
                    Text(component = IGLang.Misc.confirm)
                }
            },
        )
    }

    showSettingDialog?.let { key ->
        val menu = CustomRadialMenuManager.customRadialMenus[key]
        if (menu != null) {
            RadialMenuSettingDialog(
                setting = menu.setting,
                onDismissRequest = { showSettingDialog = null },
                onConfirm = { newSetting ->
                    CustomRadialMenuManager.updateSetting(key, newSetting).onSuccess {
                        CustomRadialMenuManager.save()
                    }
                    showSettingDialog = null
                }
            )
        } else {
            showSettingDialog = null
        }
    }

    showRenameDialog?.let { key ->
        RenameMenuDialog(
            currentName = key,
            onDismissRequest = { showRenameDialog = null },
            onConfirm = { newName ->
                CustomRadialMenuManager.rename(key, newName).onSuccess {
                    showRenameDialog = null
                    CustomRadialMenuManager.save()
                    if (selectedMenuKey == key) selectedMenuKey = newName
                }
            }
        )
    }
}
