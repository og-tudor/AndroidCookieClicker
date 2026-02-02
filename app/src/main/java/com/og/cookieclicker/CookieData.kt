package com.og.cookieclicker

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import kotlin.math.floor

data object CookieData {
    var score by mutableStateOf(0.0)
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


    // GRANDMA
    var upgradeGrandmaCost by mutableStateOf(100.0)
    var numberOfGrandmas by mutableStateOf(0.0)
    private val cookiesPerGrandma = 1.0
    val cookiesByGrandmas
        get() = numberOfGrandmas * cookiesPerGrandma
    fun upgradeGrandmas () {
        if (upgradeGrandmaCost > score)
            return
        score -= upgradeGrandmaCost
        if (numberOfGrandmas == 0.0) numberOfGrandmas = 1.0 else numberOfGrandmas *= 2
        upgradeGrandmaCost *= 2.0
    }

    // Bakery
    var upgradeBakeryCost by mutableStateOf(1000.0)
    var numberOfBakeries by mutableStateOf(0.0)
    private val cookiesPerBakery = 30.0
    val cookiesByBakery
        get() = numberOfBakeries * cookiesPerBakery
    fun upgrabeBakery () {
        if (upgradeBakeryCost > score)
            return
        score -= upgradeBakeryCost
        if (numberOfBakeries == 0.0) numberOfBakeries = 1.0 else numberOfBakeries *= 2
        upgradeBakeryCost *= 2.1
    }




    fun producePassiveCookies(updateFrequencyMs: Long) {
        val totalPassivePerSecond =  CookieData.getPassiveCookiesCount()
        val passivePerTick = totalPassivePerSecond / (1000 / updateFrequencyMs)
        score += passivePerTick
    }

    fun getPassiveCookiesCount() : Double {
        return cookiesByGrandmas + cookiesByBakery
    }






}