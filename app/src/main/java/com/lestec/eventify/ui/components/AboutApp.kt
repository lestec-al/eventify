package com.lestec.eventify.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.MainViewModel

@Composable
fun AboutApp(vm: MainViewModel) {
    val context = LocalContext.current

    Text(
        text = vm.getAppVersion(context),
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        text = stringResource(R.string.privacy_policy),
        modifier = Modifier
            .clickable { vm.openLink(context, R.string.privacy_link) }
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyLarge
    )
    Text(
        text = stringResource(R.string.support),
        modifier = Modifier
            .clickable { vm.openLink(context, R.string.email_link) }
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .fillMaxWidth(),
        textDecoration = TextDecoration.Underline,
        style = MaterialTheme.typography.bodyLarge
    )
    Spacer(Modifier.height(5.dp))
}