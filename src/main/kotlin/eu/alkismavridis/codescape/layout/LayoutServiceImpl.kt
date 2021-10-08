package eu.alkismavridis.codescape.layout

import eu.alkismavridis.codescape.layout.model.MapArea
import org.jetbrains.rpc.LOG
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.min
import kotlin.math.sqrt

class LayoutServiceImpl: LayoutService {

  override fun <T> layout(parentArea: MapArea, children: List<T>): Sequence<LayoutResult<T>> {
    if (children.isEmpty()) return emptySequence()

    val parentAspectRatio = parentArea.getWidth() / parentArea.getHeight()
    val spacing = min(parentArea.getWidth(), parentArea.getHeight()) * SPACING_RATIO
    val rowCount = floor(sqrt(children.size / parentAspectRatio))
    val colCount = ceil(children.size / rowCount).toInt()
    val childSize = min(
      (parentArea.getWidth() - spacing) / colCount - spacing,
      (parentArea.getHeight() - spacing) / rowCount - spacing,
    )

    LOG.info("Laying out ${children.size} Children.\n rowCount: $rowCount \n parentAspectRatio: $parentAspectRatio \n colCount: $colCount")

    return children.asSequence().mapIndexed { index, input ->
      val area = this.createChildArea(parentArea, index, colCount, spacing, childSize)
      LayoutResult(input, area)
    }
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

