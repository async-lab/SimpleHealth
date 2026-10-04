package club.asynclab.simplehealth.event

import club.asynclab.simplehealth.misc.Indicator
import com.destroystokyo.paper.event.server.ServerTickEndEvent
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.util.*


class CustomListener : Listener {
    private val pendingHits = mutableMapOf<UUID, EntityDamageByEntityEvent>()

    @EventHandler
    fun onTick(event: ServerTickEndEvent) {
        try {
            Bukkit.getServer().onlinePlayers.forEach { player ->
                // Damage has settled; a hit takes priority over aim for this tick only.
                val hit = this.pendingHits.remove(player.uniqueId)?.takeUnless { it.isCancelled }
                val target = (hit?.entity as? LivingEntity)?.takeIf { it.isValid || it.isDead }
                    ?: Indicator.trace(player)?.takeIf { it.isValid || it.isDead }
                    ?: return@forEach
                Indicator.render(player, target)
            }
        } finally {
            this.clear()
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onAttack(event: EntityDamageByEntityEvent) {
        val player = event.damageSource.causingEntity as? Player ?: return
        if (!player.isOnline || event.entity !is LivingEntity) return
        this.pendingHits[player.uniqueId] = event
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        this.pendingHits.remove(event.player.uniqueId)
    }

    fun clear() {
        this.pendingHits.clear()
    }
}
