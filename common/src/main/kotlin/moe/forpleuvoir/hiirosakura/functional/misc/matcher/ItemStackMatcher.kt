package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSItemStack
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.allEnchantments
import moe.forpleuvoir.hiirosakura.util.codec.dataComponentType
import moe.forpleuvoir.hiirosakura.util.codec.item
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.key
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.enum
import moe.forpleuvoir.nebula.serialization.codec.intRange
import moe.forpleuvoir.nebula.serialization.codec.serialization
import moe.forpleuvoir.nebula.serialization.extensions.requireString
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import java.util.concurrent.atomic.AtomicBoolean
import net.minecraft.core.component.DataComponentType as McDataComponentType
import net.minecraft.world.item.Item as McItem
import net.minecraft.world.item.Rarity as McRarity

data class ItemStackMatcher(
    override val mode: CompositeMatcher.MatchMode,
    override val entries: List<ItemStackMatchEntry>
) : CompositeMatcher<ItemStack> {

    constructor(mode: CompositeMatcher.MatchMode, vararg entries: ItemStackMatchEntry) : this(mode, entries.toList())

    companion object : Codec<ItemStackMatcher> {

        val handheldItemMatcher: ItemStackMatcher
            get() {
                val handleItem = handheldItemStack
                return if (handleItem != null) {
                    ItemStackMatcher(
                        CompositeMatcher.MatchMode.AllMatch,
                        ItemStackMatchEntry.Item(handleItem.item)
                    )
                } else {
                    anyMatcher
                }
            }

        @JvmStatic
        val handheldItemStack: ItemStack?
            get() {
                mc.player?.apply {
                    if (!mainHandItem.isEmpty) return mainHandItem
                    if (!offhandItem.isEmpty) return offhandItem
                }
                return null
            }

        @JvmStatic
        val handItemStackOrEmpty: ItemStack get() = handheldItemStack ?: ItemStack.EMPTY

        val anyMatcher
            get() = ItemStackMatcher(
                mode = CompositeMatcher.MatchMode.AnyMatch,
                ItemStackMatchEntry.Item(Items.AIR, MatchEntry.MatchMode.Include),
                ItemStackMatchEntry.Item(Items.AIR, mode = MatchEntry.MatchMode.Exclude)
            )

        fun isAnyMatcher(matcher: CompositeMatcher<ItemStack>): Boolean {
            val mode = matcher.mode == CompositeMatcher.MatchMode.AnyMatch
            if (!mode) return false
            val itemEntries = matcher.entries.filterIsInstance<ItemStackMatchEntry.Item>()
            if (itemEntries.size < 2) return false
            return itemEntries.any { entry1 ->
                itemEntries.any { entry2 ->
                    entry1 != entry2 && entry1.item == entry2.item && entry1.mode != entry2.mode
                }
            }
        }

        override fun deserialization(data: SerializeElement): Result<ItemStackMatcher> = DeserializationException.runCatching {
            data.checkType<SerializeObject, ItemStackMatcher> { obj ->
                val entries = obj.requireKey("entries").checkType<SerializeArray, List<ItemStackMatchEntry>> { array ->
                    array.map { element ->
                        ItemStackMatchEntry.deserialization(element).getOrThrow()
                    }
                }
                ItemStackMatcher(
                    mode = CompositeMatcher.MatchMode.deserialization(obj.requireKey("mode")).getOrThrow(),
                    entries = entries
                )
            }
        }

        override fun serialization(target: ItemStackMatcher): SerializeObject = SerializeObject.build {
            context(CompositeMatcher.MatchMode, ItemStackMatchEntry) {
                "mode" to target.mode
                "entries" arr {
                    target.entries.forEach { add(it.serialization) }
                }
            }
        }

    }

    val simpleText by lazy {
        when (entries.size) {
            0    -> IGLang.Misc.hasNothing
            1    -> entries[0].asText
            else -> if (isAnyMatcher(this)) {
                CompositeMatcher.MatchMode.AnyMatch.translateText
            } else mode.translateText.appendLiteral(":").append(IGLang.ConfigWrapper.listConfigWrapperText(entries.size))
        }
    }

}

