
package com.zora.wificontrol

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.InetAddress

private val Dark = Color(0xFF10141D)
private val Panel = Color(0xFF1B2230)
private val Blue = Color(0xFF4388FF)

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
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var filterEnabled by remember { mutableStateOf(false) }
    var filterMode by remember { mutableStateOf("Blacklist") }

    val gateway = remember {
        try {
            val wifi = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            val ip = wifi.dhcpInfo.gateway
            if (ip == 0) "Tidak terdeteksi"
            else InetAddress.getByAddress(
                byteArrayOf(
                    ip.toByte(),
                    (ip shr 8).toByte(),
                    (ip shr 16).toByte(),
                    (ip shr 24).toByte()
                )
            ).hostAddress ?: "Tidak terdeteksi"
        } catch (_: Exception) {
            "Tidak terdeteksi"
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Dark)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Text(
            "ZORA",
            color = Color.White,
            fontSize = 28.sp
        )
        Text(
            "WIFI CONTROL / ZTE F460",
            color = Blue,
            fontSize = 12.sp
        )

        Spacer(Modifier.height(24.dp))

        Text(
            page.uppercase(),
            color = Color.White,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(16.dp))

        when (page) {
            "Dashboard" -> {
                PanelCard("ROUTER GATEWAY", gateway)
                PanelCard("ROUTER MODEL", "ZTE F460")
                PanelCard("CONNECTION", "Periksa koneksi WiFi HP")
                PanelCard("MANAGEMENT", "Panel kontrol router")
            }

            "WiFi Settings" -> {
                PanelCard("SSID / NAMA WIFI", "Pengaturan nama jaringan")

                OutlinedTextField(
                    value = ssid,
                    onValueChange = { ssid = it },
                    label = { Text("Nama WiFi baru") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password WiFi baru") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    "Form ini belum mengirim perubahan ke router.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            "MAC Filter" -> {
                PanelCard("DEVICE ACCESS CONTROL", "Pengaturan filter perangkat")

                Row {
                    Text(
                        "Aktifkan MAC Filter",
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = filterEnabled,
                        onCheckedChange = { filterEnabled = it }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Text("Mode filter", color = Color.White)

                listOf("Blacklist", "Whitelist").forEach { mode ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { filterMode = mode }
                            .padding(12.dp)
                    ) {
                        RadioButton(
                            selected = filterMode == mode,
                            onClick = { filterMode = mode }
                        )
                        Text(
                            mode,
                            color = Color.White,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }

                Text(
                    "Pengaturan ini masih berupa tampilan. " +
                    "Belum mengubah konfigurasi router.",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            "Security" -> {
                PanelCard(
                    "ROUTER SECURITY",
                    "Pengaturan keamanan jaringan"
                )
                PanelCard(
                    "MAC FILTER",
                    "Kelola akses perangkat setelah integrasi tersedia"
                )
            }

            "Admin" -> {
                PanelCard(
                    "ADMIN PANEL",
                    "Pengaturan aplikasi ZORA WIFI CONTROL"
                )
                Text(
                    "Jangan simpan password router secara langsung " +
                    "di dalam source code.",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("MENU", color = Blue, fontSize = 12.sp)

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
                    .padding(16.dp)
            ) {
                Text(item, color = Color.White)
            }
        }
    }
}

@Composable
fun PanelCard(title: String, value: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Panel, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Text(title, color = Blue, fontSize = 11.sp)
        Spacer(Modifier.height(8.dp))
        Text(value, color = Color.White, fontSize = 16.sp)
    }
}
