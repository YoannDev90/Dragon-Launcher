package org.elnix.dragonlauncher.ui.helpers

import android.app.KeyguardManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import org.elnix.dragonlauncher.common.R
import java.security.MessageDigest

/**
 * Walks up the Context wrapper chain to find the hosting FragmentActivity.
 * Compose's LocalContext.current may be wrapped by ContextThemeWrapper or similar.
 */
fun Context.findFragmentActivity(): FragmentActivity? {
    var ctx: Context? = this
    while (ctx != null) {
        if (ctx is FragmentActivity) return ctx
        ctx = (ctx as? ContextWrapper)?.baseContext
    }
    return null
}

/**
 * Utility object for settings lock security operations.
 */
object SecurityHelper {

    /**
     * Hashes a PIN using SHA-256.
     */
    fun hashPin(pin: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(pin.toByteArray(Charsets.UTF_8))
        return hashBytes.joinToString("") { "%02x".format(it) }
    }

    /**
     * Verifies a PIN against a stored hash.
     */
    fun verifyPin(pin: String, storedHash: String): Boolean {
        return hashPin(pin) == storedHash
    }

    /**
     * Checks if device unlock (biometric or device credentials) is available.
     */
    fun isDeviceUnlockAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)

        // On API 30+ we can safely use BIOMETRIC_STRONG | DEVICE_CREDENTIAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) return true
        }

        // On API 28-29, BIOMETRIC_STRONG | DEVICE_CREDENTIAL is not supported.
        // Use BIOMETRIC_WEAK | DEVICE_CREDENTIAL instead.
        val canAuthWeak = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        if (canAuthWeak == BiometricManager.BIOMETRIC_SUCCESS) return true

        // Final fallback: check if a screen lock (PIN/pattern/password) is set
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return keyguardManager.isDeviceSecure
    }

    /**
     * Shows a device unlock prompt that supports biometric (fingerprint/face)
     * with automatic fallback to device credentials (PIN/pattern/password).
     */
    fun showDeviceUnlockPrompt(
        activity: FragmentActivity,
        onSuccess: () -> Unit,
        onError: (String) -> Unit,
        onFailed: () -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)

        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                if (errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                    errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                    errorCode == BiometricPrompt.ERROR_CANCELED
                ) {
                    onFailed()
                } else {
                    onError(errString.toString())
                }
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed()
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(activity.getString(R.string.biometric_prompt_title))
            .setSubtitle(activity.getString(R.string.biometric_prompt_subtitle))
            .setAllowedAuthenticators(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    BiometricManager.Authenticators.BIOMETRIC_STRONG or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                } else {
                    BiometricManager.Authenticators.BIOMETRIC_WEAK or
                            BiometricManager.Authenticators.DEVICE_CREDENTIAL
                }
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
