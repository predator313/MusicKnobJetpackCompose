package com.aamirashraf.music_knob_jetpackcompose

import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.aamirashraf.music_knob_jetpackcompose.ui.theme.Music_Knob_JetpackComposeTheme
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Music_Knob_JetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xff101010))
                ){
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .border(1.dp, Color.Green, RoundedCornerShape(10.dp))
                            .padding(30.dp)
                    ) {
                        var volume by remember {
                            mutableStateOf(0f)
                        }
                        val barCount=20
                        MusicKnob(modifier = Modifier.size(100.dp)){
                            volume=it
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        volumeBar(modifier = Modifier.fillMaxWidth()
                            .height(30.dp),
                            activeBar = (barCount*volume).roundToInt(),
                            barCount=barCount
                        )

                    }
                }

            }
        }
    }
}

//volume bar composable
@Composable
fun volumeBar(
    modifier: Modifier=Modifier,
    activeBar:Int=0,
    barCount:Int=0
){
    BoxWithConstraints(
        modifier=modifier,
        contentAlignment = Alignment.Center
    ) {
        val barWidth= remember {
            constraints.maxWidth/(2f*barCount)
        }
        Canvas(modifier = modifier){
            for(i in 0 until barCount){
                drawRect(
                    color = if(i in 0..activeBar) Color.Green else Color.DarkGray,
                    topLeft = Offset(i*barWidth*2f+barWidth/2f,0f),
                    size= Size(barWidth,constraints.maxHeight.toFloat())
                )
            }
        }
    }
}

//now make ours composable function
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun MusicKnob(
    modifier: Modifier=Modifier,
    limitingAngle:Float=25f,
    onValueChange:(Float)->Unit
){
    var rotation by remember {
        mutableStateOf(limitingAngle)
    }
    var touchX by remember {
        mutableStateOf(0f)
    }
    var touchY by remember {
        mutableStateOf(0f)
    }
    var centerX by remember {
        mutableStateOf(0f)
    }
    var centerY by remember {
        mutableStateOf(0f)
    }
    
    Image(
        painter = painterResource(id = R.drawable.music_knob),
        contentDescription = "Music knob",
        modifier= modifier
            .fillMaxSize()
            .onGloballyPositioned {
                val windowBound = it.boundsInWindow()
                centerX = windowBound.size.width / 2f
                centerY = windowBound.size.height / 2f
            }
            //now for touch event we got this
            .pointerInteropFilter { event ->
                touchX = event.x
                touchY = event.y
                val angle = -atan2(centerX - touchX, centerY - touchY) * (180f / PI).toFloat()
                when (event.action) {
                    MotionEvent.ACTION_DOWN,
                    MotionEvent.ACTION_MOVE -> {
                        if (angle !in -limitingAngle..limitingAngle) {
                            val fixedAngle = if (angle in -180f..limitingAngle) {
                                360f + angle
                            } else angle
                            rotation = fixedAngle
                            val percent = (fixedAngle - limitingAngle) / (360f - 2 * limitingAngle)
                            onValueChange(percent)
                            true
                        } else false
                    }

                    else -> false
                }
            }
            .rotate(rotation)
    )
}



