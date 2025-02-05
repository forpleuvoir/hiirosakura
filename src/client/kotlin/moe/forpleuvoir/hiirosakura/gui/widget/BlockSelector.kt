package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.DropDownMenuScope
import moe.forpleuvoir.ibukigourd.gui.widget.SelectorWithSearcher
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.defaultSelectedColor
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.nebula.common.color.ARGBColor
import net.minecraft.block.Block
import net.minecraft.registry.Registries

fun WidgetContainerScope.BlockSelector(
    block: MutableState<Block>,
    blocks: List<Block> = Registries.BLOCK.toList(),
    onChange: (Block) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Block) -> IGWidget = {
        Column(
            modifier= Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            ItemIcon(it.asItem(), .6f)
            TextLabel(it.name.copyToText())
        }
    },
    optionWrapper: ButtonScope.(Block) -> IGWidget = {
        Column(
            modifier= Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(it.asItem(), .6f)
            TextLabel(it.name.copyToText())
        }
    },
    modifier: Modifier = Modifier,
    searchBarModifier: RowScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = blocks,
    selected = block,
    predicate = { block, str ->
        block.name.string.contains(str, ignoreCase = true) || Registries.BLOCK.getKey(block).toString().contains(str)
    },
    onChange = onChange,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)