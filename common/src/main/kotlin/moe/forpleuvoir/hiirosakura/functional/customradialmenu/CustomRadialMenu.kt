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
import moe.forpleuvoir.ibukigourd.input.InputHandler
import moe.forpleuvoir.ibukigourd.input.KeyBind
import moe.forpleuvoir.ibukigourd.input.KeyBindSetting
import moe.forpleuvoir.ibukigourd.input.KeyTriggerMode
import moe.forpleuvoir.ibukigourd.input.Mouse.*
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.enableReturnHotkey
import moe.forpleuvoir.ibukigourd.mod.config.GuiConfig.PopupScreen.returnHotkeyKeycode
import moe.forpleuvoir.ibukigourd.task.scheduleStartTick
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.size
import moe.forpleuvoir.ibukigourd.util.NextAction
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.util.primitive.either
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
                it.tryUse(it.button == RIGHT || (enableReturnHotkey && InputHandler.wasKeyPressed(returnHotkeyKeycode))).onSuccess {
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
            idleInnerColor = stateOf(setting.innerColor),
            idleOuterColor = stateOf(setting.outerColor),
            selectedInnerColor = stateOf(setting.innerSelectedColor),
            selectedOuterColor = stateOf(setting.outerSelectedColor),
            innerRadius = setting.innerRadius,
            outerRadius = setting.outerRadius,
            optionRadius = setting.optionRadius,
            gap = setting.gap,
            optionCount = setting.pageSize,
            onMousePress = { mouse, task ->
                when (mouse) {
                    LEFT   -> task?.let {
                        closeScreen()
                        task.execute()
                    } ?: run {
                        TaskEditor(IconTickTask.empty, screenModifier = Modifier.renderParent(false)) { task ->
                            tasks.add(task as IconTickTask)
                            parent()?.let { p ->
                                if (p is GuiWidgetContainer) p.executeRecompose()
                            }
                        }.open()
                    }

                    RIGHT  -> task?.let {
                        TaskEditor(task, screenModifier = Modifier.renderParent(false)) { tickTask ->
                            task.fromTask(tickTask)
                        }.open()
                    }

                    MIDDLE -> task?.let { task ->
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

                    else   -> Unit
                }
            },
            selectedRenderer = { task, guiGraphics, position, _, _, _ ->
                guiGraphics.pushAlignmentText(
                    task?.nameAsInlineStyleText ?: HSLang.taskUnSelected,
                    Box(position.x() - 40f, position.y() - 20f, Size(80f, 40f)),
                    color = Colors.WHITE
                )
            },
        ) { task, guiGraphics, selected, position, _, _, _ ->
            guiGraphics {
                if (task.icon != Items.AIR) {
                    val s = selected.either(1.2f, 1f)
                    pushItem(task.iconStack, (position.x() - (width * s) / 2f), position.y() - (height * s) / 2f, scale * s)
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