package club.asynclab.simplehealth.event

import club.asynclab.simplehealth.SimpleHealth
import club.asynclab.simplehealth.misc.Indicator
import com.destroystokyo.paper.event.server.ServerTickEndEvent
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

class CustomListener(private val plugin: SimpleHealth) : Listener {
    @EventHandler
    fun onTick(event: ServerTickEndEvent) {
        runBlocking {
            Bukkit.getServer().onlinePlayers.map { player ->
                plugin.scope.launch {
                    Indicator.render(player, Indicator.trace(player) ?: return@launch)
                }
            }.joinAll()
        }
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        Bukkit.getScheduler().runTask(plugin, Runnable {
            Indicator.render(
                event.damageSource.causingEntity as? Player ?: return@Runnable,
                event.entity as? LivingEntity ?: return@Runnable
            )
        })
    }
}