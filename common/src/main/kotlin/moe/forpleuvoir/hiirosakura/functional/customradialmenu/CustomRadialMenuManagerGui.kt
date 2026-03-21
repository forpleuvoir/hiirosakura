package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.TaskEditor
import moe.forpleuvoir.hiirosakura.gui.widget.EditButton
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableFloatEditor
import moe.forpleuvoir.hiirosakura.gui.widget.SwitchableIntEditor
import moe.forpleuvoir.hiirosakura.gui.widget.WrappedBox
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.scope.GuiScope.Companion.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.tip.Tip
import moe.forpleuvoir.ibukigourd.gui.base.tip.TipHandler
import moe.forpleuvoir.ibukigourd.gui.base.tip.popHoverTip
import moe.forpleuvoir.ibukigourd.gui.base.tip.pushHoverTip
import moe.forpleuvoir.ibukigourd.gui.configwrapper.MoveButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.getContrastColor
import moe.forpleuvoir.ibukigourd.gui.modifier.bgHoverHighlightBox
import moe.forpleuvoir.ibukigourd.gui.modifier.disableRenderBackground
import moe.forpleuvoir.ibukigourd.gui.widget.*
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.ColorButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.DeleteButton
import moe.forpleuvoir.ibukigourd.gui.widget.button.FlatButton
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.icon.IconTextures
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.layout.list.ColumnListWrapped
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextEditor
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.withColor
import moe.forpleuvoir.ibukigourd.util.lateInitValueOf
import moe.forpleuvoir.ibukigourd.util.state.*
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.CustomRadialMenuManagerGui(
    modifier: Modifier = Modifier
) = Row(
    modifier,
    horizontalArrangement = Arrangement.spacedBy(5f)
) {


    var currentMenuName = CustomRadialMenuManager.customRadialMenus.firstEntry()?.key ?: ""
    var currentMenu = CustomRadialMenuManager.customRadialMenus.firstEntry()?.value ?: CustomRadialMenu(tasks = emptyList())

    var menuListRecompose by lateInitValueOf<() -> Unit>()
    var taskListRecompose by lateInitValueOf<() -> Unit>()

    Column(Modifier.maxWidth(120f), verticalArrangement = Arrangement.spacedBy(3f)) {
        Button(Modifier.hoverText(HSLang.customRadialMenuAdd).fill()) {
            Icon(IconTextures.PLUS)
            click {
                ConfirmDialog(HSLang.customRadialMenuAdd, screenModifier = Modifier.onClose {
                    TipHandler.popHoverTip()
                }) {
                    val name = "menu".asMutableState
                    val editor = TextEditor(Modifier.width(160f)) { bindState(name) }
                    onConfirm = {
                        if (name.getValue() in CustomRadialMenuManager.customRadialMenus.keys) {
                            TipHandler.pushHoverTip(2.seconds, { editor.transform }, Tip {
                                Text(HSLang.customRadialMenuExists(name.getValue()))
                            })
                        } else {
                            CustomRadialMenuManager.customRadialMenus[name.getValue()] = CustomRadialMenu(tasks = emptyList()).apply {
                                shortcuts.name(Literal(name.getValue()))
                            }
                            closeScreen()
                            menuListRecompose()
                        }
                    }
                }.open()
            }
        }
        ColumnListWrapped(
            modifier = Modifier.fill(),
            listModifier = { Modifier.fill() },
            spacing = 1f,
            onCreate = { menuListRecompose = { executeRecompose() } }
        ) {
            val hoveredColor = Colors.CYAN.alpha(0.25f).asState
            val pressedColor = Colors.CYAN.alpha(0.5f).asState
            val idleColor = { key: String ->
                if (currentMenuName == key) Colors.CYAN.alpha(0.25f) else Colors.BLACK.alpha(0f)
            }

            CustomRadialMenuManager.customRadialMenus.forEach { (name, menu) ->
                val deleteVisitor = mutableStateOf(false)
                FlatButton(
                    modifier = Modifier.fill()
                        .mouseEnter { deleteVisitor.setValue(true) }
                        .mouseLeave { deleteVisitor.setValue(false) },
                    hoveredColor = hoveredColor,
                    pressedColor = pressedColor,
                    idleColor = mutableStateBy { idleColor(name) },
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(InlineStyleText(name))
                    DeleteButton(
                        modifier = Modifier.priority(10).active(deleteVisitor).visible(deleteVisitor),
                        iconColor = Colors.GRAY,
                        confirmMessage = {
                            IGLang.removeConfirm(name)
                        }, recompose = {
                            taskListRecompose()
                            menuListRecompose()
                        }) {
                        currentMenuName = ""
                        currentMenu = CustomRadialMenu(tasks = emptyList())
                        CustomRadialMenuManager.customRadialMenus.remove(name)
                    }
                    click {
                        if (currentMenuName != name) {
                            currentMenuName = name
                            currentMenu = menu
                        }
                        taskListRecompose()
                    }
                }
            }

        }
    }


    Column(verticalArrangement = Arrangement.spacedBy(3f)) {
        //toolbar
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fill()) toolbar@{
            //Name
            WrappedBox(Modifier.padding(4f, 3f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(InlineStyleText(currentMenuName), modifier = Modifier.width(120f))
                    Row(horizontalArrangement = Arrangement.spacedBy(2f)) {
                        Rect(Colors.GRAY.alpha(.45f), Modifier.height(12f).width(1f))
                        EditButton(
                            modifier = Modifier.hoverText(HSLang.customRadialMenuEditName)
                        ) {
                            ConfirmDialog(HSLang.customRadialMenuEditName, screenModifier = Modifier.onClose {
                                TipHandler.popHoverTip()
                            }) {
                                val name = currentMenuName.asMutableState
                                val editor = TextEditor(Modifier.width(160f)) { bindState(name) }
                                onConfirm = {
                                    if (name.getValue() in CustomRadialMenuManager.customRadialMenus.keys) {
                                        TipHandler.pushHoverTip(2.seconds, { editor.transform }, Tip {
                                            Text(HSLang.customRadialMenuExists(name.getValue()))
                                        })
                                    } else {
                                        CustomRadialMenuManager.rename(currentMenuName, name.getValue())
                                        currentMenuName = name.getValue()
                                        closeScreen()
                                        this@toolbar.executeRecompose()
                                        menuListRecompose()
                                    }
                                }
                            }.open()
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(5f)) {
                //setting
                Button(Modifier.hoverText(HSLang.customRadialMenuSetting)) {
                    Icon(IconTextures.SETTING)
                    click {
                        RadialMenuSettingDialog(currentMenu.setting) { currentMenu.setting = it }.open()
                    }
                }
                //shortcut
                KeyBindSetterButton(currentMenu.shortcuts)
                KeyBindSettingSetterButton(currentMenu.shortcuts, Literal(currentMenuName))
                //add
                Button(Modifier.hoverText(HSLang.customRadialMenuEditName)) {
                    Icon(IconTextures.PLUS)
                    click {
                        TaskEditor(IconTickTask.empty, newTaskConsumer = {
                            currentMenu.tasks.add(it as IconTickTask)
                            taskListRecompose()
                        }).open()
                    }
                }
            }
        }

        ColumnListWrapped(
            modifier = Modifier.fill(),
            listModifier = { Modifier.fill() },
            spacing = 2f,
            onCreate = { taskListRecompose = { executeRecompose() } }
        ) {
            currentMenu.tasks.forEach { task ->
                Row(
                    modifier = Modifier.fill()
                        .padding(horizontal = 2f, 1f)
                        .bgHoverHighlightBox(),
                    horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
                ) TaskEntry@{
                    //Move
                    MoveButton({ taskListRecompose() }, currentMenu.tasks, currentMenu.tasks.indexOf(task))
                    //Icon
                    ItemIcon(task.icon)
                    //Name
                    Row(
                        modifier = Modifier.weight(1),
                        horizontalArrangement = Arrangement.Left
                    ) {
                        Text(InlineStyleText(task.name))
                    }
                    //run
                    FlatButton(
                        hoveredColor = Colors.LIME.alpha(0.25f),
                        modifier = Modifier.hoverText(HSLang.taskExecute).size(14f, 14f)
                    ) {
                        click { task.execute() }
                        Icon(IconTextures.RIGHT, HSVColor(120f, 1f, 0.5f))
                    }
                    //edit
                    FlatButton(
                        hoveredColor = Colors.LIME.alpha(0.25f),
                        modifier = Modifier.hoverText(IGLang.edit).size(14f, 14f)
                    ) {
                        click {
                            TaskEditor(task) {
                                task.fromTask(it)
                                this@TaskEntry.executeRecompose()
                            }.open()
                        }
                        Icon(IconTextures.EDIT, modifier = Modifier.size(12f, 12f))
                    }
                    //delete
                    FlatButton(
                        hoveredColor = Colors.RED.alpha(0.25f),
                        modifier = Modifier.hoverText(IGLang.remove).size(14f, 14f)
                    ) {
                        click {
                            currentMenu.tasks.remove(task)
                            taskListRecompose()
                        }
                        Icon(IconTextures.DELETE, HSVColor(0f, .1f, .25f), modifier = Modifier.size(12f, 12f))
                    }
                }
            }
        }
    }

}

fun RadialMenuSettingDialog(
    setting: RadialMenuSetting,
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    consumer: (RadialMenuSetting) -> Unit
) = ConfirmDialog(HSLang.customRadialMenuSetting, modifier, screenModifier) {
    val innerColor = setting.innerColor.asMutableState
    val outerColor = setting.outerColor.asMutableState
    val innerSelectedColor = setting.innerSelectedColor.asMutableState
    val outerSelectedColor = setting.outerSelectedColor.asMutableState
    val iconScale = setting.iconScale.asMutableState
    val innerRadius = setting.innerRadius.asMutableState
    val outerRadius = setting.outerRadius.asMutableState
    val optionRadius = setting.optionRadius.asMutableState
    val gap = setting.gap.asMutableState
    val pageSize = setting.pageSize.asMutableState

    onConfirm = {
        consumer(
            RadialMenuSetting(
                innerColor = innerColor.getValue(),
                outerColor = outerColor.getValue(),
                innerSelectedColor = innerSelectedColor.getValue(),
                outerSelectedColor = outerSelectedColor.getValue(),
                iconScale = iconScale.getValue(),
                innerRadius = innerRadius.getValue(),
                outerRadius = outerRadius.getValue(),
                optionRadius = optionRadius.getValue(),
                gap = gap.getValue(),
                pageSize = pageSize.getValue()
            )
        )
        closeScreen()
    }
    ColumnListWrapped(spacing = 5f, listModifier = { Modifier.width(300f) }) {
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingInnerColor)
            ColorSettingButton(color = innerColor, Modifier.minWidth(80f))
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingOuterColor)
            ColorSettingButton(color = outerColor, Modifier.minWidth(80f))
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingInnerSelectedColor)
            ColorSettingButton(color = innerSelectedColor, Modifier.minWidth(80f))
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingOuterSelectedColor)
            ColorSettingButton(color = outerSelectedColor, Modifier.minWidth(80f))
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingIconScale)
            SwitchableFloatEditor(iconScale, 0.2f..2f, width = 80f)
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingInnerRadius)
            SwitchableFloatEditor(innerRadius, 40f..100f, width = 80f)
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingOuterRadius)
            SwitchableFloatEditor(outerRadius, 100f..200f, width = 80f)
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingOptionRadius)
            SwitchableFloatEditor(optionRadius, 50f..190f, width = 80f)
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingGap)
            SwitchableFloatEditor(gap, 0f..5f, width = 80f)
        }
        Row(Modifier.fill(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(HSLang.customRadialMenuSettingPageSize)
            SwitchableIntEditor(pageSize, 4..12, width = 80f)
        }
    }
}


fun ContainerScope.ColorSettingButton(
    color: MutableState<ARGBColor>,
    modifier: Modifier = Modifier
) = ColorButton(color, modifier) {
    val text = mutableStateOf(color) {
        Literal(it.hexStr).withColor(getContrastColor(it))
    }
    Text(text)
    click {
        Dialog(
            Modifier.disableRenderBackground()
        ) {
            ColorPicker(color)
        }.open()
    }
}