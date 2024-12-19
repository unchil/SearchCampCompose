package com.unchil.searchcampcompose.model

import android.location.Location
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Forest
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Public
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.android.gms.maps.model.LatLng



object MapTypeMenuData {
    enum class Type {
        NORMAL,TERRAIN, HYBRID
    }
    val Types = listOf(
        Type.NORMAL,
        Type.TERRAIN,
        Type.HYBRID,
    )
    fun desc(type:Type):Pair<ImageVector, ImageVector?> {
        return when(type){
            Type.TERRAIN -> {
                Pair( Icons.Outlined.Forest, null)
            }
            Type.NORMAL -> {
                Pair( Icons.Outlined.Map, null)
            }
            Type.HYBRID -> {
                Pair( Icons.Outlined.Public, null)
            }
        }
    }
}



fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}