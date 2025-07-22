package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

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
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.DialogContent
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextArea
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.text.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Color
import net.minecraft.component.type.LoreComponent
import net.minecraft.util.Identifier
import kotlin.time.Duration.Companion.milliseconds

fun ContainerScope.LoreComponentWrapper(
    id: Identifier,
    component: LoreComponent,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (LoreComponent, Boolean) -> Unit,
) = DataComponentWrapperRow(id, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var component = component
    Button(
        modifier = Modifier
            .minWidth(140f)
            .hoverTip {
                Column(horizontalAlignment = Alignment.Left) {
                    if (component.lines.isEmpty()) {
                        TextLabel(IGLang.hasNothing)
                    }
                    for (text in component.lines) {
                        TextLabel(text.copyToText())
                    }
                }
            }
    ) {
        TextLabel(IGLang.listConfigWrapperText(component.lines.size))
        click {
            LoreComponentEditor(id.asTranslateText(), component) { it, recompose ->
                if (component != it) {
                    component = it
                    onValueChange(component, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}

//TODO 如果可以的话,写一个正经的文本编辑器替换掉字符串的方式
fun LoreComponentEditor(
    title: Text,
    component: LoreComponent,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (LoreComponent, Boolean) -> Unit,
): IGScreenImpl {
    val lines = component.lines.map { InlineStyleTextParser.inline(it) }.toMutableList()
    return DataComponentWrapperDialog(
        title,
        { LoreComponent(lines.map { InlineStyleTextParser.parse(it) }) to true },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        var recompose = {}
        DialogContent(
            Modifier.padding(5f, 3f, 5f, 5f)
        ) {
            ColumnListWrapped(
                modifier = Modifier.disableRenderBackground().padding(0).minWidth(280f),
                listModifier = { Modifier.height(160f) }
            ) {
                recompose = { this.executeRecompose() }
                if (lines.isEmpty()) TextLabel(IGLang.hasNothing)
                lines.forEachIndexed { index, entry ->
                    Row(Modifier, horizontalArrangement = Arrangement.spacedBy(2f)) {
                        MoveButton(recompose, lines, index)

                        Button(Modifier.width(280f).minHeight(19f)) {
                            TextLabel(entry)
                            click {
                                val stringState = mutableStateOf(entry)
                                DataComponentWrapperDialog(
                                    title,
                                    { stringState.getValue() to true },
                                    { it, recompose ->
                                        lines[index] = stringState.getValue()
                                        if (recompose) recompose()
                                    },
                                    modifier = modifier,
                                    screenModifier = screenModifier
                                ) {
                                    TextArea(Modifier.size(280f, 100f)) { bindState(stringState) }
                                    Box(Modifier.matchSibling().height(30f).margin(top = 5f)) {
                                        TextLabel(mutableStateBy {
                                            InlineStyleTextParser.parse(stringState.getValue(), InlineStyleTextParser.noneEventModifier).copyToText()
                                        }, setting = TextSetting(textLabelUpdateInterval = 1.milliseconds))
                                    }
                                }.open()
                            }
                        }

                        RemoveButton {
                            lines.removeAt(index)
                            recompose()
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
                if (lines.size <= LoreComponent.MAX_LORES) {
                    lines.addLast("")
                    recompose()
                }
            }
        }
    }
}


