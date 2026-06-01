package com.lestec.eventify.data

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher as Launcher
import androidx.activity.result.ActivityResult
import com.lestec.eventify.R
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.nio.charset.StandardCharsets
import java.util.Objects

class StorageRepo(val context: Context) {
    fun importDb(launcher: Launcher<Intent, ActivityResult>) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/json"
        launcher.launch(intent)
    }

    fun exportDb(launcher: Launcher<Intent, ActivityResult>) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "application/json"
        intent.putExtra(Intent.EXTRA_TITLE, "dataEventify.json")
        launcher.launch(intent)
    }

    fun resultImportDb(result: ActivityResult, importDB: (String) -> Boolean) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val `is`: InputStream? = context.contentResolver.openInputStream(
                    Objects.requireNonNull<Uri?>(result.data!!.data)
                )
                val `in` = BufferedReader(InputStreamReader(`is`, StandardCharsets.UTF_8))
                var inputLine: String?
                val response = StringBuilder()
                while ((`in`.readLine().also { inputLine = it }) != null) {
                    response.append(inputLine)
                }
                `in`.close()
                `is`?.close()
                if (!importDB(response.toString())) {
                    throw java.lang.Exception("Import error")
                }
                showToast(R.string.ok)
            } catch (_: java.lang.Exception) {
                showToast(R.string.error)
            }
        }
    }

    fun resultExportDb(result: ActivityResult, exportDb: () -> String) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            try {
                val os: OutputStream? = context.contentResolver.openOutputStream(Objects.requireNonNull<Uri>(result.data!!.data))
                val input: ByteArray = exportDb().toByteArray(StandardCharsets.UTF_8)
                checkNotNull(os)
                os.write(input, 0, input.size)
                os.close()
                showToast(R.string.ok)
            } catch (_: Exception) {
                showToast(R.string.error)
            }
        }
    }

    private fun showToast(resId: Int) {
        (context as Activity).runOnUiThread {
            Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
        }
    }
}