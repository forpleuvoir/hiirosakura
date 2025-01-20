package moe.forpleuvoir.hiirosakura.util

import net.minecraft.block.Block
import net.minecraft.registry.Registries

val Block.id get() = Registries.BLOCK.getId(this)