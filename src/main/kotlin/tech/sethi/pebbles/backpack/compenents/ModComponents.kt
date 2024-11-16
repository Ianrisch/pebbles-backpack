package tech.sethi.pebbles.backpack.compenents

import net.minecraft.component.ComponentType
import net.minecraft.component.ComponentType.builder
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Uuids
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer
import java.util.*

class ModComponents {

    companion object {
        val BackpackUUID: ComponentType<UUID> = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(PebblesBackpackInitializer.MODID, "backpack_uuid"),
            builder<UUID>().codec(Uuids.CODEC).build()
        )

        fun initialize() {
            PebblesBackpackInitializer.LOGGER.info("Registering {} components", PebblesBackpackInitializer.MODID)
            // Technically this method can stay empty, but some developers like to notify
            // the console, that certain parts of the mod have been successfully initialized
        }
    }


}