package moe.forpleuvoir.hiirosakura.functional.misc.matcher

import moe.forpleuvoir.hiirosakura.HSLang
import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockHitResult
import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSBlockState
import moe.forpleuvoir.hiirosakura.functional.task.executor.ScriptExecutor
import moe.forpleuvoir.hiirosakura.util.codec.block
import moe.forpleuvoir.hiirosakura.util.hasTag
import moe.forpleuvoir.hiirosakura.util.math.toVector
import moe.forpleuvoir.hiirosakura.util.targetBlock
import moe.forpleuvoir.ibukigourd.config.item.pair
import moe.forpleuvoir.ibukigourd.lang.IGLang
import moe.forpleuvoir.ibukigourd.text.*
import moe.forpleuvoir.ibukigourd.util.math.vector3ic
import moe.forpleuvoir.ibukigourd.util.mc
import moe.forpleuvoir.nebula.common.color.Colors
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
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.Util
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3i
import org.joml.Vector3ic
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.jvm.optionals.getOrNull
import net.minecraft.world.level.block.Block as McBlock

/**
 * 辅助用的数据类
 */
data class BlockInfo(
    val state: BlockState,
    val pos: Vector3ic,
    val side: Direction = Direction.EAST
) {
    constructor(state: BlockState, blockPos: BlockPos, side: Direction) : this(state, blockPos.toVector(), side)

    @Deprecated("use Minecraft.targetBlock")
    constructor(hitResult: BlockHitResult) : this(mc.level!!.getBlockState(hitResult.blockPos), hitResult.blockPos, hitResult.direction)

    val asHitResult: BlockHitResult by lazy {
        BlockHitResult(Vec3(pos.x().toDouble(), pos.y().toDouble(), pos.z().toDouble()), side, BlockPos(pos.x(), pos.y(), pos.z()), false)
    }

    companion object {

        @JvmStatic
        val emptyBlockInfo: BlockInfo = BlockInfo(Blocks.AIR.defaultBlockState(), BlockPos(0, 0, 0), Direction.EAST)

        @JvmStatic
        val targetBlockInfoOrEmpty: BlockInfo get() = mc.targetBlock ?: emptyBlockInfo

    }
}

