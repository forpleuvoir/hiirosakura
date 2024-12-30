package moe.forpleuvoir.hiirosakura.functional.script.deobfuscation

import net.minecraft.block.Block
import net.minecraft.registry.Registries

class HSBlock(private val block: Block) {

    fun getType() = Registries.BLOCK.getId(block).toString()

}