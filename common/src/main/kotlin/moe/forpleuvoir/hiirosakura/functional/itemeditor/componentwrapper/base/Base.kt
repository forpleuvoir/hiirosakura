package moe.forpleuvoir.hiirosakura.functional.itemeditor.componentwrapper.base

import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.logger

val unknownComponentType = identifier("unknown_component_type")

private val logger = logger("ComponentWrapper:Base")

//fun ContainerScope.IdentifierText(
//    key: Identifier,
//    style: Style? = Style.EMPTY,
//    modifier: Modifier = Modifier,
//    setting: TextSetting = TextSetting(),
//    scope: TextWidgetScope.() -> Unit = {}
//): TextWidget {
//    val text = key.asTranslateText().apply {
//        if (style != null) setStyle(style)
//    }
//    val commentKey = "${key.toLanguageKey()}.comment"
//    if (Language.getInstance().has(commentKey)) {
//        modifier.attachLeft {
//            hoverText(Translatable(commentKey))
//        }
//    }
//    return Text(text, modifier, setting, true, scope)
//}
//
//
//fun <C : Any> DataComponentEditor(
//    title: Component,
//    newComponent: () -> Pair<C, Boolean>,
//    onValueChange: (C, Boolean) -> Unit,
//    modifier: Modifier = Modifier,
//    screenModifier: Modifier = Modifier,
//    onConfirm: () -> Boolean = { true },
//    content: ColumnScope.() -> Unit
//) = ConfirmDialog(
//    title.asState,
//    modifier,
//    screenModifier,
//    onConfirm = {
//        if (onConfirm()) {
//            runCatching {
//                val (newComponent, recompose) = newComponent()
//                onValueChange(newComponent, recompose)
//            }.onFailure {
//                logger.error(it)
//                Toast.showToast(text = Literal(it.message ?: "Unknown Error").withStyle(ChatFormatting.RED))
//            }.onSuccess {
//                closeScreen()
//            }
//        }
//    }
//) {
//    content()
//}
//
//fun ContainerScope.DataComponentWrapperRow(
//    key: Identifier,
//    removeAction: () -> Unit,
//    modifier: Modifier = Modifier,
//    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(5f, Alignment.Right),
//    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
//    content: RowScope.() -> Unit
//) = Row(Modifier.bgHoverHighlightBox().padding(horizontal = 2f).minHeight(19f).then(modifier), horizontalArrangement = Arrangement.SpaceBetween) {
//    val style = DataComponentWrappers.isAdaptedComponent(key).either(
//        style(HSVColor(195f, 1f, 1f)),
//        style(HSVColor(5f, .6f, 1f))
//    )
//    IdentifierText(key, style, Modifier.weight(1))
//    Row(Modifier, horizontalArrangement, verticalAlignment) {
//        content()
//        DeleteButton(
//            confirmMessage = { HSLang.deleteConfirm(key) },
//            recompose = { }
//        ) {
//            removeAction()
//            this@DataComponentWrapperRow.executeRecompose()
//        }
//    }
//}