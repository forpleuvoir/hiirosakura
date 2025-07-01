package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSEntity
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
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
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.entity.Entity
import net.minecraft.entity.EntityType
import net.minecraft.registry.Registries
import net.minecraft.screen.ScreenTexts
import net.minecraft.util.Identifier

class EntityMatcher(override var mode: MultiMatcher.MatchMode, entries: List<EntityMatchEntry>) : MultiMatcher<Entity>, Deserializable {

    constructor(mode: MultiMatcher.MatchMode, vararg entries: EntityMatchEntry) : this(mode, entries.toList())

    companion object : Deserializer<EntityMatcher> {

        val targetEntityMatcher: EntityMatcher
            get() {
                val targetEntity = targetEntity
                return if (targetEntity != null) {
                    EntityMatcher(MultiMatcher.MatchMode.AllMatch).apply {
                        addEntry(EntityMatchEntry.Type(targetEntity.type))
                    }
                } else {
                    anyMatcher
                }
            }

        @JvmStatic
        val targetEntity: Entity?
            get() {
                return mc.targetedEntity
            }

        val anyMatcher
            get() = EntityMatcher(
                mode = MultiMatcher.MatchMode.AnyMatch,
                listOf(
                    EntityMatchEntry.Type(EntityType.CREEPER, MatchEntry.MatchMode.Include),
                    EntityMatchEntry.Type(EntityType.CREEPER, MatchEntry.MatchMode.Exclude)
                )
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
            val itemEntries = matcher.entries.filterIsInstance<EntityMatchEntry.Type>()
            if (itemEntries.size < 2) return false
            return itemEntries.any { entry1 ->
                itemEntries.any { entry2 ->
                    entry1 != entry2 && entry1.entityType == entry2.entityType && entry1.mode != entry2.mode
                }
            }
        }
    }

    override val entries: List<EntityMatchEntry> = entries.toMutableList()

    val simpleText
        get() = when (entries.size) {
            0    -> IGLang.hasNothing
            1    -> entries[0].asText
            else -> if (isAnyMatcher(this)) {
                MultiMatcher.MatchMode.AnyMatch.translateText
            } else mode.translateText.appendLiteral(":").append(IGLang.listConfigWrapperText(entries.size))
        }

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

        other as EntityMatcher

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
            Matcher.TYPE to { Matcher.deserialization(it) },
            Type.TYPE to { Type.deserialization(it) },
            Name.TYPE to { Name.deserialization(it) },
            DisplayName.TYPE to { DisplayName.deserialization(it) },
            Script.TYPE to { Script.deserialization(it) },
            UUID.TYPE to { UUID.deserialization(it) },
            Alive.TYPE to { Alive.deserialization(it) },
        )

        override fun deserialization(serializeElement: SerializeElement): EntityMatchEntry {
            return serializeElement.checkType<SerializeObject, EntityMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Matcher(val matcher: EntityMatcher, mode: MatchEntry.MatchMode) : EntityMatchEntry(mode, TYPE) {

        companion object : Deserializer<Matcher> {
            const val TYPE = "matcher"
            override fun deserialization(serializeElement: SerializeElement): Matcher {
                return serializeElement.checkType<SerializeObject, Matcher> {
                    Matcher(
                        matcher = EntityMatcher.deserialization(it["matcher"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text get() = matcher.simpleText

        override fun match(obj: Entity): Boolean = matcher.match(obj)

        override fun SerializeObjectScope.entrySerialization() {
            "matcher" to matcher.serialization()
        }

    }

    class Type(val entityType: EntityType<*>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

        companion object : Deserializer<Type> {
            const val TYPE = "entity_type"
            override fun deserialization(serializeElement: SerializeElement): Type {
                return serializeElement.checkType<SerializeObject, Type> {
                    Type(
                        entityType = Registries.ENTITY_TYPE.get(Identifier.of(it["entity_type"]!!.asString)),
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

    class Name(val name: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

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

        override fun match(obj: Entity): Boolean {
            return name.toRegex().matches(obj.name.string)
        }

        override fun SerializeObjectScope.entrySerialization() {
            "name" to name
        }

    }

    class DisplayName(val displayName: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

        companion object : Deserializer<DisplayName> {
            const val TYPE = "display_name"
            override fun deserialization(serializeElement: SerializeElement): DisplayName {
                return serializeElement.checkType<SerializeObject, DisplayName> {
                    DisplayName(
                        displayName = it["display_name"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = Literal(displayName)

        override fun match(obj: Entity): Boolean {
            return displayName.toRegex().matches(obj.displayName?.string ?: "")
        }

        override fun SerializeObjectScope.entrySerialization() {
            "display_name" to displayName
        }

    }

    class Script(val script: String = defaultScript, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

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
                // The variable entity represents a wrapped Entity object [HSEntity].
                // To indicate a successful match, set the return value by calling:
                // result.setValue(true);
            """.trimIndent()
        }

        override val asText: Text = Literal("Script Matcher")

        override fun match(obj: Entity): Boolean {
            val result = mutableStateOf(false)
            ScriptExecutor(
                script, mapOf(
                    "entity" to HSEntity(obj),
                    "result" to result
                )
            ).execute()
            return result.getValue()
        }

        override fun SerializeObjectScope.entrySerialization() {
            "script" to script
        }

    }

    class UUID(val uuid: java.util.UUID, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

        companion object : Deserializer<UUID> {
            const val TYPE = "uuid"
            override fun deserialization(serializeElement: SerializeElement): UUID {
                return serializeElement.checkType<SerializeObject, UUID> {
                    UUID(
                        uuid = java.util.UUID.fromString(it["uuid"]!!.asString),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = Literal(uuid.toString())

        override fun match(obj: Entity): Boolean {
            return obj.uuid == uuid
        }

        override fun SerializeObjectScope.entrySerialization() {
            "uuid" to uuid.toString()
        }

    }

    class Alive(val alive: Boolean = true, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, TYPE) {

        companion object : Deserializer<Alive> {
            const val TYPE = "alive"
            override fun deserialization(serializeElement: SerializeElement): Alive {
                return serializeElement.checkType<SerializeObject, Alive> {
                    Alive(
                        alive = it["alive"]!!.asBoolean,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

        }

        override val asText: Text = (if (alive) ScreenTexts.YES else ScreenTexts.NO).copyToText()

        override fun match(obj: Entity): Boolean {
            return obj.isAlive == alive
        }

        override fun SerializeObjectScope.entrySerialization() {
            "alive" to alive
        }

    }

}