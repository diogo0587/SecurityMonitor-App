package com.devicesecurity.monitor.data.scanner

import android.app.KeyguardManager
import android.content.Context
import android.hardware.biometrics.BiometricManager
import com.devicesecurity.monitor.domain.model.LockscreenInfo
import android.os.Build

class LockscreenScanner(private val context: Context) {

    fun scan(): LockscreenInfo {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

        val isSecure = keyguardManager.isDeviceSecure

        val lockType = when {
            !isSecure -> "None"
            hasBiometric() -> "Biometric"
            hasPinOrPassword(keyguardManager) -> "PIN/Password"
            hasPattern(keyguardManager) -> "Pattern"
            else -> "Swipe"
        }

        return LockscreenInfo(
            isSecure = isSecure,
            lockType = lockType,
            hasBiometric = hasBiometric()
        )
    }

    private fun hasBiometric(): Boolean {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val biometricManager = context.getSystemService(BiometricManager::class.java)
                    ?: return false
                biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                        BiometricManager.BIOMETRIC_SUCCESS
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    @Suppress("DEPRECATION")
    private fun hasPinOrPassword(keyguardManager: KeyguardManager): Boolean {
        return keyguardManager.isDeviceSecure
    }

    @Suppress("DEPRECATION")
    private fun hasPattern(keyguardManager: KeyguardManager): Boolean {
        return keyguardManager.isDeviceSecure
    }
}