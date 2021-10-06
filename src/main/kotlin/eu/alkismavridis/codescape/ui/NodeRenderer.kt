package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.map.ChildrenLoadState
import eu.alkismavridis.codescape.map.CodeScapeNode
import eu.alkismavridis.codescape.map.MapArea
import java.awt.*
import kotlin.math.min
import kotlin.math.roundToInt


class NodeRenderer(
  private val scale: Double,
  private val mapArea: MapArea,
  private val g: Graphics2D,
  private val loadChildren: (node: CodeScapeNode) -> Unit,
  private val getImage: (path: String) -> Image,
) {
  fun render(node: CodeScapeNode) {
    if (this.mapArea.intersectsWith(node)) {
      renderVisibleNode(node)
    } else {
      node.unloadChildren()
    }
  }

  private fun renderVisibleNode(node: CodeScapeNode) {
    if (node.file.isDirectory) {
      renderVisibleDirectory(node)
    } else {
      renderVisibleFile(node)
    }

    renderNodeLabel(node)
  }

  private fun renderVisibleDirectory(node: CodeScapeNode) {
    val widthPx = node.width * this.scale
    val heightPx = node.height * this.scale
    val shouldRenderOpen = node.file.isDirectory && widthPx > OPEN_DIR_THRESHOLD || heightPx > OPEN_DIR_THRESHOLD

    if (shouldRenderOpen) {
      renderOpenDirectory(node)
    } else {
      node.unloadChildren()
      renderClosedDirectory(node)
    }
  }

  private fun renderOpenDirectory(node: CodeScapeNode) {
    when(node.loadingState) {
      ChildrenLoadState.UNCHECKED -> {
        this.loadChildren(node)
        renderLoadingDirectory(node)
      }

      ChildrenLoadState.LOADING -> renderLoadingDirectory(node)
      ChildrenLoadState.LOADED -> renderOpenLoadedDirectory(node)
      else -> renderExplicitlyClosedDirectory(node)
    }
  }

  private fun renderOpenLoadedDirectory(node: CodeScapeNode) {
    val image = node.file.options.image?.let { this.getImage(it) }
    if (image == null) {
      val x = node.x.toPixelSpace(scale)
      val y = node.y.toPixelSpace(scale)
      val width = node.width.toPixelSpace(scale)
      val height = node.height.toPixelSpace(scale)

      this.g.color = OPEN_DIR_BACKGROUND
      this.g.fillRect(x, y, width, height)

      this.g.color = OPEN_DIR_BORDER_COLOR
      this.g.drawRect(x, y, width, height)
    } else {
      renderImage(node, image)
    }

    if (node.children.isEmpty()) return

    val translateX = node.x.toPixelSpace(scale)
    val translateY = node.y.toPixelSpace(scale)

    this.g.translate(translateX, translateY)
    node.children.forEach { render(it) }
    this.g.translate(-translateX, -translateY)
  }

  private fun renderLoadingDirectory(node: CodeScapeNode) = renderSolidNode(node, LOADING_DIR_BACKGROUND)
  private fun renderVisibleFile(node: CodeScapeNode) = renderSolidNode(node, FILE_BACKGROUND)
  private fun renderExplicitlyClosedDirectory(node: CodeScapeNode) = renderSolidNode(node, EXPLICITLY_CLOSED_BACKGROUND)
  private fun renderClosedDirectory(node: CodeScapeNode) = renderSolidNode(node, CLOSED_DIR_BACKGROUND)

  private fun renderNodeLabel(node: CodeScapeNode) {
    val widthPx = node.width * this.scale
    if (widthPx > SHOW_LABEL_THRESHOLD) {
      val nodeXPixel = node.x.toPixelSpace(this.scale)
      val nodeYPixel = node.y.toPixelSpace(this.scale) - 4
      val fontSize = min(widthPx / 10, 20.0).roundToInt()

      val originalClip = this.g.clip
      this.g.clip = Rectangle(nodeXPixel, nodeYPixel - 26, node.width.toPixelSpace(this.scale), 30)

      this.g.font = Font("serif", Font.PLAIN, fontSize)
      val fm = this.g.fontMetrics
      val rect = fm.getStringBounds(node.file.name, this.g)

      this.g.color = LABEL_BACKGROUND
      this.g.fillRect(nodeXPixel - 4, nodeYPixel - fm.ascent, rect.width.roundToInt() + 8, rect.height.roundToInt())

      this.g.color = LABEL_COLOR
      this.g.drawString(node.file.name, nodeXPixel, nodeYPixel)

      this.g.clip = originalClip
    }
  }

  private fun renderSolidNode(node: CodeScapeNode, defaultColor: Color) {
    val image = node.file.options.image?.let { this.getImage(it) }

    if (image == null) {
      this.g.color = defaultColor
      this.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
    } else {
      renderImage(node, image)
    }
  }

  private fun renderImage(node: CodeScapeNode, image: Image) {
    val nodeLeft = node.x.toPixelSpace(scale)
    val nodeTop = node.y.toPixelSpace(scale)
    val nodeWidth = node.width.toPixelSpace(scale)
    val nodeHeight = node.height.toPixelSpace(scale)
    val imageWidth = image.getWidth(null)
    val imageHeight = image.getHeight(null)

    val aspectRatio = min(imageWidth.toDouble() / nodeWidth, imageHeight.toDouble()  / nodeHeight)
    val clipLeft = ((imageWidth - aspectRatio * nodeWidth) / 2).toInt()
    val clipTop = ((imageHeight - aspectRatio * nodeHeight) / 2).toInt()
    val clipWidth = (aspectRatio * nodeWidth).toInt()
    val clipHeight = (aspectRatio * nodeHeight).toInt()
    this.g.drawImage(
      image,
      nodeLeft,
      nodeTop,
      nodeLeft + nodeWidth,
      nodeTop + nodeHeight,
      clipLeft,
      clipTop,
      clipLeft + clipWidth,
      clipTop + clipHeight,
      null
    )
  }

  private fun Double.toPixelSpace(scale: Double): Int {
    return (this * scale).roundToInt()
  }

  companion object {
    private const val OPEN_DIR_THRESHOLD = 200
    private const val SHOW_LABEL_THRESHOLD = 60

    private val OPEN_DIR_BACKGROUND = Color(192, 192, 192, 200)
    private val OPEN_DIR_BORDER_COLOR = Color.BLACK
    private val LOADING_DIR_BACKGROUND = Color.GRAY
    private val EXPLICITLY_CLOSED_BACKGROUND = Color(255, 0, 255, 200)
    private val CLOSED_DIR_BACKGROUND = Color(255, 200, 0, 200)
    private val FILE_BACKGROUND = Color(0, 255, 0, 200)
    private val LABEL_COLOR = Color(0,0, 0)
    private val LABEL_BACKGROUND = Color(200,200, 200)
  }
}
