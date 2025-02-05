package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.item
import moe.forpleuvoir.hiirosakura.util.logger
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.serialization.Deserializable
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.component.ComponentType
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.util.Collections
import net.minecraft.item.Item as McItem
import net.minecraft.util.Rarity as McRarity

class ItemStackMatcher(override var mode: MultiMatcher.MatchMode, entries: List<ItemStackMatchEntry>) : MultiMatcher<ItemStack>, Deserializable, Cloneable {

    constructor(mode: MultiMatcher.MatchMode, vararg entries: ItemStackMatchEntry) : this(mode, entries.toList())

    companion object {
        private val log by lazy { logger(ItemStackMatcher::class) }
    }

    override val entries: List<ItemStackMatchEntry> = entries.toMutableList()

    public override fun clone(): ItemStackMatcher {
        return ItemStackMatcher(mode, ArrayList(entries))
    }

    fun addEntry(entry: ItemStackMatchEntry) {
        (this.entries as MutableList).add(entry)
    }

    fun setEntry(index: Int, entry: ItemStackMatchEntry) {
        (this.entries as MutableList)[index] = entry
    }

    fun removeEntry(index: Int) {
        (this.entries as MutableList).removeAt(index)
    }

    fun removeEntry(entry: ItemStackMatchEntry) {
        (this.entries as MutableList).remove(entry)
    }

    private fun clear() {
        (this.entries as MutableList).clear()
    }



    override fun deserialization(serializeElement: SerializeElement) {
        clear()
        serializeElement.checkType<SerializeObject, Unit> { obj ->
            mode = MultiMatcher.MatchMode.deserialization(obj["mode"]!!)
            obj["entries"]!!.checkType<SerializeArray, Unit> { array ->
                array.forEach { element ->
                    ItemStackMatchEntry.deserialization(element)
                }
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ItemStackMatcher

        if (mode != other.mode) return false
        if (entries != other.entries) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mode.hashCode()
        result = 31 * result + entries.hashCode()
        return result
    }

}

sealed class ItemStackMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<ItemStack> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.item_stack_matcher_entry.$type"

    val translateText = Translatable(translateKey)

    companion object : Deserializer<ItemStackMatchEntry> {

        val desMapping = mutableMapOf<String, (SerializeElement) -> ItemStackMatchEntry>(
            "item" to Item.Companion::deserialization,
            "script" to Script.Companion::deserialization,
            "count" to Count.Companion::deserialization,
            "rarity" to Rarity.Companion::deserialization,
            "tag" to Tag.Companion::deserialization,
            "data_component_type" to DataComponentType.Companion::deserialization,
        )

        override fun deserialization(serializeElement: SerializeElement): ItemStackMatchEntry {
            return serializeElement.checkType<SerializeObject, ItemStackMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Item(val item: McItem, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "item") {

        companion object : Deserializer<Item> {
            override fun deserialization(serializeElement: SerializeElement): Item {
                return serializeElement.checkType<SerializeObject, Item> {
                    Item(
                        item = it["item"]!!.item,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean = obj.item == item

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "item" to item.serialization
        }

    }

    class Script(val script: String, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "script") {

        companion object : Deserializer<Script> {
            override fun deserialization(serializeElement: SerializeElement): Script {
                return serializeElement.checkType<SerializeObject, Script> {
                    Script(
                        script = it["script"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean {
            val result = mutableStateOf<Boolean>(false)
            ScriptExecutor(
                script, mapOf(
                    "itemStack" to HSItemStack(obj),
                    "result" to result
                )
            ).execute()
            return result.getValue()
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "script" to script
        }

    }

    class Count(val count: IntRange, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "count") {

        companion object : Deserializer<Count> {
            override fun deserialization(serializeElement: SerializeElement): Count {
                return serializeElement.checkType<SerializeObject, Count> {
                    Count(
                        count = IntRange.deserialization(it["count"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean = obj.count in count

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "count" to count.serialization()
        }

    }

    class Rarity(val rarity: McRarity, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "rarity") {

        companion object : Deserializer<Rarity> {
            override fun deserialization(serializeElement: SerializeElement): Rarity {
                return serializeElement.checkType<SerializeObject, Rarity> {
                    Rarity(
                        rarity = McRarity.valueOf(it["rarity"]!!.asString),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean = obj.rarity == rarity

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "rarity" to rarity
        }

    }

    class Tag(val tag: String, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "tag") {

        companion object : Deserializer<Tag> {
            override fun deserialization(serializeElement: SerializeElement): Tag {
                return serializeElement.checkType<SerializeObject, Tag> {
                    Tag(
                        tag = it["tag"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean = obj.hasTag(tag)

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "tag" to tag
        }

    }

    class DataComponentType(val componentType: ComponentType<*>, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, "data_component_type") {

        companion object : Deserializer<DataComponentType> {
            override fun deserialization(serializeElement: SerializeElement): DataComponentType {
                return serializeElement.checkType<SerializeObject, DataComponentType> {
                    DataComponentType(
                        componentType = Registries.DATA_COMPONENT_TYPE.get(Identifier.of(it["component_type"]!!.asString))!!,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: ItemStack): Boolean = obj.components.contains(componentType)

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "component_type" to componentType.id.toString()
        }

    }

}