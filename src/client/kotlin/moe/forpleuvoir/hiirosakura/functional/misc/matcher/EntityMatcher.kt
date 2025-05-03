package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.serialization.Deserializable
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.SerializeObjectScope
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

class EntityMatcher(override var mode: MultiMatcher.MatchMode, entries: List<EntityMatchEntry>) : MultiMatcher<Entity>, Deserializable {

    constructor(mode: MultiMatcher.MatchMode, vararg entries: EntityMatchEntry) : this(mode, entries.toList())

    companion object : Deserializer<EntityMatcher> {

        @JvmStatic
        val handItemStack: ItemStack?
            get() {
                mc.player?.apply {
                    if (!mainHandStack.isEmpty) return mainHandStack
                    if (!offHandStack.isEmpty) return offHandStack
                }
                return null
            }

        val anyMatcher
            get() = EntityMatcher(
                mode = MultiMatcher.MatchMode.AnyMatch,

                )

        override fun deserialization(serializeElement: SerializeElement): EntityMatcher {
            return serializeElement.checkType<SerializeObject, EntityMatcher> { obj ->
                val entries = obj["entries"]!!.checkType<SerializeArray, List<EntityMatchEntry>> { array ->
                    array.map { element ->
                        EntityMatchEntry.deserialization(element)
                    }
                }.getOrThrow()
                EntityMatcher(
                    mode = MultiMatcher.MatchMode.deserialization(obj["mode"]!!),
                    entries = entries
                )
            }.getOrThrow()
        }

        fun isAnyMatcher(matcher: MultiMatcher<Entity>): Boolean {
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

    override val entries: List<EntityMatchEntry> = entries.toMutableList()

    override fun clone(): EntityMatcher {
        return EntityMatcher(mode, ArrayList(entries))
    }

    fun addEntry(entry: EntityMatchEntry) {
        (this.entries as MutableList).add(entry)
    }

    fun setEntry(index: Int, entry: EntityMatchEntry) {
        (this.entries as MutableList)[index] = entry
    }

    fun removeEntry(index: Int) {
        (this.entries as MutableList).removeAt(index)
    }

    fun removeEntry(entry: EntityMatchEntry) {
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
                    addEntry(EntityMatchEntry.deserialization(element))
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

sealed class EntityMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<Entity> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.entity_match_entry.$type"

    val translateText = Translatable(translateKey)

    abstract val asText: Text

    override fun serialization(): SerializeElement = serializeObject {
        "type" to type
        "mode" to mode
        entrySerialization()
    }

    abstract fun SerializeObjectScope.entrySerialization()

    companion object : Deserializer<EntityMatchEntry> {

        val desMapping = mutableMapOf<String, (SerializeElement) -> EntityMatchEntry>(
            "entity_type" to Type::deserialization,
            "name" to Name::deserialization,
        )

        override fun deserialization(serializeElement: SerializeElement): EntityMatchEntry {
            return serializeElement.checkType<SerializeObject, EntityMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Type(val entityType: EntityType<*>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, "entity_type") {

        companion object : Deserializer<Type> {
            override fun deserialization(serializeElement: SerializeElement): Type {
                return serializeElement.checkType<SerializeObject, Type> {
                    Type(
                        entityType = Registries.ENTITY_TYPE.get(Identifier.of(serializeElement.asString)),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = Translatable(entityType.translationKey)

        override fun match(obj: Entity): Boolean {
            return obj.type == entityType
        }

        override fun SerializeObjectScope.entrySerialization() {
            "entity_type" to Registries.ENTITY_TYPE.getId(entityType).toString()
        }

    }

    class Name(val name: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, "name") {

        companion object : Deserializer<Name> {
            override fun deserialization(serializeElement: SerializeElement): Name {
                return serializeElement.checkType<SerializeObject, Name> {
                    Name(
                        name = serializeElement.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = Literal(name)

        override fun match(obj: Entity): Boolean {
            return (obj.displayName?.string ?: obj.name.string) == name
        }

        override fun SerializeObjectScope.entrySerialization() {
            "name" to name
        }

    }

}