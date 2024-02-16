package edu.uw.ischool.jho12.awty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.uw.ischool.jho12.awty.ui.theme.AwtyTheme
import kotlinx.coroutines.delay
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AwtyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AwtyApp()
                }
            }
        }
    }
}

@Composable
fun AwtyApp() {
    var message by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var interval by remember { mutableStateOf("") }
    var isStarted by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val isIntervalValid by derivedStateOf {
        interval.toIntOrNull()?.let {
            it > 0
        } ?: false
    }

    val isPhoneNumberValid by derivedStateOf {
        phoneNumber.length == 10 && phoneNumber.all { it.isDigit() }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(
            value = message,
            onValueChange = { message = it },
            label = { Text("Message") },
            modifier = Modifier.padding(PaddingValues(bottom = 8.dp))
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {
                if (it.all { char -> char.isDigit() } && it.length <= 10) {
                    phoneNumber = it
                }
            },
            label = { Text("Phone Number") },
            modifier = Modifier.padding(PaddingValues(bottom = 8.dp))
        )
        OutlinedTextField(
            value = interval,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    interval = it
                }
            },
            label = { Text("Interval (Minutes)") },
            modifier = Modifier.padding(PaddingValues(bottom = 8.dp))
        )
        Button(
            onClick = {
                when {
                    !isPhoneNumberValid -> Toast.makeText(context, "Invalid phone number. Ensure it's 10 digits.", Toast.LENGTH_LONG).show()
                    !isIntervalValid -> Toast.makeText(context, "Invalid interval. Ensure it's a positive integer.", Toast.LENGTH_LONG).show()
                    else -> isStarted = !isStarted
                }
            },
            modifier = Modifier.padding(PaddingValues(top = 8.dp)),
            enabled = !isStarted || (isStarted && isIntervalValid && isPhoneNumberValid)
        ) {
            Text(if (isStarted) "Stop" else "Start")
        }

        if (isStarted && isIntervalValid && isPhoneNumberValid) {
            LaunchedEffect(key1 = phoneNumber, key2 = interval) {
                while (isStarted) {
                    Toast.makeText(context, "Texting $phoneNumber", Toast.LENGTH_SHORT).show()
                    delay(1000)
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                    println("$phoneNumber: $message")
                    delay((interval.toLongOrNull() ?: 1) * 60000)
                }
            }
        }
    }
}
