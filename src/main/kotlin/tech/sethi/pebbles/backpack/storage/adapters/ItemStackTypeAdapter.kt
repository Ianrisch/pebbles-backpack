package tech.sethi.pebbles.backpack.storage.adapters

import com.google.gson.*
import kotlinx.serialization.json.Json
import net.minecraft.item.ItemStack
import net.minecraft.nbt.StringNbtReader
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import java.lang.reflect.Type

class ItemStackTypeAdapter : JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
    override fun serialize(src: ItemStack?, typeOfSrc: Type, context: JsonSerializationContext?): JsonElement {
        if (src == null) return JsonNull.INSTANCE

        val jsonObject = JsonObject()
        jsonObject.addProperty("item", Registries.ITEM.getId(src.item).toString())
        jsonObject.addProperty("count", src.count)

        if (src.hasNbt()) {
            jsonObject.addProperty("nbt", src.nbt.toString())
        }

        return jsonObject
    }

    @Throws(JsonParseException::class)
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext?): ItemStack? {
        if (json is JsonNull) return null

        val jsonObject = json.asJsonObject
        val item = Registries.ITEM.get(Identifier(jsonObject["item"].asString))
        val count = jsonObject["count"].asInt

        val stack = ItemStack(item, count)

        if (jsonObject.has("nbt")) {
            val nbtString = jsonObject["nbt"].asString
            val nbt = StringNbtReader.parse(nbtString)
            stack.nbt = nbt
        }

        return stack
    }
}

