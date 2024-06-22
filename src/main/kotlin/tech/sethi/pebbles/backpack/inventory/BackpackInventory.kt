package tech.sethi.pebbles.backpack.inventory

import net.minecraft.inventory.SimpleInventory

class BackpackInventory(size: Int) : SimpleInventory(size) {

    var playerName: String? = null

    fun getRows(): Int {
        return this.size() / 9
    }

}
