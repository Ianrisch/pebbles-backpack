package tech.sethi.pebbles.backpack

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.luckperms.api.LuckPerms
import net.luckperms.api.LuckPermsProvider
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EntityArgumentType
import net.minecraft.command.argument.UuidArgumentType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.collection.DefaultedList
import tech.sethi.pebbles.backpack.api.Backpack
import tech.sethi.pebbles.backpack.api.BackpackTier
import tech.sethi.pebbles.backpack.inventory.BackpackInventory
import tech.sethi.pebbles.backpack.inventory.InventoryHandler
import tech.sethi.pebbles.backpack.storage.BackpackCache
import tech.sethi.pebbles.backpack.storage.adapters.ItemStackTypeAdapter
import java.io.File


object BackpackCommands {
    val backpacks = mutableMapOf<Int, BackpackInventory>()

    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        val padminCommand = CommandManager.literal("padmin").requires { source ->
            val player = source.player as? PlayerEntity
            player != null && (source.hasPermissionLevel(2) || isLuckPermsPresent() && getLuckPermsApi()?.userManager?.getUser(
                player.uuid
            )!!.cachedData.permissionData.checkPermission("pebbles.admin").asBoolean()) || source.entity == null
        }

        val createBackpackCommand = CommandManager.literal("bp")
            .then(
                CommandManager.literal("create")
                    .then(
                        CommandManager.argument("tier", StringArgumentType.word())
                            .suggests { _, builder ->
                                CommandSource.suggestMatching(BackpackTier.values().map { it.name }, builder)
                            }
                            .then(
                                CommandManager.argument("target", EntityArgumentType.players())
                                    .executes { ctx ->
                                        val target = EntityArgumentType.getPlayer(ctx, "target")
                                        val tier = BackpackTier.valueOf(StringArgumentType.getString(ctx, "tier"))

                                        val backpack = Backpack(tier = tier, items = DefaultedList.ofSize(tier.size, ItemStack.EMPTY))
                                        BackpackCache[backpack.uuid] = backpack
                                        BackpackCache.saveAsync(backpack.uuid)

                                        target.giveItemStack(backpack.toItemStack())

                                        ctx.source.sendFeedback(
                                            { Text.literal("Backpack created with uuid ${backpack.uuid} for ${target.name}") }, false
                                        )

                                        return@executes 1
                                    })
                    )
            )

        val getBackpackCommand = CommandManager.literal("bp")
            .then(
                CommandManager.literal("get")
                    .then(
                        CommandManager.argument("uuid", UuidArgumentType.uuid())
                            .suggests { _, builder ->
                                CommandSource.suggestMatching(BackpackCache.keys.map { it.toString() }, builder)
                            }
                            .executes { ctx ->
                                val uuid = UuidArgumentType.getUuid(ctx, "uuid")
                                val player = ctx.source.playerOrThrow

                                val backpack = BackpackCache[uuid]

                                if (backpack == null) {
                                    ctx.source.sendError(Text.literal("Backpack with uuid $uuid not found."))
                                    return@executes 0
                                }

                                player.giveItemStack(backpack.toItemStack())
                                ctx.source.sendFeedback(
                                    { Text.literal("Backpack retrieved with uuid $uuid") }, false
                                )

                                return@executes 1
                            })
            )



        padminCommand.then(getBackpackCommand)


        val openBackpackCommand = CommandManager.literal("bp")
            .then(
                CommandManager.argument("uuid", UuidArgumentType.uuid())
                    .suggests { _, builder ->
                        CommandSource.suggestMatching(BackpackCache.keys.map { it.toString() }, builder)
                    }
                    .executes { ctx ->
                        val uuid = UuidArgumentType.getUuid(ctx, "uuid")
                        val player = ctx.source.playerOrThrow
                        val backpack = BackpackCache[uuid]

                        if (backpack == null) {
                            ctx.source.sendError(Text.literal("Backpack with uuid $uuid not found."))
                            return@executes 0
                        }

                        InventoryHandler.openBackpack(player, backpack)
                        return@executes 1
                    })

        padminCommand.then(createBackpackCommand)
        padminCommand.then(openBackpackCommand)

