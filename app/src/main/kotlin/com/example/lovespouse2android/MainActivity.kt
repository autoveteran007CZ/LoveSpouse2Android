package com.example.lovespouse2android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoveSpouseApp()
        }
    }
}

@Composable
fun LoveSpouseApp() {
    var selectedMode by remember { mutableStateOf(1) }
    var bpmValue by remember { mutableStateOf(60f) }
    var isBPMMode by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("LoveSpouse2 Android Controller", style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(16.dp))

        // Režim volba
        Text("Mód:", style = MaterialTheme.typography.h6)
        Row(modifier = Modifier.fillMaxWidth()) {
            for (i in 0..9) {
                Button(
                    onClick = { selectedMode = i },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (selectedMode == i) MaterialTheme.colors.primary else MaterialTheme.colors.surface
                    )
                ) {
                    Text("$i")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // BPM Slider (logaritmický)
        Text("BPM: ${calculateBPM(bpmValue).toInt()}", style = MaterialTheme.typography.h6)
        Slider(
            value = bpmValue,
            onValueChange = { bpmValue = it },
            valueRange = 0f..100f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // BPM mód toggle
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("BPM Mód:")
            Switch(checked = isBPMMode, onCheckedChange = { isBPMMode = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Akční tlačítka
        Button(
            onClick = { /* Spustit */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("SPUSTIT")
        }

        Button(
            onClick = { /* Stop */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("STOP")
        }
    }
}

// Logaritmická funkce pro BPM
fun calculateBPM(value: Float): Float {
    return 20f * (2f.pow(value / 50f))
}
