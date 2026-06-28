// File: com/example/nutrifit/data/helper/GitHubAuthHelper.kt
package com.example.nutrifit.data.helper

import android.content.Context
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent

class GitHubAuthHelper(
    private val context: Context,
    private val clientId: String,
    private val redirectUri: String = "nutrifit://callback"
) {
    private val authorizeUrl = "https://github.com/login/oauth/authorize"
    private val scope = "user:email"

    fun launchGitHubAuth() {
        val authUrl = buildAuthUrl()
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(authUrl))
    }

    private fun buildAuthUrl(): String {
        return Uri.parse(authorizeUrl).buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("redirect_uri", redirectUri)
            .appendQueryParameter("scope", scope)
            .appendQueryParameter("state", generateState())
            .build()
            .toString()
    }

    private fun generateState(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun handleCallback(uri: Uri): String? {
        return uri.getQueryParameter("code")
    }
}
