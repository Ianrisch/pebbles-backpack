package tech.sethi.pebbles.backpack.migration

import net.minecraft.item.ItemStack

data class BackpackData(val id: Int, val size: Int, val items: List<ItemStack>, var playerName: String? = null)
