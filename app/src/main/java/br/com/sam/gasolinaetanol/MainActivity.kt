package br.com.sam.gasolinaetanol

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import br.com.sam.gasolinaetanol.ui.theme.GasolinaOuEtanolTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GasolinaOuEtanolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FuelCalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun FuelCalculatorScreen() {
    var gasText by remember { mutableStateOf("") }
    var ethText by remember { mutableStateOf("") }
    var threshold by remember { mutableFloatStateOf(0.70f) }

    val gas = gasText.replace(",", ".").toDoubleOrNull()
    val eth = ethText.replace(",", ".").toDoubleOrNull()

    val result = if (gas != null && eth != null && gas > 0) {
        val ratio = eth / gas
        val compensaEtanol = ratio <= threshold.toDouble()
        val advantage = kotlin.math.abs((ratio - threshold.toDouble()) / threshold.toDouble()) * 100

        Triple(compensaEtanol, ratio, advantage)
    } else null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Gasolina x Etanol",
            style = MaterialTheme.typography.headlineSmall
        )

        OutlinedTextField(
            value = gasText,
            onValueChange = { gasText = it },
            label = { Text("Preço da gasolina (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = ethText,
            onValueChange = { ethText = it },
            label = { Text("Preço do etanol (R$)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        Text(text = "Etanol compensa até: ${(threshold * 100).toInt()}%")
        Slider(
            value = threshold,
            onValueChange = { threshold = it },
            valueRange = 0.60f..0.80f
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (result == null) {
                    Text(text = "Informe os preços para calcular.")
                } else {
                    val (etanol, ratio, advantage) = result
                    Text(
                        text = if (etanol) "Compensa ETANOL" else "Compensa GASOLINA",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(text = "Razão etanol/gasolina: ${"%.2f".format(ratio)}")
                    Text(text = "Diferença vs regra: ${"%.1f".format(advantage)}%")
                }
            }
        }

        OutlinedButton(
            onClick = {
                gasText = ""
                ethText = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Limpar")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FuelCalculatorPreview() {
    GasolinaOuEtanolTheme {
        FuelCalculatorScreen()
    }
}
