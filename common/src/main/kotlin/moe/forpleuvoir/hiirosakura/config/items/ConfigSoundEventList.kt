package moe.forpleuvoir.hiirosakura.config.items

import moe.forpleuvoir.nebula.common.util.checkType
import moe.forpleuvoir.nebula.common.util.requireType
import moe.forpleuvoir.nebula.config.ConfigGroup
import moe.forpleuvoir.nebula.config.item.configList
import moe.forpleuvoir.nebula.serialization.DeserializationException
import moe.forpleuvoir.nebula.serialization.base.SerializeElement
import moe.forpleuvoir.nebula.serialization.base.SerializePrimitive
import moe.forpleuvoir.nebula.serialization.codec.Codec
import net.minecraft.resources.Identifier
import net.minecraft.sounds.SoundEvent

context(group: ConfigGroup)
fun configSoundEventList(name: String, defaultValue: List<SoundEvent>) = configList(name, defaultValue, SoundEventCodec)

object SoundEventCodec : Codec<SoundEvent> {
    override fun serialization(target: SoundEvent): SerializeElement = SerializePrimitive(target.location.toString())
    override fun deserialization(data: SerializeElement): Result<SoundEvent> = DeserializationException.runCatching {
        data.checkType<SerializePrimitive, SoundEvent> {
            SoundEvent.createVariableRangeEvent(Identifier.parse(it.value.requireType()))
        }
    }
}

//------------ UI Wrapper 需要播放按钮 ------------\\
