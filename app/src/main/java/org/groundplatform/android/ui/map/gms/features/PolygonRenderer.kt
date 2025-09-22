/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.groundplatform.android.ui.map.gms.features

import android.content.res.Resources
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.Polygon as MapsPolygon
import com.google.android.gms.maps.model.PolygonOptions
import javax.inject.Inject
import org.groundplatform.android.R
import org.groundplatform.android.model.geometry.Polygon
import org.groundplatform.android.ui.map.Feature
import org.groundplatform.android.ui.map.gms.POLYGON_Z
import org.groundplatform.android.ui.map.gms.toLatLng
import org.groundplatform.android.ui.map.gms.toLatLngList
import timber.log.Timber
import android.graphics.Color

fun getColorWithAlpha(yourColor: Int, alpha: Int): Int {
    val red = Color.red(yourColor);
    val blue = Color.blue(yourColor);
    val green = Color.green(yourColor);
    return Color.argb(alpha, red, green, blue);
}

class PolygonRenderer @Inject constructor(resources: Resources) :
  MapsItemRenderer<Polygon, MapsPolygon> {
  private val defaultStrokeWidth = resources.getDimension(R.dimen.line_geometry_width)

  override fun add(
    map: GoogleMap,
    tag: Feature.Tag,
    geometry: Polygon,
    style: Feature.Style,
    selected: Boolean,
    visible: Boolean,
    tooltipText: String?,
    strokeRatio: Float?,
  ): MapsPolygon {
    check(tooltipText == null) { "Tooltip text not implemented for polygon features" }
    val strokeScale = if (selected) 2f else 1f
    val sR = strokeRatio ?: 1f
    val options = PolygonOptions()

    var opacity = 100
    if (sR != 2f) {
      opacity = 50
    }
    val fillColor = getColorWithAlpha(style.color, opacity)
    with(options) {
      addAll(geometry.shell.coordinates.map { it.toLatLng() })
      geometry.holes.forEach { addHole(it.coordinates.toLatLngList()) }
      clickable(false)
      visible(visible)
      zIndex(POLYGON_Z)
      strokeWidth(defaultStrokeWidth * strokeScale * sR)
      strokeColor(style.color)
      fillColor(fillColor)
      strokeJointType(JointType.ROUND)
    }

    val mapsPolygon = map.addPolygon(options)
    mapsPolygon.tag = tag
    return mapsPolygon
  }
}