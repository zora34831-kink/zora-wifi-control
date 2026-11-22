
package com.zora.wificontrol

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

private val Dark = Color(0xFF10141D)
private val Panel = Color(0xFF1B2230)
private val Blue = Color(0xFF4388FF)
private const val ROUTER = "192.168.1.1"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    primary = Blue,
                    background = Dark,
                    surface = Panel
                )
            ) {
                ZoraApp(this)
            }
        }
    }
}

@Composable
fun ZoraApp(context: Context) {
    var page by remember { mutableStateOf("Dashboard") }
    var status by remember { mutableStateOf("Belum diperiksa") }
    var checking by remember { mutableStateOf(false) }
    var response by remember { mutableStateOf("Belum ada hasil tes") }

    val scope = rememberCoroutineScope()

    fun checkRouter() {
        scope.launch {
            checking = true
            status = "Memeriksa router..."

            val result = withContext(Dispatchers.IO) {
                try {
                    val connection = URL("http://$ROUTER/")
                        .openConnection() as HttpURLConnection

                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.requestMethod = "GET"
                    connection.instanceFollowRedirects = false

                    val code = connection.responseCode
                    connection.disconnect()

                    "Router merespons. HTTP $code"
                } catch (e: Exception) {
                    "Gagal terhubung: ${e.javaClass.simpleName}"
                }
            }

            response = result
            status = if (result.startsWith("Router merespons")) {
                "ONLINE"
            } else {
                "Tidak terhubung"
            }

            checking = false
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Dark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text("ZORA", color = Color.White, fontSize = 30.sp)
        Text(
            "WIFI CONTROL / ZTE F460",
            color = Blue,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(25.dp))

        Text(
            page.uppercase(),
            color = Color.White,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(18.dp))

        when (page) {
            "Dashboard" -> {
                CardBox("ROUTER ADDRESS", ROUTER)
                CardBox("ROUTER MODEL", "ZTE F460")
                CardBox("SOFTWARE VERSION", "V6.0.3P1T1")
                CardBox("HARDWARE VERSION", "V6.0")

                CardBox("CONNECTION STATUS", status)
                CardBox("TEST RESULT", response)

                Button(
                    onClick = { checkRouter() },
                    enabled = !checking,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        if (checking) "MEMERIKSA..."
                        else "TEST ROUTER CONNECTION"
                    )
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://$ROUTER/")
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("OPEN ROUTER ADMIN")
                }
            }

            "WiFi Settings" -> {
                CardBox(
                    "WIFI SETTINGS",
                    "Integrasi pengaturan SSID dan password menunggu verifikasi firmware."
                )
            }

            "MAC Filter" -> {
                CardBox(
                    "MAC FILTER",
                    "Integrasi Blacklist dan Whitelist menunggu verifikasi firmware."
                )
            }

            "Security" -> {
                CardBox(
                    "SECURITY",
                    "Pengaturan keamanan router belum diubah oleh aplikasi."
                )
            }

            "Admin" -> {
                CardBox(
                    "APPLICATION",
                    "ZORA WIFI CONTROL"
                )
                CardBox(
                    "ROUTER",
                    "ZTE F460"
                )
            }
        }

        Spacer(Modifier.height(25.dp))

        Text("NAVIGATION", color = Blue, fontSize = 12.sp)

        listOf(
            "Dashboard",
            "WiFi Settings",
            "MAC Filter",
            "Security",
            "Admin"
        ).forEach { item ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .background(
                        if (page == item) Blue.copy(alpha = 0.2f)
                        else Panel,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { page = item }
                    .padding(17.dp)
            ) {
                Text(item, color = Color.White)
            }
        }
    }
}

@Composable
fun CardBox(title: String, value: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Panel, RoundedCornerShape(15.dp))
            .padding(17.dp)
    ) {
        Text(title, color = Blue, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 15.sp)
    }
}