        dispatcher.register(padminCommand)
    }

    private fun isLuckPermsPresent(): Boolean {
        return try {
            Class.forName("net.luckperms.api.LuckPerms")
            true
        } catch (e: ClassNotFoundException) {
            false
        }
    }

    private fun getLuckPermsApi(): LuckPerms? {
        return try {
            LuckPermsProvider.get()
        } catch (e: IllegalStateException) {
            null
        }
    }


    private fun getBackpackNbt(tier: String): String {
        return when (tier) {
            "leather" -> "{display:{Name:\"{\\\"text\\\":\\\"Leather Backpack\\\"}\"},SkullOwner:{Id:[I;-1865738760,-355187999,-1172757398,374987400],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBiMWI1MzY3NDkxODM5MWEwN2E5ZDAwNTgyYzA1OGY5MjgwYmM1MjZhNzE2Yzc5NmVlNWVhYjRiZTEwYTc2MCJ9fX0=\"}]}}}"
            "copper" -> "{display:{Name:\"{\\\"text\\\":\\\"Copper Backpack\\\"}\"},SkullOwner:{Id:[I;1162937850,1879723887,-1267568232,-499049394],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU1ODNjYjc3MTU4MWQzYjI3YjIzZjYxN2M3YjhhNDNkY2Q3MjIwNDQ3ZmY5NWZmMTk2MDQxNGQyMzUwYmRiOSJ9fX0=\"}]}}}"
            "iron" -> "{display:{Name:\"{\\\"text\\\":\\\"Iron Backpack\\\"}\"},SkullOwner:{Id:[I;1804696949,1735083680,-1716683629,-1934495154],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRhZjhlZGMzMmFmYjQ2MWFlZTA3MTMwNTgwMjMxMDFmOTI0ZTJhN2VmYTg4M2RhZTcyZDVkNTdkNGMwNTNkNyJ9fX0=\"}]}}}"
            "gold" -> "{display:{Name:\"{\\\"text\\\":\\\"Gold Backpack\\\"}\"},SkullOwner:{Id:[I;1780200479,157369315,-1565115920,-961015289],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y4NzUyNWFkODRlZmQxNjgwNmEyNmNhMDE5ODRiMjgwZTViYTY0MDM1MDViNmY2Yzk4MDNjMjQ2NDJhYmZjNyJ9fX0=\"}]}}}"
            "diamond" -> "{display:{Name:\"{\\\"text\\\":\\\"Diamond Backpack\\\"}\"},SkullOwner:{Id:[I;-104595003,-2052699552,-1909633784,2079891327],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBkMWIwNzMyYmY3YTcwZGU0ZGMwMTU1OWNjNWM5ODExMDY4ZWY3YjYwOTUwMTAzODI3MDlmOTQwOTM5MjdmNiJ9fX0=\"}]}}}"
            "netherite" -> "{display:{Name:\"{\\\"text\\\":\\\"Netherite Backpack\\\"}\"},SkullOwner:{Id:[I;-814574281,-1699395768,-1993160043,-1564669232],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1ZDdjYzA5ZmZmYmNhM2UxYzAwZDQyMWFmYWE0MzJjZjcxZmNiMDk1NTVmNTQ1MjNlNTIyMGQxYWYwZjk3ZCJ9fX0=\"}]}}}"
            "gucci" -> "{display:{Name:\"{\\\"text\\\":\\\"Gucci Backpack\\\"}\"},SkullOwner:{Id:[I;945208130,1552895596,-2057951394,2057894273],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTIwOGY1ODk3ZjEyZTVmY2IyZThiNDM4MWY1NDQ1YTc3MTFlODQ3MjFlYzRhN2ZjMTAxZDViNzQwYjg2ZjhmYSJ9fX0=\"}]}}}"
            else -> "{display:{Name:\"{\\\"text\\\":\\\"Bag\\\"}\"},SkullOwner:{Id:[I;-1980288287,-640760459,800809409,-1213206538],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzViMTE2ZGM3NjlkNmQ1NzI2ZjEyYTI0ZjNmMTg2ZjgzOTQyNzMyMWU4MmY0MTM4Nzc1YTRjNDAzNjdhNDkifX19\"}]}}}"
        }
    }
}
