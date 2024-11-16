package tech.sethi.pebbles.backpack.storage.adapters

import com.google.gson.*
import com.mojang.serialization.JsonOps
import net.minecraft.component.ComponentMap
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.lang.reflect.Type

class ItemStackTypeAdapter : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    override fun serialize(src: ItemStack?, typeOfSrc: Type, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonNull.INSTANCE

        val jsonObject = JsonObject()
        jsonObject.addProperty("item", Registries.ITEM.getId(src.item).toString())
        jsonObject.addProperty("count", src.count)

        if (!src.components.isEmpty) {
            jsonObject.add("nbt", ComponentMap.CODEC.encodeStart(JsonOps.INSTANCE, src.components).orThrow)
        }

        return jsonObject
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): ItemStack? {
        if (json is JsonNull) return null

        val jsonObject = json.asJsonObject
        val item = Registries.ITEM.get(Identifier.of(jsonObject["item"].asString))
        val count = jsonObject["count"].asInt

        val stack = ItemStack(item, count)

        if (jsonObject.has("nbt")) {
            try {
                stack.applyComponentsFrom(
                    ComponentMap.CODEC.parse(
                        JsonOps.INSTANCE, jsonObject["nbt"]
                    ).orThrow
                )
            } catch (_: Exception) {

            }
        }

        return stack
    }
}

