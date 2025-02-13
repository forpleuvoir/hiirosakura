package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.block
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.math.Vector3icDeserializer
import moe.forpleuvoir.hiirosakura.util.math.serialization
import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.ibukigourd.text.Literal
import moe.forpleuvoir.ibukigourd.text.Text
import moe.forpleuvoir.ibukigourd.text.Translatable
import moe.forpleuvoir.ibukigourd.text.copyToText
import moe.forpleuvoir.ibukigourd.util.state.mutableStateOf
import moe.forpleuvoir.nebula.serialization.Deserializable
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeArray
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializeObject
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import moe.forpleuvoir.nebula.serialization.extensions.serializeObject
import net.minecraft.block.BlockState
import net.minecraft.util.Util
import net.minecraft.util.math.BlockPos
import org.joml.Vector3ic
import net.minecraft.block.Block as McBlock

data class BlockInfo(
    val state: BlockState,
    val pos: Vector3ic
) {
    constructor(state: BlockState, blockPos: BlockPos) : this(state, blockPos.toVector())
}

class BlockInfoMatcher(
    override var mode: MultiMatcher.MatchMode,
    entries: List<BlockInfoMatchEntry>
) : MultiMatcher<BlockInfo>, Deserializable, Cloneable {

    constructor(
        mode: MultiMatcher.MatchMode,
        vararg entries: BlockInfoMatchEntry
    ) : this(mode, entries.toList())

    companion object : Deserializer<BlockInfoMatcher> {
        override fun deserialization(serializeElement: SerializeElement): BlockInfoMatcher {
            return serializeElement.checkType<SerializeObject, BlockInfoMatcher> { obj ->
                val entries = obj["entries"]!!.checkType<SerializeArray, List<BlockInfoMatchEntry>> { array ->
                    array.map { element ->
                        BlockInfoMatchEntry.deserialization(element)
                    }
                }.getOrThrow()
                BlockInfoMatcher(
                    mode = MultiMatcher.MatchMode.deserialization(obj["mode"]!!),
                    entries = entries
                )
            }.getOrThrow()
        }
    }

    override val entries: List<BlockInfoMatchEntry> = entries.toMutableList()

    public override fun clone(): BlockInfoMatcher {
        return BlockInfoMatcher(mode, ArrayList(entries))
    }

    fun addEntry(entry: BlockInfoMatchEntry) {
        (this.entries as MutableList).add(entry)
    }

    fun setEntry(index: Int, entry: BlockInfoMatchEntry) {
        (this.entries as MutableList)[index] = entry
    }

    fun removeEntry(index: Int) {
        (this.entries as MutableList).removeAt(index)
    }

    fun removeEntry(entry: BlockInfoMatchEntry) {
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
                    addEntry(BlockInfoMatchEntry.deserialization(element))
                }
            }
        }
    }

}


sealed class BlockInfoMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<BlockInfo> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.block_info_matcher_entry.$type"

    val translateText = Translatable(translateKey)

    abstract val asText: Text

    companion object : Deserializer<BlockInfoMatchEntry> {

        val desMapping = mutableMapOf<String, (SerializeElement) -> BlockInfoMatchEntry>(
            "block" to { Block.deserialization(it) },
            "script" to { Script.deserialization(it) },
            "pos" to { Pos.deserialization(it) },
            "tag" to { Tag.deserialization(it) },
            "property" to { Property.deserialization(it) },
        )

        override fun deserialization(serializeElement: SerializeElement): BlockInfoMatchEntry {
            return serializeElement.checkType<SerializeObject, BlockInfoMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Block(val block: McBlock, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : BlockInfoMatchEntry(mode, "block") {

        companion object : Deserializer<Block> {
            override fun deserialization(serializeElement: SerializeElement): Block {
                return serializeElement.checkType<SerializeObject, Block> {
                    Block(
                        block = it["block"]!!.block,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = block.name.copyToText()

        override fun match(obj: BlockInfo): Boolean = obj.state.block == this.block

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "block" to block.serialization
        }
    }

    class Script(val script: String = defaultScript, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : BlockInfoMatchEntry(mode, "script") {

        companion object : Deserializer<Script> {
            override fun deserialization(serializeElement: SerializeElement): Script {
                return serializeElement.checkType<SerializeObject, Script> {
                    Script(
                        script = it["script"]!!.asString,
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }

            val defaultScript = """
                // The variable blockState represents a wrapped BlockState object [HSBlockState].
                // The variable blockPos represents a Vector3ic object.
                // To indicate a successful match, set the return value by calling:
                // result.setValue(true)
            """.trimIndent()
        }

        override val asText: Text = Literal(script.substring(0..(64.coerceAtMost(script.lastIndex))))

        override fun match(obj: BlockInfo): Boolean {
            val result = mutableStateOf(false)
            ScriptExecutor(
                script,
                buildMap {
                    this["blockState"] = HSBlockState(obj.state)
                    this["blockPos"] = obj.pos
                    this["result"] = result
                }
            ).execute()
            return result.getValue()
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "script" to script
        }

    }

    class Pos(val min: Vector3ic, val max: Vector3ic, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : BlockInfoMatchEntry(mode, "pos") {

        companion object : Deserializer<Pos> {
            override fun deserialization(serializeElement: SerializeElement): Pos {
                return serializeElement.checkType<SerializeObject, Pos> {
                    Pos(
                        min = Vector3icDeserializer.deserialization(it["min"]!!),
                        max = Vector3icDeserializer.deserialization(it["max"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal("[x:${min.x()},y:${min.y()},z:${min.z()}]..[x:${max.x()},y:${max.y()},z:${max.z()}]")

        override fun match(obj: BlockInfo): Boolean = obj.pos.let {
            it.x() in min.x()..max.x()
                    && it.y() in min.y()..max.y()
                    && it.z() in min.z()..max.z()
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "min" to min.serialization()
            "max" to max.serialization()
        }

    }

    class Tag(val tag: String, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : BlockInfoMatchEntry(mode, "tag") {

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

        override val asText: Text = Literal("#$tag")

        override fun match(obj: BlockInfo): Boolean = obj.state.hasTag(tag)

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "tag" to tag
        }

    }

    class Property(val property: Pair<String, String>, mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) : BlockInfoMatchEntry(mode, "property") {

        companion object : Deserializer<Property> {
            override fun deserialization(serializeElement: SerializeElement): Property {
                return serializeElement.checkType<SerializeObject, Property> {
                    Property(
                        property = it["property"]!!.asString.let { str ->
                            val strings = str.split(" = ")
                            strings[0] to strings[1]
                        },
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override val asText: Text = Literal(property.first + " = " + property.second)

        override fun match(obj: BlockInfo): Boolean = obj.state.entries.any {
            it.key.name == property.first && property.second == Util.getValueAsString(it.key, it.value)
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to type
            "mode" to mode
            "property" to "${property.first} = ${property.second}"
        }

    }

}