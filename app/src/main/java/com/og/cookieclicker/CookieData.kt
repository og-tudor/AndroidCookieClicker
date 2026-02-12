package com.og.cookieclicker

import PassiveBuilding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.math.floor

data object CookieData {
    //  Clicker Values
    var score by mutableStateOf(0.0)
    var clickStrength by mutableStateOf(1.0)
    private var lastClickStength = 1.0
    var baseClickStrength by mutableStateOf(1.0)

    var upgradeStrengthCost by mutableStateOf(10.0)

    // Formula: Baza + (5% din producția pasiva)
    val totalClickStrength: Double
        get() {
            val passiveBoost = getPassiveCookiesCount() * 0.05
            return baseClickStrength + passiveBoost
        }

    fun upgradeStrength() {
        if (score >= upgradeStrengthCost) {
            score -= upgradeStrengthCost

            upgradeStrengthCost *= 2.5

            baseClickStrength *= 2
        }
    }

    fun getCookieImageState(): Int {
        return when {
            score < 1e6 -> 0
            score < 1e8 -> 1
            else -> 2
        }
    }

    // Experiment
    val experimentCost = 1e9

    // PASSIVE BUILDINGS
    val buildings = listOf(
        PassiveBuilding(
            id = "grandma",
            name = "Grandma",
            description = "Hire a grandma to bake cookies.",
            imageRes = R.drawable.grandma_cookies_sheet,
            initialCost = 50.0,
            baseProduction = 1.0,
            costMultiplier = 1.15,
            isSpriteSheet = true,
            columns = 4,
            rows = 1,
            frameDuration = 150L
        ),
        PassiveBuilding(
            id = "bakery",
            name = "Bakery",
            description = "Build a bakery for mass production.",
            imageRes = R.drawable.bakery,
            initialCost = 400.0,
            baseProduction = 10.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "factory",
            name = "Factory",
            description = "Industrial cookie production.",
            imageRes = R.drawable.factory,
            initialCost = 15000.0,
            baseProduction = 400.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "moonBase",
            name = "Moon Base",
            description = "Lunar cookie mining operation.",
            imageRes = R.drawable.moon_base,
            initialCost = 500000.0,
            baseProduction = 20000.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "cookieParticle",
            name = "Cookie Particle Collider",
            description = "Smash cookies at the quantum level.",
            imageRes = R.drawable.cookie_particle,
            initialCost = 1e7,
            baseProduction = 5 * 1e5,
            costMultiplier = 1.15
        )
    )

    fun buyBuilding(building: PassiveBuilding) {
        if (score >= building.cost) {

            score -= building.cost

            building.performUpgrade()
        }
    }

    fun producePassiveCookies(updateFrequencyMs: Long) {
        val totalPassivePerSecond =  CookieData.getPassiveCookiesCount()
        val passivePerTick = totalPassivePerSecond / (1000 / updateFrequencyMs)
        score += passivePerTick
    }

    fun resetGame() {
        score = 0.0
        clickStrength = 1.0
        upgradeStrengthCost = 10.0
        // reset cladiri
        buildings.forEach {
            it.count = 0.0
            it.cost = it.initialCost
        }
    }

    fun getPassiveCookiesCount() : Double {
        return buildings.sumOf { it.totalProduction }
    }

    fun formatNumber(value: Double): String {
        return when {
            value >= 1_000_000_000_000 -> "%.2ft".format(value / 1_000_000_000_000)
            value >= 1_000_000_000 -> "%.2fb".format(value / 1_000_000_000)
            value >= 1_000_000 -> "%.2fm".format(value / 1_000_000)
            value >= 1_000 -> "%.1fk".format(value / 1_000)
            else -> "%.0f".format(value)
        }
    }


}