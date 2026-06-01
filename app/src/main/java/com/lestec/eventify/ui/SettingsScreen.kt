package com.lestec.eventify.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
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
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current
    BackHandler(onBack = onBack)
    val launcherImport = rememberLauncherForActivityResult(Contracts.StartActivityForResult()) {
        vm.resultImportDb(it)
    }
    val launcherExport = rememberLauncherForActivityResult(Contracts.StartActivityForResult()) {
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(items = listOf(vm.settings, vm.aboutSettings)) { items ->
                val showIndicator = if (items == vm.aboutSettings) vm.showAbout else null

                ElevatedCard(modifier = Modifier.padding(horizontal = 10.dp)) {
                    items.forEach {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 50.dp)
                                .clickable(
                                    enabled = !vm.isLoading,
                                    role = Role.Button,
                                    onClick = { it.action(context, launcherExport) }
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = null,
                                modifier = Modifier.padding(12.dp)
                            )
                            Text(
                                text = stringResource(it.text),
                                style = MaterialTheme.typography.titleMedium
                            )
                            if (showIndicator != null) {
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = if (showIndicator) {
                                        Icons.Default.ArrowDropUp
                                    } else {
                                        Icons.Default.ArrowDropDown
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                            }
                        }
                    }
                    if (showIndicator != null && showIndicator == true) {
                        AboutApp(vm)
                    }
                }
            }
            item { Spacer(Modifier.height(10.dp)) }
        }
    }
}