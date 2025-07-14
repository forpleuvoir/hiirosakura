package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.id
import moe.forpleuvoir.hiirosakura.util.item
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.ibukigourd.IGLang
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.serialization.Deserializable
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.SerializeObjectScope
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.deserialization
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.component.ComponentType
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.item.Item as McItem
import net.minecraft.util.Rarity as McRarity

class ItemStackMatcher(override var mode: MultiMatcher.MatchMode, entries: List<ItemStackMatchEntry>) : MultiMatcher<ItemStack>, Deserializable {

    constructor(mode: MultiMatcher.MatchMode, vararg entries: ItemStackMatchEntry) : this(mode, entries.toList())

    companion object : Deserializer<ItemStackMatcher> {

        val handheldItemMatcher: ItemStackMatcher
            get() {
                val handleItem = handheldItemStack
                return if (handleItem != null) {
                    ItemStackMatcher(MultiMatcher.MatchMode.AllMatch).apply {
                        addEntry(ItemStackMatchEntry.Item(handleItem.item))
                    }
                } else {
                    anyMatcher
                }
            }

        @JvmStatic
        val handheldItemStack: ItemStack?
            get() {
                mc.player?.apply {
                    if (!mainHandStack.isEmpty) return mainHandStack
                    if (!offHandStack.isEmpty) return offHandStack
                }
                return null
            }

        @JvmStatic
        val handItemStackOrEmpty: ItemStack get() = handheldItemStack ?: ItemStack.EMPTY

        val anyMatcher
            get() = ItemStackMatcher(
                mode = MultiMatcher.MatchMode.AnyMatch,
                ItemStackMatchEntry.Item(Items.AIR, MatchEntry.MatchMode.Include),
                ItemStackMatchEntry.Item(Items.AIR, mode = MatchEntry.MatchMode.Exclude)
            )

        override fun deserialization(serializeElement: SerializeElement): ItemStackMatcher {
            return serializeElement.checkType<SerializeObject, ItemStackMatcher> { obj ->
                val entries = obj["entries"]!!.checkType<SerializeArray, List<ItemStackMatchEntry>> { array ->
                    array.map { element ->
                        ItemStackMatchEntry.deserialization(element)
                    }
                }.getOrThrow()
                ItemStackMatcher(
                    mode = MultiMatcher.MatchMode.deserialization(obj["mode"]!!),
                    entries = entries
                )
            }.getOrThrow()
        }

        fun isAnyMatcher(matcher: MultiMatcher<ItemStack>): Boolean {
            val mode = matcher.mode == MultiMatcher.MatchMode.AnyMatch
            if (!mode) return false
            val itemEntries = matcher.entries.filterIsInstance<ItemStackMatchEntry.Item>()
            if (itemEntries.size < 2) return false
            return itemEntries.any { entry1 ->
                itemEntries.any { entry2 ->
                    entry1 != entry2 && entry1.item == entry2.item && entry1.mode != entry2.mode
                }
            }
        }
    }

    override val entries: List<ItemStackMatchEntry> = entries.toMutableList()

    val simpleText
        get() = when (entries.size) {
            0    -> IGLang.hasNothing
            1    -> entries[0].asText
            else -> if (isAnyMatcher(this)) {
                MultiMatcher.MatchMode.AnyMatch.translateText
            } else mode.translateText.appendLiteral(":").append(IGLang.listConfigWrapperText(entries.size))
        }

