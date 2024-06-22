package tech.sethi.pebbles.backpack

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.registry.Registries
import net.minecraft.server.MinecraftServer
import net.minecraft.util.*
import net.minecraft.world.World
import org.slf4j.LoggerFactory
import tech.sethi.pebbles.backpack.inventory.InventoryHandler
import tech.sethi.pebbles.backpack.migration.LegacyMigration
import tech.sethi.pebbles.backpack.storage.BackpackCache
import java.io.File
import java.util.concurrent.CompletableFuture

class PebblesBackpackInitializer : ModInitializer {
    private val logger = LoggerFactory.getLogger("pebbles-backpack")

    override fun onInitialize() {
        logger.info("Registering Pebble's Backpack Commands!")

        ServerLifecycleEvents.SERVER_STARTED.register { server ->
            BackpackCommands.register(server.commandManager.dispatcher)
        }

        ServerLifecycleEvents.SERVER_STARTING.register(ServerLifecycleEvents.ServerStarting { server ->
            BackpackCache.initialize(getOrCreateRootBackpackFolder(server))
            LegacyMigration.migrateLegacyBackpacks(server)
        })


        UseBlockCallback.EVENT.register(UseBlockCallback { player, world, hand, _ ->
            val result = handleBackpackInteraction(player, world, hand)
            return@UseBlockCallback if (result) ActionResult.SUCCESS else ActionResult.PASS
        })


        UseItemCallback.EVENT.register(UseItemCallback { player, world, hand ->
            handleBackpackInteraction(player, world, hand)
            val stack = player.getStackInHand(hand)
            return@UseItemCallback TypedActionResult.pass(stack)
        })


        logger.info("Pebble's Backpack loaded!")
    }

    private fun handleBackpackInteraction(player: PlayerEntity, world: World, hand: Hand): Boolean {
        if (world.isClient) return false

        val stack = player.getStackInHand(hand)
        val skullItem = Registries.ITEM.get(Identifier("minecraft:player_head"))
        if (stack.item != skullItem) return false

        LegacyMigration.migrateItemStack(stack)
        if (!stack.orCreateNbt.containsUuid("BackpackUUID")) return false
        val backpackUUID = stack.orCreateNbt.getUuid("BackpackUUID")

        val backpack = BackpackCache[backpackUUID]
        if (backpack != null) {
            InventoryHandler.openBackpack(player, backpack)
        }

        return true
    }

    private fun getOrCreateRootBackpackFolder(server: MinecraftServer): File {
        val worldDir = server.getSavePath(WorldSavePath.ROOT).toFile()
        val rootFile = File(worldDir, "/backpacks/")
        if (!rootFile.exists()) {
            rootFile.mkdirs()
        }
        return rootFile
    }

}
