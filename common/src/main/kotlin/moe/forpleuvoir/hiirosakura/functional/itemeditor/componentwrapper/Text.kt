package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper

import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentEditor
import moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base.DataComponentWrapperRow
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
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Box
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextArea
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextSetting
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.inlinestyletext.InlineStyleTextParser
import moe.forpleuvoir.ibukigourd.util.state.asMutableState
import moe.forpleuvoir.ibukigourd.util.state.mutableStateBy
import net.minecraft.resources.ResourceLocation
import kotlin.time.Duration.Companion.milliseconds

fun ContainerScope.TextComponentWrapper(
    key: ResourceLocation,
    component: Text,
    removeAction: () -> Unit,
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    onValueChange: (Text, Boolean) -> Unit,
) = DataComponentWrapperRow(key, removeAction, modifier, horizontalArrangement, verticalAlignment) {
    var text = component
    Box(
        Modifier.width(115f).padding(4f).renderBackground { guiGraphics, x, y, d ->
            guiGraphics.pushWidgetTexture(transform, WidgetTextures.DROP_DOWN_MENU_BACKGROUND)
        }
    ) {
        Text(text)
    }
    Button(
        Modifier.hoverText(IGLang.edit)
    ) {
        Icon(IconTextures.EDIT)
        click {
            TextComponentEditor(key.asTranslateText(), text) { it, recompose ->
                if (text != it) {
                    text = it
                    onValueChange(text, recompose)
                    if (recompose) executeRecompose()
                }
            }.open()
        }
    }
}


//TODO 如果可以的话,写一个正经的文本编辑器替换掉字符串的方式
fun TextComponentEditor(
    title: Text,
    component: Text,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    onValueChange: (Text, Boolean) -> Unit,
): IGScreenImpl {
    val stringState = InlineStyleTextParser.inline(component).asMutableState
    return DataComponentEditor(
        title,
        { InlineStyleTextParser.parse(stringState.getValue(), InlineStyleTextParser.noneEventModifier) to true },
        onValueChange,
        modifier = modifier,
        screenModifier = screenModifier
    ) {
        TextArea(Modifier.size(280f, 100f)) { bindState(stringState) }
        Box(Modifier.matchSibling().height(30f).margin(top = 5f)) {
            Text(mutableStateBy {
                InlineStyleTextParser.parse(stringState.getValue(), InlineStyleTextParser.noneEventModifier)
            }, setting = TextSetting(textLabelUpdateInterval = 1.milliseconds))
        }
    }
}
