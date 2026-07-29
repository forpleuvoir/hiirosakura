package moe.forpleuvoir.hiirosakura.util

import com.mojang.blaze3d.vertex.PoseStack
import kotlinx.coroutines.delay
import kotlinx.serialization.descriptors.PrimitiveKind
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.render.pose
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.common.util.primitive.either
import net.minecraft.client.Minecraft
import net.minecraft.resources.Identifier
import net.minecraft.world.item.TooltipFlag
import org.joml.Vector3f
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon

private val logger = logger("ClientMisc")

val registryAccess get() = mc.player?.level()?.registryAccess()

val Minecraft.tooltipFlag: TooltipFlag
    get() = this.options.advancedItemTooltips.either(TooltipFlag.ADVANCED, TooltipFlag.NORMAL)


fun PoseStack.restPoseStackKeepTranslation(): PoseStack {
    val translation = this.pose.getTranslation(Vector3f())
    val newPose = PoseStack()
    newPose.setIdentity()
    newPose.translate(translation.x(), translation.y(), translation.z())
    return newPose
}

fun PoseStack.clearRotation(): PoseStack {
    val translation = this.pose.getTranslation(Vector3f())
    val scale = this.last().normal().getScale(Vector3f())
    val newPose = PoseStack()
    newPose.setIdentity()
    newPose.translate(translation.x(), translation.y(), translation.z())
    newPose.scale(scale.x(), scale.y(), scale.z())
    return newPose
}

fun Identifier.asTranslateKey(prefix: String? = null, suffix: String? = null): String {
    return if (prefix != null && suffix != null)
        this.toLanguageKey(prefix, suffix)
    else if (prefix != null)
        this.toLanguageKey(prefix)
    else if (suffix != null)
        this.toLanguageKey() + "." + suffix
    else
        this.toLanguageKey()
}

fun Identifier.asTranslateText(prefix: String? = null, suffix: String? = null, fallback: String? = null): MutableText {
    return Translatable(asTranslateKey(prefix, suffix), fallback ?: this.toString())
}

fun Identifier.asText(): MutableText = Literal(this.toString())

fun sendNotification(title: String, message: String) {
    if (!sendAWTNotification(title, message)) {
        sendPowerShellNotification(title, message)
    }
}

private fun sendPowerShellNotification(title: String, message: String) {
    defaultLaunch {
        runCatching {
            val command = $$"""
            powershell -Command "
                [Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null;
                $template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent([Windows.UI.Notifications.ToastTemplateType]::ToastText02);
                $template.GetElementsByTagName('text').Item(0).AppendChild($template.CreateTextNode('$$title')) | Out-Null;
                $template.GetElementsByTagName('text').Item(1).AppendChild($template.CreateTextNode('$$message')) | Out-Null;
                $toast = [Windows.UI.Notifications.ToastNotification]::new($template);
                [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('$${HiiroSakura.MOD_NAME}').Show($toast);
            "
        """.trimIndent()
            ProcessBuilder().command(command).start()
//            Runtime.getRuntime().exec(command)
        }.onFailure {
            logger.warn(it)
        }
    }
}

private fun sendAWTNotification(title: String, message: String): Boolean {
    System.setProperty("java.awt.headless", "false")
    if (!SystemTray.isSupported()) {
        return false
    }

    val systemTray = SystemTray.getSystemTray()

    val image = Toolkit.getDefaultToolkit().getImage("assets/icon.png")
    val trayIcon = TrayIcon(image, HiiroSakura.MOD_NAME)

    trayIcon.isImageAutoSize = true

    runCatching {
        defaultLaunch {
            systemTray.add(trayIcon)
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)
            delay(5000)
            systemTray.remove(trayIcon)
        }
    }.onFailure {
        logger.warn(it)
        return false
    }
    return true
}