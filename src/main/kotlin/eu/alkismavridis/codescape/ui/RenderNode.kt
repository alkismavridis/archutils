package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.map.ChildrenLoadState
import eu.alkismavridis.codescape.map.CodeScapeNode
import java.awt.Color
import java.awt.Font
import java.awt.Image
import java.awt.Rectangle
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt


fun renderNode(node: CodeScapeNode, ctx: RenderContext) {
  if (ctx.mapArea.intersectsWith(node)) {
    renderVisibleNode(node, ctx)
  } else {
    node.unloadChildren()
  }
}

private fun renderVisibleNode(node: CodeScapeNode, ctx: RenderContext) {
  if (node.file.isDirectory) {
    renderVisibleDirectory(node, ctx)
  } else {
    renderVisibleFile(node, ctx)
  }

  renderNodeLabel(node, ctx)
}

private fun renderVisibleDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val widthPx = node.width * ctx.scale
  val heightPx = node.height * ctx.scale
  val shouldRenderOpen = node.file.isDirectory && widthPx > OPEN_DIR_THRESHOLD || heightPx > OPEN_DIR_THRESHOLD

  if (shouldRenderOpen) {
    renderOpenDirectory(node, ctx)
  } else {
    node.unloadChildren()
    renderClosedDirectory(node, ctx)
  }
}

private fun renderOpenDirectory(node: CodeScapeNode, ctx: RenderContext) {
  when(node.loadingState) {
    ChildrenLoadState.UNCHECKED -> {
      ctx.loadChildren(node)
      renderLoadingDirectory(node, ctx)
    }

    ChildrenLoadState.LOADING -> renderLoadingDirectory(node, ctx)
    ChildrenLoadState.LOADED -> renderOpenLoadedDirectory(node, ctx)
    else -> renderExplicitlyClosedDirectory(node, ctx)
  }
}

private fun renderOpenLoadedDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  val image = node.file.options.image?.let { ctx.getImage(it) }
  if (image == null) {
    val x = node.x.toPixelSpace(scale)
    val y = node.y.toPixelSpace(scale)
    val width = node.width.toPixelSpace(scale)
    val height = node.height.toPixelSpace(scale)

    ctx.g.color = OPEN_DIR_BACKGROUND
    ctx.g.fillRect(x, y, width, height)

    ctx.g.color = OPEN_DIR_BORDER_COLOR
    ctx.g.drawRect(x, y, width, height)
  } else {
    renderImage(node, image, ctx)
  }

  if (node.children.isEmpty()) return

  val translateX = node.x.toPixelSpace(scale)
  val translateY = node.y.toPixelSpace(scale)

  ctx.g.translate(translateX, translateY)
  node.children.forEach { renderNode(it, ctx) }
  ctx.g.translate(-translateX, -translateY)
}

private fun renderLoadingDirectory(node: CodeScapeNode, ctx: RenderContext) = renderSolidNode(node, LOADING_DIR_BACKGROUND, ctx)
private fun renderVisibleFile(node: CodeScapeNode, ctx: RenderContext) = renderSolidNode(node, FILE_BACKGROUND, ctx)
private fun renderExplicitlyClosedDirectory(node: CodeScapeNode, ctx: RenderContext) = renderSolidNode(node, EXPLICITLY_CLOSED_BACKGROUND, ctx)
private fun renderClosedDirectory(node: CodeScapeNode, ctx: RenderContext) = renderSolidNode(node, CLOSED_DIR_BACKGROUND, ctx)

private fun renderNodeLabel(node: CodeScapeNode, ctx: RenderContext) {
  val widthPx = node.width * ctx.scale
  if (widthPx > SHOW_LABEL_THRESHOLD) {
    val nodeXPixel = node.x.toPixelSpace(ctx.scale)
    val nodeYPixel = node.y.toPixelSpace(ctx.scale) - 4
    val fontSize = min(widthPx / 10, 20.0).roundToInt()

    val originalClip = ctx.g.clip
    ctx.g.clip = Rectangle(nodeXPixel, nodeYPixel - 26, node.width.toPixelSpace(ctx.scale), 30)

    ctx.g.font = Font("serif", Font.PLAIN, fontSize)
    val fm = ctx.g.fontMetrics
    val rect = fm.getStringBounds(node.file.name, ctx.g)

    ctx.g.color = LABEL_BACKGROUND
    ctx.g.fillRect(nodeXPixel - 4, nodeYPixel - fm.ascent, rect.width.roundToInt() + 8, rect.height.roundToInt())

    ctx.g.color = LABEL_COLOR
    ctx.g.drawString(node.file.name, nodeXPixel, nodeYPixel)

    ctx.g.clip = originalClip
  }
}

private fun renderSolidNode(node: CodeScapeNode, defaultColor: Color, ctx: RenderContext) {
  val image = node.file.options.image?.let { ctx.getImage(it) }
  val scale = ctx.scale

  if (image == null) {
    ctx.g.color = defaultColor
    ctx.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
  } else {
    renderImage(node, image, ctx)
  }
}

private fun renderImage(node: CodeScapeNode, image: Image, ctx: RenderContext) {
  val scale = ctx.scale
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
  ctx.g.drawImage(
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
