package moe.forpleuvoir.hiirosakura.util

import moe.forpleuvoir.hiirosakura.HiiroSakura
import moe.forpleuvoir.ibukigourd.util.ModLogger
import moe.forpleuvoir.ibukigourd.util.identifier
import net.minecraft.util.Identifier

internal fun Any.logger(): ModLogger = ModLogger(this::class, HiiroSakura.MOD_NAME)

internal fun identifier(path: String): Identifier = identifier(HiiroSakura.MOD_ID, path)
