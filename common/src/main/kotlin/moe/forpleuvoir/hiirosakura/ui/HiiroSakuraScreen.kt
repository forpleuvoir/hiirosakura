package moe.forpleuvoir.hiirosakura.ui

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.customradialmenu.ui.CustomRadialMenuManagerUI
import moe.forpleuvoir.hiirosakura.functional.event.ui.HSEventManagerUI
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemEditorManagerUI
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskManagerUI
import moe.forpleuvoir.hiirosakura.ui.compat.BlitTexture
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.registryAccess
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.ui.ModScreen
import moe.forpleuvoir.ibukigourd.ui.ModScreenIcon
import moe.forpleuvoir.ibukigourd.ui.ModScreenTab
import moe.forpleuvoir.ibukigourd.ui.rememberModScreenState
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerWrapper
import moe.forpleuvoir.ibukigourd.ui.sokitsu.SokitsuScreen
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Text
import moe.forpleuvoir.ibukigourd.ui.sokitsu.theme.SokitsuTheme
import net.minecraft.client.gui.screens.Screen

/**
 * 打开 HiiroSakura 主界面。
 *
 * 新版上游把「抽屉 + 页脚」的模组屏幕换成了 [ModScreen] 的页签布局，因此本界面改为一个页签一页；
 * 主题切换不再占用屏内位置，改由配置页的 `IGConfig.Gui.Theme.mode` 调整。
 */
fun HiiroSakuraScreen(
    parentScreen: Screen? = null,
) = SokitsuScreen.create(
    parent = parentScreen,
    content = {
        HiiroSakuraScreenContent()
    },
)

fun OpenHiiroSakuraScreen(
    parentScreen: Screen? = null,
) = SokitsuScreen.open(
    parent = parentScreen,
    content = {
        HiiroSakuraScreenContent()
    },
)

@Composable
internal fun HiiroSakuraScreenContent() {
    val access = registryAccess
    val tabs = remember(access) {
        buildList {
            add(ModScreenTab(HSConfig.translateText) { ConfigManagerWrapper(HSConfig) })
            add(ModScreenTab(HSLang.Task.manager) { TaskManagerUI() })
            add(ModScreenTab(HSLang.CustomRadialMenu.title) { CustomRadialMenuManagerUI() })
            add(ModScreenTab(HSLang.Event.subscriberManager) { HSEventManagerUI() })
            if (access != null) {
                add(ModScreenTab(HSLang.ItemEditor.title) { ItemEditorManagerUI(access) })
            }
        }
    }
    ModScreen(
        tabs = tabs,
        state = rememberModScreenState(),
        title = {
            Text(HiiroSakura.MOD_NAME, color = Color(0xFFBD4246), fontWeight = FontWeight.Bold)
        },
        icon = {
            ModScreenIcon(identifier("icon.png"))
        },
    )
}
