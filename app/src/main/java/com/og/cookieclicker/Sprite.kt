package com.og.cookieclicker

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.delay

/**
 * Draws a selected image trough a Canvas.
 * @param frameIndex is  indexed from 0, left to right, top to bottom.
 */
@Composable
fun SingleSprite(
    imageRes: Int,
    frameIndex: Int,
    nrRows: Int,
    nrColumns: Int,
    modifier: Modifier = Modifier
) {
    val imageBitmap = ImageBitmap.imageResource(imageRes)

    Canvas(modifier = modifier) {
        val frameWidth = imageBitmap.width / nrColumns
        val frameHeight = imageBitmap.height / nrRows

        val srcX = (frameIndex % nrColumns) * frameWidth
        val srcY = (frameIndex / nrColumns) * frameHeight

        drawImage(
            image = imageBitmap,
            srcOffset = IntOffset(x = srcX, y = srcY),
            srcSize = IntSize(frameWidth, frameHeight),
            dstSize = IntSize(size.width.toInt(), size.height.toInt())
        )
    }
}


@Composable
fun AnimatedSprite(
    imageRes: Int,
    nrRows: Int = 1,
    nrColumns: Int = 1,

    idleFrame: Int = 0,
    startFrame: Int = 0,
    endFrame: Int = nrRows * nrColumns - 1,
    frameDurationMs: Long = 100L,

    loop: Boolean = false,
    trigger: Long = 0L,

    modifier: Modifier = Modifier
) {
    var currentFrame by remember { mutableIntStateOf(idleFrame) }

    // Logica de animatie
    LaunchedEffect(key1 = loop, key2 = trigger, key3 = imageRes) {
        if (loop) {
            // --- CAZUL 1: LOOP INFINIT ---
            currentFrame = startFrame
            while (true) {
                delay(frameDurationMs)
                if (currentFrame < endFrame) {
                    currentFrame++
                } else {
                    currentFrame = startFrame
                }
            }
        } else {
            // --- CAZUL 2: ONE-SHOT (LA COMANDĂ) ---
            if (trigger > 0) {
                for (frame in startFrame..endFrame) {
                    currentFrame = frame
                    delay(frameDurationMs)
                }
                currentFrame = idleFrame
            } else {
                currentFrame = idleFrame
            }
        }
    }

    // Desenarea efectivă
    SingleSprite(
        imageRes = imageRes,
        frameIndex = currentFrame,
        nrRows = nrRows,
        nrColumns = nrColumns,
        modifier = modifier
    )
}