package forpleuvoir.hiirosakura.client.config.base

import fi.dy.masa.malilib.config.IConfigOptionListEntry
import fi.dy.masa.malilib.util.StringUtils
import forpleuvoir.hiirosakura.client.util.StringUtil


/**
 *

 * 项目名 hiirosakura

 * 包名 forpleuvoir.hiirosakura.client.config.base

 * 文件名 ListMode

 * 创建时间 2021/11/19 22:09

 * @author forpleuvoir

 */
enum class ListMode(private val translationKey: String) : IConfigOptionListEntry {
    BLACK_LIST(StringUtil.translatableText("label.list_mode.black_list").key),
    WHITE_LIST(StringUtil.translatableText("label.list_mode.white_list").key),
    NONE(StringUtil.translatableText("label.list_mode.none").key);

    override fun getStringValue(): String {
        return name
    }

    override fun getDisplayName(): String {
        return StringUtils.translate(translationKey)
    }

    override fun cycle(forward: Boolean): ListMode {
        var id = ordinal
        if (forward) {
            if (++id >= values().size) {
                id = 0
            }
        } else {
            if (--id < 0) {
                id = values().size - 1
            }
        }
        return values()[id % values().size]
    }

    override fun fromString(value: String): ListMode {
       return valueOf(value)
    }


}