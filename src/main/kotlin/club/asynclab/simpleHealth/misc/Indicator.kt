package club.asynclab.simpleHealth.misc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import kotlin.math.ceil

object Indicator {
    private val miniMessage = MiniMessage.miniMessage()

    fun trace(player: Player): LivingEntity? {
        val range = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)?.value ?: return null
        val entity = player.rayTraceEntities(range.toInt())?.hitEntity as? LivingEntity ?: return null
        return entity
    }

    fun render(player: Player, entity: LivingEntity) {
        val health = ceil(entity.health).toInt()
        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: return
        val rate = health / maxHealth

        val entityNameComponent = when {
            entity.customName() != null -> entity.customName()!!
            entity is Player -> Component.text(entity.name)
            else -> Component.translatable(entity.type.translationKey())
        }.color(NamedTextColor.GRAY)

        val healthComponent =
            miniMessage.deserialize("<transition:red:yellow:green:${"%.4f".format(rate)}>${if (entity.isDead) "☠" else "❤ $health"}</transition>")

        val actionBarComponent = Component.text()
            .append(entityNameComponent)
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(healthComponent)

        player.sendActionBar(actionBarComponent)
    }
}