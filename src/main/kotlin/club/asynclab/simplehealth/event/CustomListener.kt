package club.asynclab.simplehealth.event

import club.asynclab.simplehealth.SimpleHealth
import club.asynclab.simplehealth.misc.Indicator
import com.destroystokyo.paper.event.server.ServerTickEndEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent


class CustomListener(private val plugin: SimpleHealth) : Listener {
    @EventHandler
    fun onTick(event: ServerTickEndEvent) {
        Bukkit.getServer().onlinePlayers.forEach { player ->
            plugin.playerChannels[player.uniqueId]?.trySend {
                Indicator.render(player, Indicator.trace(player) ?: return@trySend)
            }
        }
    }

    @EventHandler
    fun onAttack(event: EntityDamageByEntityEvent) {
        val player = event.damageSource.causingEntity as? Player ?: return
        val entity = event.entity as? LivingEntity ?: return

        plugin.playerChannels[player.uniqueId]?.trySend {
            Indicator.render(player, entity)
        }
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val channel = Channel<() -> Unit>()
        plugin.playerChannels[event.player.uniqueId] = channel
        plugin.scope.launch {
            for (f in channel) {
                f()
            }
        }
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        plugin.playerChannels.remove(event.player.uniqueId)?.close()
    }
}