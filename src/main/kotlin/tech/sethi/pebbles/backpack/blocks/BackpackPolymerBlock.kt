package tech.sethi.pebbles.backpack.blocks

import eu.pb4.polymer.core.api.block.PolymerHeadBlock
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.math.BlockPos


class BackpackPolymerBlock() : PolymerHeadBlock, Block(Settings.create()) {
    override fun getPolymerSkinValue(p0: BlockState?, p1: BlockPos?, p2: ServerPlayerEntity?): String {
        return ""
    }
}

