package moe.forpleuvoir.hiirosakura.gui

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraDataManager
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.functional.customdata.CustomData
import moe.forpleuvoir.hiirosakura.functional.event.HSEventManagerGui
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackManager
import moe.forpleuvoir.hiirosakura.functional.itemeditor.ItemStackManagerGui
import moe.forpleuvoir.hiirosakura.functional.task.TaskManagerGui
import moe.forpleuvoir.hiirosakura.gui.widget.TreeNodeEditor
import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.hiirosakura.util.registryManager
import moe.forpleuvoir.ibukigourd.config.translateText
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Alignment
import moe.forpleuvoir.ibukigourd.gui.base.layout.arrange.Arrangement
import moe.forpleuvoir.ibukigourd.gui.base.modifier.Modifier
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.onClose
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.padding
import moe.forpleuvoir.ibukigourd.gui.base.modifier.impl.size
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.Corner
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.TextureInfo
import moe.forpleuvoir.ibukigourd.gui.base.render.texture.WidgetTexture
import moe.forpleuvoir.ibukigourd.gui.base.widget.IGWidget
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigManagerWrapper
import moe.forpleuvoir.ibukigourd.gui.screen.TabScreen
import moe.forpleuvoir.ibukigourd.gui.widget.TabScope
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.BoxScope
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Row
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.util.state.State
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.ARGBColor
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.Colors
import moe.forpleuvoir.nebula.common.color.HSVColor
import net.minecraft.registry.DynamicRegistryManager

private val icon = WidgetTexture(Corner(), 0, 0, 128, 128, TextureInfo(128, 128, identifier("icon.png")))

fun HiiroSakuraScreen() = TabScreen(
    header = {
        Row(
            Modifier
                .fill()
                .padding(5f),
            horizontalArrangement = Arrangement.spacedBy(5f, Alignment.Left)
        ) {
            Icon(icon, modifier = Modifier.size(16f, 16f))
            TextLabel(Literal(HiiroSakura.MOD_NAME).style {
                color(HSVColor(358f, 0.65f, 0.74f))
                bold()
            }) {
                setting { }
            }
        }
    },
    modifier = Modifier.onClose {
        HSConfig.asyncSave()
        HiiroSakuraDataManager.asyncSave()
        registryManager?.let {
            ItemStackManager.saveDataAsync(it)
        }
    },
    tabColor = stateOf(Color(0xffffccf0)),
    inactiveColor = stateOf(Color(0xffb3f2ff))
) {
    Config()
    CustomData()
    TaskManager()
    HSEventManager()
    registryManager?.let {
        ItemEditor(it)
    }
}

private var currentTab = HSConfig.translateText

private fun TabScope.HSTab(
    title: Text,
    onTabChanged: TabScope.(Boolean) -> Unit = {},
    activeTextColor: State<ARGBColor> = stateOf(Colors.WHITE),
    inactiveTextColor: State<ARGBColor> = stateOf(Colors.BLACK),
    modifier: Modifier = Modifier,
    content: BoxScope.() -> IGWidget
) = Tab(
    title.plainText,
    title == currentTab,
    {
        if (it) currentTab = title
        onTabChanged(it)
    },
    activeTextColor,
    inactiveTextColor,
    modifier,
    content
)

private fun TabScope.Config() = HSTab(
    HSConfig.translateText
) {
    ConfigManagerWrapper(HSConfig)
}

private fun TabScope.TaskManager() = HSTab(
    HSLang.taskManager
) {
    TaskManagerGui()
}

private fun TabScope.HSEventManager() = HSTab(
    HSLang.eventSubscriberManager
) {
    HSEventManagerGui()
}

private fun TabScope.CustomData() = HSTab(
    HSLang.customData
) {
    TreeNodeEditor(CustomData.data, Modifier.fill(), listModifier = { Modifier.weight(1).fill() })
}

private fun TabScope.ItemEditor(registryManager: DynamicRegistryManager) = HSTab(
    HSLang.itemEditor,
    onTabChanged = {
        if (!it) ItemStackManager.saveDataAsync(registryManager)
    }
) {
    ItemStackManagerGui(registryManager)
}