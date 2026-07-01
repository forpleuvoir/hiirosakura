package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSEntity
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.appendLiteral
import moe.forpleuvoir.ibukigourd.text.translateText
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireKey
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.base.builder.build
import moe.forpleuvoir.nebula.serialization.codec.Codec
import moe.forpleuvoir.nebula.serialization.codec.serialization
import moe.forpleuvoir.nebula.serialization.extensions.requireString
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import java.util.concurrent.atomic.AtomicBoolean
import moe.forpleuvoir.hiirosakura.util.codec.entityType
import moe.forpleuvoir.hiirosakura.util.codec.uuid

class EntityMatcher(override var mode: CompositeMatcher.MatchMode, entries: List<EntityMatchEntry>) : CompositeMatcher<Entity> {

    constructor(mode: CompositeMatcher.MatchMode, vararg entries: EntityMatchEntry) : this(mode, entries.toList())

    companion object : Codec<EntityMatcher> {

        val targetEntityMatcher: EntityMatcher
            get() {
                val targetEntity = targetEntity
                return if (targetEntity != null) {
                    EntityMatcher(CompositeMatcher.MatchMode.AllMatch).apply {
                        addEntry(EntityMatchEntry.Type(targetEntity.type))
                    }
                } else {
                    anyMatcher
                }
            }

        @JvmStatic
        val targetEntity: Entity?
            get() {
                return mc.crosshairPickEntity
            }

        val anyMatcher
            get() = EntityMatcher(
                mode = CompositeMatcher.MatchMode.AnyMatch,
                listOf(
                    EntityMatchEntry.Type(EntityType.CREEPER, MatchEntry.MatchMode.Include),
                    EntityMatchEntry.Type(EntityType.CREEPER, MatchEntry.MatchMode.Exclude)
                )
            )

        fun isAnyMatcher(matcher: CompositeMatcher<Entity>): Boolean {
            val mode = matcher.mode == CompositeMatcher.MatchMode.AnyMatch
            if (!mode) return false
            val itemEntries = matcher.entries.filterIsInstance<EntityMatchEntry.Type>()
            if (itemEntries.size < 2) return false
            return itemEntries.any { entry1 ->
                itemEntries.any { entry2 ->
                    entry1 != entry2 && entry1.entityType == entry2.entityType && entry1.mode != entry2.mode
                }
            }
        }

        override fun deserialization(data: SerializeElement): Result<EntityMatcher> = DeserializationException.runCatching {
            data.checkType<SerializeObject, EntityMatcher> { obj ->
                val entries = obj.requireKey("entries").checkType<SerializeArray, List<EntityMatchEntry>> { array ->
                    array.map { element ->
                        EntityMatchEntry.deserialization(element).getOrThrow()
                    }
                }
                EntityMatcher(
                    mode = CompositeMatcher.MatchMode.deserialization(obj.requireKey("mode")).getOrThrow(),
                    entries = entries
                )
            }
        }

        override fun serialization(target: EntityMatcher): SerializeObject = SerializeObject.build {
            context(CompositeMatcher.MatchMode, EntityMatchEntry) {
                "mode" to target.mode
                "entries" arr {
                    target.entries.forEach { add(it.serialization) }
                }
            }
        }
    }

    override val entries: List<EntityMatchEntry> = entries.toMutableList()

    val simpleText
        get() = when (entries.size) {
            0    -> IGLang.Misc.hasNothing
            1    -> entries[0].asText
            else -> if (isAnyMatcher(this)) {
                CompositeMatcher.MatchMode.AnyMatch.translateText
            } else mode.translateText.appendLiteral(":").append(IGLang.ConfigWrapper.listConfigWrapperText(entries.size))
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

}

sealed class EntityMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<Entity> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.entity_match_entry.$type"

    val translateText = Translatable(translateKey)

    abstract val asText: Component

