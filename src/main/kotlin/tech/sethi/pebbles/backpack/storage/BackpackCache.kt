package tech.sethi.pebbles.backpack.storage

import com.google.gson.GsonBuilder
import tech.sethi.pebbles.backpack.PebblesBackpackInitializer
import tech.sethi.pebbles.backpack.api.Backpack
import tech.sethi.pebbles.backpack.storage.adapters.BackpackTypeAdapter
import java.io.File
import java.util.*
import java.util.concurrent.CompletableFuture

object BackpackCache {

    private lateinit var rootFolder: File

    private val cache = mutableMapOf<UUID, Backpack>()
    private val gson = GsonBuilder()
        .registerTypeAdapter(Backpack::class.java, BackpackTypeAdapter())
        .create()

    fun initialize(rootFolder: File) {
        this.rootFolder = rootFolder
        if (!rootFolder.exists()) {
            rootFolder.mkdirs()
        }
        loadAll()
    }

    val keys
        get() = cache.keys

    operator fun get(id: UUID): Backpack? = cache[id]

    operator fun set(id: UUID, backpack: Backpack) {
        cache[id] = backpack
    }

    private fun loadAll() {
        cache.clear()

        this.rootFolder.walkTopDown().forEach { file ->
            if (!file.name.endsWith(".json")) return@forEach

            file.reader().use { reader ->
                try {
                    val backpack = gson.fromJson(reader.readText(), Backpack::class.java)
                    if (backpack != null) cache[backpack.uuid] = backpack
                } catch (e: Exception) {
                    PebblesBackpackInitializer.LOGGER.warn("Could not load backpack file ${file.name}")
                    e.printStackTrace()
                }
            }
        }
    }

    fun saveAsync(uuid: UUID) {
        CompletableFuture.runAsync {
            save(uuid)
        }
    }

    fun save(uuid: UUID) {
        val backpack = cache[uuid] ?: return

        val file = File(rootFolder, "$uuid.json")
        try {
            file.writer().use { writer ->
                gson.toJson(backpack, writer)
            }
        }
        catch (e: Exception) {
            PebblesBackpackInitializer.LOGGER.warn("Could not save backpack file ${file.name}")
            e.printStackTrace()
        }
    }

}