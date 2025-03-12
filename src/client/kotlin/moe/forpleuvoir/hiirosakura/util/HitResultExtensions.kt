package moe.forpleuvoir.hiirosakura.util

import net.minecraft.client.MinecraftClient
import net.minecraft.util.hit.BlockHitResult


val MinecraftClient.hitBlock: BlockHitResult?
    get() = crosshairTarget as BlockHitResult?
