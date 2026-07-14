package moe.forpleuvoir.hiirosakura.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.task.ui.TaskManagerUI
import moe.forpleuvoir.hiirosakura.ui.icon.default.Assignment
import moe.forpleuvoir.hiirosakura.ui.icon.filled.Assignment
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.mod.config.IGConfig
import moe.forpleuvoir.ibukigourd.mod.ui.*
import moe.forpleuvoir.ibukigourd.render.extension.texture.Corner
import moe.forpleuvoir.ibukigourd.render.extension.texture.IGTexture
import moe.forpleuvoir.ibukigourd.render.extension.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.text.InlineStyleText
import moe.forpleuvoir.ibukigourd.text.plainText
import moe.forpleuvoir.ibukigourd.ui.ComposeScreen
import moe.forpleuvoir.ibukigourd.ui.configwrapper.ConfigManagerWrapper
import moe.forpleuvoir.ibukigourd.ui.icon.Icons
import moe.forpleuvoir.ibukigourd.ui.icon.default.Settings
import moe.forpleuvoir.ibukigourd.ui.icon.filled.Settings
import moe.forpleuvoir.ibukigourd.ui.platformcontext.IbukiGourdTheme
import moe.forpleuvoir.ibukigourd.ui.preset.BlitTexture
import moe.forpleuvoir.ibukigourd.ui.preset.Text
import net.minecraft.client.gui.screens.Screen

private val icon = IGTexture(Corner(), 0, 0, 128, 128, TextureInfo(128, 128, identifier("icon.png")))

fun HiiroSakuraScreen(
    pauseGame: Boolean = false,
    renderParent: Boolean = false,
    parentScreen: Screen? = null,
    shouldRenderLevel: Boolean = false,
    entryAnimation: Boolean = true,
) = ComposeScreen(
    pauseGame,
    renderParent,
    parentScreen,
    { shouldRenderLevel },
    entryAnimation,
    ::HiiroSakuraScreenContent
)

private var selectedIndex by mutableStateOf(0)

@Composable
internal fun HiiroSakuraScreenContent() {
    val items = remember {
        mutableStateListOf(
            //配置页面
            DrawerItem {
                label { Text(InlineStyleText(HSConfig.translateText.plainText)) }
                icon {
                    Icon(if (LocalDrawerItemSelected.current) Icons.Filled.Settings else Icons.Settings, null)
                }
                content {
                    ConfigManagerWrapper(HSConfig)
                }
            },
            //任务管理器
            DrawerItem {
                label { Text(HSLang.Task.manager) }
                icon {
                    Icon(if (LocalDrawerItemSelected.current) Icons.Filled.Assignment else Icons.Assignment, null)
                }
                content {
                    TaskManagerUI(Modifier)
                }
            }
        )
    }
    IbukiGourdTheme {
        ModScreen(
            title = {
                Text(HiiroSakura.MOD_NAME, fontWeight = FontWeight.Bold)
            },
            items = items,
            selectedIndex = selectedIndex.coerceIn(items.indices),
            onSelectIndex = { selectedIndex = it },
            header = {
                DrawerHeader(
                    monogram = {
                        BlitTexture(identifier("icon.png"))
                    },
                    name = {
                        Text(HiiroSakura.MOD_NAME, fontWeight = FontWeight.Bold)
                    },
                    subtitle = {
                        Text(HiiroSakura.MOD_ID)
                    }
                )
            },
            footer = {
                ThemeSwitcher(
                    isLight = IGConfig.Gui.Theme.lightMode,
                    onToggle = { IGConfig.Gui.Theme.lightMode = it }
                )
            }
        )
    }
}
