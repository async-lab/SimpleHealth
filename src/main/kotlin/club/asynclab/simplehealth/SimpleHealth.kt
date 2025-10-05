package club.asynclab.simplehealth

import club.asynclab.simplehealth.event.CustomListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import org.bukkit.plugin.java.JavaPlugin
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors

class SimpleHealth : JavaPlugin() {
    val scope = CoroutineScope(SupervisorJob() + Executors.newVirtualThreadPerTaskExecutor().asCoroutineDispatcher())

    val playerChannels = ConcurrentHashMap<UUID, Channel<() -> Unit>>()

    override fun onEnable() {
        this.logger.info("SimpleHealth enabled")
        this.server.pluginManager.registerEvents(CustomListener(this), this)
    }

    override fun onDisable() {
        this.scope.cancel()
        this.logger.info("SimpleHealth disabled")
    }
}
