package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import moe.forpleuvoir.hiirosakura.gui.widget.ENCHANTMENT_LIST
import moe.forpleuvoir.hiirosakura.gui.widget.EnchatmentSelector
import moe.forpleuvoir.hiirosakura.gui.widget.RemoveButton
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.widget.IntSlider
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.SwitchButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Table
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.component.type.ItemEnchantmentsComponent
import net.minecraft.enchantment.Enchantment
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.util.Identifier

fun ContainerScope.ItemEnchantmentsComponentWrapper(
    id: Identifier,
    component: ItemEnchantmentsComponent,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (ItemEnchantmentsComponent, Boolean) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = ItemEnchantmentsComponent.Builder(component).build()
    Button(
        modifier = Modifier
            .minWidth(120f)
            .hoverTip {
                Column(horizontalAlignment = Alignment.Left) {
                    val list = listOf(Literal("showInTooltip") to Literal(component.showInTooltip.toString())) +
                            component.enchantmentEntries.map { (enchantment, level) ->
                                enchantment.value().description.copyToText() to Translatable("enchantment.level.$level", level.toString())
                            }
                    Table(list, rowGap = 10f) {
                        ColumnBuilder { (key, _) ->
                            TextLabel(key, Modifier.align(Alignment.CenterLeft))
                        }
                        ColumnBuilder { (_, value) ->
                            TextLabel(value)
                        }
                    }
                }
            }
    ) {
        TextLabel(IGLang.listConfigWrapperText(component.enchantments.size))
        click {
            ItemEnchantmentsComponentEditor(id.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

fun ItemEnchantmentsComponentEditor(
    title: Text,
    component: ItemEnchantmentsComponent,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (ItemEnchantmentsComponent, Boolean) -> Unit,
): IGScreenImpl {
    val ces = component.enchantments.object2IntEntrySet()
    val enchantments = mutableListOf<RegistryEntry<Enchantment>>()
    val levels = mutableListOf<Int>()
    ces.sortedBy { it.key.idAsString }.forEach {
        enchantments.add(it.key)
        levels.add(it.intValue)
    }

    val showInTooltip = component.showInTooltip.asMutableState
    return DataComponentWrapperDialog(
        title,
        {
            val es = Object2IntOpenHashMap<RegistryEntry<Enchantment>>().apply {
                for (i in enchantments.indices) {
                    put(enchantments[i], levels[i])
                }
            }
            ItemEnchantmentsComponent(es, showInTooltip.getValue()) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        var recompose = {}
        Row(Modifier.matchSibling(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                TextLabel("show_in_tooltip")
                SwitchButton(showInTooltip)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                val selectedEnchantment = mutableStateOf(ENCHANTMENT_LIST.first())
                EnchatmentSelector(selectedEnchantment)
                Button(
                    Modifier.hoverText(IGLang.add)
                ) {
                    Icon(IconTextures.PLUS, Color(0xFF2EE62E), Modifier.size(8f, 8f))
                    click {
                        val list = enchantments.map { it.idAsString }
                        if (selectedEnchantment.getValue().idAsString !in list) {
                            enchantments.add(selectedEnchantment.getValue())
                            levels.add(1)
                            recompose()
                        }
                    }
                }
            }
        }

        TableWrapped(
            enchantments.withIndex(),
            tableModifier = { Modifier.width(280f).height(140f) }
        ) {
            recompose = { executeRecompose() }

            Header(1) {
                TextLabel("enchantment", setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally))
            }.Column { (index, enchantment) ->
                val state = enchantment.asMutableState
                state.onSetValue = { entry ->
                    enchantments[index] = entry
                    recompose()
                    entry
                }
                EnchatmentSelector(state, modifier = Modifier.width(555f))
            }

            Header {
                TextLabel("level")
            }.Column { (index, _) ->
                Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                    val valueState = mutableStateOf(levels[index])
                    valueState.subscribe {
                        levels[index] = it
                    }
                    val state = mutableStateOf(false)
                    SwitchableProxy(
                        {
                            IntSlider(valueState, 1..255, textMapper = { l ->
                                Translatable("enchantment.level.$l", l.toString())
                            }, modifier = Modifier.width(60f))
                        },
                        {
                            IntEditor(valueState, 1..255, modifier = Modifier.width(60f), editorModifier = { Modifier.weight(1) })
                        },
                        state
                    )
                    Button {
                        click { state.switch() }
                        Icon(IconTextures.SWITCH)
                    }
                }
            }

            Header {
                TextLabel(text = IGLang.remove)
            }.Column { (index, _) ->
                RemoveButton {
                    enchantments.removeAt(index)
                    levels.removeAt(index)
                    recompose()
                }
            }
        }

    }
}


