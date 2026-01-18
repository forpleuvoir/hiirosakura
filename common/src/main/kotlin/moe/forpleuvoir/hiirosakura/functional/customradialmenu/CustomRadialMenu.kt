package moe.forpleuvoir.hiirosakura.functional.customradialmenu

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.functional.task.IconTickTask
import moe.forpleuvoir.hiirosakura.functional.task.TaskEditor
import moe.forpleuvoir.hiirosakura.gui.widget.radialmenu.RadialMenu
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.attachLeft
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.keyPress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.minWidth
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.mousePress
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.renderParent
import moe.forpleuvoir.ibukigourd.gui.base.render.Size
import moe.forpleuvoir.ibukigourd.gui.base.render.shape.box.Box
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.screen.closeScreen
import moe.forpleuvoir.ibukigourd.gui.base.widget.GuiWidgetContainer
import moe.forpleuvoir.ibukigourd.gui.base.widget.executeRecompose
import moe.forpleuvoir.ibukigourd.gui.screen.BoxScreen
import moe.forpleuvoir.ibukigourd.gui.widget.ConfirmDialog
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.input.*
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.enableReturnHotkey
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.returnHotkeyKeycode
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.util.NextAction
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.Serializable
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.world.item.Items

class CustomRadialMenu(
    shortcuts: KeyBind = KeyBind(
        defaultSetting = KeyBindSetting {
            nextAction = NextAction.Continue
            exactMatch = false
            triggerMode = KeyTriggerMode.OnPress
        }
    ),
    var setting: RadialMenuSetting = RadialMenuSetting(),
    tasks: List<IconTickTask>
) : Serializable {

    companion object : Deserializer<CustomRadialMenu> {
        override fun deserialization(serializeElement: SerializeElement): CustomRadialMenu =
            serializeElement.checkType<SerializeObject, CustomRadialMenu> {
                CustomRadialMenu(
                    KeyBind(
                        defaultSetting = KeyBindSetting {
                            nextAction = NextAction.Continue
                            exactMatch = false
                            triggerMode = KeyTriggerMode.OnPress
                        }
                    ).apply { deserialization(it["shortcuts"]!!) },
                    RadialMenuSetting.deserialization(it["setting"]!!),
                    it["tasks"]!!.asArray.map { task -> IconTickTask.deserialization(task) }
                )
            }.getOrThrow()
    }

    val shortcuts: KeyBind = shortcuts.apply { action = { screen().open() } }

    val tasks: ArrayList<IconTickTask> = ArrayList(tasks)

    fun onLoad() {
        InputHandler.register(shortcuts)
    }

    fun onUnload() {
        InputHandler.unregister(shortcuts)
    }

    fun screen(modifier: Modifier = Modifier) = BoxScreen(
        modifier.attachLeft {
            mousePress {
                onMousePress(it)
                it.tryUse(it.button == Mouse.RIGHT || (enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode))).onSuccess {
                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
                }

            }.keyPress {
                onKeyPress(it)
                it.tryUse(enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode)).onSuccess {
                    mc.scheduleStartTick(1) { _, _ -> closeScreen() }
                }
            }
        }
    ) {
        val scale = setting.iconScale
        val (width, height) = 16f * scale to 16f * scale
        RadialMenu(
            tasks,
            idleColor = stateOf(setting.color),
            selectedColor = stateOf(setting.selectedColor),
            innerRadius = setting.innerRadius,
            outerRadius = setting.outerRadius,
            optionRadius = setting.optionRadius,
            gapDistance = setting.gap,
            maxOptions = setting.pageSize,
            onLeftPressSelected = {
                it?.let {
                    closeScreen()
                    it.execute()
                } ?: run {
                    TaskEditor(IconTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
                        tasks.add(task as IconTickTask)
                        parent()?.let { p ->
                            if (p is GuiWidgetContainer) p.executeRecompose()
                        }
                    }.open()
                }
            },
            onRightPressSelected = {
                it?.let { task ->
                    TaskEditor(task, screenModifier = Modifier.renderParent(false)) { tickTask ->
                        task.fromTask(tickTask)
                    }.open()
                }
            },
            onMiddlePressSelected = {
                it?.let { task ->
                    ConfirmDialog(
                        stateOf(IGLang.remove),
                        onConfirm = {
                            tasks.remove(task)
                            parent()?.let { p ->
                                if (p is GuiWidgetContainer) p.executeRecompose()
                            }
                            closeScreen()
                        }
                    ) {
                        Text(InlineStyleText(task.name), Modifier.minWidth(120f))
                    }.open()
                }
            },
            selectedRenderer = { task, guiGraphics, position, _, _, _ ->
                guiGraphics.pushAlignmentText(
                    task?.nameAsInlineStyleText ?: HSLang.taskUnSelected,
                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
                    color = Colors.WHITE
                )
            },
        ) { task, guiGraphics, _, position, _, _, _ ->
            guiGraphics {
                if (task.icon != Items.AIR) {
                    pushItem(task.iconStack, (position.x() - width / 2f), position.y() - height / 2f, scale)
                } else {
                    val size = task.nameAsInlineStyleText.size
                    pushAlignmentText(
                        task.nameAsInlineStyleText,
                        Box(position.x() - size.halfWidth, position.y() - size.halfHeight, size),
                        color = Colors.WHITE
                    )
                }
            }
        }
    }

    override fun serialization(): SerializeElement = serializeObject {
        "shortcuts" to shortcuts
        "setting" to setting
        "tasks" to tasks
    }

}