    companion object : Codec<EntityMatchEntry> {

        private const val MATCHER_TYPE = "matcher"
        private const val ENTITY_TYPE_TYPE = "entity_type"
        private const val NAME_TYPE = "name"
        private const val DISPLAY_NAME_TYPE = "display_name"
        private const val SCRIPT_TYPE = "script"
        private const val UUID_TYPE = "uuid"
        private const val ALIVE_TYPE = "alive"

        private inline fun <reified T : EntityMatchEntry> codec(type: String, defaultMode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) =
            Codec.create<T>()
                .field<String>("type").getter(EntityMatchEntry::type).default(type).codec(Codec.string)
                .field<MatchEntry.MatchMode>("mode").getter(EntityMatchEntry::mode).default(defaultMode).codec(MatchEntry.MatchMode)

        val desMapping = mutableMapOf<String, (SerializeElement) -> Result<EntityMatchEntry>>(
            MATCHER_TYPE to { Matcher.deserialization(it) },
            ENTITY_TYPE_TYPE to { Type.deserialization(it) },
            NAME_TYPE to { Name.deserialization(it) },
            DISPLAY_NAME_TYPE to { DisplayName.deserialization(it) },
            SCRIPT_TYPE to { Script.deserialization(it) },
            UUID_TYPE to { UUID.deserialization(it) },
            ALIVE_TYPE to { Alive.deserialization(it) },
        )

        override fun deserialization(data: SerializeElement): Result<EntityMatchEntry> = DeserializationException.runCatching {
            data.checkType<SerializeObject, EntityMatchEntry> {
                desMapping[it.requireString("type")]?.invoke(it)?.getOrThrow() ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }
        }

        override fun serialization(target: EntityMatchEntry): SerializeElement = when (target) {
            is Matcher     -> Matcher.serialization(target)
            is Type        -> Type.serialization(target)
            is Name        -> Name.serialization(target)
            is DisplayName -> DisplayName.serialization(target)
            is Script      -> Script.serialization(target)
            is UUID        -> UUID.serialization(target)
            is Alive       -> Alive.serialization(target)
        }

    }

    //region Matcher
    class Matcher(val matcher: EntityMatcher, mode: MatchEntry.MatchMode) : EntityMatchEntry(mode, MATCHER_TYPE) {
        companion object : Codec<Matcher> by codec<Matcher>(MATCHER_TYPE)
            .field<EntityMatcher>("matcher").getter(Matcher::matcher).codec(EntityMatcher)
            .build({ _, mode, matcher -> Matcher(matcher, mode) })

        override val asText: Component get() = matcher.simpleText

        override fun match(obj: Entity): Boolean = matcher.match(obj)

    }
    //endregion

    //region Type
    class Type(val entityType: EntityType<*>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, ENTITY_TYPE_TYPE) {
        companion object : Codec<Type> by codec<Type>(ENTITY_TYPE_TYPE)
            .field<EntityType<*>>("entity_type").getter(Type::entityType).codec(Codec.entityType)
            .build({ _, mode, entityType -> Type(entityType, mode) })

        override val asText: Component = Translatable(entityType.descriptionId)

        override fun match(obj: Entity): Boolean = obj.type == entityType

    }
    //endregion

    //region Name
    class Name(val name: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, NAME_TYPE) {
        companion object : Codec<Name> by codec<Name>(NAME_TYPE)
            .field<String>("name").getter(Name::name).codec(Codec.string)
            .build({ _, mode, name -> Name(name, mode) })

        override val asText: Component = Literal(name)

        override fun match(obj: Entity): Boolean = runCatching {
            name.toRegex().matches(obj.name.string)
        }.getOrDefault(false)

    }
    //endregion

    //region DisplayName
    class DisplayName(val displayName: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, DISPLAY_NAME_TYPE) {
        companion object : Codec<DisplayName> by codec<DisplayName>(DISPLAY_NAME_TYPE)
            .field<String>("display_name").getter(DisplayName::displayName).codec(Codec.string)
            .build({ _, mode, displayName -> DisplayName(displayName, mode) })

        override val asText: Component = Literal(displayName)

        override fun match(obj: Entity): Boolean = displayName.toRegex().matches(obj.displayName.string)

    }
    //endregion

    //region Script
    class Script(val script: String = defaultScript, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, SCRIPT_TYPE) {
        companion object : Codec<Script> {
            val defaultScript = """
                // The variable entity represents a wrapped Entity object [HSEntity].
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

        override fun match(obj: Entity): Boolean {
            val result = AtomicBoolean(false)
            ScriptExecutor(
                script, mutableMapOf(
                    "entity" to HSEntity(obj),
                    "result" to result
                )
            ).execute()
            return result.get()
        }

    }
    //endregion

    //region UUID
    class UUID(val uuid: java.util.UUID, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, UUID_TYPE) {
        companion object : Codec<UUID> by codec<UUID>(UUID_TYPE)
            .field<java.util.UUID>("uuid").getter(UUID::uuid).codec(Codec.uuid)
            .build({ _, mode, uuid -> UUID(uuid, mode) })

        override val asText: Component = Literal(uuid.toString())

        override fun match(obj: Entity): Boolean = obj.uuid == uuid

    }
    //endregion

    //region Alive
    class Alive(val alive: Boolean = true, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : EntityMatchEntry(mode, ALIVE_TYPE) {
        companion object : Codec<Alive> by codec<Alive>(ALIVE_TYPE)
            .field<Boolean>("alive").getter(Alive::alive).default(true).codec(Codec.boolean)
            .build({ _, mode, alive -> Alive(alive, mode) })

        override val asText: Component = (if (alive) CommonComponents.GUI_YES else CommonComponents.GUI_NO)

        override fun match(obj: Entity): Boolean = obj.isAlive == alive

    }
    //endregion

}
