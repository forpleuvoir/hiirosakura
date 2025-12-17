package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.BlockSelector
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.gui.widget.WrappedBox
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidget
import moe.forpleuvoir.ibukigourd.gui.util.Direction
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.*
import moe.forpleuvoir.ibukigourd.gui.widget.layout.*
import moe.forpleuvoir.ibukigourd.gui.widget.text.*
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.core.HolderSet
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.component.Tool
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import java.util.*
import kotlin.jvm.optionals.getOrDefault
import kotlin.time.Duration.Companion.milliseconds

fun ContainerScope.ToolComponentWrapper(
    key: ResourceLocation,
    component: Tool,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Tool, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    Button(
        Modifier.width(140f)
    ) {
        Text(IGLang.edit)
        click {
            ToolEditor(key.asTranslateText(), component, modifier, onValueChange = onValueChange).open()
        }
    }
}


fun ToolEditor(
    title: Component,
    tool: Tool,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Tool, Boolean) -> Unit,
): IGScreenImpl {
    val rules = tool.rules.toMutableList()
    val defaultMiningSpeed = tool.defaultMiningSpeed.asMutableState
    val damagePerBlock = tool.damagePerBlock.asMutableState
    val canDestroyBlocksInCreative = tool.canDestroyBlocksInCreative.asMutableState

    return DataComponentEditor(
        title,
        { Tool(rules, defaultMiningSpeed.getValue(), damagePerBlock.getValue(), canDestroyBlocksInCreative.getValue()) to true },
        onValueChange,
        modifier,
        screenModifier
    ) {
        var recompose = {}
        Row(horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Right)) {
            FloatEditor(defaultMiningSpeed, modifier = Modifier.width(60f).hoverText("default_mining_speed"), editorModifier = { Modifier.weight(1) })
            IntEditor(damagePerBlock, modifier = Modifier.width(60f).hoverText("damage_per_block"), editorModifier = { Modifier.weight(1) })
            SwitchButton(canDestroyBlocksInCreative, modifier = Modifier.hoverText("can_destroy_blocks_in_creative"))
        }

        TableWrapped(rules, tableModifier = { Modifier.width(320f).height(160f) }) {
            recompose = { executeRecompose() }
            Header(1) {
                Text("blocks", setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, _ ->
                Row {
                    val blocks = rules[index].blocks
                    Button(
                        horizontalArrangement = Arrangement.spacedBy(2f),
                        modifier = Modifier.weight(1)
                            .hoverTip {
                                Column(horizontalAlignment = Alignment.Left) {
                                    blocks.forEachWithLimit(15) { Text(it.value().name) }
                                    if (blocks.count() > 15) Text("...")
                                    if (blocks.count() == 0) Text(IGLang.hasNothing)
                                }
                            }
                    ) {
                        blocks.forEachWithLimit(10) { ItemIcon(it.value(), scale = 0.5f) }
                        if (blocks.count() > 10) Text("...")
                        if (blocks.count() == 0) Text(IGLang.hasNothing)
                        click {
                            BlockHolderSetEditor(Literal("rules[$index].blocks"), rules[index].blocks, onValueChange = { blocks, _ ->
                                rules[index] = Tool.Rule(blocks, rules[index].speed, rules[index].correctForDrops)
                                this@Row.executeRecompose()
                            }).open()
                        }
                    }
                }
            }

            Header {
                Text("speed", setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, _ ->
                var isEmpty = rules[index].speed.isEmpty
                val speed = rules[index].speed.getOrDefault(0f).asMutableState
                speed.subscribe {
                    isEmpty = false
                    rules[index] = Tool.Rule(
                        rules[index].blocks,
                        if (isEmpty) Optional.empty() else Optional.of(speed.getValue()),
                        rules[index].correctForDrops
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2f), modifier = Modifier.hoverTip {
                    Text(
                        mutableStateBy { if (isEmpty) Literal("null") else Literal(speed.getValue().toString()) },
                        setting = TextSetting(textLabelUpdateInterval = 16.milliseconds)
                    )
                }) {
                    FloatEditor(speed, 0f..Float.MAX_VALUE, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
                    RemoveButton {
                        rules[index] = Tool.Rule(rules[index].blocks, Optional.empty(), rules[index].correctForDrops)
                        this@Column.executeRecompose()
                    }
                }
            }

            Header {
                Text("correct_for_drops", setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, _ ->
                var isEmpty = rules[index].correctForDrops.isEmpty
                val correctForDrops = rules[index].correctForDrops.getOrDefault(!isEmpty).asMutableState
                correctForDrops.subscribe {
                    isEmpty = false
                    rules[index] = Tool.Rule(
                        rules[index].blocks,
                        rules[index].speed,
                        if (isEmpty) Optional.empty() else Optional.of(correctForDrops.getValue()),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(2f), modifier = Modifier.hoverTip {
                    Text(
                        mutableStateBy { if (isEmpty) Literal("null") else Literal(correctForDrops.getValue().toString()) },
                        setting = TextSetting(textLabelUpdateInterval = 16.milliseconds)
                    )
                }) {
                    SwitchButton(correctForDrops)
                    RemoveButton {
                        rules[index] = Tool.Rule(rules[index].blocks, rules[index].speed, Optional.empty())
                        this@Column.executeRecompose()
                    }
                }
            }

            Header {
                Text(IGLang.remove, setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { index, _ ->
                DeleteButton({ HSLang.deleteConfirm("rule[$index]") }, recompose) {
                    rules.removeAt(index)
                }
            }

        }
    }
}

private val blockTags
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).tags.map { it.key() }

private val TagKey<Block>.blocks
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).getTagOrEmpty(this)

private val TagKey<Block>.asHolderSet
    get() = registryAccess!!.lookupOrThrow(Registries.BLOCK).tags.filter {
        it.key().location == this.location
    }.findFirst().get()

fun BlockHolderSetEditor(
    title: Component,
    blocks: HolderSet<Block>,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (HolderSet<Block>, Boolean) -> Unit,
): IGScreenImpl {
    val (tag, mode) =
        if (blocks is HolderSet.Named) blocks.key().asMutableState to false.asMutableState
        else blockTags.findFirst().get().asMutableState to true.asMutableState

    val list = blocks.map { it.value() }.toMutableList()

    return DataComponentEditor(
        title,
        {
            mode.getValue().pick(
                { HolderSet.direct(list.map { BuiltInRegistries.BLOCK.wrapAsHolder(it) }) },
                { tag.getValue().asHolderSet }) to true
        },
        onValueChange,
        modifier,
        screenModifier,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
            RadioButtons(listOf(true, false), mode.getValue().asMutableState, onChange = {
                mode.setValue(it)
            }, optionWrapper = {
                Text(if (it) "From Blocks" else "From Tag")
            })
        }
        SwitchableProxy(
            switch = mode,
            widgetA = {
                var recompose = {}
                Column {
                    Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                            BlockSelector(
                                Blocks.MELON.asMutableState,
                                onSelected = {
                                    if (!list.contains(it)) {
                                        list.add(it)
                                        recompose()
                                    }
                                },
                                modifier = Modifier.width(100f),
                                selectedWrapper = {
                                    Text("Add Block")
                                },
                            )
                        }
                        BlockTagSelector(
                            tag,
                            onSelected = { tag ->
                                var result = false
                                tag.blocks.map { it.value() }.forEach {
                                    if (!list.contains(it)) {
                                        list.add(it)
                                        result = true
                                    }
                                }
                                if (result) recompose()
                            },
                            selectedWrapper = {
                                Text("Add From Tag")
                            },
                            modifier = Modifier.width(100f)
                        )
                    }

                    TableWrapped(list, tableModifier = { Modifier.width(200f).height(160f) }) {
                        recompose = { executeRecompose() }
                        Header(1) {
                            Text("Block", modifier = Modifier.padding(2f), setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
                        }.Column { index, _ ->
                            WrappedBox {
                                Row(Modifier.fillWidth(), horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)) {
                                    ItemIcon(list[index], .6f)
                                    Text(list[index].name)
                                }
                            }
                        }
                        Header {
                            Text(IGLang.remove, setting = TextWidget.Setting(horizontalAlignment = Alignment.CenterHorizontally))
                        }.Column { index, _ ->
                            DeleteButton({ HSLang.deleteConfirm(list[index].name) }, recompose) {
                                list.removeAt(index)
                            }
                        }
                    }

                }
            },
            widgetB = {
                BlockTagSelector(
                    tag, modifier = Modifier.width(240f),
                    listWrapperModifier = {
                        Modifier.width(blockTags.toList().map { "#${it.location}" }.maxWidth + 12f).maxHeight(120f)
                    },
                )
            }
        )
    }
}

fun ContainerScope.BlockTagSelector(
    blockTag: MutableState<TagKey<Block>>,
    blockTags: Iterable<TagKey<Block>> = moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.blockTags.toList(),
    onSelected: (TagKey<Block>) -> Unit = {},
    selectedColor: ARGBColor = defaultSelectedColor,
    selectedWrapper: DropDownMenuScope.(TagKey<Block>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),
            modifier = Modifier.weight(1)
                .hoverTip {
                    val items = it.blocks
                    if (items.count() == 0) {
                        Text(IGLang.hasNothing)
                        return@hoverTip
                    } else {
                        Column(horizontalAlignment = Alignment.Left) {
                            items.forEachWithLimit(20) { type ->
                                Text(type.value().name)
                            }
                            if (items.count() > 20) Text("......")
                        }
                    }
                }
        ) {
            Text("#${it.location}")
        }
    },
    optionWrapper: ButtonScope.(TagKey<Block>) -> GuiWidget = {
        Row(
            horizontalArrangement = Arrangement.spacedBy(2f, Alignment.Left),
            modifier = Modifier.weight(1).hoverTip {
                val items = it.blocks
                if (items.count() == 0) {
                    Text(IGLang.hasNothing)
                    return@hoverTip
                } else {
                    Column(horizontalAlignment = Alignment.Left) {
                        items.forEachWithLimit(20) { type ->
                            Text(type.value().name)
                        }
                        if (items.count() > 20) Text("......")
                    }
                }
            }
        ) {
            Text("#${it.location}")
        }
    },
    modifier: Modifier = Modifier.width(280f),
    searchBarModifier: ColumnScope.() -> Modifier = {
        Modifier.width(blockTags.map { "#${it.location}" }.maxWidth + 12f)
    },
    listWrapperModifier: ColumnScope.() -> Modifier = {
        Modifier.width(blockTags.map { "#${it.location}" }.maxWidth + 12f).maxHeight(160f)
    },
    listModifier: RowScope.() -> Modifier = { Modifier.weight(1) },
    optionsDirection: List<Direction> = Direction.bottomTopRightLeft,
    scope: DropDownMenuScope.() -> Unit = {}
) = SelectorWithSearcher(
    options = blockTags,
    selected = blockTag,
    predicate = { tag, str ->
        "#${tag.location}".contains(str)
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
    amountStep = 15f,
    scope = scope
)
