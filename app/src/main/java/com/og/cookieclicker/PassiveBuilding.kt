import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PassiveBuilding(
    val id: String,
    val name: String,
    val description: String,
    val imageRes: Int,
    val initialCost: Double,
    val baseProduction: Double,
    val costMultiplier: Double,

    // Variabile SpriteSheet
    val isSpriteSheet: Boolean = false,
    val columns: Int = 1,
    val rows: Int = 1,
    val frameDuration: Long = 100L
) {
    var cost by mutableStateOf(initialCost)
    var count by mutableStateOf(0.0)

    val totalProduction: Double
        get() = count * baseProduction

    fun performUpgrade() {
        count += 1.0
        cost *= costMultiplier
    }
}