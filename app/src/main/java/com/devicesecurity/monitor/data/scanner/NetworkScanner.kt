package com.devicesecurity.monitor.data.scanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Build
import com.devicesecurity.monitor.domain.model.NetworkInfo

class NetworkScanner(private val context: Context) {

    fun scan(): NetworkInfo {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        val isVpnActive = networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_VPN) == true

        val wifiInfo = wifiManager.connectionInfo
        val ssid = wifiInfo?.ssid?.removeSurrounding("\"") ?: "Not connected"
        val isWifiConnected = wifiInfo != null && wifiInfo.networkId != -1

        var wifiSecurityType = "Unknown"
        var isWifiSecure = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val networkCapabilities2 = connectivityManager.getNetworkCapabilities(activeNetwork)
            isWifiSecure = networkCapabilities2?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
                    && !ssid.contentEquals("<unknown ssid>")
                    && !ssid.contentEquals("Not connected")
            wifiSecurityType = if (isWifiSecure) "WPA2/WPA3" else "Open/Unknown"
        } else {
            @Suppress("DEPRECATION")
            val security = wifiInfo?.let {
                try {
                    val wifiConfig = wifiManager.configuredLists?.find {
                        it.networkId == wifiInfo.networkId
                    }
                    wifiConfig?.allowedKeyManagement?.let { km ->
                        when {
                            km.get(WifiManager.KeyMgmt.WPA_PSK) -> "WPA-PSK"
                            km.get(WifiManager.KeyMgmt.WPA_EAP) -> "WPA-EAP"
                            km.get(WifiManager.KeyMgmt.IEEE8021X) -> "IEEE8021X"
                            km.get(WifiManager.KeyMgmt.NONE) -> "Open"
                            else -> "Unknown"
                        }
                    } ?: "Unknown"
                } catch (_: Exception) {
                    "Unknown"
                }
            } ?: "Unknown"
            wifiSecurityType = security
            isWifiSecure = security != "Open" && security != "Unknown"
        }

        val connectionType = when {
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Cellular"
            networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
            else -> "Unknown"
        }

        val suspiciousConnections = mutableListOf<String>()
        try {
            val proc = Runtime.getRuntime().exec("cat /proc/net/tcp")
            proc.inputStream.bufferedReader().use { reader ->
                val lines = reader.readLines().drop(1)
                if (lines.size > 50) {
                    suspiciousConnections.add("${lines.size} active TCP connections")
                }
            }
        } catch (_: Exception) {
        }

        return NetworkInfo(
            wifiSsid = ssid,
            wifiSecurityType = wifiSecurityType,
            isWifiSecure = isWifiSecure,
            isVpnActive = isVpnActive,
            connectionType = connectionType,
            suspiciousConnections = suspiciousConnections
        )
    }
}