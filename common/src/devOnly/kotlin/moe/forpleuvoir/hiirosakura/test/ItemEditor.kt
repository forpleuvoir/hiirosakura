package moe.forpleuvoir.hiirosakura.test

import androidx.compose.runtime.CompositionLocalProvider
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackEditor
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen
import moe.forpleuvoir.hiirosakura.ui.util.LocalRegistryAccess
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.util.mc

fun testItemEditor() {
    openComposeScreen {
        registryAccess?.let { access ->
            CompositionLocalProvider(LocalRegistryAccess provides access) {
                ItemStackEditor(
                    onDismissRequest = { closeScreen() }
                ) { stack ->
                    val player = mc.player ?: return@ItemStackEditor
                    if (player.isCreative) {
                        player.inventory.selectedItem = stack
                        mc.gameMode?.handleCreativeModeItemAdd(stack, 36 + player.inventory.selectedSlot)
                    }
                }
            }
        }
    }
}