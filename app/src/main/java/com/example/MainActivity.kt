package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.VaultRepository
import com.example.ui.VaultDashboard
import com.example.ui.VaultViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        // We just need the permission requested, whether granted or not we proceed.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        enableEdgeToEdge()
    setContent {
      MyApplicationTheme(darkTheme = true) { // Loaded dark theme by default with cyber styling
          val context = LocalContext.current
          val database = remember { AppDatabase.getDatabase(context.applicationContext) }
          val repository = remember { VaultRepository(database.fileDao(), database.transferHistoryDao()) }
          
          val viewModel: VaultViewModel = viewModel(
              factory = VaultViewModel.Factory(context.applicationContext, repository)
          )

          VaultDashboard(viewModel = viewModel)
      }
    }
  }
}
