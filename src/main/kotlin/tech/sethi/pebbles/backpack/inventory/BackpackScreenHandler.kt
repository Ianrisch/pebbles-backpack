package tech.sethi.pebbles.backpack.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.Slot
import net.minecraft.screen.slot.SlotActionType
import tech.sethi.pebbles.backpack.debounce.debounce
import tech.sethi.pebbles.backpack.migration.LegacyMigration
import tech.sethi.pebbles.backpack.storage.BackpackCache
import java.util.*
import kotlin.time.Duration.Companion.seconds

class BackpackScreenHandler(
    syncId: Int,
    rows: Int,
    private val backpackId: UUID,
    private val playerInventory: PlayerInventory,
    private val backpackInventory: BackpackInventory
) : ScreenHandler(getScreenHandlerTypeForRows(rows), syncId) {

    companion object {
        fun getScreenHandlerTypeForRows(rows: Int): ScreenHandlerType<*> {
            return when (rows) {
                2 -> ScreenHandlerType.GENERIC_9X2
                3 -> ScreenHandlerType.GENERIC_9X3
                4 -> ScreenHandlerType.GENERIC_9X4
                5 -> ScreenHandlerType.GENERIC_9X5
                6 -> ScreenHandlerType.GENERIC_9X6
                else -> ScreenHandlerType.GENERIC_3X3
            }
        }
    }

    private val save = debounce(5.seconds) {
        println("Saving")
        BackpackCache.saveAsync(backpackId)
    }

    init {
        // Backpack slots
        for (m in 0 until rows) {
            for (l in 0..8) {
                this.addSlot(Slot(backpackInventory, l + m * 9, 8 + l * 18, 18 + m * 18))
            }
        }

        // Player inventory slots
        val playerInventoryYOffset = 18 + (rows - 1) * 18 + 12
        for (m in 0..2) {
            for (l in 0..8) {
                this.addSlot(Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, playerInventoryYOffset + m * 18))
            }
        }

        // Player hotbar slots
        for (l in 0..8) {
            this.addSlot(Slot(playerInventory, l, 8 + l * 18, playerInventoryYOffset + 58))
        }
    }

    override fun quickMove(player: PlayerEntity?, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[index]
        if (slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (index < this.backpackInventory.size()) {
                if (!this.insertItem(itemStack2, this.backpackInventory.size(), this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!this.insertItem(itemStack2, 0, this.backpackInventory.size(), false)) {
                return ItemStack.EMPTY
            }

            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }

        if (player != null && !player.world.isClient) {
            save()
        }

        return itemStack
    }

    override fun insertItem(stack: ItemStack, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        if (isBackpack(stack)) {
            return false // Do not insert the item if it's a backpack
        }
        return super.insertItem(stack, startIndex, endIndex, fromLast)
    }

    override fun canUse(player: PlayerEntity): Boolean {
        return this.backpackInventory.canPlayerUse(player)
    }

    private fun isBackpack(itemStack: ItemStack): Boolean {
        return itemStack.item == Items.PLAYER_HEAD && LegacyMigration.isBackpack(itemStack)
    }

    override fun onContentChanged(inventory: Inventory?) {
        val player = playerInventory.player
        this.backpackInventory.markDirty()
        super.onContentChanged(inventory)
        if (!player.world.isClient) {
            save()
        }
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        save()
    }


    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity?) {
        if (slotIndex < 0) {
            return
        }
        val stack = this.slots[slotIndex].stack
        if (isBackpack(stack)) {
            return // Do not insert the item if it's a backpack
        }

        this.backpackInventory.markDirty()
        super.onSlotClick(slotIndex, button, actionType, player)

        if (player != null) {
            if (!player.world.isClient) {
                save()
            }
        }
    }
}
