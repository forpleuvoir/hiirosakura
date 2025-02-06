package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.nebula.config.container.ConfigContainer
import moe.forpleuvoir.nebula.config.item.impl.ConfigList
import moe.forpleuvoir.nebula.serialization.Deserializer
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.extensions.checkType
import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier

class ConfigSoundEventList(
    key: String,
    defaultValue: List<SoundEvent> = emptyList()
) : ConfigList<SoundEvent>(
    key,
    defaultValue,
    {
        it.serialization()
    }, {
        SoundEventDeserializer.deserialization(it)
    }
)

fun ConfigContainer.soundEventList(
    key: String,
    defaultValue: List<SoundEvent> = emptyList()
) = addConfig(ConfigSoundEventList(key, defaultValue))

fun SoundEvent.serialization(): SerializeElement =
    SerializePrimitive(this.id.toString())

object SoundEventDeserializer : Deserializer<SoundEvent> {
    override fun deserialization(serializeElement: SerializeElement): SoundEvent {
        return serializeElement.checkType<SerializePrimitive, SoundEvent> {
            SoundEvent.of(Identifier.of(it.asString))
        }.getOrThrow()
    }

}