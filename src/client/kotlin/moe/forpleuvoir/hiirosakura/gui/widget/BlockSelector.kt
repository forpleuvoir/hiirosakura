package moe.forpleuvoir.hiirosakura.gui.widget

import moe.forpleuvoir.hiirosakura.util.closeScreen
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.extensions.drawcontext.batchRenderTextureColored
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRender
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.tip.PopupTip
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.util.collection.notifiableList
import net.minecraft.block.Block
import net.minecraft.registry.Registries

fun ContainerScope.BlockSelector(
    block: MutableState<Block>,
    blocks: List<Block> = Registries.BLOCK.toList(),
    onSelected: (Block) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(Block) -> IGWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
        ) {
            ItemIcon(it.asItem(), .6f)
            TextLabel(it.name.copyToText())
        }
    },
    optionWrapper: ButtonScope.(Block) -> IGWidget = {
        Row(
            modifier = Modifier.weight(1),
            horizontalArrangement = Arrangement.spacedBy(2f)
        ) {
            ItemIcon(it.asItem(), .6f)
            TextLabel(it.name.copyToText())
        }
    },
    modifier: Modifier = Modifier,
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = blocks,
    selected = block,
    predicate = { block, str ->
        block.name.string.contains(str, ignoreCase = true) || Registries.BLOCK.getKey(block).toString().contains(str)
    },
    onSelected = onSelected,
    selectedColor = selectedColor,
    selectedWrapper = selectedWrapper,
    optionWrapper = optionWrapper,
    modifier = modifier,
    searchBarModifier = searchBarModifier,
    listWrapperModifier = listWrapperModifier,
    listModifier = listModifier,
    optionsDirection = optionsDirection,
    scope = scope
)

fun ContainerScope.BlockSelector(
    block: MutableState<Block>,
    blocks: List<Block> = Registries.BLOCK.toList(),
    onSelected: (Block) -> Unit = {},
    selectorBGColor: ARGBColor = Color(0xffffccf0),
    optionsDirection: List<Direction> = listOf(Direction.Bottom, Direction.Right, Direction.Top, Direction.Left),
    modifier: Modifier = Modifier
) = Button(
    modifier.attachLeft {
        padding(horizontal = 5f, vertical = 4f)
            .render { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
                }
            }
    },
    horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left)
) {
    ItemIcon(block, .6f)
    val text = mutableStateOf(block.getValue().name.copyToText())
    TextLabel(text)
    click {
        PopupTip(
            modifier = Modifier.disableRender().margin(0f).padding(0f),
            optionalDirection = notifiableList(optionsDirection)
        ) {
            BlockSelector(blocks = blocks, bgColor = selectorBGColor, onSelected = {
                block.setValue(it)
                onSelected(it)
                text.setValue(it.name.copyToText())
                closeScreen()
            })
        }.open()
    }
}

fun ContainerScope.BlockSelector(
    blocks: List<Block> = Registries.BLOCK.toList(),
    onSelected: (Block) -> Unit = {},
    columnSize: Int = 9,
    searchBarHideLimit: Int = 9 * columnSize,
    selectedColor: ARGBColor = defaultSelectedColor,
    optionWrapper: ButtonScope.(Block) -> IGWidget = {
        ItemIcon(it, .9f)
    },
    modifier: Modifier = Modifier,
    bgColor: ARGBColor = Color(0xffffccf0),
    searchBarModifier: ColumnScope.() -> Modifier = { Modifier.matchSibling() },
    listWrapperModifier: ColumnScope.() -> Modifier = { Modifier },
    listModifier: RowScope.() -> Modifier = { Modifier.height(147f).width(147f) },
) = Column(
    modifier = modifier.attachLeft {
        padding(5)
            .renderBackground { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_BG, bgColor)
                }
            }
    }
) {
    val showList = notifiableList(blocks)
    if (blocks.size > searchBarHideLimit) {
        SearchBar(
            textConsumer = { str ->
                showList.disableNotify {
                    showList.clear()
                    showList.addAll(blocks.filter { it.name.string.contains(str, ignoreCase = true) || Registries.BLOCK.getKey(it).toString().contains(str) })
                }
                showList.onChange(showList)
            },
            hintText = stateOf(IGLang.search.plainText),
            modifier = searchBarModifier().padding(0).disableRender(),
            textEditorModifier = { Modifier.weight(1) }
        )
    }
    ColumnListWrapped(
        modifier = listWrapperModifier().attachLeft {
            padding(3f).renderBackground { ctx, _, _, _ ->
                ctx.batchRenderTextureColored {
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_OUTLINE)
                    pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_INNER)
                }
            }
        },
        listModifier = listModifier
    ) {
        if (showList.isEmpty()) {
            TextLabel(IGLang.hasNothing)
            return@ColumnListWrapped
        }
        showList.chunked(columnSize).forEach { column ->
            Row(
                modifier = Modifier.fill(),
                horizontalArrangement = Arrangement.Left
            ) {
                column.forEach { block ->
                    FlatButton(
                        modifier = Modifier.hoverText(block.name.copyToText().append(Literal("\n${block.id}").withColor(Color(10, 136, 226)))),
                        hoveredColor = selectedColor,
                        round = 1,
                    ) {
                        optionWrapper(block)
                        click { onSelected(block) }
                    }
                }
            }
        }
    }.apply {
        showList.subscribe {
            executeRecompose()
        }
    }
}