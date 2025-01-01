package moe.forpleuivoir.hiirosakura.test

import kotlinx.coroutines.delay
import moe.forpleuvoir.hiirosakura.gui.widget.calculateQuadrilaterals
import moe.forpleuvoir.nebula.common.util.defaultLaunch
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import kotlin.test.Test

class OtherTest{


    @Test
    fun test1(){
        val centerX = 300f
        val centerY = 300f
        val rInner = 50f
        val rOuter = 100f
        val options = 6
        val gapAngle = 10f // 每个间隔角度为 10 度

        val quadrilaterals = calculateQuadrilaterals(centerX, centerY, rInner, rOuter, options, gapAngle)

        quadrilaterals.forEachIndexed { index, quad ->
            println("选项 ${index + 1}: $quad")
        }

    }

    @Test
    fun test2(){
        sendNotification("标题","内容")
    }

    fun sendNotification(title: String, message: String) {
        if (!SystemTray.isSupported()) {
            println("系统不支持通知功能")
            return
        }

        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().createImage("assets/icon.png") // 图标，也可以是 null

        val trayIcon = TrayIcon(image, "HiiroSakura")
        trayIcon.isImageAutoSize = true
        trayIcon.toolTip = "HiiroSakura"
        tray.add(trayIcon)

        trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO)

        defaultLaunch {
            delay(3000)
            tray.remove(trayIcon)
        }
    }

}