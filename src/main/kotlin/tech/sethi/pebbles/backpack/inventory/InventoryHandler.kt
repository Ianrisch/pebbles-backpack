package tech.sethi.pebbles.backpack.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.SimpleNamedScreenHandlerFactory
import tech.sethi.pebbles.backpack.api.Backpack

object InventoryHandler {

    fun openBackpack(player: PlayerEntity, backpack: Backpack) {
        player.openHandledScreen(createScreenHandlerFactory(backpack))
    }

    private fun createScreenHandlerFactory(backpack: Backpack): NamedScreenHandlerFactory {
        return SimpleNamedScreenHandlerFactory({ syncId, inv, _ ->
            BackpackScreenHandler(syncId, backpack.tier.size / 9, backpack.uuid, inv, backpack.inventory)
        }, backpack.tier.description)
    }

}