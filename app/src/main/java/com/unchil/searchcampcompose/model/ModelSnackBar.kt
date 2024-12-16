package com.unchil.searchcampcompose.model

import androidx.compose.material3.SnackbarDuration
import com.unchil.searchcampcompose.R

enum class SnackBarChannelType {
    SEARCH_RESULT,
    SEARCH_CLEAR,
}

data class SnackBarChannelData(
    val channelType: SnackBarChannelType,
    val channel:Int,
    var message:Int,
    val duration: SnackbarDuration,
    val actionLabel:String?,
    val withDismissAction:Boolean,
)


val snackbarChannelList = listOf<SnackBarChannelData>(

    SnackBarChannelData(
        channelType = SnackBarChannelType.SEARCH_RESULT,
        channel = 3,
        message = R.string.snackbar_3,
        duration = SnackbarDuration.Short,
        actionLabel =  null,
        withDismissAction = true,
    ),

    SnackBarChannelData(
        channelType = SnackBarChannelType.SEARCH_CLEAR,
        channel = 2,
        message = R.string.snackbar_2,
        duration = SnackbarDuration.Short,
        actionLabel = null,
        withDismissAction = true,
    ),

    )