package club.asynclab.simplehealth

import club.asynclab.simplehealth.event.CustomListener
import org.bukkit.plugin.java.JavaPlugin

class SimpleHealth : JavaPlugin() {
    private val listener = CustomListener()

    override fun onEnable() {
        this.logger.info("SimpleHealth enabled")
        this.server.pluginManager.registerEvents(this.listener, this)
    }

    override fun onDisable() {
        this.listener.clear()
        this.logger.info("SimpleHealth disabled")
    }
}
