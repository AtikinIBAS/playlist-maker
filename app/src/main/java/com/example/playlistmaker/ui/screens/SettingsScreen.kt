package com.example.playlistmaker.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

@Composable
fun SettingsScreen(
    innerPadding: PaddingValues,
    developerEmail: String,
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
            .padding(horizontal = 20.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            onBackClick = onBackClick
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AppMenuRow(
                text = stringResource(R.string.share_app_title),
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

            AppMenuRow(
                text = stringResource(R.string.contact_developers_title),
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

            AppMenuRow(
                text = stringResource(R.string.user_agreement_title),
                onClick = {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW, Uri.parse(agreementUrl))
                    )
                }
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsScreenPreview() {
    PlaylistMakerTheme {
        SettingsScreen(
            innerPadding = PaddingValues(),
            developerEmail = "pochta_for_yandex@yandex.ru",
            onBackClick = {}
        )
    }
}
