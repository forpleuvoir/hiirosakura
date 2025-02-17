package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.config.items.*
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.*
import moe.forpleuvoir.hiirosakura.gui.widget.*
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.Transform
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.execute
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.recompose
import moe.forpleuvoir.ibukigourd.gui.base.scope.WidgetContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ButtonScope
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.BoxScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.ColumnScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.renameKey
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.config.Config
import net.minecraft.block.Blocks
import net.minecraft.item.Items
import kotlin.time.Duration.Companion.seconds

private val handItemMatcher
    get() = ItemStackMatcher(MultiMatcher.MatchMode.AllMatch).apply {
        mc.targetBlock
            ?.let {
                ItemStackMatchEntry.Item(ItemStackMatcher.handItemStack?.item ?: Items.MELON)
            }?.let { item ->
                addEntry(item)
            }
    }

private val targetBlockMatcher
    get() = BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch).apply {
        mc.targetBlock
            ?.let {
                BlockInfoMatchEntry.Block(mc.targetBlock?.state?.block ?: Blocks.MELON)
            }?.let { block ->
                addEntry(block)
            }
    }

fun WidgetContainerScope.ItemStackMatcherWrapper(
    config: ConfigItemStackMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(120f)
                .hoverTip { ItemStackMatcherInfo(value.getValue()) }
        ) {
            ItemStackMatcherSimpleInfo(value.getValue()).apply {
                value.subscribe { this.recompose() }
            }
            click {
                ItemStackMatcherBuilder(value.getValue(), {
                    value.setValue(it)
                }).open()
            }
        }
        ConfigResetButton(config) {
            value.setValue(config.getValue())
        }
    }
}

fun WidgetContainerScope.BlockInfoMatcherWrapper(
    config: ConfigBlockInfoMatcher,
    modifier: Modifier = Modifier,
) = ConfigColumnWrapper(config, modifier) {
    val value = mutableStateOf(config.getValue()).apply {
        subscribe { config.setValue(it) }
    }
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(120f)
                .hoverTip { BlockInfoMatcherInfo(value.getValue()) }
        ) {
            BlockInfoMatcherSimpleInfo(value.getValue()).apply {
                value.subscribe { this.recompose() }
            }
            click {
                BlockInfoMatcherBuilder(value.getValue(), {
                    value.setValue(it)
                }).open()
            }
        }
        ConfigResetButton(config) {
            value.setValue(config.getValue())
        }
    }
}

//------------ MapWrapper ------------\\


fun <T, M : MultiMatcher<T>> WidgetContainerScope.ConfigMatcherMapWrappedButton(
    config: Config<MutableMap<String, M>, *>,
    newValue: (Iterable<Map.Entry<String, M>>) -> Map.Entry<String, M>,
    hoverContent: ColumnScope.(M) -> Unit,
    entryWrapper: RowListScope.(Map.Entry<String, M>, index: Int) -> Unit
) = MapConfigWrapedButton(
    config = config,
    newValue = newValue,
    hoverContent = {
        Row(
            verticalArrangement = Arrangement.spacedBy(1f),
            horizontalAlignment = Alignment.Left,
        ) {
            it.forEachWithLimit(10) { (k, v) ->
                Column(horizontalArrangement = Arrangement.spacedBy(2f)) {
                    TextLabel("$k => ")
                    hoverContent(v)
                }
            }
            if (it.count() > 10) TextLabel("...")
            if (it.count() == 0) TextLabel(IGLang.hasNothing.plainText)
        }
    },

    entryWrapper = entryWrapper
)

