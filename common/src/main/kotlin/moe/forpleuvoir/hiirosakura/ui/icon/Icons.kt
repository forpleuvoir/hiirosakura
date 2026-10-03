package moe.forpleuvoir.hiirosakura.ui.icon

import moe.forpleuvoir.hiirosakura.util.identifier
import moe.forpleuvoir.ibukigourd.ui.sokitsu.Icons
import moe.forpleuvoir.ibukigourd.ui.sokitsu.texture.atlas.SokitsuAtlasManager

private val ICON_ATLAS = identifier("icon")

private fun of(id: String) = SokitsuAtlasManager.sprite(ICON_ATLAS, identifier("icon/$id"))

/** 本项目自有图标图集里的播放图标；IG 的 `Icons` 没有同名成员。 */
val Icons.Play get() = of("play")
