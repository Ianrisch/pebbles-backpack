package tech.sethi.pebbles.backpack.api

import net.minecraft.text.Text

enum class BackpackTier(
    val size: Int,
    val description: Text,
    val itemName: String,
    val skullOwner: String,
    val texture: String,
) {
    Leather(
        size = 9,
        description = Text.literal("Leather Backpack"),
        itemName = "Leather Backpack",
        skullOwner = "[I;-1865738760,-355187999,-1172757398,374987400]",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNDBiMWI1MzY3NDkxODM5MWEwN2E5ZDAwNTgyYzA1OGY5MjgwYmM1MjZhNzE2Yzc5NmVlNWVhYjRiZTEwYTc2MCJ9fX0=",
    ),
    Copper(
        size = 18,
        description = Text.literal("Copper Backpack"),
        itemName = "Copper Backpack",
        skullOwner = "[I;1162937850,1879723887,-1267568232,-499049394]",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWU1ODNjYjc3MTU4MWQzYjI3YjIzZjYxN2M3YjhhNDNkY2Q3MjIwNDQ3ZmY5NWZmMTk2MDQxNGQyMzUwYmRiOSJ9fX0=",
    ),
    Iron(
        size = 27,
        description = Text.literal("Iron Backpack"),
        skullOwner = "[I;1804696949,1735083680,-1716683629,-1934495154]",
        itemName = "Iron Backpack",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGRhZjhlZGMzMmFmYjQ2MWFlZTA3MTMwNTgwMjMxMDFmOTI0ZTJhN2VmYTg4M2RhZTcyZDVkNTdkNGMwNTNkNyJ9fX0=",

        ),
    Gold(
        size = 36,
        description = Text.literal("Gold Backpack"),
        skullOwner = "[I;1780200479,157369315,-1565115920,-961015289]",
        itemName = "Gold Backpack",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvY2Y4NzUyNWFkODRlZmQxNjgwNmEyNmNhMDE5ODRiMjgwZTViYTY0MDM1MDViNmY2Yzk4MDNjMjQ2NDJhYmZjNyJ9fX0=",

        ),
    Diamond(
        size = 45,
        description = Text.literal("Diamond Backpack"),
        skullOwner = "[I;-104595003,-2052699552,-1909633784,2079891327]",
        itemName = "Diamond Backpack",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTBkMWIwNzMyYmY3YTcwZGU0ZGMwMTU1OWNjNWM5ODExMDY4ZWY3YjYwOTUwMTAzODI3MDlmOTQwOTM5MjdmNiJ9fX0=",


        ),
    Netherite(
        size = 54,
        description = Text.literal("Netherite Backpack"),
        skullOwner = "[I;-814574281,-1699395768,-1993160043,-1564669232]",
        itemName = "Netherite Backpack",
        texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODM1ZDdjYzA5ZmZmYmNhM2UxYzAwZDQyMWFmYWE0MzJjZjcxZmNiMDk1NTVmNTQ1MjNlNTIyMGQxYWYwZjk3ZCJ9fX0=",

        )
}