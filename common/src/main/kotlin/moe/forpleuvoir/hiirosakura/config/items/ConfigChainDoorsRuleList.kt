package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainDoorsRule
import moe.forpleuvoir.hiirosakura.functional.gameplay.chaindoors.ChainStrategy
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherSimpleInfo
import moe.forpleuvoir.hiirosakura.gui.widget.BlockInfoMatcherTableColumn
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableIntEditor
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateTextWithParent
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.configwrapper.*
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.EnumSelector
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.RadioButtons
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.BoxScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.RowScope
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.MutableState
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList

class ConfigChainDoorsRuleList(
    key: String,
    defaultValue: List<ChainDoorsRule>
) : ConfigList<ChainDoorsRule>(
    key,
    defaultValue,
    { it.serialization() },
    { ChainDoorsRule.deserialization(it) }
)

fun ConfigContainer.configChainDoorsRuleList(
    key: String,
    defaultValue: List<ChainDoorsRule>
) = addConfig(ConfigChainDoorsRuleList(key, defaultValue))

//------------ GUI Wrapper ------------\\

fun ContainerScope.ConfigChainDoorsRuleListWrapper(
    config: ConfigChainDoorsRuleList,
    modifier: Modifier = Modifier,
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        TableConfigListWrappedButton(
            config,
            title = config.translateTextWithParent(1, " → "),
            newValue = { ChainDoorsRule.MOB_INTERACTABLE_DOORS },
            hoverTableScope = {
                Header {
                    Text(HSLang.chainDoorsOriginDoor)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.originDoor, Modifier.height(10f).align(Alignment.CenterLeft))
                }
                Header {
                    Text(HSLang.chainDoorsChainDoor)
                }.Column {
                    BlockInfoMatcherSimpleInfo(it.chainDoor, Modifier.height(10f).align(Alignment.CenterLeft))
                }
            },
            dialogModifier = Modifier.padding(20f),
            tableWrappedModifier = { Modifier.maxWidth(500f) }
        ) {
            val recompose = { this@TableConfigListWrappedButton.executeRecompose() }
            MoveableTableHeader().MoveableTableColumCell(config, recompose, false)

            val consumer = { index: Int, entry: ChainDoorsRule ->
                config[index] = entry
                this@TableConfigListWrappedButton.executeRecompose()
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(originDoor = block))
            }, { it.originDoor }, { Modifier }, 1) {
                Text(
                    HSLang.chainDoorsOriginDoor,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.chainDoorsOriginDoorComment)
                )
            }

            BlockInfoMatcherTableColumn({ index, entry, block ->
                consumer(index, entry.copy(chainDoor = block))
            }, { it.chainDoor }, { Modifier }, 1) {
                Text(
                    HSLang.chainDoorsChainDoor,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.chainDoorsChainDoorComment)
                )
            }

            Header {
                Text(
                    HSLang.chainDoorsKeyKeyToggleMode,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.chainDoorsKeyKeyToggleModeComment)
                )
            }.Column { index, _ ->
                val state = mutableStateOf(config[index].keyToggleMode)
                state.subscribe {
                    config[index] = config[index].copy(keyToggleMode = it)
                }
                SwitchButton(state)
            }

            Header {
                Text(
                    HSLang.chainDoorsStrategy,
                    setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally),
                    modifier = Modifier.hoverText(HSLang.chainDoorsStrategyComment)
                )
            }.Column { index, _ ->
                ChainStrategyEditorButton(config[index].strategy) {
                    config[index] = config[index].copy(strategy = it)
                    recompose()
                }
            }

            Header {
                Text(IGLang.remove)
            }.Column { index, entry ->
                DeleteButton(confirmMessage = { IGLang.removeConfirm("$index") }, recompose = recompose) {
                    config.removeAt(index)
                }
            }

        }

        ConfigResetButton(config) {
            this@Row.executeRecompose()
        }
    }
}

fun ContainerScope.ChainStrategyEditorButton(
    chainStrategy: ChainStrategy,
    modifier: Modifier = Modifier,
    editorModifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    consumer: (ChainStrategy) -> Unit
) = Button(modifier) {
    Text(IGLang.edit)
    click {
        ChainStrategyEditor(
            chainStrategy,
            modifier = editorModifier,
            screenModifier = screenModifier,
            consumer = consumer
        ).open()
    }
}

private val options = listOf(
    ChainStrategy.Neighborhood.text to ChainStrategy.Neighborhood.hoverText,
    ChainStrategy.Recursive.text to ChainStrategy.Recursive.hoverText
)

