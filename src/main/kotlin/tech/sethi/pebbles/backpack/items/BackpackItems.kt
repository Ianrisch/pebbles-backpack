package tech.sethi.pebbles.backpack.items

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer
import tech.sethi.pebbles.backpack.blocks.BackpackPolymerBlock


object BackpackItems {

    private val BACKPACK_BLOCK: BackpackPolymerBlock =
        register(BackpackPolymerBlock(), "backpack_block") as BackpackPolymerBlock
    val BACKPACK: Item = register(BackpackPolymerItem(BACKPACK_BLOCK), "backpack")

    private fun register(item: Item, path: String): Item {
        return Registry.register(Registries.ITEM, Identifier.of(PebblesBackpackInitializer.MODID, path), item)
    }

    private fun register(item: Block, path: String): Block {
        return Registry.register(Registries.BLOCK, Identifier.of(PebblesBackpackInitializer.MODID, path), item)
    }

    fun initialize() {
        PebblesBackpackInitializer.LOGGER.info("Registering {} items", PebblesBackpackInitializer.MODID)
    }
}