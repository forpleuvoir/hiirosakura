package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackEditor
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.util.mc
fun testItemEditor() {
    openComposeScreen {
        IbukiGourdTheme {
            var showDialog by remember { mutableStateOf(true) }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { showDialog = true }) {
                    Text("Show Dialog")
                }
            }
            if (showDialog) {
                ItemStackEditor(
                    onDismissRequest = { showDialog = false }
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