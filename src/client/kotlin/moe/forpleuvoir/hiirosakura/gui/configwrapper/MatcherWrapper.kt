package moe.forpleuvoir.hiirosakura.gui.configwrapper

import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcher
import moe.forpleuvoir.hiirosakura.config.items.ConfigBlockInfoMatcherMap
import moe.forpleuvoir.hiirosakura.config.items.ConfigItemStackMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatchEntry
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfoMatcher
import moe.forpleuvoir.hiirosakura.functional.misc.matcher.MultiMatcher
import moe.forpleuvoir.hiirosakura.gui.widget.*
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.IGLang.mapConfigWrapperText
import moe.forpleuvoir.ibukigourd.config.translateText
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
import moe.forpleuvoir.ibukigourd.gui.configwrapper.CONFIG_WRAPPER_TIP
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigColumnWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.RowListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.maxWidth
import moe.forpleuvoir.ibukigourd.util.forEachWithLimit
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.renameKey
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.collection.notifiableMap
import net.minecraft.util.math.BlockPos
import kotlin.time.Duration.Companion.seconds

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
                .hoverTtp { ItemStackMathcerInfo(value) }
        ) {
            ItemStackMathcerSimpleInfo(value).apply {
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
                .hoverTtp { BlockInfoMatcherInfo(value) }
        ) {
            BlockInfoMathcerSimpleInfo(value).apply {
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

//------------ BlockInfoMatcherMap ------------\\

fun WidgetContainerScope.BlockInfoMatcherMapWrapper(
    config: ConfigBlockInfoMatcherMap,
    modifier: Modifier = Modifier
) = ConfigColumnWrapper(config, modifier) {

    val mapValue = notifiableMap(config.getValue()).apply {
        subscribe {
            config.setValue(it)
        }
    }

    Column(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            Modifier.width(80f)
                .hoverTtp {
                    Row(
                        verticalArrangement = Arrangement.spacedBy(1f),
                        horizontalAlignment = Alignment.Left,
                    ) {
                        mapValue.forEachWithLimit(10) { k, v ->
                            Column(horizontalArrangement = Arrangement.spacedBy(2f)) {
                                TextLabel("$k => ")
                                BlockInfoMathcerSimpleInfo(stateOf(v))
                            }
                        }
                        if (mapValue.size > 10) TextLabel("...")
                        if (mapValue.isEmpty()) TextLabel(IGLang.hasNothing.plainText)
                    }
                }
        ) {
            TextLabel(mutableStateBy { mapConfigWrapperText(mapValue.size) })
            click {
                Dialog {
                    TextLabel(stateOf(config.translateText))
                    DialogContent(
                        Modifier.padding(5f, 3f, 5f, 5f)
                    ) {
                        RowListWrapped(
                            modifier = Modifier.disableRenderBackground().padding(0).minWidth(300f),
                            listModifier = { Modifier.height(160f) }
                        ) {
                            if (mapValue.isEmpty()) TextLabel(IGLang.hasNothing)
                            mapValue.forEach { key, value ->
                                Column(
                                    horizontalArrangement = Arrangement.spacedBy(2f)
                                ) {
                                    TextLabel(
                                        key, modifier = Modifier.width(mapValue.keys.maxWidth(mc.textRenderer).coerceIn(119, 239) + 1f)
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
                                                    if (mapValue.containsKey(newKey)) {
                                                        editor?.let {
                                                            TipHandler.pushTip(CONFIG_WRAPPER_TIP, 2.seconds, it, Tip {
                                                                TextLabel(IGLang.keyExists(newKey).withColor(Colors.RED))
                                                            })
                                                        }

                                                        return@ConfirmDialog
                                                    }
                                                    mapValue.renameKey(key, newKey)
                                                    mc.currentScreen?.close()
                                                    this@RowListWrapped.execute {
                                                        this@RowListWrapped.recompose()
                                                    }
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
                                        Modifier.width(180f)
                                            .hoverTtp { BlockInfoMatcherInfo(stateOf(value)) }
                                    ) {
                                        BlockInfoMathcerSimpleInfo(stateOf(value))
                                        click {
                                            BlockInfoMatcherBuilder(value, {
                                                mapValue[key] = it
                                                execute { this@RowListWrapped.recompose() }
                                            }).open()
                                        }
                                    }

                                    FlatButton(
                                        hoveredColor = Colors.LIGHT_RED,
                                        modifier = Modifier.margin(right = 2f).hoverText(IGLang.remove)
                                    ) {
                                        Icon(IconTextures.DELETE, Colors.RED, Modifier.size(10f, 10f))
                                        click {
                                            mapValue.remove(key)
                                            execute {
                                                this@RowListWrapped.recompose()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Button(
                        Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(40f)
                            .hoverText(IGLang.add)
                    ) {
                        Icon(IconTextures.PLUS, Color(0xFF2EE62E), Modifier.size(8f, 8f))
                        click {
                            mapValue["block matcher ${(mapValue.size)}"] = BlockInfoMatcher(MultiMatcher.MatchMode.AllMatch).apply {
                                mc.targetBlock
                                    ?.let {
                                        BlockInfoMatchEntry.Block(mc.world!!.getBlockState(BlockPos(it.pos.x(), it.pos.y(), it.pos.z()).down()).block)
                                    }?.let {
                                        addEntry(it)
                                    }
                            }
                            this@Dialog.recompose()
                        }
                    }
                }.open()
            }
        }
        ConfigResetButton(config) {
            mapValue.clear()
            mapValue.putAll(config.defaultValue)
        }
    }
}
