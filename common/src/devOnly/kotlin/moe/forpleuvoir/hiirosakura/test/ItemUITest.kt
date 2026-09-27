package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.*
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Button
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
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
import moe.forpleuvoir.ibukigourd.ui.sokitsu.toast.ToastHandler
import moe.forpleuvoir.ibukigourd.util.mc
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen
import moe.forpleuvoir.ibukigourd.ui.item.ItemIcon

fun openItemBrowser() {
    openComposeScreen(shouldRenderLevel = { false }) {
        SokitsuTheme {
            Box(Modifier.fillMaxWidth().fillMaxHeight()) {
                var item by remember { mutableStateOf(Items.MELON) }

                var block by remember { mutableStateOf(mc.targetBlock?.state?.block ?: Blocks.MELON) }

                Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row {
                        ItemSelector(item, { item = it })
                        BlockSelector(block, { block = it })
                    }
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = {
                        ToastHandler.showContent {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ItemIcon(ItemStack(item))
                                    Text(item.getName(ItemStack((item))))
                                }
                                Spacer(Modifier.height(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    ItemIcon(ItemStack(block))
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