fun <T, M : MultiMatcher<T>> WidgetContainerScope.ConfigMatcherMapEntryWrapper(
    config: Config<MutableMap<String, M>, *>,
    recompose: () -> Unit,
    key: String,
    value: M,
    hoverContent: BoxScope.(M) -> Unit,
    simpleInfo: ButtonScope.(M) -> Unit,
    matcherBuilder: (M, (M) -> Unit) -> Unit,
) = Column(
    horizontalArrangement = Arrangement.spacedBy(2f)
) {
    TextLabel(
        key, modifier = Modifier.width(config.getValue().keys.maxWidth(mc.textRenderer).coerceIn(119, 239) + 1f)
    )
    FlatButton(
        hoveredColor = Colors.PALEGREEN.alpha(.5f),
        modifier = Modifier.hoverText(IGLang.edit)
    ) {
        Icon(IconTextures.EDIT, modifier = Modifier.size(10f, 10f))
        click {
            var newKey = key
            var editor: (() -> Transform)? = null
            ConfirmDialog(
                stateOf(IGLang.edit.appendLiteral(" => $key")),
                onConfirm = {
                    if (newKey == key) {
                        mc.currentScreen?.close()
                        return@ConfirmDialog
                    }
                    if (config.getValue().containsKey(newKey)) {
                        editor?.let {
                            TipHandler.pushTip(CONFIG_WRAPPER_TIP, 2.seconds, it, Tip {
                                TextLabel(IGLang.keyExists(newKey).withColor(Colors.RED))
                            })
                        }

                        return@ConfirmDialog
                    }
                    config.getValue().renameKey(key, newKey)
                    mc.currentScreen?.close()
                    recompose()
                },
                screenModifier = Modifier.onClose {
                    TipHandler.popTip(CONFIG_WRAPPER_TIP)
                }
            ) {
                TextEditor(modifier = Modifier.width(240f)) {
                    editor = { this.owner().transform }
                    text = key
                    textConsumer { newKey = it }
                }
            }.open()
        }
    }

    Button(
        Modifier.width(180f).hoverTip { hoverContent(value) }
    ) {
        simpleInfo(value)

        click {
            matcherBuilder(value) {
                config.getValue()[key] = it
                recompose()
            }

        }
    }

    FlatButton(
        hoveredColor = Colors.LIGHT_RED,
        modifier = Modifier.margin(right = 2f).hoverText(IGLang.remove)
    ) {
        Icon(IconTextures.DELETE, Colors.RED, Modifier.size(10f, 10f))
        click {
            config.getValue().remove(key)
            recompose()
        }
    }
}


//------------ BlockInfoMatcherMap ------------\\

