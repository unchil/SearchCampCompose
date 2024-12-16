package com.unchil.searchcampcompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.Map
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import com.unchil.searchcampcompose.R

fun NavHostController.navigateTo(route: String) =
    this.navigate(route) {
        // Pop up to the start destination of the graph to
        // avoid building up a large stack of destinations
        // on the back stack as users select items
        popUpTo(
            this@navigateTo.graph.findStartDestination().id
        ) {
            saveState = false
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

fun NavHostController.navigateToNotSave(route: String) =
    this.navigate(route) {
        popUpTo(
            this@navigateToNotSave.graph.findStartDestination().id
        ) {
            saveState = false
        }
        launchSingleTop = true
        restoreState = false
    }


val resultScreens:List<SearchCampDestinations> = listOf(
    SearchCampDestinations.resultListScreen,
    SearchCampDestinations.resultMapScreen
)



sealed class SearchCampDestinations(
    val route:String,
    val name:Int = 0,
    val icon: ImageVector? = null,
){




    object resultListScreen : SearchCampDestinations(
        route = "resultListScreen",
        name = R.string.resultmenu_list,
        icon = Icons.AutoMirrored.Outlined.List
    ){


    }

    object resultMapScreen : SearchCampDestinations(
        route = "resultMapScreen",
        name = R.string.resultmenu_map,
        icon = Icons.Outlined.Map
    ){


    }


    companion object {


    }

}

