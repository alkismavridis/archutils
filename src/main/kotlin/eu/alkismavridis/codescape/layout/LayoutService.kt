package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.layout.model.MapArea

interface LayoutService {
  fun layoutIt(parentArea: MapArea, childCount: Int): Sequence<MapArea>
}
