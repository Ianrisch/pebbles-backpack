package tech.sethi.pebbles.backpack.items

import eu.pb4.polymer.core.api.item.SimplePolymerItem
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer


object BackpackItems {

    val BACKPACK: Item = register(SimplePolymerItem(Item.Settings()., Items.PLAYER_HEAD), "backpack")

    private fun register(item: Item, path: String): Item {
        return Registry.register(Registries.ITEM, Identifier.of(PebblesBackpackInitializer.MODID, path), item)
    }

    fun initialize() {
        PebblesBackpackInitializer.LOGGER.info("Registering {} items", PebblesBackpackInitializer.MODID)
    }
}