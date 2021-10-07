package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.layout.model.MapArea

interface LayoutService {
  fun <T> layout(parentArea: MapArea, children: List<T>): Sequence<LayoutResult<T>>
}

class LayoutResult<T>(val data: T, val area: MapArea)
