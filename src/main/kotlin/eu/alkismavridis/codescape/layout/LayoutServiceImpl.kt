package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.layout.model.MapArea
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class LayoutServiceImpl: LayoutService {

  override fun layoutIt(parentArea: MapArea, childCount: Int): Sequence<MapArea> {
    if(childCount == 0) return emptySequence()

    val parentAspectRatio = parentArea.getWidth() / parentArea.getHeight()
    val spacing = min(parentArea.getWidth(), parentArea.getHeight()) * SPACING_RATIO
    val rowCount = floor( sqrt(childCount / parentAspectRatio) )
    val colCount = floor(childCount / rowCount).toInt()
    val childSize = min(
      (parentArea.getWidth() - spacing) / colCount - spacing,
      (parentArea.getHeight() - spacing) / rowCount - spacing,
    )

    return (0 until childCount)
      .asSequence()
      .map { this.createChildArea(parentArea, it, colCount, spacing, childSize) }
  }

  private fun createChildArea(parentArea: MapArea, index: Int, colCount: Int, spacing: Double, size: Double): MapArea {
    val childRow = index / colCount
    val childCol = index - childRow * colCount
    val x = spacing + childCol * (size + spacing)
    val y = spacing + childRow * (size + spacing)

    return MapArea(x, y, size, size, parentArea)
  }

  companion object {
    private const val SPACING_RATIO = 0.05
  }
}