sealed class ItemStackMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<ItemStack> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.item_stack_matcher_entry.$type"

    val translateText = Translatable(translateKey)

    abstract val asText: Component

    companion object : Codec<ItemStackMatchEntry> {

        private const val MATCHER_TYPE = "matcher"
        private const val ITEM_TYPE = "item"
        private const val NAME_TYPE = "name"
        private const val SCRIPT_TYPE = "script"
        private const val COUNT_TYPE = "count"
        private const val RARITY_TYPE = "rarity"
        private const val ENCHANTMENT_TYPE = "enchantment"
        private const val TAG_TYPE = "tag"
        private const val DATA_COMPONENT_TYPE_TYPE = "data_component_type"

        private inline fun <reified T : ItemStackMatchEntry> codec(type: String, defaultMode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) =
            Codec.create<T>()
                .field<String>("type").getter(ItemStackMatchEntry::type).default(type).codec(Codec.string)
                .field<MatchEntry.MatchMode>("mode").getter(ItemStackMatchEntry::mode).default(defaultMode).codec(MatchEntry.MatchMode)

        val desMapping = mutableMapOf<String, (SerializeElement) -> Result<ItemStackMatchEntry>>(
            MATCHER_TYPE to { Matcher.deserialization(it) },
            ITEM_TYPE to { Item.deserialization(it) },
            NAME_TYPE to { Name.deserialization(it) },
            SCRIPT_TYPE to { Script.deserialization(it) },
            COUNT_TYPE to { Count.deserialization(it) },
            RARITY_TYPE to { Rarity.deserialization(it) },
            ENCHANTMENT_TYPE to { Enchantment.deserialization(it) },
            TAG_TYPE to { Tag.deserialization(it) },
            DATA_COMPONENT_TYPE_TYPE to { DataComponentType.deserialization(it) },
        )

        override fun deserialization(data: SerializeElement): Result<ItemStackMatchEntry> = DeserializationException.runCatching {
            data.checkType<SerializeObject, ItemStackMatchEntry> {
                desMapping[it.requireString("type")]?.invoke(it)?.getOrThrow() ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }
        }

        override fun serialization(target: ItemStackMatchEntry): SerializeElement = when (target) {
            is Matcher           -> Matcher.serialization(target)
            is Item              -> Item.serialization(target)
            is Name              -> Name.serialization(target)
            is Script            -> Script.serialization(target)
            is Count             -> Count.serialization(target)
            is Rarity            -> Rarity.serialization(target)
            is Enchantment       -> Enchantment.serialization(target)
            is Tag               -> Tag.serialization(target)
            is DataComponentType -> DataComponentType.serialization(target)
        }

    }

    //region Matcher
    class Matcher(val matcher: ItemStackMatcher, mode: MatchEntry.MatchMode) : ItemStackMatchEntry(mode, MATCHER_TYPE) {
        companion object : Codec<Matcher> by codec<Matcher>(MATCHER_TYPE)
            .field<ItemStackMatcher>("matcher").getter(Matcher::matcher).codec(ItemStackMatcher)
            .build({ _, mode, matcher -> Matcher(matcher, mode) })

        override val asText: Component get() = matcher.simpleText

        override fun match(obj: ItemStack): Boolean = matcher.match(obj)

    }
    //endregion

    //region Item
    class Item(val item: McItem, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, ITEM_TYPE) {
        companion object : Codec<Item> by codec<Item>(ITEM_TYPE)
            .field<McItem>("item").getter(Item::item).codec(Codec.item)
            .build({ _, mode, item -> Item(item, mode) })

        override val asText: Component by lazy { item.getName(ItemStack(item)) }

        override fun match(obj: ItemStack): Boolean = obj.item == item

    }
    //endregion

    //region Name
    class Name(val name: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, NAME_TYPE) {
        companion object : Codec<Name> by codec<Name>(NAME_TYPE)
            .field<String>("name").getter(Name::name).codec(Codec.string)
            .build({ _, mode, name -> Name(name, mode) })

        override val asText: Component = Literal(name)

        override fun match(obj: ItemStack): Boolean =
            name.toRegex().matches(obj.itemName.string)

    }
    //endregion

    //region Script
    class Script(val script: String = defaultScript, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, SCRIPT_TYPE) {
        companion object : Codec<Script> {
            val defaultScript = """
                // The variable itemStack represents a wrapped ItemStack object [HSItemStack].
                // To indicate a successful match, set the return value by calling:
                // result.set(true);
            """.trimIndent()

            private val codec = codec<Script>(SCRIPT_TYPE)
                .field<String>("script").getter(Script::script).default(defaultScript).codec(Codec.string)
                .build { _, mode, script -> Script(script, mode) }

            override fun serialization(target: Script): SerializeElement = codec.serialization(target)
            override fun deserialization(data: SerializeElement): Result<Script> = codec.deserialization(data)
        }

        override val asText: Component = Literal("Script Matcher")

        override fun match(obj: ItemStack): Boolean {
            val result = AtomicBoolean(false)
            ScriptExecutor(
                script, mutableMapOf(
                    "itemStack" to HSItemStack(obj),
                    "result" to result
                )
            ).execute()
            return result.get()
        }

    }
    //endregion

    //region Count
    class Count(val count: IntRange, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, COUNT_TYPE) {
        companion object : Codec<Count> by codec<Count>(COUNT_TYPE)
            .field<IntRange>("count").getter(Count::count).codec(Codec.intRange)
            .build({ _, mode, count -> Count(count, mode) })

        override val asText: Component = Literal(if (count.first == count.last) "x${count.first}" else "x${count.first}..${count.last}")

        override fun match(obj: ItemStack): Boolean = obj.count in count

    }
    //endregion

    //region Rarity
    class Rarity(val rarity: McRarity, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, RARITY_TYPE) {
        companion object : Codec<Rarity> by codec<Rarity>(RARITY_TYPE)
            .field<McRarity>("rarity").getter(Rarity::rarity).codec(Codec.enum<McRarity>())
            .build({ _, mode, rarity -> Rarity(rarity, mode) })

        override val asText: Component = Literal(rarity.name)

        override fun match(obj: ItemStack): Boolean = obj.rarity == rarity

    }
    //endregion

    //region Enchantment
    class Enchantment(
        val enchantment: String,
        val level: IntRange,
        mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : ItemStackMatchEntry(mode, ENCHANTMENT_TYPE) {
        companion object : Codec<Enchantment> by codec<Enchantment>(ENCHANTMENT_TYPE)
            .field<String>("enchantment").getter(Enchantment::enchantment).codec(Codec.string)
            .field<IntRange>("level").getter(Enchantment::level).codec(Codec.intRange)
            .build({ _, mode, enchantment, level -> Enchantment(enchantment, level, mode) })

        override val asText: Component = Literal(enchantment)
            .appendLiteral(" ")
            .appendTranslate("enchantment.level.${level.first}", level.first.toString())
            .appendLiteral("..")
            .appendTranslate("enchantment.level.${level.last}", level.last.toString())

        override fun match(obj: ItemStack): Boolean {
            val lv = obj.allEnchantments.entries.find { it.key.registeredName == enchantment }?.value
            return lv in level
        }

    }
    //endregion

    //region Tag
    class Tag(val tag: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : ItemStackMatchEntry(mode, TAG_TYPE) {
        companion object : Codec<Tag> by codec<Tag>(TAG_TYPE)
            .field<String>("tag").getter(Tag::tag).codec(Codec.string)
            .build({ _, mode, tag -> Tag(tag, mode) })

        override val asText: Component = Literal("#$tag")

        override fun match(obj: ItemStack): Boolean = obj.hasTag(tag)

    }
    //endregion

    //region DataComponentType
    class DataComponentType(val componentType: McDataComponentType<*>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) :
        ItemStackMatchEntry(mode, DATA_COMPONENT_TYPE_TYPE) {
        companion object : Codec<DataComponentType> by codec<DataComponentType>(DATA_COMPONENT_TYPE_TYPE)
            .field<McDataComponentType<*>>("component_type").getter(DataComponentType::componentType).codec(Codec.dataComponentType)
            .build({ _, mode, componentType -> DataComponentType(componentType, mode) })

        override val asText: Component = Literal(componentType.key.toString())

        override fun match(obj: ItemStack): Boolean = obj.components.has(componentType)

    }
    //endregion

}
