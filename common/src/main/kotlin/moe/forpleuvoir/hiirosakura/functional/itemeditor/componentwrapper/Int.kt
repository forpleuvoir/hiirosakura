package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEntryRow
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigControlDefaults
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configControlHeight
import moe.forpleuvoir.ibukigourd.ui.configwrapper.configIconScale
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icon
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IconButton
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntField
import moe.forpleuvoir.ibukigourd.ui.sokitsu.IntSlider
import net.minecraft.resources.Identifier

/**
 * 整数组件行：区间跨度不超过 [ConfigControlDefaults.SliderSpanLimit] 时主控件槽给滑条或数值框、
 * 动作格给「滑条 ⇄ 数值框」切换按钮；跨度超过时不占动作格，数值框填满主控件槽。
 *
 * @param valueRange 取值区间（含两端）
 * @param valueToText 值的文本形式，滑条与数值框共用
 */
@Composable
fun IntComponentWrapper(
    key: Identifier,
    value: Int,
    onValueChange: (Int) -> Unit,
    valueRange: IntRange,
    valueToText: (Int) -> String = { it.toString() },
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(8.dp),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
) {
    val span = valueRange.last.toLong() - valueRange.first.toLong()
    if (span in 1 until ConfigControlDefaults.SliderSpanLimit.toLong()) {
        var sliderMode by remember(valueRange) { mutableStateOf(true) }
        val rotation by animateFloatAsState(if (sliderMode) 0f else 180f)

        DataComponentEntryRow(
            key, removeAction, modifier, horizontalArrangement, verticalAlignment,
            action = {
                IconButton(
                    onClick = { sliderMode = !sliderMode },
                    contentPadding = ConfigControlDefaults.IconButtonPadding,
                ) {
                    Icon(Icons.SyncAlt, scale = configIconScale(), modifier = Modifier.rotate(rotation))
                }
            },
        ) {
            if (sliderMode) {
                IntSlider(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    valueToText = valueToText,
                    modifier = Modifier.fillMaxWidth().height(configControlHeight()),
                )
            } else {
                IntField(
                    value = value,
                    onValueChange = onValueChange,
                    valueRange = valueRange,
                    valueToText = valueToText,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    } else {
        DataComponentEntryRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
            IntField(
                value = value,
                onValueChange = onValueChange,
                valueRange = valueRange,
                valueToText = valueToText,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
