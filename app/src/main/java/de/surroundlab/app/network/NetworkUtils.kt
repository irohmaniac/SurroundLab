package de.surroundlab.app.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import java.net.Inet4Address
import java.net.NetworkInterface
import java.security.SecureRandom

object NetworkUtils {

    private const val TOKEN_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
    private val secureRandom = SecureRandom()

    fun generateSessionToken(length: Int = 6): String {
        val sb = StringBuilder(length)
        repeat(length) {
            sb.append(TOKEN_ALPHABET[secureRandom.nextInt(TOKEN_ALPHABET.length)])
        }
        return sb.toString()
    }

    /**
     * Restricts cleartext HTTP/WebSocket traffic strictly to private RFC-1918 LAN addresses,
     * loopback, and .local mDNS hostnames (AGENTS.md 4.1).
     */
    fun isPrivateLanHost(host: String): Boolean {
        val trimmed = host.trim().lowercase()
        if (trimmed == "localhost" || trimmed == "127.0.0.1" || trimmed.endsWith(".local")) {
            return true
        }
        val parts = trimmed.split('.')
        if (parts.size != 4) return false
        val octets = parts.map { it.toIntOrNull() ?: return false }
        if (octets.any { it !in 0..255 }) return false
        val a = octets[0]
        val b = octets[1]
        return when {
            a == 10 -> true // 10.0.0.0/8
            a == 172 && b in 16..31 -> true // 172.16.0.0/12
            a == 192 && b == 168 -> true // 192.168.0.0/16
            a == 169 && b == 254 -> true // Link-local fallback
            else -> false
        }
    }

    /**
     * Resolves the device's active local IPv4 address (preferring Wi-Fi / Ethernet over cellular).
     */
    fun getLocalIpv4Address(context: Context): String? {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNet = cm.activeNetwork
        if (activeNet != null) {
            val caps = cm.getNetworkCapabilities(activeNet)
            val isLan = caps != null && (
                caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
                )
            if (isLan) {
                val linkProps = cm.getLinkProperties(activeNet)
                val v4 = linkProps?.linkAddresses
                    ?.map { it.address }
                    ?.firstOrNull { it is Inet4Address && !it.isLoopbackAddress && !it.isLinkLocalAddress }
                if (v4 != null) return v4.hostAddress
            }
        }

        // Fallback scan via NetworkInterface
        return runCatching {
            val interfaces = NetworkInterface.getNetworkInterfaces() ?: return@runCatching null
            for (ni in interfaces) {
                if (!ni.isUp || ni.isLoopback || ni.isVirtual) continue
                for (addr in ni.inetAddresses) {
                    if (addr is Inet4Address && !addr.isLoopbackAddress && !addr.isLinkLocalAddress) {
                        val ip = addr.hostAddress
                        if (ip != null && isPrivateLanHost(ip)) {
                            return@runCatching ip
                        }
                    }
                }
            }
            null
        }.getOrNull()
    }
}