fun WidgetContainerScope.BlockInfoMatcherMapWrapper(
    config: ConfigBlockInfoMatcherMap,
    modifier: Modifier = Modifier
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ConfigMatcherMapWrappedButton(
            config = config,
            newValue = {
                mapEntry("block matcher ${(it.count())}", targetBlockMatcher)
            },
            hoverContent = { BlockInfoMatcherSimpleInfo(it) }
        ) { (key, value), index ->
            ConfigMatcherMapEntryWrapper(
                config,
                { execute { this@ConfigMatcherMapWrappedButton.recompose() } },
                key,
                value,
                { BlockInfoMatcherInfo(it) },
                { BlockInfoMatcherSimpleInfo(it) }
            ) { matcher, matcherConsumer ->
                BlockInfoMatcherBuilder(value, matcherConsumer).open()
            }
        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}

//------------ ItemStackMatcherMap ------------\\

fun WidgetContainerScope.ItemStackMatcherMapWrapper(
    config: ConfigItemStackMatcherMap,
    modifier: Modifier = Modifier
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ConfigMatcherMapWrappedButton(
            config = config,
            newValue = {
                mapEntry("item matcher ${(it.count())}", handItemMatcher)
            },
            hoverContent = { ItemStackMatcherSimpleInfo(it) }
        ) { (key, value), index ->
            ConfigMatcherMapEntryWrapper(
                config,
                { execute { this@ConfigMatcherMapWrappedButton.recompose() } },
                key,
                value,
                { ItemStackMatcherInfo(it) },
                { ItemStackMatcherSimpleInfo(it) }
            ) { matcher, matcherConsumer ->
                ItemStackMatcherBuilder(value, matcherConsumer).open()
            }

        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}

//------------ BlockInfoItemStack ------------\\

fun <A, B, C : Pair<MultiMatcher<A>, MultiMatcher<B>>> WidgetContainerScope.ConfigMatcherPairMapWrappedButton(
    config: Config<MutableMap<String, C>, *>,
    newValue: (Iterable<Map.Entry<String, C>>) -> Map.Entry<String, C>,
    hoverContentA: ColumnScope.(MultiMatcher<A>) -> Unit,
    hoverContentB: ColumnScope.(MultiMatcher<B>) -> Unit,
    entryWrapper: RowListScope.(Map.Entry<String, C>, index: Int) -> Unit
) = MapConfigWrapedButton(
    config = config,
    newValue = newValue,
    hoverContent = {
        Row(
            verticalArrangement = Arrangement.spacedBy(1f),
            horizontalAlignment = Alignment.Left,
        ) {
            it.forEachWithLimit(10) { (k, v) ->
                Column(horizontalArrangement = Arrangement.spacedBy(2f)) {
                    TextLabel("$k => ")
                    hoverContentA(v.first)
                    TextLabel(" => ")
                    hoverContentB(v.second)
                }
            }
            if (it.count() > 10) TextLabel("...")
            if (it.count() == 0) TextLabel(IGLang.hasNothing.plainText)
        }
    },

    entryWrapper = entryWrapper
)

fun <A, B, C : Pair<MultiMatcher<A>, MultiMatcher<B>>> WidgetContainerScope.ConfigMatcherPairMapEntryWrapper(
    config: Config<MutableMap<String, C>, *>,
    recompose: () -> Unit,
    key: String,
    value: C,
    hoverContentA: BoxScope.(MultiMatcher<A>) -> Unit,
    hoverContentB: BoxScope.(MultiMatcher<B>) -> Unit,
    simpleInfoA: ButtonScope.(MultiMatcher<A>) -> Unit,
    simpleInfoB: ButtonScope.(MultiMatcher<B>) -> Unit,
    matcherBuilderA: (C, (C) -> Unit) -> Unit,
    matcherBuilderB: (C, (C) -> Unit) -> Unit,
) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(2f)
    ) {
        FlatButton(
            hoveredColor = Colors.PALEGREEN.alpha(.5f),
            modifier = Modifier.hoverText(IGLang.edit)
        ) {
            TextLabel(
                key, modifier = Modifier.width(config.getValue().keys.maxWidth(mc.textRenderer).coerceIn(59, 99) + 1f)
            )
            click {
                var newKey = key
                var editor: (() -> Transform)? = null
                ConfirmDialog(
                    stateOf(IGLang.edit.appendLiteral(" => $key")),
                    onConfirm = {
                        if (newKey == key) {
                            mc.currentScreen?.close()
                            return@ConfirmDialog
                        }
                        if (config.getValue().containsKey(newKey)) {
                            editor?.let {
                                TipHandler.pushTip(CONFIG_WRAPPER_TIP, 2.seconds, it, Tip {
                                    TextLabel(IGLang.keyExists(newKey).withColor(Colors.RED))
                                })
                            }

                            return@ConfirmDialog
                        }
                        config.getValue().renameKey(key, newKey)
                        mc.currentScreen?.close()
                        recompose()
                    },
                    screenModifier = Modifier.onClose {
                        TipHandler.popTip(CONFIG_WRAPPER_TIP)
                    }
                ) {
                    TextEditor(modifier = Modifier.width(240f)) {
                        editor = { this.owner().transform }
                        text = key
                        textConsumer { newKey = it }
                    }
                }.open()
            }
        }

        Button(
            Modifier.width(180f).hoverTip { hoverContentA(value.first) }
        ) {
            simpleInfoA(value.first)
            click {
                matcherBuilderA(value) {
                    config.getValue()[key] = it
                    recompose()
                }

            }
        }
        TextLabel(" => ")
        Button(
            Modifier.width(180f).hoverTip { hoverContentB(value.second) }
        ) {
            simpleInfoB(value.second)
            click {
                matcherBuilderB(value) {
                    config.getValue()[key] = it
                    recompose()
                }
            }
        }

        FlatButton(
            hoveredColor = Colors.LIGHT_RED,
            modifier = Modifier.margin(right = 2f).hoverText(IGLang.remove)
        ) {
            Icon(IconTextures.DELETE, Colors.RED, Modifier.size(10f, 10f))
            click {
                config.getValue().remove(key)
                recompose()
            }
        }
    }
}

