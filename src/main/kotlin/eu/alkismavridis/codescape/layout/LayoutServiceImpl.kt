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

    val itemLayoutWidth = parentArea.getWidth()
    val labelHeight = parentArea.getHeight() * LABEL_HEIGHT_PERCENTAGE
    val itemLayoutHeight = parentArea.getHeight() - labelHeight


    val parentAspectRatio = itemLayoutWidth/ itemLayoutHeight
    val spacing = min(itemLayoutWidth, itemLayoutHeight) * SPACING_RATIO
    val colCount = ceil(sqrt(children.size * parentAspectRatio)).toInt()
    val rowCount = if(children.size % colCount == 0) children.size / colCount else children.size / colCount + 1
    val childSize = min(
      (itemLayoutWidth - spacing) / colCount - spacing,
      (itemLayoutHeight - spacing) / rowCount - spacing,
    )

    LOG.info("LAYOUT count: ${children.size}, itemLayoutSize: ($itemLayoutWidth, $itemLayoutHeight), rowCount: $rowCount, colCount: $colCount")

    return children.asSequence().mapIndexed { index, input ->
      val area = this.createChildArea(parentArea, index, colCount, spacing, labelHeight, childSize)
      LayoutResult(input, area)
    }
  }

  private fun createChildArea(parentArea: MapArea, index: Int, colCount: Int, spacing: Double, labelHeight: Double, size: Double): MapArea {
    val childRow = index / colCount
    val childCol = index - childRow * colCount
    val x = spacing + childCol * (size + spacing)
    val y = labelHeight + spacing + childRow * (size + spacing)

    if (y + size > parentArea.getHeight()) {
      LOG.info("Oops, I exceeded my parent. ParentHeight: ${parentArea.getHeight()}, MyBottom: ${y + size}, y: $y, height: $size, labelHeight: $labelHeight")
    }
    return MapArea(x, y, size, size, parentArea)
  }

  companion object {
    private const val SPACING_RATIO = 0.05
    private const val LABEL_HEIGHT_PERCENTAGE = 0.15
  }
}
