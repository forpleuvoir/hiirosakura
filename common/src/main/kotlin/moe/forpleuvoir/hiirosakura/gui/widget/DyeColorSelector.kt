package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.margin
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.Rect
import moe.forpleuvoir.ibukigourd.gui.widget.Selector
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.world.item.DyeColor

fun ContainerScope.DyeColorSelector(
    color: MutableState<DyeColor>,
    onSelected: (DyeColor) -> Unit = {},
    modifier: Modifier = Modifier,
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    amountStep: Float? = null
) = Selector(
    options = DyeColor.entries,
    selected = color,
    onSelected = onSelected,
    selectedWrapper = {
        Row(Modifier.weight(1)) {
            Rect(Color.ofARGB(it.textureDiffuseColor), Modifier.size(9f, 9f).margin(right = 2f))
            Text(
                Translatable("enum.${javaClass.name}.${it.name}", it.getName()),
                modifier = Modifier
                    .weight(1)
                    .hoverText(Translatable("enum.${javaClass.name}.${it.name}.comment", it.getName()), optionalDirection = Direction.clockwiseFromTop)
            )
        }
    },
    optionWrapper = {
        Row {
            Rect(Color.ofARGB(it.textureDiffuseColor), Modifier.size(9f, 9f).margin(right = 2f))
            Text(
                Translatable("enum.${javaClass.name}.${it.name}", it.getName()),
                modifier = Modifier
                    .width(DyeColor.entries.map { dyeColor -> dyeColor.translateText }.maxWidth.coerceAtLeast(30f))
                    .hoverText(Translatable("enum.${javaClass.name}.${it.name}.comment", it.getName()), optionalDirection = Direction.rightLeftBottomTop)
            )
        }
    },
    modifier = modifier,
    optionsDirection = optionsDirection,
    amountStep = amountStep
)