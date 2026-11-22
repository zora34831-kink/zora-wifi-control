
package com.zora.wificontrol

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.Inet4Address

private val Bg = Color(0xFF080812)
private val Panel = Color(0xFF171629)
private val Purple = Color(0xFF8B5CF6)
private val Muted = Color(0xFFAAA7C7)
private val Green = Color(0xFF35D49A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { ZoraApp() }
    }
}

@Composable
fun ZoraApp() {
    val context = LocalContext.current

    var gateway by remember {
        mutableStateOf(readGateway(context))
    }

    var wifiConnected by remember {
        mutableStateOf(isWifiConnected(context))
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Purple,
            background = Bg,
            surface = Panel,
            onSurface = Color.White
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Bg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(Modifier.weight(1f)) {
                        Text(
                            "ZORA",
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            "WIFI CONTROL / ZTE F460",
                            color = Muted,
                            fontSize = 11.sp,
                            letterSpacing = 1.5.sp
                        )
                    }

                    Icon(
                        Icons.Default.Wifi,
                        contentDescription = null,
                        tint = Purple,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // ROUTER STATUS
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Panel
                    )
                ) {
                    Column(
                        Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(13.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Router,
                                contentDescription = null,
                                tint = Purple,
                                modifier = Modifier.size(35.dp)
                            )

                            Spacer(Modifier.width(12.dp))

                            Column(Modifier.weight(1f)) {
                                Text(
                                    "ZTE F460",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    if (wifiConnected)
                                        "Terhubung ke WiFi"
                                    else
                                        "WiFi tidak terdeteksi",
                                    color = Muted,
                                    fontSize = 12.sp
                                )
                            }

                            Text(
                                if (wifiConnected) "ONLINE" else "OFFLINE",
                                color = if (wifiConnected) Green else Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(
                            color = Color(0xFF302C48)
                        )

                        Text(
                            "GATEWAY ROUTER",
                            color = Muted,
                            fontSize = 11.sp
                        )

                        Text(
                            gateway ?: "Tidak ditemukan",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Button(
                            onClick = {
                                openRouter(context, gateway)
                            },
                            enabled = gateway != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Purple
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.OpenInBrowser,
                                contentDescription = null
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("BUKA ADMIN ROUTER")
                        }
                    }
                }

                Text(
                    "NETWORK MANAGEMENT",
                    color = Muted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                FeatureCard(
                    Icons.Default.Devices,
                    "Connected Devices",
                    "Buka menu perangkat terhubung pada router untuk melihat daftar perangkat."
                )

                FeatureCard(
                    Icons.Default.Block,
                    "Block / Unblock Device",
                    "Kelola daftar perangkat melalui fitur MAC Filter jika tersedia pada firmware."
                )

                FeatureCard(
                    Icons.Default.Lock,
                    "WiFi Password",
                    "Buka pengaturan WLAN untuk mengganti password WiFi."
                )

                FeatureCard(
                    Icons.Default.Settings,
                    "SSID Settings",
                    "Atur nama jaringan WiFi melalui konfigurasi resmi router."
                )

                OutlinedButton(
                    onClick = {
                        gateway = readGateway(context)
                        wifiConnected = isWifiConnected(context)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(8.dp))
                    Text("REFRESH STATUS")
                }

                Text(
                    "Pengaturan router memerlukan akun administrator " +
                    "dan dukungan firmware ZTE F460. Perubahan dilakukan " +
                    "melalui halaman resmi router.",
                    color = Muted,
                    fontSize = 11.sp,
                    lineHeight = 17.sp
                )

                Text(
                    "ZORA WIFI CONTROL • VERSION 1.0",
                    color = Muted,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Panel
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(45.dp)
                    .background(
                        Brush.linearGradient(
                            listOf(
                                Color(0xFF30205B),
                                Color(0xFF1B1A35)
                            )
                        ),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Purple
                )
            }

            Spacer(Modifier.width(13.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Text(
                    description,
                    color = Muted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

fun isWifiConnected(context: Context): Boolean {
    val manager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    val network = manager.activeNetwork ?: return false
    val capabilities =
        manager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasTransport(
        NetworkCapabilities.TRANSPORT_WIFI
    )
}

fun readGateway(context: Context): String? {
    val manager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as ConnectivityManager

    val network = manager.activeNetwork ?: return null
    val properties = manager.getLinkProperties(network)
        ?: return null

    val route = properties.routes.firstOrNull {
        it.isDefaultRoute && it.gateway is Inet4Address
    }

    return route?.gateway?.hostAddress
}

fun openRouter(context: Context, address: String?) {
    if (address == null) return

    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("http://$address")
    )

    runCatching {
        context.startActivity(intent)
    }
}
