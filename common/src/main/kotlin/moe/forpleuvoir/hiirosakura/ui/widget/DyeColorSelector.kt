package moe.forpleuvoir.hiirosakura.ui.widget

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Surface
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.DropdownMenu
import moe.forpleuvoir.ibukigourd.ui.sokitsu.menu.dropdownMenuAnchor
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.resolve
import moe.forpleuvoir.ibukigourd.ui.sokitsu.tooltip.tooltip
import moe.forpleuvoir.ibukigourd.util.contrasting
import net.minecraft.world.item.DyeColor

/**
 * 染料色选择器：锚点是当前颜色的色块，点击弹出 4×4 网格。
 *
 * 色块由 [Surface] 自身的 `color` 渲染，取值来自 [displayColor]；选中格描边取
 * [SokitsuTheme.selectedOutlineColor]（与 Button / Slider 的选中描边同源），其余格取
 * 当前配色板的 outline。指针悬停时为手型。
 *
 * @param value 当前染料色
 * @param onValueChange 选中回调
 * @param displayColor 染料色 → 色块渲染色
 * @param modifier 作用于锚点
 */
@Composable
fun DyeColorSelector(
    value: DyeColor,
    onValueChange: (DyeColor) -> Unit,
    displayColor: (DyeColor) -> Color,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }


    var menuAnchorBounds by remember { mutableStateOf(Rect.Zero) }
    Box(modifier.dropdownMenuAnchor { menuAnchorBounds = it }) {
        val interactionSource = remember { MutableInteractionSource() }
        val hovered by interactionSource.collectIsHoveredAsState()
        val outlineColor = if (hovered) displayColor(value).contrasting() else SokitsuTheme.colorScheme.outline
        Surface(
            modifier = Modifier
                .size(42.dp)
                .clickable { expanded = true }
                .hoverable(interactionSource)
                .pointerHoverIcon(PointerIcon.Hand),
            outlineColor = outlineColor,
            color = displayColor(value),
        ) {}

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            anchorBounds = menuAnchorBounds,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                DyeColor.entries
                    .chunked(4)
                    .forEach { rowEntries ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            rowEntries.forEach { entry ->
                                val selected = entry == value

                                Surface(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clickable {
                                            expanded = false
                                            if (!selected) {
                                                onValueChange(entry)
                                            }
                                        }
                                        .pointerHoverIcon(PointerIcon.Hand)
                                        .tooltip { Text(entry.translateText) },
                                    outlineColor = if (selected) {
                                        SokitsuTheme.selectedOutlineColor
                                    } else {
                                        SokitsuTheme.colorScheme.outline
                                    },
                                    color = displayColor(entry),
                                ) {}
                            }
                        }
                    }
            }
        }
    }
}
