package tech.sethi.pebbles.backpack.api

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.ProfileComponent
import net.minecraft.inventory.SimpleInventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import tech.sethi.pebbles.backpack.compenents.ModComponents
import tech.sethi.pebbles.backpack.items.BackpackItems
import tech.sethi.pebbles.backpack.items.BackpackPolymerItem
import tech.sethi.pebbles.backpack.migration.LegacyMigration
import java.util.*

class Backpack(val uuid: UUID = UUID.randomUUID(), val tier: BackpackTier, items: DefaultedList<ItemStack>) {

    @Transient
    val inventory = SimpleInventory(tier.size)

    init {
        items.forEachIndexed { index, itemStack ->
            inventory.setStack(index, itemStack)
        }
    }

    fun toItemStack(): ItemStack {
        val item = ItemStack(BackpackItems.BACKPACK)

        val profileComponent = ProfileComponent(
            GameProfile(UUID.nameUUIDFromBytes(tier.skullOwner.toByteArray()), "")
        )

        profileComponent.properties.put(
            "textures", Property("textures", tier.texture)
        )

        item.set(ModComponents.BackpackUUID, uuid)
        item.set(DataComponentTypes.CUSTOM_NAME, Text.literal(tier.itemName))
        item.set(DataComponentTypes.PROFILE, profileComponent)

        return item
    }

    companion object {
        fun isBackpack(itemStack: ItemStack): Boolean {
            if (itemStack.item != Items.PLAYER_HEAD && itemStack.item != BackpackItems.BACKPACK) return false
            return LegacyMigration.isLegacyBackpack(itemStack) || itemStack.contains(ModComponents.BackpackUUID)
        }
    }

}