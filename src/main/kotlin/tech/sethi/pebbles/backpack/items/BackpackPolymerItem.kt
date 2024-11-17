package tech.sethi.pebbles.backpack.items

import eu.pb4.polymer.core.api.item.PolymerHeadBlockItem
import net.minecraft.component.DataComponentTypes
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.registry.RegistryWrapper
import net.minecraft.server.network.ServerPlayerEntity
import tech.sethi.pebbles.backpack.blocks.BackpackPolymerBlock

class BackpackPolymerItem(block: BackpackPolymerBlock) : PolymerHeadBlockItem(block, Settings()) {

    override fun getPolymerItemStack(
        itemStack: ItemStack?,
        tooltipType: TooltipType?,
        lookup: RegistryWrapper.WrapperLookup?,
        player: ServerPlayerEntity?
    ): ItemStack {
        val out = super.getPolymerItemStack(itemStack, tooltipType, lookup, player)
        if (itemStack != null) out.set(DataComponentTypes.PROFILE, itemStack.get(DataComponentTypes.PROFILE))
        return out
    }
}