package tech.sethi.pebbles.backpack.api

import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.nbt.NbtHelper
import net.minecraft.util.collection.DefaultedList
import tech.sethi.pebbles.backpack.inventory.BackpackInventory
import java.util.UUID

class Backpack(val uuid: UUID = UUID.randomUUID(), val tier: BackpackTier, items: DefaultedList<ItemStack>) {

    @Transient
    val inventory = BackpackInventory(tier.size)

    init {
        items.forEachIndexed { index, itemStack ->
            inventory.setStack(index, itemStack)
        }
    }

    fun toItemStack(): ItemStack {
        val item = ItemStack(Items.PLAYER_HEAD)
        item.nbt = NbtHelper.fromNbtProviderString(tier.nbt)
        item.orCreateNbt.putUuid("BackpackUUID", uuid)
        return item
    }

}