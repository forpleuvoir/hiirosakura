package moe.forpleuvoir.hiirosakura

import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable

object HSLang {
    private fun lang(key: String, vararg args: Any): Text {
        return Translatable(HiiroSakura.MOD_ID + ".${key}", args = args)
    }




}