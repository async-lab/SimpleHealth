package club.asynclab.simplehealth.misc

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import java.util.*
import kotlin.math.ceil

object Indicator {
    private val miniMessage = MiniMessage.miniMessage()

    fun trace(player: Player): LivingEntity? {
        val range = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE)?.value ?: return null
        return player.getTargetEntity(range.toInt()) as? LivingEntity
    }

    fun render(player: Player, entity: LivingEntity) {
        val health = ceil(entity.health).toInt()
        val maxHealth = entity.getAttribute(Attribute.MAX_HEALTH)?.value ?: return
        val rate = (health / maxHealth).coerceIn(0.0, 1.0)

        val entityNameComponent = (entity.customName() ?: when (entity) {
            is Player -> Component.text(entity.name)
            else -> Component.translatable(entity.type.translationKey())
        }).color(NamedTextColor.GRAY)

        val formattedRate = "%.4f".format(Locale.ROOT, rate)
        val healthValue = if (entity.isDead) "☠" else "❤ $health"
        val healthComponent =
            miniMessage.deserialize("<transition:red:yellow:green:$formattedRate>$healthValue</transition>")

        val actionBarComponent = Component.text()
            .append(entityNameComponent)
            .append(Component.text(" -> ", NamedTextColor.GRAY))
            .append(healthComponent)

        player.sendActionBar(actionBarComponent)
    }
}
