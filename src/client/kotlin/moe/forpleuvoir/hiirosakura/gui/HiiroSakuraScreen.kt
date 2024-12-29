package moe.forpleuvoir.hiirosakura.gui

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.common.HiiroSakuraDataManager
import moe.forpleuvoir.hiirosakura.config.HSConfig
import moe.forpleuvoir.hiirosakura.config.HSDataConfig
import moe.forpleuvoir.hiirosakura.functional.task.TaskManagerGui
import moe.forpleuvoir.hiirosakura.util.identifier
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
import moe.forpleuvoir.ibukigourd.gui.configwrapper.ConfigManagerWrapper
import moe.forpleuvoir.ibukigourd.gui.screen.TabScreen
import moe.forpleuvoir.ibukigourd.gui.widget.TabScope
import moe.forpleuvoir.ibukigourd.gui.widget.icon.Icon
import moe.forpleuvoir.ibukigourd.gui.widget.layout.Column
import moe.forpleuvoir.ibukigourd.gui.widget.text.TextLabel
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.util.state.stateOf
import moe.forpleuvoir.nebula.common.color.Color
import moe.forpleuvoir.nebula.common.color.HSVColor

private val icon = WidgetTexture(Corner(), 0, 0, 128, 128, TextureInfo(128, 128, identifier("icon.png")))

fun HiiroSakuraScreen() = TabScreen(
    header = {
        Column(
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
        HSDataConfig.asyncSave()
        HiiroSakuraDataManager.asyncSave()
    },
    tabColor = stateOf(Color(0xffffccf0)),
    inactiveColor = stateOf(Color(0xffb3f2ff))
) {
    Config()
    TaskManager()
}

private fun TabScope.Config() = Tab(
    HSConfig.translateText.plainText
) {
    ConfigManagerWrapper(HSConfig)
}

private fun TabScope.TaskManager() = Tab(
    HSLang.taskManager.plainText
) {
    TaskManagerGui()
}