package tech.sethi.pebbles.backpack.inventory

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.screen.GenericContainerScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.screen.slot.SlotActionType
import tech.sethi.pebbles.backpack.api.Backpack.Companion.isBackpack
import tech.sethi.pebbles.backpack.api.BackpackTier
import tech.sethi.pebbles.backpack.debounce.debounce
import tech.sethi.pebbles.backpack.storage.BackpackCache
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

class BasicBackpackScreenHandler(
    tier: BackpackTier,
    syncId: Int,
    private val backpackUUID: UUID,
    private val playerInventory: PlayerInventory,
    backpackInventory: Inventory
) : GenericContainerScreenHandler(
    getScreenHandlerType(tier),
    syncId,
    playerInventory,
    backpackInventory,
    tier.size / 9
) {

    companion object {
        fun getScreenHandlerType(tier: BackpackTier): ScreenHandlerType<*> {
            return when (tier) {
                BackpackTier.Copper -> ScreenHandlerType.GENERIC_9X2
                BackpackTier.Iron -> ScreenHandlerType.GENERIC_9X3
                BackpackTier.Gold -> ScreenHandlerType.GENERIC_9X4
                BackpackTier.Diamond -> ScreenHandlerType.GENERIC_9X5
                BackpackTier.Netherite -> ScreenHandlerType.GENERIC_9X6
                else -> throw IllegalStateException("Attempted to use improper screen handler for tier $tier")
            }
        }
    }

    private val save = debounce(time = 5.seconds, key = backpackUUID) {
        BackpackCache.saveAsync(backpackUUID)
    }

    override fun insertItem(stack: ItemStack, startIndex: Int, endIndex: Int, fromLast: Boolean): Boolean {
        if (isBackpack(stack)) return false
        return super.insertItem(stack, startIndex, endIndex, fromLast)
    }

    override fun onSlotClick(slotIndex: Int, button: Int, actionType: SlotActionType?, player: PlayerEntity) {
        if (actionType == SlotActionType.SWAP) {
            val stack = player.inventory.getStack(button)
            if (isBackpack(stack)) return
        }
        if (slotIndex >= 0) {
            val stack = this.slots[slotIndex].stack
            if (isBackpack(stack)) return
        }

        super.onSlotClick(slotIndex, button, actionType, player)
        if (!player.world.isClient) {
            save()
        }
    }



    override fun onContentChanged(inventory: Inventory?) {
        super.onContentChanged(inventory)
        val player = playerInventory.player
        if (!player.world.isClient) {
            save()
        }
    }

    override fun onClosed(player: PlayerEntity) {
        super.onClosed(player)
        if (!player.world.isClient) {
            save()
        }
    }

}