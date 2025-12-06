package moe.forpleuvoir.hiirosakura.util

import net.minecraft.client.Minecraft
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

val Minecraft.hitBlock: BlockHitResult?
    get() {
        val target = hitResult
        if (target != null && target.type == HitResult.Type.BLOCK) {
            return target as BlockHitResult
        }
        return null
    }
