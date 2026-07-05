package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

val Block.key get() = BuiltInRegistries.BLOCK.getKey(this)

val Block.serialization get() = SerializePrimitive(key.toString())

val Minecraft.targetBlock: BlockInfo?
    get() = hitBlock?.let { result -> level?.let {
        @Suppress("DEPRECATION")
        BlockInfo(result)
    } }

val SerializeElement.asBlock: Block
    get() = this.checkType<SerializePrimitive, Block> {
        BuiltInRegistries.BLOCK.get(Identifier.parse(it.value.requireType())).get().value()
    }

fun BlockState.hasTag(tag: String): Boolean = this.tags().anyMatch { it.location.toString() == tag }