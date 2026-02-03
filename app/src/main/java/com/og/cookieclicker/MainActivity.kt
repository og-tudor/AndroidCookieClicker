package com.og.cookieclicker

import android.os.Bundle
import android.view.SoundEffectConstants
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.og.cookieclicker.ui.theme.CookieClickerTheme
import androidx.navigation.compose.composable
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

val waitTimeBeforeEnd = 1000L

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SoundPlayer.init(this)
        enableEdgeToEdge()
        setContent {
            CookieClickerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "menu_screen",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Ruta 1
                        composable("menu_screen") {
                            HomeScreen(
                                onPlayClicked = {
                                    navController.navigate("game_screen")
                                }
                            )
                        }

                        // Ruta 2
                        composable("game_screen") {
                            val scope = rememberCoroutineScope()
                            CookieApp(onEndReached = {
                                scope.launch {
                                    delay(waitTimeBeforeEnd)
                                    navController.navigate("end_screen")
                                }
                            })
                        }

                        // Ruta 3
                        composable("end_screen") {
                            EndScreen(onResetClicked = {
                                CookieData.resetGame()
                                navController.navigate("menu_screen") {
                                    // curata stiva sa nu te poti intoarce cu "back button"
                                    popUpTo("menu_screen") { inclusive = true }
                                }
                            })
                        }
                    }

                }
            }
        }
    }
}


@Composable
fun CookieApp(onEndReached: () -> Unit, modifier: Modifier = Modifier) {
    val updateFrequencyMs = 50L
    val view = LocalView.current
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        SoundPlayer.startGameLoop(context)
        while (true) {
            delay(updateFrequencyMs)
            CookieData.producePassiveCookies(updateFrequencyMs)

            // Calcule Volum
            val targetScore = CookieData.experimentCost
            val currentScore = CookieData.score
            val threshold = targetScore / 2

            val volume = if (currentScore < threshold) {
                0f
            } else {
                // Procentul intre threshold si targetScore
                ((currentScore - threshold) / (targetScore - threshold)).toFloat()
            }

            // aplica volumul
            SoundPlayer.updateGameLoopVolume(volume)
        }
    }

    // oprire loop cand se paraseste ecranul
    DisposableEffect(Unit) {
        onDispose {
            SoundPlayer.stopGameLoop()
        }
    }

    LazyColumn (
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.coockie),
                contentDescription = "Cookie",
                modifier = Modifier.size(300.dp)
                    .clickable {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                        CookieData.score += CookieData.totalClickStrength
                    }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        item {
            Text(
                text = "Cookies: ${CookieData.formatNumber(CookieData.score)}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

        }
        item {
            Text(
                text = "Passive Income: ${CookieData.formatNumber(CookieData.getPassiveCookiesCount())}/s",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // --- CLICKER UPGRADE ---
        item {
            Upgrade(
                Modifier, R.drawable.bite,
                imageDescription = "Bite",
                title = "Increase bite",
                description = "Increases click power.",
                upgradeCost = CookieData.upgradeStrengthCost,
                currentValue = CookieData.formatNumber(CookieData.totalClickStrength) + " Click",
                canAfford = CookieData.score >= CookieData.upgradeStrengthCost,
                upgradeFunction = CookieData::upgradeStrength
            )
        }
        // --- UPGRADE PASSIVE ---
        items(CookieData.buildings) { building ->
            Upgrade(
                modifier = Modifier,
                imageId = building.imageRes,
                imageDescription = building.name,
                title = building.name,
                description = building.description,
                upgradeCost = building.cost,
                currentValue = "${CookieData.formatNumber(building.totalProduction)} /s",
                canAfford = CookieData.score >= building.cost,
                upgradeFunction = { CookieData.buyBuilding(building) }
            )
        }

        // --- END GAME ---
        item {
            Upgrade(
                Modifier,R.drawable.experiment,
                imageDescription = "Experiment",
                title = "Breakthrough Experiment?",
                description = "Scientists are close to a breakthrough in Cookie Technology.",
                upgradeCost = CookieData.experimentCost,
                currentValue = "???",
                canAfford = CookieData.score >= CookieData.experimentCost,
                upgradeFunction = {
                    if (CookieData.score >= CookieData.experimentCost) {
                        onEndReached()
                    }
                }
            )
        }
    }
}

@Composable
fun EndScreen(onResetClicked: () -> Unit) {
    val context = LocalContext.current
    // play doar o data cand se intra in ecran
    LaunchedEffect(Unit) {
        SoundPlayer.playEndMusic(context)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.abstract_end),
            contentDescription = "The End",
            modifier = Modifier
                .size(400.dp)
                .padding(bottom = 24.dp)
        )

        Text(
            text = "Everything became shapes and cookies...",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            SoundPlayer.stopEndMusic()
            onResetClicked()
        })  {
            Text(text = "RESET GAME")
        }
    }
}


@Composable
fun HomeScreen(modifier: Modifier = Modifier, onPlayClicked: () -> Unit) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

    ) {
        Text(text = "Welcome to Cookie Clicker!", fontSize = 24.sp)


        Button(onClick = { onPlayClicked() }) {
            Text(text = "PLAY")
        }
    }
}

@Composable
fun Upgrade(
    modifier: Modifier = Modifier, imageId: Int, imageDescription: String,
    title: String, description: String, upgradeCost: Double, currentValue: String,
    canAfford: Boolean,
    upgradeFunction: () -> Unit

) {
    val alpha = if (canAfford) 1f else 0.5f
    val context = LocalContext.current
    Row(
        modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp)
                    .clickable {
                        if (canAfford)
                            SoundPlayer.playBuySound(context)

                        upgradeFunction()
                    }
                    .graphicsLayer(alpha = alpha)
    ) {
        Image(
            painter = painterResource(imageId),
            contentDescription = "",
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = buildAnnotatedString {
                    append("Current: ")
                    withStyle(style = SpanStyle(
                        color = Color.Blue,
                        fontWeight = FontWeight.ExtraBold
                    )
                    ) {
                        append(currentValue)
                    }
                },
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
            Text(
                text = description,
                fontSize = 15.sp
            )

        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.padding(end = 8.dp), horizontalAlignment = Alignment.End) {
            Text(
                text = "Cost",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
            Text(
                text = CookieData.formatNumber(upgradeCost),
                fontWeight = FontWeight.Bold,
                color = Color.hsl(49f , 1F,  0.29f),
                fontSize = 15.sp
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun UpgradePreview() {
    CookieClickerTheme {
        Upgrade(
            Modifier, R.drawable.bite,
            imageDescription = "Bite",
            title = "Increase bite",
            description = "Increases click power",
            upgradeCost = 16.0,
            canAfford = false,
            currentValue = CookieData.clickStrength.toString()
        ) { }
    }
}