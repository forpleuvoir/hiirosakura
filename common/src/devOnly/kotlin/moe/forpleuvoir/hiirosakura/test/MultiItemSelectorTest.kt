package moe.forpleuvoir.hiirosakura.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.ui.compat.openComposeScreen
import moe.forpleuvoir.hiirosakura.ui.widget.MultiItemSelector
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import net.minecraft.world.item.Item

/** `/hstest multi_item_selector`:打开多物品选择器的验收屏。 */
fun openMultiItemSelectorTest() {
    openComposeScreen {
        Box(Modifier.fillMaxWidth().fillMaxHeight()) {
            MultiItemSelectorTestContent(Modifier.align(Alignment.Center))
        }
    }
}

/**
 * [MultiItemSelector] 的开发用验收屏:两个实例覆盖不限与上限 5。验收点:
 * 1. 点物品是**累积选中**、对话框不关;2. 下方容器多行铺开、点一格移除;
 * 3. 「确认」才写回(计数变化),「取消」/ESC 丢弃;4. 上限 5 的实例第 6 个加不进去。
 */
@Composable
fun MultiItemSelectorTestContent(modifier: Modifier = Modifier) {
    var unlimited by remember { mutableStateOf(emptyList<Item>()) }
    var limited by remember { mutableStateOf(emptyList<Item>()) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("多物品选择器 · 不限　已选 ${unlimited.size} 个")
        MultiItemSelector(value = unlimited, onValueChange = { unlimited = it })

        Spacer(Modifier.height(8.dp))

        Text("多物品选择器 · 上限 5　已选 ${limited.size} 个")
        MultiItemSelector(value = limited, onValueChange = { limited = it }, limit = 5)
    }
}
