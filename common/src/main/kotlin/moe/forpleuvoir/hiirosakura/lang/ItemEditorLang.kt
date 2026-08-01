package moe.forpleuvoir.hiirosakura.lang

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.MutableText
import moe.forpleuvoir.ibukigourd.text.Translatable
import net.minecraft.network.chat.Component

@Suppress("NOTHING_TO_INLINE")
object ItemEditorLang {

    @PublishedApi
    internal inline fun lang(key: String, vararg args: Any): MutableText =
        Translatable("${HiiroSakura.MOD_ID}.item_editor.$key", args = args)

    inline val title get() = Translatable("${HiiroSakura.MOD_ID}.item_editor")

    inline val loading get() = lang("loading")

    inline val saveSuccess get() = lang("save_success")

    inline val addFromHandheldItem get() = lang("add_from_handheld_item")

    inline val getToBackpack get() = lang("get_to_backpack")

    inline fun getToBackpackSuccess(text: Component) = lang("get_to_backpack.success", text)

    inline val removeConfirm get() = lang("remove_confirm")

    inline val copyToCommand get() = lang("copy_to_command")

    inline val itemType get() = lang("item_type")

    inline val itemPreview get() = lang("item_preview")

    inline val itemCount get() = lang("item_count")
    inline val rootType get() = lang("root_type")

    inline val addItemComponent get() = lang("add_item_component")

    inline val dataComponents get() = lang("data_components")

    inline val adaptedComponent get() = lang("adapted_component")

    inline val unadaptedComponent get() = lang("unadapted_component")

    inline fun itemComponentExist(component: Any) = lang("item_component_exist", component)

    inline fun enchantmentExist(enchantment: Any) = lang("enchantment_exist", enchantment)

    inline val fromRegistry get() = lang("from_registry")
    inline val fromTag get() = lang("from_tag")
    inline val addFromRegistry get() = lang("add_from_registry")
    inline val addFromTag get() = lang("add_from_tag")
    inline val tags get() = lang("tags")
    inline val items get() = lang("items")
    inline val mobEffects get() = lang("mob_effects")
    inline val fromResourceManager get() = lang("from_resource_manager")
}
