package moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.CustomRadialMenuManager
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.ui.preset.SimpleAlertDialog
import moe.forpleuvoir.ibukigourd.ui.preset.Text

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

    Row(modifier.fillMaxSize()) {
        RadialMenuListPane(
            selectedMenuKey = selectedMenuKey,
            onSelectMenu = { selectedMenuKey = it },
            onNewMenu = { showCreateDialog = true },
            onEditSetting = { showSettingDialog = it },
            onRename = { showRenameDialog = it },
            onDeleteMenu = { showDeleteConfirm = it },
        )

        VerticalDivider(modifier = Modifier.fillMaxHeight())
        AnimatedContent(
            targetState = selectedMenuKey,
            modifier = Modifier.weight(1f),
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
            title = { Text(HSLang.CustomRadialMenu.deleteConfirm) },
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
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    )
                ) {
                    Text(IGLang.Misc.confirm)
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