fun WidgetContainerScope.ConfigStringBlockInfoItemStackPairMapWrapper(
    config: ConfigStringBlockInfoItemStackPairMap,
    modifier: Modifier = Modifier
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ConfigMatcherPairMapWrappedButton(
            config = config,
            newValue = {
                mapEntry("block to item matcher ${(it.count())}", targetBlockMatcher to handItemMatcher)
            },
            hoverContentA = { BlockInfoMatcherSimpleInfo(it) },
            hoverContentB = { ItemStackMatcherSimpleInfo(it) }
        ) { (key, value), index ->
            ConfigMatcherPairMapEntryWrapper(
                config,
                { execute { this@ConfigMatcherPairMapWrappedButton.recompose() } },
                key,
                value,
                { BlockInfoMatcherInfo(it) },
                { ItemStackMatcherInfo(it) },
                {
                    Column(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLabel(HSLang.targetBlock.appendLiteral(" -> "))
                        BlockInfoMatcherSimpleInfo(it)
                    }
                },
                {
                    Column(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLabel(HSLang.handledItem.appendLiteral(" -> "))
                        ItemStackMatcherSimpleInfo(it)
                    }
                },
                { (block, item), matcherConsumer ->
                    BlockInfoMatcherBuilder(
                        block,
                        {
                            matcherConsumer.invoke(it to item)
                        }
                    ).open()
                },
                { (block, item), matcherConsumer ->
                    ItemStackMatcherBuilder(
                        item,
                        {
                            matcherConsumer.invoke(block to it)
                        }
                    ).open()
                }
            )
        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}


fun WidgetContainerScope.ConfigStringItemStackBlockInfoPairMapWrapper(
    config: ConfigStringItemStackBlockInfoPairMap,
    modifier: Modifier = Modifier
) = ConfigColumnWrapper(config, modifier) {
    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        ConfigMatcherPairMapWrappedButton(
            config = config,
            newValue = {
                mapEntry("item to block matcher ${(it.count())}", handItemMatcher to targetBlockMatcher)
            },
            hoverContentA = { ItemStackMatcherSimpleInfo(it) },
            hoverContentB = { BlockInfoMatcherSimpleInfo(it) },
        ) { (key, value), index ->
            ConfigMatcherPairMapEntryWrapper(
                config,
                { execute { this@ConfigMatcherPairMapWrappedButton.recompose() } },
                key,
                value,
                { ItemStackMatcherInfo(it) },
                { BlockInfoMatcherInfo(it) },
                {
                    Column(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLabel(HSLang.handledItem.appendLiteral(" -> "))
                        ItemStackMatcherSimpleInfo(it)
                    }
                },
                {
                    Column(
                        modifier = Modifier,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TextLabel(HSLang.targetBlock.appendLiteral(" -> "))
                        BlockInfoMatcherSimpleInfo(it)
                    }
                },
                { (item, block), matcherConsumer ->
                    ItemStackMatcherBuilder(
                        item,
                        {
                            matcherConsumer.invoke(it to block)
                        }
                    ).open()
                },
                { (item, block), matcherConsumer ->
                    BlockInfoMatcherBuilder(
                        block,
                        {
                            matcherConsumer.invoke(item to it)
                        }
                    ).open()
                }
            )
        }
        ConfigResetButton(config) {
            execute { this@Column.recompose() }
        }
    }
}