package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

val Block.id get() = Registries.BLOCK.getId(this)

val Block.serialization get() = SerializePrimitive(id.toString())

val SerializeElement.block: Block
    get() = this.checkType<SerializePrimitive, Block> {
        Registries.BLOCK.get(Identifier.of(it.asString))
    }.getOrThrow()

fun BlockState.hasTag(tag: String): Boolean = this.streamTags().anyMatch { it.id.toString() == tag }