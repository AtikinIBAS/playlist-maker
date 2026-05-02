package com.example.playlistmaker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.example.playlistmaker.R

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    developerEmail: String,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val shareMessage = stringResource(R.string.share_app_message)
    val emailSubject = stringResource(R.string.developer_email_subject)
    val emailBody = stringResource(R.string.developer_email_body)
    val agreementUrl = stringResource(R.string.user_agreement_url)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            onBackClick = onBackClick
        )

        ThemeToggleRow(
            text = stringResource(R.string.dark_theme_title),
            checked = isDarkTheme,
            onCheckedChange = onThemeToggle
        )

        SettingsIconActionRow(
            text = stringResource(R.string.share_app_title),
            icon = Icons.Outlined.Share,
            onClick = {
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, shareMessage)
                }
                context.startActivity(
                    Intent.createChooser(
                        shareIntent,
                        context.getString(R.string.share_chooser_title)
                    )
                )
            }
        )

        SettingsIconActionRow(
            text = stringResource(R.string.contact_developers_title),
            icon = Icons.Outlined.SupportAgent,
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = "mailto:".toUri()
                    putExtra(Intent.EXTRA_EMAIL, arrayOf(developerEmail))
                    putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                    putExtra(Intent.EXTRA_TEXT, emailBody)
                }
                context.startActivity(emailIntent)
            }
        )

        SettingsActionRow(
            text = stringResource(R.string.user_agreement_title),
            onClick = {
                context.startActivity(
                    Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
                )
            }
        )
    }
}