fun ChainStrategyEditor(
    chainStrategy: ChainStrategy,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    consumer: (ChainStrategy) -> Unit
): IGScreenImpl {
    return ConfirmDialog(
        HSLang.chainDoorsStrategy,
        modifier,
        screenModifier
    ) {

        var strategy = chainStrategy

        var neighborhood = strategy as? ChainStrategy.Neighborhood ?: ChainStrategy.Neighborhood.DEFAULT
        var recursive = strategy as? ChainStrategy.Recursive ?: ChainStrategy.Recursive.DEFAULT

        val supplier: MutableState<() -> ChainStrategy> = mutableStateOf { chainStrategy }

        onConfirm = {
            consumer(supplier.getValue()())
            closeScreen()
        }

        var recompose by lateInitValueOf<() -> Unit>()

        Row {
            RadioButtons(
                options,
                (if (strategy is ChainStrategy.Neighborhood) options[0] else options[1]).asMutableState,
                optionWrapper = { Text(it.first) },
                modifier = { Modifier.hoverText(it.second).hoverable { widget -> widget.visible } },
                onChange = {
                    when (it.first) {
                        ChainStrategy.Neighborhood.text -> {
                            recursive = supplier.getValue().invoke() as ChainStrategy.Recursive
                            strategy = neighborhood
                        }

                        ChainStrategy.Recursive.text    -> {
                            neighborhood = supplier.getValue().invoke() as ChainStrategy.Neighborhood
                            strategy = recursive
                        }
                    }
                    recompose()
                }
            )
        }
        DialogContent {
            content(strategy, supplier)
        }.apply {
            recompose = { this.executeRecompose() }
        }
    }
}

private fun BoxScope.content(strategy: ChainStrategy, supplierState: MutableState<() -> ChainStrategy>) {
    when (strategy) {
        is ChainStrategy.Neighborhood -> {
            Neighborhood(strategy, Modifier, supplierState)
        }

        is ChainStrategy.Recursive    -> {
            Recursive(strategy, Modifier, supplierState)
        }
    }
}

fun ContainerScope.Neighborhood(
    strategy: ChainStrategy.Neighborhood,
    modifier: Modifier = Modifier,
    supplierState: MutableState<() -> ChainStrategy>
) = Column(modifier, verticalArrangement = Arrangement.spacedBy(5f)) {
    val radius = strategy.radius.asMutableState
    val shape = strategy.shape.asMutableState
    val sameBlock = strategy.sameBlock.asMutableState
    val syncState = strategy.syncState.asMutableState
    val limit = strategy.limit.asMutableState

    EntryRow {
        Text(HSLang.chainDoorsStrategyRadius)
        SwitchableIntEditor(radius, 1..ChainStrategy.Neighborhood.MAX_RADIUS, 60f)
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategyShape)
        EnumSelector(shape, ChainStrategy.Neighborhood.Shape.entries, modifier = Modifier.width(60f))
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategySameBlock)
        SwitchButton(sameBlock)
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategySyncState)
        SwitchButton(syncState)
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategyLimit)
        SwitchableIntEditor(limit, 1..ChainStrategy.MAX_LIMIT, 60f)
    }
    supplierState.setValue {
        ChainStrategy.Neighborhood(
            radius = radius.getValue(),
            shape = shape.getValue(),
            sameBlock = sameBlock.getValue(),
            syncState = syncState.getValue(),
            limit = limit.getValue()
        )
    }
}

fun ContainerScope.Recursive(
    strategy: ChainStrategy.Recursive,
    modifier: Modifier = Modifier,
    supplierState: MutableState<() -> ChainStrategy>
) = Column(modifier, verticalArrangement = Arrangement.spacedBy(5f)) {
    val sameBlock = strategy.sameBlock.asMutableState
    val syncState = strategy.syncState.asMutableState
    val limit = strategy.limit.asMutableState
    EntryRow {
        Text(HSLang.chainDoorsStrategySameBlock)
        SwitchButton(sameBlock)
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategySyncState)
        SwitchButton(syncState)
    }
    EntryRow {
        Text(HSLang.chainDoorsStrategyLimit)
        SwitchableIntEditor(limit, 1..ChainStrategy.MAX_LIMIT, 60f)
    }
    supplierState.setValue {
        ChainStrategy.Recursive(
            sameBlock = sameBlock.getValue(),
            syncState = syncState.getValue(),
            limit = limit.getValue()
        )
    }
}


fun ContainerScope.EntryRow(
    modifier: Modifier = Modifier.width(200f),
    content: RowScope.() -> Unit
) = Row(modifier, horizontalArrangement = Arrangement.SpaceBetween, content = content)