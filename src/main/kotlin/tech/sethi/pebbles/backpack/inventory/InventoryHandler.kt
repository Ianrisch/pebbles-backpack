package tech.sethi.pebbles.backpack.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import tech.sethi.pebbles.backpack.api.Backpack
import tech.sethi.pebbles.backpack.api.BackpackTier

object InventoryHandler {

    fun openBackpack(player: PlayerEntity, backpack: Backpack) {
        player.openHandledScreen(createScreenHandlerFactory(backpack))
    }

    private fun createScreenHandlerFactory(backpack: Backpack): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId, inv, _ ->
            if (backpack.tier == BackpackTier.Leather) {
                return@SimpleNamedScreenHandlerFactory LeatherBackpackScreenHandler(syncId, backpack.uuid, inv, backpack.inventory)
            }
            else {
                return@SimpleNamedScreenHandlerFactory BasicBackpackScreenHandler(backpack.tier, syncId, backpack.uuid, inv, backpack.inventory)
            }
        }, backpack.tier.description)
    }

}