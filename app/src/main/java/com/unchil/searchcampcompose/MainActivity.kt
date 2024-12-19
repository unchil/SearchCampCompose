package com.unchil.searchcampcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.unchil.searchcampcompose.db.LocalSearchCampDB
import com.unchil.searchcampcompose.db.SearchCampDB
import com.unchil.searchcampcompose.shared.ChkNetWork
import com.unchil.searchcampcompose.shared.LocalPermissionsManager
import com.unchil.searchcampcompose.shared.PermissionsManager
import com.unchil.searchcampcompose.shared.checkInternetConnected
import com.unchil.searchcampcompose.ui.theme.SearchCampTheme
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
                ){
                    CompositionLocalProvider(
                        LocalPermissionsManager provides permissionsManager,
                        LocalSearchCampDB provides searchCampDB
                    ){

                        Box(modifier = Modifier.fillMaxSize()
                        ){
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

