package moe.forpleuvoir.hiirosakura.util

import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.client.MinecraftClient
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.util.Identifier
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.IOException

private val log = logger("ClientMisc")

val MinecraftClient.tooltipType: TooltipType
    get() = this.options.advancedItemTooltips.pick(TooltipType.ADVANCED, TooltipType.BASIC)


internal fun identifier(path: String): Identifier = identifier(HiiroSakura.MOD_ID, path)

internal fun Any.logger(): ModLogger = ModLogger(this::class, HiiroSakura.MOD_NAME)

internal fun logger(name: String): ModLogger = ModLogger(name, HiiroSakura.MOD_NAME)


fun sendNotification(title: String, message: String) {
    if (!sendAWTNotification(title, message)) {
        sendPowerShellNotification(title, message)
    }
}

private fun sendPowerShellNotification(title: String, message: String) {
    defaultLaunch {
        runCatching {
            val command = """
            powershell -Command "
                [Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] | Out-Null;
                ${'$'}template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent([Windows.UI.Notifications.ToastTemplateType]::ToastText02);
                ${'$'}template.GetElementsByTagName('text').Item(0).AppendChild(${'$'}template.CreateTextNode('$title')) | Out-Null;
                ${'$'}template.GetElementsByTagName('text').Item(1).AppendChild(${'$'}template.CreateTextNode('$message')) | Out-Null;
                ${'$'}toast = [Windows.UI.Notifications.ToastNotification]::new(${'$'}template);
                [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('${HiiroSakura.MOD_NAME}').Show(${'$'}toast);
            "
        """.trimIndent()
            Runtime.getRuntime().exec(command)
        }.onFailure {
            log.warn(it)
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
        return false
        log.warn(it)
    }
    return true
}