package eu.alkismavridis.codescape.ui

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.ScalableIcon
import eu.alkismavridis.codescape.config.StyleConfiguration
import eu.alkismavridis.codescape.tree.model.ChildrenLoadState
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.layout.calculations.intersectsWith
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.tree.TreeDataService
import eu.alkismavridis.codescape.tree.actions.unloadChildren
import eu.alkismavridis.codescape.tree.model.NodeType
import java.awt.*
import javax.swing.Icon
import kotlin.math.min
import kotlin.math.roundToInt


class NodeRenderer(
  private val scale: Double,
  private val camera: MapArea,
  private val g: Graphics2D,
  private val styleConfig: StyleConfiguration,
  private val loadChildren: (node: CodeScapeNode) -> Unit,
  private val onAutoOpen: (node: CodeScapeNode) -> Unit,
  private val onAutoClose: (node: CodeScapeNode) -> Unit,
  private val imageProvider: ImageProvider,
) {
  fun render(node: CodeScapeNode) {
    if (this.camera.intersectsWith(node.area)) {
      renderVisibleNode(node)
    } else {
      this.onAutoClose(node)
    }
  }

  private fun renderVisibleNode(node: CodeScapeNode) {
    when(node.type) {
      NodeType.BRANCH -> renderVisibleDirectory(node)
      NodeType.LEAF -> renderVisibleFile(node)
    }
  }

  private fun renderVisibleDirectory(node: CodeScapeNode) {
    if (!node.isOpen && !node.autoLoad) {
      renderExplicitlyClosedDirectory(node)
      return
    }

    if (this.shouldAutoOpen(node)) {
      this.onAutoOpen(node)
    } else {
      this.onAutoClose(node)
    }

    if (node.isOpen) {
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
    }
  }

  private fun renderOpenLoadedDirectory(node: CodeScapeNode) {
    val area = node.area
    val image = node.options.imageId?.let { this.imageProvider.getImage(it) }
    if (image == null) {
      val x = area.getLeft().toPixelSpace(scale)
      val y = area.getTop().toPixelSpace(scale)
      val width = area.getWidth().toPixelSpace(scale)
      val height = area.getHeight().toPixelSpace(scale)

      this.g.color = node.options.openColor?.let { this.imageProvider.getColor(it) }
        ?: node.options.color?.let { this.imageProvider.getColor(it) }
        ?: this.imageProvider.getColor(this.styleConfig.openDirColor)
      this.g.fillRect(x, y, width, height)

      g.stroke = BasicStroke(node.options.borderWidth ?: this.styleConfig.borderWidth)
      this.g.color = node.options.borderColor
        ?.let { this.imageProvider.getColor(it) }
        ?: this.imageProvider.getColor(this.styleConfig.borderColor)
      this.g.drawRect(x, y, width, height)
    } else {
      renderImage(node.area, image)
    }

    renderNodeLabel(node)

    if (node.children.isEmpty()) return

    val translateX = area.getLeft().toPixelSpace(scale)
    val translateY = area.getTop().toPixelSpace(scale)

    this.g.translate(translateX, translateY)
    node.children.forEach { render(it) }
    this.g.translate(-translateX, -translateY)
  }

  private fun renderLoadingDirectory(node: CodeScapeNode) = renderSolidNode(node, this.styleConfig.loadingDirColor, AllIcons.General.Ellipsis)
  private fun renderVisibleFile(node: CodeScapeNode) = renderSolidNode(node, this.styleConfig.fileColor, AllIcons.Actions.Checked)
  private fun renderExplicitlyClosedDirectory(node: CodeScapeNode) = renderSolidNode(node, this.styleConfig.lockedDirColor, AllIcons.Process.Stop)
  private fun renderClosedDirectory(node: CodeScapeNode) = renderSolidNode(node, this.styleConfig.closedDirColor, AllIcons.Nodes.Folder)

  private fun renderNodeLabel(node: CodeScapeNode) {
    val area = node.area
    val widthPx = area.getWidth() * this.scale
    if (widthPx > SHOW_LABEL_THRESHOLD) {
      val nodeXPixel = area.getLeft().toPixelSpace(this.scale)
      val nodeYPixel = area.getTop().toPixelSpace(this.scale)
      val fontSize = min(widthPx / 10, 20.0).roundToInt()

      val originalClip = this.g.clip
      this.g.clip = Rectangle(nodeXPixel, nodeYPixel - 26, area.getWidth().toPixelSpace(this.scale), 30)

      this.g.font = Font("serif", Font.PLAIN, fontSize)
      val fm = this.g.fontMetrics
      val rect = fm.getStringBounds(node.label, this.g)

      this.g.color = this.imageProvider.getColor(this.styleConfig.labelBackground)
      this.g.fillRect(nodeXPixel - 4, nodeYPixel - fm.ascent, rect.width.roundToInt() + 8, rect.height.roundToInt())

      this.g.color = this.imageProvider.getColor(this.styleConfig.labelColor)
      this.g.drawString(node.label, nodeXPixel, nodeYPixel)

      this.g.clip = originalClip
    }
  }

  private fun renderSolidNode(node: CodeScapeNode, defaultColor: String, icon: Icon?) {
    val image = node.options.imageId?.let { this.imageProvider.getImage(it) }
    val area = node.area

    val leftPx = area.getLeft().toPixelSpace(scale)
    val topPx = area.getTop().toPixelSpace(scale)
    val widthPx = area.getWidth().toPixelSpace(scale)
    val heightPx = area.getHeight().toPixelSpace(scale)

    if (image == null) {
      this.g.color = node.options.color
        ?.let{ this.imageProvider.getColor(it) }
        ?: this.imageProvider.getColor(defaultColor)
      this.g.fillRect(leftPx, topPx, widthPx, heightPx)
    } else {
      renderImage(node.area, image)
    }

    g.stroke = BasicStroke(node.options.borderWidth ?: this.styleConfig.borderWidth)
    g.color = node.options.borderColor
      ?.let { this.imageProvider.getColor(it) }
      ?: this.imageProvider.getColor(this.styleConfig.borderColor)
    this.g.drawRect(leftPx, topPx, widthPx, heightPx)

    if (icon != null && !node.options.hideIcon) {
      this.renderIcon(icon, leftPx, topPx, widthPx, heightPx)
    }
    renderNodeLabel(node)
  }

  private fun renderImage(area: MapArea, image: Image) {
    val nodeLeft = area.getLeft().toPixelSpace(scale)
    val nodeTop = area.getTop().toPixelSpace(scale)
    val nodeWidth = area.getWidth().toPixelSpace(scale)
    val nodeHeight = area.getHeight().toPixelSpace(scale)
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

  private fun renderIcon(icon: Icon, nodeLeft: Int, nodeTop: Int, nodeWidth: Int, nodeHeight: Int) {
    if (icon is ScalableIcon) {
      val scaleFactor = nodeWidth / icon.iconHeight * 0.618033988
      val scaled = icon.scale(scaleFactor.toFloat())
      scaled.paintIcon(null, this.g, nodeLeft + nodeWidth / 2 - scaled.iconWidth / 2, nodeTop + nodeHeight / 2 - scaled.iconHeight / 2)
    }
  }

  private fun shouldAutoOpen(node: CodeScapeNode) : Boolean {
    val widthPx = node.area.getWidth() * this.scale
    val heightPx = node.area.getHeight() * this.scale
    return widthPx > OPEN_DIR_THRESHOLD || heightPx > OPEN_DIR_THRESHOLD
  }

  private fun Double.toPixelSpace(scale: Double): Int {
    return (this * scale).roundToInt()
  }

  companion object {
    private const val OPEN_DIR_THRESHOLD = 200
    private const val SHOW_LABEL_THRESHOLD = 60
  }
}
