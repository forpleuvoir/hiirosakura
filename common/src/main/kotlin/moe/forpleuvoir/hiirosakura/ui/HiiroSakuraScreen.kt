package moe.forpleuvoir.hiirosakura.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.config.HSConfig
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
        mutableStateListOf(DrawerItem {
            label { Text(InlineStyleText(HSConfig.translateText.plainText)) }
            icon {
                Icon(if (LocalDrawerItemSelected.current) Icons.Filled.Settings else Icons.Settings, null)
            }
            content {
                ConfigManagerWrapper(HSConfig)
            }
        })
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
//
//fun HiiroSakuraScreen() = TabScreen(
//    header = {
//        Row(
//            Modifier
//                .fill()
//                .padding(5f),
//            horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
//        ) {
//            Icon(icon, modifier = Modifier.size(16f, 16f))
//            Text(Literal(HiiroSakura.MOD_NAME).style {
//                color(HSVColor(358f, 0.65f, 0.74f))
//                bold()
//            })
//        }
//    },
//    modifier = Modifier.onClose {
//        @Suppress("DeferredResultUnused")
//        HSConfig.asyncSave()
//        HiiroSakuraDataManager.asyncSave()
//        registryAccess?.let {
//            ioLaunch {
//                if (ItemStackManager.saveDataAsync(it).await()) {
//                    mc.schedule {
//                        Toast.showToast(HSLang.itemEditorSaveSuccess)
//                    }
//                }
//            }
//        }
//    },
//    tabColor = stateOf(Color.ofRGB(0xFFCCF0)),
//    inactiveColor = stateOf(Color.ofRGB(0xB3F2FF))
//) {
//    HSTab(HSConfig.translateText) { ConfigManagerWrapper(HSConfig) }
////    HSTab(HSLang.customData) { TreeNodeEditor(CustomData.data, Modifier.fill(), listModifier = { Modifier.weight(1).fill() }) }
//    HSTab(HSLang.taskManager) { TaskManagerGui() }
//    HSTab(HSLang.customRadialMenu) {
//        CustomRadialMenuManagerGui()
//    }
//    HSTab(HSLang.eventSubscriberManager) { HSEventManagerGui() }
//    registryAccess?.let { registryManager ->
//        HSTab(
//            HSLang.itemEditor,
//            onTabChanged = {
//                if (!it) ioLaunch {
//                    if (ItemStackManager.saveDataAsync(registryManager).await()) {
//                        mc.schedule {
//                            Toast.showToast(HSLang.itemEditorSaveSuccess)
//                        }
//                    }
//                }
//            }
//        ) { ItemStackManagerGui(registryManager) }
//    }
//
//}
//
//private var currentTab = HSConfig.translateText
//
//private fun TabScope.HSTab(
//    title: MutableText,
//    onTabChanged: TabScope.(Boolean) -> Unit = {},
//    activeTextColor: State<Color> = stateOf(Colors.WHITE),
//    inactiveTextColor: State<Color> = stateOf(Colors.BLACK),
//    modifier: Modifier = Modifier,
//    content: BoxScope.() -> GuiWidget
//) = Tab(
//    title.plainText,
//    title == currentTab,
//    {
//        if (it) currentTab = title
//        onTabChanged(it)
//    },
//    activeTextColor,
//    inactiveTextColor,
//    modifier,
//    content
//)