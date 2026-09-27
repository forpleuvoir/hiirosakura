package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackEditor
import moe.forpleuvoir.hiirosakura.ui.compat.closeScreen
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen

fun testItemEditor() {
    openComposeScreen {
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