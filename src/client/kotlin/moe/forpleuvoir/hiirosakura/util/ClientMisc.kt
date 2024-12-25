package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.identifier
import moe.forpleuvoir.nebula.common.util.primitive.pick
import net.minecraft.client.MinecraftClient
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.util.Identifier

val MinecraftClient.tooltipType: TooltipType
    get() = this.options.advancedItemTooltips.pick(TooltipType.ADVANCED, TooltipType.BASIC)


internal fun identifier(path: String): Identifier = identifier(HiiroSakura.MOD_ID, path)

internal fun Any.logger(): ModLogger = ModLogger(this::class, HiiroSakura.MOD_NAME)