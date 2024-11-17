package tech.sethi.pebbles.backpack.compenents

import eu.pb4.polymer.core.api.other.PolymerComponent
import net.minecraft.component.ComponentType
import net.minecraft.component.ComponentType.builder
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.Uuids
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer.Companion.LOGGER
import java.util.*

object ModComponents {

    val BackpackUUID: ComponentType<UUID> = register(
        "backpack_uuid", builder<UUID>().codec(Uuids.CODEC).build()
    )

    private fun <T> register(path: String, componentType: ComponentType<T>): ComponentType<T> {
        val registeredComponentType = Registry.register(
            Registries.DATA_COMPONENT_TYPE, Identifier.of(PebblesBackpackInitializer.MODID, path), componentType
        )
        PolymerComponent.registerDataComponent(registeredComponentType)
        return registeredComponentType
    }

    fun initialize() {
        LOGGER.info("Registering {} components", PebblesBackpackInitializer.MODID)
        // Technically this method can stay empty, but some developers like to notify
        // the console, that certain parts of the mod have been successfully initialized


    }
}