    override fun clone(): ItemStackMatcher {
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
                    addEntry(ItemStackMatchEntry.deserialization(element))
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

    abstract val asText: Text

    fun entrySerialization(scope: SerializeObjectScope.() -> Unit) = serializeObject {
        "type" to type
        "mode" to mode
        scope()
    }

    companion object : Deserializer<ItemStackMatchEntry> {

        val desMapping = mutableMapOf<String, (SerializeElement) -> ItemStackMatchEntry>(
            Matcher.TYPE to { Matcher.deserialization(it) },
            Item.TYPE to { Item.deserialization(it) },
            Name.TYPE to { Name.deserialization(it) },
            Script.TYPE to { Script.deserialization(it) },
            Count.TYPE to { Count.deserialization(it) },
            Rarity.TYPE to { Rarity.deserialization(it) },
            Enchantment.TYPE to { Enchantment.deserialization(it) },
            Tag.TYPE to { Tag.deserialization(it) },
            DataComponentType.TYPE to { DataComponentType.deserialization(it) },
        )

        override fun deserialization(serializeElement: SerializeElement): ItemStackMatchEntry {
            return serializeElement.checkType<SerializeObject, ItemStackMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Matcher(val matcher: ItemStackMatcher, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Matcher> {
            const val TYPE = "matcher"
            override fun deserialization(serializeElement: SerializeElement): Matcher {
                return serializeElement.checkType<SerializeObject, Matcher> {
                    Matcher(
                        matcher = ItemStackMatcher.deserialization(it["matcher"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text get() = matcher.simpleText

        override fun match(obj: ItemStack): Boolean = matcher.match(obj)

        override fun serialization(): SerializeElement = entrySerialization { "matcher" to matcher.serialization() }

    }

    class Item(val item: McItem, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Item> {
            const val TYPE = "item"
            override fun deserialization(serializeElement: SerializeElement): Item {
                return serializeElement.checkType<SerializeObject, Item> {
                    Item(
                        item = it["item"]!!.item,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = item.name.copyToText()

        override fun match(obj: ItemStack): Boolean = obj.item == item

        override fun serialization(): SerializeElement = entrySerialization {
            "item" to item.serialization
        }

    }

    class Name(val name: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {
        companion object : Deserializer<Name> {
            const val TYPE = "name"
            override fun deserialization(serializeElement: SerializeElement): Name {
                return serializeElement.checkType<SerializeObject, Name> {
                    Name(
                        name = it["name"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal(name)

        override fun match(obj: ItemStack): Boolean =
            name.toRegex().matches(obj.item.name.string)

        override fun serialization(): SerializeElement = entrySerialization {
            "name" to name
        }
    }

    class Script(val script: String = defaultScript, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Script> {
            const val TYPE = "script"
            override fun deserialization(serializeElement: SerializeElement): Script {
                return serializeElement.checkType<SerializeObject, Script> {
                    Script(
                        script = it["script"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

            val defaultScript = """
                // The variable itemStack represents a wrapped ItemStack object [HSItemStack].
                // To indicate a successful match, set the return value by calling:
                // result.setValue(true);
            """.trimIndent()
        }

        override val asText: Text = Literal("Script Matcher")

        override fun match(obj: ItemStack): Boolean {
            val result = mutableStateOf(false)
            ScriptExecutor(
                script, mapOf(
                    "itemStack" to HSItemStack(obj),
                    "result" to result
                )
            ).execute()
            return result.getValue()
        }

        override fun serialization(): SerializeElement = entrySerialization {
            "script" to script
        }

    }

    class Count(val count: IntRange, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Count> {
            const val TYPE = "count"
            override fun deserialization(serializeElement: SerializeElement): Count {
                return serializeElement.checkType<SerializeObject, Count> {
                    Count(
                        count = IntRange.deserialization(it["count"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal(if (count.first == count.last) "x${count.first}" else "x${count.first}..${count.last}")

        override fun match(obj: ItemStack): Boolean = obj.count in count

        override fun serialization(): SerializeElement = entrySerialization {
            "count" to count.serialization()
        }

    }

    class Rarity(val rarity: McRarity, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Rarity> {
            const val TYPE = "rarity"
            override fun deserialization(serializeElement: SerializeElement): Rarity {
                return serializeElement.checkType<SerializeObject, Rarity> {
                    Rarity(
                        rarity = McRarity.valueOf(it["rarity"]!!.asString),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal(rarity.name)

        override fun match(obj: ItemStack): Boolean = obj.rarity == rarity

        override fun serialization(): SerializeElement = entrySerialization {
            "rarity" to rarity
        }

    }

    class Enchantment(
        val enchantment: String,
        val level: IntRange,
        mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Enchantment> {
            const val TYPE = "enchantment"
            override fun deserialization(serializeElement: SerializeElement): ItemStackMatchEntry.Enchantment {
                return serializeElement.checkType<SerializeObject, Enchantment> {
                    Enchantment(
                        enchantment = it["enchantment"]!!.asString,
                        level = IntRange.deserialization(it["level"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = Literal(enchantment)
            .appendLiteral(" ")
            .appendTranslate("enchantment.level.${level.first}", level.first.toString())
            .appendLiteral("..")
            .appendTranslate("enchantment.level.${level.last}", level.last.toString())

        override fun match(obj: ItemStack): Boolean {
            val lv = obj.enchantments.enchantmentEntries.find { it.key.idAsString == enchantment }?.intValue ?: 0
            return lv in level
        }

        override fun serialization(): SerializeElement = entrySerialization {
            "enchantment" to enchantment
            "level" to level.serialization()
        }

    }

    class Tag(val tag: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<Tag> {
            const val TYPE = "tag"
            override fun deserialization(serializeElement: SerializeElement): Tag {
                return serializeElement.checkType<SerializeObject, Tag> {
                    Tag(
                        tag = it["tag"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal("#$tag")

        override fun match(obj: ItemStack): Boolean = obj.hasTag(tag)

        override fun serialization(): SerializeElement = entrySerialization {
            "tag" to tag
        }

    }

    class DataComponentType(val componentType: ComponentType<*>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) :
        ItemStackMatchEntry(mode, TYPE) {

        companion object : Deserializer<DataComponentType> {
            const val TYPE = "data_component_type"
            override fun deserialization(serializeElement: SerializeElement): DataComponentType {
                return serializeElement.checkType<SerializeObject, DataComponentType> {
                    DataComponentType(
                        componentType = Registries.DATA_COMPONENT_TYPE.get(Identifier.of(it["component_type"]!!.asString))!!,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal(componentType.id.toString())

        override fun match(obj: ItemStack): Boolean = obj.components.contains(componentType)

        override fun serialization(): SerializeElement = entrySerialization {
            "component_type" to componentType.id.toString()
        }

    }

}