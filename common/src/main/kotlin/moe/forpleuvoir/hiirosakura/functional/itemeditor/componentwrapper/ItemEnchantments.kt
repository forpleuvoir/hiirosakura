package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
import moe.forpleuvoir.hiirosakura.gui.widget.EnchatmentSelector
import moe.forpleuvoir.hiirosakura.gui.widget.REGISTERED_ENCHANTMENT
import moe.forpleuvoir.hiirosakura.util.asTranslateText
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.height
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverText
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.hoverTip
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.width
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.toast.Toast
import moe.forpleuvoir.ibukigourd.gui.widget.IntSlider
import moe.forpleuvoir.ibukigourd.gui.widget.SwitchableProxy
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Table
import moe.forpleuvoir.ibukigourd.gui.widget.layout.TableWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.IntEditor
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.ibukigourd.util.state.switch
import net.minecraft.core.Holder
import net.minecraft.resources.Identifier
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.ItemEnchantments

fun ContainerScope.ItemEnchantmentsComponentWrapper(
    key: Identifier,
    component: ItemEnchantments,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (ItemEnchantments, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = ItemEnchantments.Mutable(component).toImmutable()
    Button(
        modifier = Modifier
            .width(140f)
            .hoverTip {
                Column(horizontalAlignment = Alignment.Left) {
                    val list = component.entrySet().map { (enchantment, level) ->
                        enchantment.value().description to Translatable("enchantment.level.$level", level.toString())
                    }
                    if (list.isEmpty()) {
                        Text(IGLang.hasNothing)
                        return@Column
                    }
                    Table(list, rowGap = 10f) {
                        ColumnBuilder { (key, _) ->
                            Text(key, Modifier.align(Alignment.CenterLeft))
                        }
                        ColumnBuilder { (_, value) ->
                            Text(value)
                        }
                    }
                }
            }
    ) {
        Text(IGLang.listConfigWrapperText(component.enchantments.size))
        click {
            ItemEnchantmentsComponentEditor(key.asTranslateText(), component) { it, recompose ->
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
    component: ItemEnchantments,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (ItemEnchantments, Boolean) -> Unit,
): IGScreenImpl {
    val ces = component.enchantments.object2IntEntrySet()
    val enchantments = mutableListOf<Holder<Enchantment>>()
    val levels = mutableListOf<Int>()
    ces.sortedBy { it.key.registeredName }.forEach {
        enchantments.add(it.key)
        levels.add(it.intValue)
    }

    return DataComponentEditor(
        title,
        {
            val es = Object2IntOpenHashMap<Holder<Enchantment>>().apply {
                for (i in enchantments.indices) {
                    put(enchantments[i], levels[i])
                }
            }
            ItemEnchantments(es) to true
        },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        var recompose = {}
        val selectedEnchantment = mutableStateOf(REGISTERED_ENCHANTMENT.first())
        var toggle by lateInitValueOf {}
        EnchatmentSelector(
            selectedEnchantment,
            modifier = Modifier.hoverText(IGLang.add).width(140f),
            selectedWrapper = {
                Text("Add From Enchantments", modifier = Modifier.weight(1))
            },
            onSelected = { enchantment ->
                closeScreen()
                toggle()
                val list = enchantments.map { it.registeredName }
                if (enchantment.registeredName !in list) {
                    enchantments.add(enchantment)
                    levels.add(1)
                    recompose()
                } else {
                    Toast.showToast(HSLang.itemEditorEnchantmentExist(enchantment.value().description))
                }
            }) {
            toggle = { this.toggle() }
        }

        TableWrapped(
            enchantments.withIndex(),
            tableModifier = { Modifier.width(280f).height(140f) }
        ) {
            recompose = { executeRecompose() }

            Header(1) {
                Text("enchantment", setting = TextSetting(horizontalAlignment = Alignment.CenterHorizontally))
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
                Text("level")
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
                Text(text = IGLang.remove)
            }.Column { (index, _) ->
                DeleteButton(
                    confirmMessage = {
                        HSLang.deleteConfirm(
                            "${enchantments[index].value().description.string} ${
                                Translatable(
                                    "enchantment.level.${levels[index]}",
                                    levels[index].toString()
                                ).string
                            }"
                        )
                    },
                    recompose = { recompose() }
                ) {
                    enchantments.removeAt(index)
                    levels.removeAt(index)
                }
            }
        }

    }
}


