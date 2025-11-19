package com.lestec.eventify.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.components.AskDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: MainViewModel
) {
    val context = LocalContext.current
    BackHandler(onBack = onBack)
    val launcherImport = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        vm.resultImportDB(context, it)
    }
    val launcherExport = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        vm.resultExportDB(context, it)
    }

    AskDialog(
        visible = vm.isAskDialogOpen,
        text = context.getString(R.string.data_replace),
        confirmButtonCLicked = {
            when(vm.askDialogAction) {
                R.string.import_db -> vm.importDB(launcherImport)
            }
            vm.setAskDialog(false, null)
        },
        cancelClicked = { vm.setAskDialog(false, null) }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(context.getString(R.string.settings))
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding),
            contentAlignment = Alignment.TopStart
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                LazyColumn {
                    items(items = vm.settings) {
                        val text = context.getString(it.text)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp)
                                .clickable(role = Role.Button) {
                                    when (it.text) {
                                        R.string.import_db -> vm.setAskDialog(true, R.string.import_db)
                                        R.string.export_db -> vm.exportDB(launcherExport)
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(it.icon, text, Modifier.padding(12.dp))
                            Text(
                                text = text,
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                Spacer(Modifier.height(32.dp).weight(1f))
                Text(
                    text = vm.getAppVersion(context),
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}