data class BlockInfoMatcher(
    override val mode: CompositeMatcher.MatchMode,
    override val entries: List<BlockInfoMatchEntry>
) : CompositeMatcher<BlockInfo> {

    constructor(
        mode: CompositeMatcher.MatchMode,
        vararg entries: BlockInfoMatchEntry
    ) : this(mode, entries.toList())

    companion object : Codec<BlockInfoMatcher> {

        val targetBlockMatcher: BlockInfoMatcher
            get() {
                val targetBlock = mc.targetBlock
                return if (targetBlock != null) {
                    BlockInfoMatcher(
                        mode = CompositeMatcher.MatchMode.AllMatch,
                        BlockInfoMatchEntry.Block(targetBlock.state.block)
                    )
                } else {
                    anyMatcher
                }
            }

        val anyMatcher
            get() = BlockInfoMatcher(
                mode = CompositeMatcher.MatchMode.AnyMatch,
                BlockInfoMatchEntry.Block(Blocks.AIR, MatchEntry.MatchMode.Include),
                BlockInfoMatchEntry.Block(Blocks.AIR, mode = MatchEntry.MatchMode.Exclude)
            )

        fun isAnyMatcher(matcher: CompositeMatcher<BlockInfo>): Boolean {
            val mode = matcher.mode == CompositeMatcher.MatchMode.AnyMatch
            if (!mode) return false
            val blockEntries = matcher.entries.filterIsInstance<BlockInfoMatchEntry.Block>()
            if (blockEntries.size < 2) return false
            return blockEntries.any { entry1 ->
                blockEntries.any { entry2 ->
                    entry1 != entry2 && entry1.block == entry2.block && entry1.mode != entry2.mode
                }
            }
        }

        override fun deserialization(data: SerializeElement): Result<BlockInfoMatcher> = DeserializationException.runCatching {
            data.checkType<SerializeObject, BlockInfoMatcher> { obj ->
                val entries = obj.requireKey("entries").checkType<SerializeArray, List<BlockInfoMatchEntry>> { array ->
                    array.map { element ->
                        BlockInfoMatchEntry.deserialization(element).getOrThrow()
                    }
                }
                BlockInfoMatcher(
                    mode = CompositeMatcher.MatchMode.deserialization(obj.requireKey("mode")).getOrThrow(),
                    entries = entries
                )
            }
        }

        override fun serialization(target: BlockInfoMatcher): SerializeObject = SerializeObject.build {
            context(CompositeMatcher.MatchMode, BlockInfoMatchEntry) {
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
            1    -> entries.first().asText
            else -> if (isAnyMatcher(this)) {
                CompositeMatcher.MatchMode.AnyMatch.translateText
            } else mode.translateText.appendLiteral(":").append(IGLang.ConfigWrapper.listConfigWrapperText(entries.size))
        }
    }

}


sealed class BlockInfoMatchEntry(override val mode: MatchEntry.MatchMode, val type: String) : MatchEntry<BlockInfo> {

    val translateKey: String = "${HiiroSakura.MOD_ID}.block_info_matcher_entry.$type"

    val translateText by lazy { Translatable(translateKey) }

    abstract val asText: Component

    abstract fun copyWithMode(mode: MatchEntry.MatchMode): BlockInfoMatchEntry

    companion object : Codec<BlockInfoMatchEntry> {

        private const val MATCHER_TYPE = "matcher"
        private const val BLOCK_TYPE = "block"
        private const val SCRIPT_TYPE = "script"
        private const val POS_TYPE = "pos"
        private const val TAG_TYPE = "tag"
        private const val PROPERTY_TYPE = "property"

        private inline fun <reified T : BlockInfoMatchEntry> codec(type: String, defaultMode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include) =
            Codec.create<T>()
                .field<String>("type").getter(BlockInfoMatchEntry::type).default(type).codec(Codec.string)
                .field<MatchEntry.MatchMode>("mode").getter(BlockInfoMatchEntry::mode).default(defaultMode).codec(MatchEntry.MatchMode)

        val desMapping = mutableMapOf<String, (SerializeElement) -> Result<BlockInfoMatchEntry>>(
            MATCHER_TYPE to { Matcher.deserialization(it) },
            BLOCK_TYPE to { Block.deserialization(it) },
            SCRIPT_TYPE to { Script.deserialization(it) },
            POS_TYPE to { Pos.deserialization(it) },
            TAG_TYPE to { Tag.deserialization(it) },
            PROPERTY_TYPE to { Property.deserialization(it) },
        )

        override fun deserialization(data: SerializeElement): Result<BlockInfoMatchEntry> = DeserializationException.runCatching {
            data.checkType<SerializeObject, BlockInfoMatchEntry> {
                desMapping[it.requireString("type")]?.invoke(it)?.getOrThrow() ?: throw IllegalArgumentException("Unsupported type ${it["type"]}")
            }
        }

        override fun serialization(target: BlockInfoMatchEntry): SerializeElement = when (target) {
            is Matcher  -> Matcher.serialization(target)
            is Block    -> Block.serialization(target)
            is Script   -> Script.serialization(target)
            is Pos      -> Pos.serialization(target)
            is Tag      -> Tag.serialization(target)
            is Property -> Property.serialization(target)
        }

    }

    //region Matcher
    data class Matcher(
        val matcher: BlockInfoMatcher,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, MATCHER_TYPE) {
        companion object : Codec<Matcher> by codec<Matcher>(MATCHER_TYPE)
            .field<BlockInfoMatcher>("matcher").getter(Matcher::matcher).codec(BlockInfoMatcher)
            .build({ _, mode, matcher -> Matcher(matcher, mode) }) {

            inline val title get() = HSLang.BlockInfoMatcher.Entry.matcher

            val default get() = Matcher(BlockInfoMatcher.targetBlockMatcher)
        }

        override val asText: Component get() = matcher.simpleText

        override fun copyWithMode(mode: MatchEntry.MatchMode): Matcher = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean = matcher.match(obj)


    }
    //endregion


    //region Block
    data class Block(
        val block: McBlock,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, BLOCK_TYPE) {
        companion object : Codec<Block> by codec<Block>(BLOCK_TYPE)
            .field<McBlock>("block").getter(Block::block).codec(Codec.block)
            .build({ _, mode, block -> Block(block, mode) }) {
            inline val title get() = HSLang.BlockInfoMatcher.Entry.block

            val default get() = Block(mc.targetBlock?.state?.block ?: Blocks.MELON, MatchEntry.MatchMode.Include)
        }

        override val asText: Component = block.name

        override fun copyWithMode(mode: MatchEntry.MatchMode): Block = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean = obj.state.block == this.block
    }
    //endregion

    //region Script
    data class Script(
        val script: String = defaultScript,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, SCRIPT_TYPE) {
        companion object : Codec<Script> {
            inline val title get() = HSLang.BlockInfoMatcher.Entry.script

            val default get() = Script(defaultScript)

            val defaultScript = """
                // The variable blockResult represents a wrapped BlockHitResult object [HSBlockHitResult].
                // The variable blockState represents a wrapped BlockState object [HSBlockState].
                // The variable blockPos represents a Vector3ic object.
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

        override fun copyWithMode(mode: MatchEntry.MatchMode): Script = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean {
            val result = AtomicBoolean(false)
            ScriptExecutor(
                script,
                mutableMapOf(
                    "blockResult" to HSBlockHitResult(obj.asHitResult),
                    "blockState" to HSBlockState(obj.state),
                    "blockPos" to obj.pos,
                    "result" to result
                )
            ).execute()
            return result.get()
        }
    }
    //endregion

    //region Pos
    data class Pos(
        val min: Vector3ic,
        val max: Vector3ic,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, POS_TYPE) {

        companion object : Codec<Pos> by codec<Pos>(POS_TYPE)
            .field<Vector3ic>("min").getter(Pos::min).codec(Codec.vector3ic)
            .field<Vector3ic>("max").getter(Pos::max).codec(Codec.vector3ic)
            .build({ _, mode, min, max -> Pos(min, max, mode) }) {

            inline val title get() = HSLang.BlockInfoMatcher.Entry.pos

            val default get() = mc.targetBlock?.let { Pos(it.pos, it.pos) } ?: Pos(Vector3i(), Vector3i())
        }

        override val asText: Component = Literal("[x: ${min.x()}, y: ${min.y()}, z: ${min.z()}]..[x: ${max.x()}, y: ${max.y()}, z: ${max.z()}]")

        private fun TextBuilder.vec(vec: Vector3ic) {
            literal("[")
            literal("x") { style { color(Colors.RED) } }
            literal(": ${vec.x()}, ")
            literal("y") { style { color(Colors.LIME) } }
            literal(": ${vec.y()}, ")
            literal("z") { style { color(Colors.BLUE) } }
            literal(": ${vec.z()}]")
        }

        override fun copyWithMode(mode: MatchEntry.MatchMode): Pos = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean = obj.pos.let {
            it.x() in min.x()..max.x()
                    && it.y() in min.y()..max.y()
                    && it.z() in min.z()..max.z()
        }

    }
    //endregion

    //region Tag
    data class Tag(
        val tag: String,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, TAG_TYPE) {

        companion object : Codec<Tag> by codec<Tag>(TAG_TYPE)
            .field<String>("tag").getter(Tag::tag).codec(Codec.string)
            .build({ _, mode, tag -> Tag(tag, mode) }) {

            inline val title get() = HSLang.BlockInfoMatcher.Entry.tag

            val default
                get() = mc.targetBlock?.let {
                    Tag(it.state.tags().findFirst().getOrNull()?.location?.toString() ?: "")
                } ?: Tag("")
        }

        override val asText: Component = Literal("#$tag")

        override fun copyWithMode(mode: MatchEntry.MatchMode): Tag = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean = obj.state.hasTag(tag)

    }
    //endregion

    //region Property
    data class Property(
        val property: Pair<String, String>,
        override val mode: MatchEntry.MatchMode = MatchEntry.MatchMode.Include
    ) : BlockInfoMatchEntry(mode, PROPERTY_TYPE) {
        companion object : Codec<Property> by codec<Property>(PROPERTY_TYPE)
            .field<Pair<String, String>>("property").getter(Property::property).codec(Codec.pair(Codec.string, Codec.string))
            .build({ _, mode, property -> Property(property, mode) }) {

            inline val title get() = HSLang.BlockInfoMatcher.Entry.property

            val default
                get() = Property(
                    mc.targetBlock?.state?.values?.findFirst()?.getOrNull()?.run {
                        property.name to Util.getPropertyName(property, value)
                    } ?: ("" to "")
                )
        }

        override val asText: Component = Literal(property.first + " = " + property.second)

        override fun copyWithMode(mode: MatchEntry.MatchMode): Property = this.copy(mode = mode)

        override fun match(obj: BlockInfo): Boolean = obj.state.values.anyMatch {
            it.property.name == property.first && property.second == Util.getPropertyName(it.property, it.value)
        }
    }
    //endregion

}