package com.og.cookieclicker

import PassiveBuilding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.math.floor

data object CookieData {
    //  Clicker Values
    var score by mutableStateOf(1e9)
    var clickStrength by mutableStateOf(1.0)
    private var lastClickStength = 1.0
    var upgradeStrengthCost by mutableStateOf(10.0)
    fun upgradeStrength () {
        if (upgradeStrengthCost > score)
            return
        score -= upgradeStrengthCost
        upgradeStrengthCost *= 2

        val nextStrength = clickStrength + lastClickStength
        lastClickStength = clickStrength
        clickStrength = nextStrength
    }

    // Experiment
    val experimentCost = 1e10

    // PASSIVE BUILDINGS
    val buildings = listOf(
        PassiveBuilding(
            id = "grandma",
            name = "Grandma",
            description = "Hire a grandma to bake cookies.",
            imageRes = R.drawable.grandma,
            initialCost = 50.0,
            baseProduction = 1.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "bakery",
            name = "Bakery",
            description = "Build a bakery for mass production.",
            imageRes = R.drawable.bakery,
            initialCost = 750.0,
            baseProduction = 10.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "factory",
            name = "Factory",
            description = "Industrial cookie production.",
            imageRes = R.drawable.factory,
            initialCost = 15000.0,
            baseProduction = 350.0,
            costMultiplier = 1.15
        ),
        PassiveBuilding(
            id = "moonBase",
            name = "Moon Base",
            description = "Lunar cookie mining operation.",
            imageRes = R.drawable.moon_base,
            initialCost = 500000.0,
            baseProduction = 15000.0,
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




}