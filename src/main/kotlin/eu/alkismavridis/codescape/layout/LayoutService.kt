package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.layout.model.MapArea

interface LayoutService {
  fun <INP, RES> layout(parentArea: MapArea, children: List<INP>, mapper: (INP, MapArea) -> RES): Sequence<RES>
}
