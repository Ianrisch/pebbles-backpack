package tech.sethi.pebbles.backpack.migration

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.server.MinecraftServer
import net.minecraft.util.WorldSavePath
import net.minecraft.util.collection.DefaultedList
import tech.sethi.pebbles.backpack.api.Backpack
import tech.sethi.pebbles.backpack.api.BackpackTier
import tech.sethi.pebbles.backpack.compenents.ModComponents
import tech.sethi.pebbles.backpack.storage.BackpackCache
import tech.sethi.pebbles.backpack.storage.adapters.ItemStackTypeAdapter
import java.io.File
import java.util.*

object LegacyMigration {

    private val legacyGson = GsonBuilder().registerTypeAdapter(ItemStack::class.java, ItemStackTypeAdapter()).create()


    fun migrateItemStack(backpack: ItemStack) {
        if (!isLegacyBackpack(backpack)) return

        val nbt = backpack.get(DataComponentTypes.CUSTOM_DATA)?.nbt!!
        val legacyLegacyId = nbt.getInt("BackpackID")
        var uuid = nbt.getUuid("BackpackUUID")
        if (legacyLegacyId != 0) {
            uuid = UUID(0L, legacyLegacyId.toLong())
            nbt.remove("BackpackID")
        }
        nbt.remove("BackpackUUID")

        backpack.set(ModComponents.BackpackUUID, uuid)
        backpack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt))
    }

    private fun isLegacyBackpack(backpack: ItemStack): Boolean {
        if (backpack.components.isEmpty) return false
        val oldData = backpack.get(DataComponentTypes.CUSTOM_DATA) ?: return false
        return oldData.contains("BackpackID") || oldData.contains("BackpackUUID")
    }

    fun migrateLegacyBackpacks(server: MinecraftServer) {
        val worldDir = server.getSavePath(WorldSavePath.ROOT).toFile()
        val legacySaveFile = File(worldDir, "pebbles_backpacks.json")
        if (!legacySaveFile.exists()) return

        val backupDestination = File(worldDir, "pebbles_backpacks.backup.json")

        backupLegacyFile(legacySaveFile, backupDestination)

        legacySaveFile.reader().use { reader ->
            val jsonElement = JsonParser.parseReader(reader)
            var backpackDataArray = JsonArray()
            if (!jsonElement.isJsonNull) {
                backpackDataArray = jsonElement.asJsonArray
            }

            for (backpackDataJson in backpackDataArray) {
                val backpackData = legacyGson.fromJson(backpackDataJson, BackpackData::class.java)
                val uuid = UUID(0L, backpackData.id.toLong())
                val tier = getBackpackTierFromSize(backpackData.size)
                val items = DefaultedList.ofSize(tier.size, ItemStack.EMPTY)
                for (i in 0..backpackData.items.size) {
                    if (i < tier.size) items[i] = backpackData.items[i]
                }

                val backpack = Backpack(uuid, tier, items)
                BackpackCache[uuid] = backpack
                BackpackCache.save(uuid)
            }
        }

        legacySaveFile.delete()
    }

    private fun backupLegacyFile(original: File, destination: File) {
        if (!original.exists()) return

        val contents = original.readText()
        if (!destination.exists()) destination.createNewFile()
        destination.writeText(contents)
    }

    private fun getBackpackTierFromSize(size: Int): BackpackTier {
        return BackpackTier.entries.firstOrNull { it.size == size } ?: BackpackTier.Copper
    }

}