package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.functional.misc.matcher.BlockInfo
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.client.Minecraft
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState

val Block.key get() = BuiltInRegistries.BLOCK.getKey(this)

val Block.serialization get() = SerializePrimitive(key.toString())

val Minecraft.targetBlock: BlockInfo?
    get() = hitBlock?.let { BlockInfo(it) }

val SerializeElement.asBlock: Block
    get() = this.checkType<SerializePrimitive, Block> {
        BuiltInRegistries.BLOCK.get(ResourceLocation.parse(it.asString)).get().value()
    }.getOrThrow()

fun BlockState.hasTag(tag: String): Boolean = this.tags.anyMatch { it.location.toString() == tag }