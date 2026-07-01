package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.widget.BlockSelector
import moe.forpleuvoir.hiirosakura.ui.widget.ItemSelector
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.ui.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.ItemIconVanilla
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import moe.forpleuvoir.ibukigourd.ui.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks

fun openItemBrowser() {
    openComposeScreen(shouldRenderLevel = { false }) {
        IbukiGourdTheme {
            Box(Modifier.fillMaxWidth().fillMaxHeight()) {
                var item by remember { mutableStateOf(Items.MELON) }
                var block by remember { mutableStateOf(mc.targetBlock?.state?.block ?: Blocks.MELON) }
                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        ItemSelector(item, { item = it }, Modifier)
                        BlockSelector(block, { block = it }, Modifier)
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        ToastHandler.showContent {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ItemIconVanilla(ItemStack(item))
                                    Text(item.getName(ItemStack((item))))
                                }
                                Spacer(Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ItemIconVanilla(ItemStack(block))
                                    Text(block.name)
                                }
                            }
                        }
                    }) {
                        Text("当前物品")
                    }
                }

            }
        }
    }
}