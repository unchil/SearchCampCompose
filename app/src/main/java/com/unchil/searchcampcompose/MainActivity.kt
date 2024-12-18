package com.unchil.searchcampcompose

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.unchil.searchcampcompose.ui.theme.SearchCampTheme
import com.unchil.searchcampcompose.db.LocalSearchCampDB
import com.unchil.searchcampcompose.db.SearchCampDB
import com.unchil.searchcampcompose.shared.ChkNetWork
import com.unchil.searchcampcompose.shared.LocalPermissionsManager
import com.unchil.searchcampcompose.shared.PermissionsManager
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.shared.view.CheckPermission
import com.unchil.searchcampcompose.shared.view.PermissionRequiredCompose
import com.unchil.searchcampcompose.view.SearchScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


val LocalUsableDarkMode =  compositionLocalOf{ false }
val LocalUsableHaptic = compositionLocalOf{ true }

class MainActivity : ComponentActivity() {


    private val permissionsManager = PermissionsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val searchCampDB = SearchCampDB.getInstance(context.applicationContext)
            var isConnected  by remember { mutableStateOf(context.checkInternetConnected()) }

            val coroutineScope = rememberCoroutineScope()


            LaunchedEffect(key1 = isConnected ){
                while(!isConnected) {
                    delay(500)
                    isConnected = context.checkInternetConnected()
                }
            }


            SearchCampTheme( dynamicColor = false) {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                )
                {
                    CompositionLocalProvider(
                        LocalPermissionsManager provides permissionsManager,
                        LocalSearchCampDB provides searchCampDB)
                    {

                        Box(modifier = Modifier.fillMaxSize()) {
                            if (isConnected) {
                                SearchScreen()
                            } else {
                                ChkNetWork(onCheckState = {
                                    coroutineScope.launch {
                                        isConnected = checkInternetConnected()
                                    }
                                })
                            }
                        }

                    }
                }

            }
        }


    }

}

