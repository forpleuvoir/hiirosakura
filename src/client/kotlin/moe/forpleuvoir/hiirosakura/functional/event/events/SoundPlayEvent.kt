package moe.forpleuvoir.hiirosakura.functional.event.events

import moe.forpleuvoir.hiirosakura.functional.script.deobfuscation.HSSoundInstance
import moe.forpleuvoir.nebula.event.CancellableEvent

/**
 * 表示一个播放声音的事件。
 *
 * 当触发播放某个声音实例的行为时激活此事件，事件中包含要播放的声音信息：
 * - `sound` 表示具体的声音实例，该实例封装了声音的相关属性和行为。
 *
 * 本事件可以被取消，取消后将阻止声音的播放。
 *
 * @property sound [HSSoundInstance] 表示需要播放的具体声音实例。
 */
class SoundPlayEvent(
    @JvmField
    val sound: HSSoundInstance
) : CancellableEvent {
    override var canceled: Boolean = false
}