package moe.forpleuvoir.hiirosakura.functional.chataddons.chatbubble

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.gui.widget.ExpandableTextEditor
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.extensions.guigraphics.useScissor
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.*
import moe.forpleuvoir.ibukigourd.gui.base.scope.ContainerScope
import moe.forpleuvoir.ibukigourd.gui.base.screen.IGScreenImpl.Companion.open
import moe.forpleuvoir.ibukigourd.gui.base.widget.WidgetTextures
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigResetButton
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigRowWrapper
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigsWrapper
import moe.forpleuvoir.ibukigourd.gui.widget.Canvas
import moe.forpleuvoir.ibukigourd.gui.widget.Dialog
import moe.forpleuvoir.ibukigourd.gui.widget.button.Button
import moe.forpleuvoir.ibukigourd.gui.widget.button.LockButton
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.Text
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.client.gui.screens.inventory.InventoryScreen
import net.minecraft.world.entity.player.Player
import org.joml.Vector3f
import kotlin.time.Duration.Companion.seconds

fun ContainerScope.ChatBubbleConfigGui(
    config: ChatBubbleHandler,
    modifier: Modifier = Modifier
) = ConfigRowWrapper(config, modifier) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5f)
    ) {
        Button(
            modifier = Modifier.width(80f)
        ) {
            Text(IGLang.setting)
            click {
                Dialog {
                    Text(config.translateText)
                    Row(
                        Modifier.padding(0f)
                    ) {
                        mc.player?.let {
                            Preview(it, Modifier.width(145f).height(262f).margin(right = 5f))
                        }
                        ConfigsWrapper(
                            config.configs(),
                            modifier = Modifier
                                .maxWidth(400f)
                                .maxHeight(262f)
                                .renderBackground { guiGraphics, x, y, d ->
                                    guiGraphics {
                                        pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_OUTLINE, Color.ofRGB(0xF4D9FF))
                                        pushWidgetTexture(transform, WidgetTextures.DIALOG_CONTENT_INNER, Colors.WHITE)
                                    }
                                }
                                .padding(4),
                            listModifier = {
                                Modifier.weight(1)
                            }
                        )
                    }
                }.open()
            }
        }
        ConfigResetButton(config)
    }
}

private fun ContainerScope.Preview(
    player: Player,
    modifier: Modifier = Modifier
) = Column(modifier, verticalArrangement = Arrangement.spacedBy(2f)) {
    val msg = mutableStateOf("Hello Hiirosakura")
    val lock = mutableStateOf(true)
    val chatBubble = mutableStateOf(
        ChatBubble(
            message = msg.getValue(),
            duration = if (lock.getValue()) 114514.seconds else ChatBubbleHandler.duration
        )
    )
    PreviewCanvas(Modifier.width(145f).weight(1), player, 1f, chatBubble)
    ExpandableTextEditor(msg, HSLang.message, Modifier.matchSibling(), textEditorModifier = { Modifier.weight(1) })
    Row(Modifier.matchSibling()) {
        Button(Modifier.weight(1)) {
            Text(HSLang.send)
            click {
                chatBubble.setValue(
                    ChatBubble(
                        message = msg.getValue(),
                        duration = if (lock.getValue()) 114514.seconds else ChatBubbleHandler.duration
                    )
                )
            }
        }
        LockButton(lock, modifier = Modifier.hoverText(HSLang.chatBubblePreviewLock))
    }
}

private fun ContainerScope.PreviewCanvas(
    modifier: Modifier = Modifier,
    player: Player,
    scale: Float = 1f,
    chatBubble: State<ChatBubble>
) = Canvas(modifier) { guiGraphics, x, y, d ->
    val box = transform.asWorldCoordinateBox
    guiGraphics {
        pushRoundBox(box, Color(244, 217, 255), 4)
        pushRoundBox(box.trimEdges(1f), HSVColor(270f, 0.25f, 0.5f), 4)
        val yOffset = pose().transform(1f, 1f, 1f, Vector3f()).y()
        val box = box.trimEdges(top = box.height * 0.4f)
        val bubble = chatBubble.getValue()
        useScissor(transform.asWorldCoordinateBox) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                guiGraphics,
                box.x.toInt(),
                (box.y + yOffset).toInt(),
                box.right.toInt(),
                (box.bottom + yOffset).toInt(),
                (scale * 50f).toInt(),
                0.0625f,
                x,
                y,
                player
            )
            if (!bubble.shouldRemove) {
                bubble.renderInGui(guiGraphics, box)
            }
        }
    }

}
