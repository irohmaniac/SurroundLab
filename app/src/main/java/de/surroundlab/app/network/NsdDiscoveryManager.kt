package de.surroundlab.app.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.net.wifi.WifiManager
import de.surroundlab.app.domain.model.DiscoveredHost
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.charset.StandardCharsets

/**
 * Handles mDNS / Android NSD registration (TV Host) and discovery (Mobile Controller)
 * under service type `_surroundtest._tcp.` without requiring location permissions.
 */
class NsdDiscoveryManager(context: Context) {

    companion object {
        const val SERVICE_TYPE = "_surroundtest._tcp."
        const val SERVICE_NAME_PREFIX = "SurroundLab"
        const val ATTR_TOKEN = "token"
    }

    private val appContext = context.applicationContext
    private val nsdManager = appContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val wifiManager = appContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    private var multicastLock: WifiManager.MulticastLock? = null
    private var registrationListener: NsdManager.RegistrationListener? = null
    private var discoveryListener: NsdManager.DiscoveryListener? = null

    private val _discoveredHosts = MutableStateFlow<List<DiscoveredHost>>(emptyList())
    val discoveredHosts: StateFlow<List<DiscoveredHost>> = _discoveredHosts.asStateFlow()

    fun registerHostService(port: Int, token: String, serviceName: String = SERVICE_NAME_PREFIX) {
        unregisterHostService()
        acquireMulticastLock()

        val serviceInfo = NsdServiceInfo().apply {
            this.serviceName = serviceName
            this.serviceType = SERVICE_TYPE
            this.port = port
            setAttribute(ATTR_TOKEN, token)
        }

        val listener = object : NsdManager.RegistrationListener {
            override fun onServiceRegistered(info: NsdServiceInfo) = Unit
            override fun onRegistrationFailed(info: NsdServiceInfo, errorCode: Int) = Unit
            override fun onServiceUnregistered(info: NsdServiceInfo) = Unit
            override fun onUnregistrationFailed(info: NsdServiceInfo, errorCode: Int) = Unit
        }
        registrationListener = listener
        runCatching {
            nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, listener)
        }
    }

    fun unregisterHostService() {
        registrationListener?.let { listener ->
            runCatching { nsdManager.unregisterService(listener) }
        }
        registrationListener = null
        releaseMulticastLock()
    }

    fun startDiscovery() {
        stopDiscovery()
        acquireMulticastLock()
        _discoveredHosts.value = emptyList()

        val listener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) = Unit

            override fun onServiceFound(serviceInfo: NsdServiceInfo) {
                if (serviceInfo.serviceType.contains("_surroundtest")) {
                    resolveServiceInfo(serviceInfo)
                }
            }

            override fun onServiceLost(serviceInfo: NsdServiceInfo) {
                _discoveredHosts.value = _discoveredHosts.value.filterNot {
                    it.serviceName == serviceInfo.serviceName
                }
            }

            override fun onDiscoveryStopped(serviceType: String) = Unit
            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                runCatching { nsdManager.stopServiceDiscovery(this) }
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                runCatching { nsdManager.stopServiceDiscovery(this) }
            }
        }

        discoveryListener = listener
        runCatching {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, listener)
        }
    }

    fun stopDiscovery() {
        discoveryListener?.let { listener ->
            runCatching { nsdManager.stopServiceDiscovery(listener) }
        }
        discoveryListener = null
        releaseMulticastLock()
    }

    @Suppress("DEPRECATION")
    private fun resolveServiceInfo(serviceInfo: NsdServiceInfo) {
        runCatching {
            nsdManager.resolveService(serviceInfo, object : NsdManager.ResolveListener {
                override fun onResolveFailed(info: NsdServiceInfo, errorCode: Int) = Unit

                override fun onServiceResolved(resolved: NsdServiceInfo) {
                    val hostIp = resolved.host?.hostAddress ?: return
                    if (!NetworkUtils.isPrivateLanHost(hostIp)) return
                    val tokenBytes = resolved.attributes[ATTR_TOKEN]
                    val token = if (tokenBytes != null) {
                        String(tokenBytes, StandardCharsets.UTF_8)
                    } else ""
                    val host = DiscoveredHost(
                        serviceName = resolved.serviceName ?: "SurroundLab TV",
                        hostIp = hostIp,
                        port = resolved.port,
                        token = token
                    )
                    val current = _discoveredHosts.value.filterNot { it.hostIp == host.hostIp && it.port == host.port }
                    _discoveredHosts.value = current + host
                }
            })
        }
    }

    private fun acquireMulticastLock() {
        if (multicastLock == null) {
            multicastLock = wifiManager.createMulticastLock("SurroundLab_mDNS").apply {
                setReferenceCounted(false)
            }
        }
        if (multicastLock?.isHeld == false) {
            runCatching { multicastLock?.acquire() }
        }
    }

    private fun releaseMulticastLock() {
        if (multicastLock?.isHeld == true) {
            runCatching { multicastLock?.release() }
        }
    }
}
