package moe.forpleuvoir.hiirosakura.util

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult

val MinecraftClient.hitBlock: BlockHitResult?
    get() {
        val target = crosshairTarget
        if (target != null && target.type == HitResult.Type.BLOCK) {
            return target as BlockHitResult
        }
        return null
    }
