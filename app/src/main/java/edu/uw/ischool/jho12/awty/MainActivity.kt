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
import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        } else {
            Toast.makeText(this, "SMS permission denied", Toast.LENGTH_LONG).show()
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
            LaunchedEffect(key1 = phoneNumber, key2 = interval, key3 = isStarted) {
                while (isStarted) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context as Activity, arrayOf(Manifest.permission.SEND_SMS), 0)
                    } else {
                        SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
                        Log.d("AwtyApp", "SMS sent to $phoneNumber: $message")
                    }

                    val delayTimeMillis = (interval.toLongOrNull() ?: 1) * 60000
                    delay(delayTimeMillis)
                }
            }
        }
    }
}
