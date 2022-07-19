package com.example.daveexpress.utils

import android.app.Activity
import android.view.WindowManager
import androidx.annotation.ColorRes
import com.example.daveexpress.R
import com.google.android.gms.maps.GoogleMap


class Tools {

      companion object {
          fun insertPeriodically(text: String, insert: String, period: Int): String {

              val builder = StringBuilder(text.length + insert.length * (text.length / period) + 1)
              var index = 0
              var prefix = ""
              while (index < text.length) {
                  builder.append(prefix)
                  prefix = insert
                  builder.append(text.substring(index, Math.min(index + period, text.length)))
                  index += period
              }
              return builder.toString()
          }

          fun configActivityMaps(googleMap: GoogleMap): GoogleMap? {
              // set map type
              googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
              // Enable / Disable zooming controls
              googleMap.uiSettings.isZoomControlsEnabled = false

              // Enable / Disable Compass icon
              googleMap.uiSettings.isCompassEnabled = true
              // Enable / Disable Rotate gesture
              googleMap.uiSettings.isRotateGesturesEnabled = true
              // Enable / Disable zooming functionality
              googleMap.uiSettings.isZoomGesturesEnabled = true
              googleMap.uiSettings.isScrollGesturesEnabled = true
              googleMap.uiSettings.isMapToolbarEnabled = true
              return googleMap
          }

          fun setSystemBarColor(act: Activity) {
              val window = act.window
              window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
              window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
              window.statusBarColor = act.resources.getColor(R.color.colorPrimaryDark)
          }

          fun setSystemBarColor(act: Activity, @ColorRes color: Int) {
              val window = act.window
              window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
              window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
              window.statusBarColor = act.resources.getColor(color)
          }
      }


  }