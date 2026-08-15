package com.lestec.eventify.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult as rememberLauncher
import androidx.activity.result.contract.ActivityResultContracts as Contracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.lestec.eventify.R
import com.lestec.eventify.ui.components.AboutApp
import com.lestec.eventify.ui.components.AskDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    vm: MainViewModel
) {
    BackHandler(onBack = onBack)
    val launcherImport = rememberLauncher(Contracts.StartActivityForResult()) {
        vm.resultImportDb(it)
    }
    val launcherExport = rememberLauncher(Contracts.StartActivityForResult()) {
        vm.resultExportDb(it)
    }

    AskDialog(
        visible = vm.isAskDialogOpen,
        text = stringResource(R.string.data_replace),
        confirmButtonCLicked = { vm.askDialogConfirm(launcherImport) },
        cancelClicked = { vm.setAskDialog(false, null) }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (vm.isLoading) {
                        CircularProgressIndicator()
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ElevatedCard(modifier = Modifier.padding(horizontal = 10.dp)) {
                    listOf(
                        Triple(R.string.import_db, R.drawable.ic_file_download) {
                            vm.setAskDialog(true, R.string.import_db)
                        },
                        Triple(R.string.export_db, R.drawable.ic_file_upload) {
                            vm.exportDb(launcherExport)
                        }
                    ).forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp)
                                .clickable(
                                    enabled = !vm.isLoading,
                                    role = Role.Button,
                                    onClick = { it.third() }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(it.second),
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )
                            Text(
                                text = stringResource(it.first),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
            }
            item { AboutApp(vm) }
            item { Spacer(Modifier.height(10.dp)) }
        }
    }
}