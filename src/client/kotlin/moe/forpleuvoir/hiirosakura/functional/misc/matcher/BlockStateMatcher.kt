package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.block
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.math.Vector3icDeserializer
import moe.forpleuvoir.hiirosakura.util.math.serialization
import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.hiirosakura.util.serialization
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

typealias BlockInfo = Pair<BlockState, Vector3ic?>

fun BlockInfo(blockState: BlockState, blockPos: BlockPos? = null): BlockInfo =
    blockState to blockPos?.toVector()

class BlockStateMatcher(
    override var mode: MultiMatcher.MatchMode,
    vararg entries: BlockStateMatchEntry
) : MultiMatcher<BlockInfo>, Deserializable {

    override val entries: List<BlockStateMatchEntry> = mutableListOf(*entries)

    fun addEntry(entry: BlockStateMatchEntry) {
        (this.entries as MutableList).add(entry)
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
                    BlockStateMatchEntry.deserialization(element)
                }
            }

        }
    }

}


sealed class BlockStateMatchEntry(override val mode: MatchEntry.MatchMode) : MatchEntry<BlockInfo> {

    companion object : Deserializer<BlockStateMatchEntry> {

        val desMapping = mutableMapOf<String, (SerializeElement) -> BlockStateMatchEntry>(
            "block" to Block.Companion::deserialization,
            "script" to Script.Companion::deserialization,
            "pos_range" to PosRange.Companion::deserialization,
            "tag" to Tag.Companion::deserialization,
            "property" to Property.Companion::deserialization,
        )

        override fun deserialization(serializeElement: SerializeElement): BlockStateMatchEntry {
            return serializeElement.checkType<SerializeObject, BlockStateMatchEntry> {
                desMapping[it["type"]!!.asString]?.invoke(it) ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }.getOrThrow()
        }

        private fun getMode(serializeObject: SerializeObject): MatchEntry.MatchMode =
            MatchEntry.MatchMode.deserialization(serializeObject["mode"]!!)

    }

    class Block(val block: McBlock, mode: MatchEntry.MatchMode) : BlockStateMatchEntry(mode) {

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

        override fun match(obj: BlockInfo): Boolean = obj.first == this.block

        override fun serialization(): SerializeElement = serializeObject {
            "type" to "item"
            "mode" to mode
            "block" to block.serialization
        }
    }

    class Script(val script: String, mode: MatchEntry.MatchMode) : BlockStateMatchEntry(mode) {

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

        override fun match(obj: BlockInfo): Boolean {
            val result = mutableStateOf<Boolean>(false)
            ScriptExecutor(
                script,
                buildMap {
                    this["blockState"] = HSBlockState(obj.first)
                    obj.second?.let { this["blockPos"] = it }
                    this["result"] = result
                }
            ).execute()
            return result.getValue()
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to "script"
            "mode" to mode
            "script" to script
        }

    }

    class PosRange(val start: Vector3ic, val end: Vector3ic, mode: MatchEntry.MatchMode) : BlockStateMatchEntry(mode) {

        companion object : Deserializer<PosRange> {
            override fun deserialization(serializeElement: SerializeElement): PosRange {
                return serializeElement.checkType<SerializeObject, PosRange> {
                    PosRange(
                        start = Vector3icDeserializer.deserialization(it["start"]!!),
                        end = Vector3icDeserializer.deserialization(it["end"]!!),
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: BlockInfo): Boolean = obj.second?.let {
            it.x() in start.x()..end.x()
                    && it.y() in start.y()..end.y()
                    && it.z() in start.z()..end.z()
        } == true

        override fun serialization(): SerializeElement = serializeObject {
            "type" to "item"
            "mode" to mode
            "start" to start.serialization()
            "end" to end.serialization()
        }

    }

    class Tag(val tag: String, mode: MatchEntry.MatchMode) : BlockStateMatchEntry(mode) {

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

        override fun match(obj: BlockInfo): Boolean = obj.first.hasTag(tag)

        override fun serialization(): SerializeElement = serializeObject {
            "type" to "tag"
            "mode" to mode
            "tag" to tag
        }

    }

    class Property(val property: Pair<String, String>, mode: MatchEntry.MatchMode) : BlockStateMatchEntry(mode) {

        companion object : Deserializer<Property> {
            override fun deserialization(serializeElement: SerializeElement): Property {
                return serializeElement.checkType<SerializeObject, Property> {
                    Property(
                        property = it["property"]!!.asString.let {
                            val strings = it.split(" = ")
                            strings[0] to strings[1]
                        },
                        mode = getMode(it)
                    )
                }.getOrThrow()
            }
        }

        override fun match(obj: BlockInfo): Boolean = obj.first.entries.any {
            it.key.name == property.first && property.second == Util.getValueAsString(it.key, it.value)
        }

        override fun serialization(): SerializeElement = serializeObject {
            "type" to "tag"
            "mode" to mode
            "property" to "${property.first} = ${property.second}"
        }

    }

}