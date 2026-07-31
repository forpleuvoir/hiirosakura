package moe.forpleuvoir.hiirosakura.util.codec

import moe.forpleuvoir.hiirosakura.util.asBlock
import moe.forpleuvoir.hiirosakura.util.asItem
import moe.forpleuvoir.hiirosakura.util.serialization
import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object BlockCodec : Codec<Block> {
    override fun serialization(target: Block): SerializeElement =
        target.serialization

    override fun deserialization(data: SerializeElement): Result<Block> = DeserializationException.runCatching {
        data.asBlock
    }
}

inline val Codec.Companion.block get() = BlockCodec

object EntityTypeCodec : Codec<EntityType<*>> {
    override fun serialization(target: EntityType<*>): SerializeElement =
        SerializePrimitive(BuiltInRegistries.ENTITY_TYPE.getKey(target).toString())

    override fun deserialization(data: SerializeElement): Result<EntityType<*>> = DeserializationException.runCatching {
        data.asEntityType
    }
}

inline val Codec.Companion.entityType get() = EntityTypeCodec

val SerializeElement.asEntityType: EntityType<*>
    get() = this.checkType<SerializePrimitive, EntityType<*>> {
        val id = Identifier.parse(it.value.requireType())
        BuiltInRegistries.ENTITY_TYPE.get(id).get().value()
    }

object UuidCodec : Codec<java.util.UUID> {
    override fun serialization(target: java.util.UUID): SerializeElement =
        SerializePrimitive(target.toString())

    override fun deserialization(data: SerializeElement): Result<java.util.UUID> = DeserializationException.runCatching {
        data.checkType<SerializePrimitive, java.util.UUID> {
            java.util.UUID.fromString(it.value.requireType())
        }
    }
}

inline val Codec.Companion.uuid get() = UuidCodec

object ItemCodec : Codec<Item> {
    override fun serialization(target: Item): SerializeElement =
        target.serialization

    override fun deserialization(data: SerializeElement): Result<Item> = DeserializationException.runCatching {
        data.asItem
    }
}

inline val Codec.Companion.item get() = ItemCodec

object DataComponentTypeCodec : Codec<DataComponentType<*>> {
    override fun serialization(target: DataComponentType<*>): SerializeElement =
        SerializePrimitive(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(target).toString())

    override fun deserialization(data: SerializeElement): Result<DataComponentType<*>> = DeserializationException.runCatching {
        data.asDataComponentType
    }
}

inline val Codec.Companion.dataComponentType get() = DataComponentTypeCodec

val SerializeElement.asDataComponentType: DataComponentType<*>
    get() = this.checkType<SerializePrimitive, DataComponentType<*>> {
        BuiltInRegistries.DATA_COMPONENT_TYPE.get(Identifier.parse(it.value.requireType())).get().value()
    }