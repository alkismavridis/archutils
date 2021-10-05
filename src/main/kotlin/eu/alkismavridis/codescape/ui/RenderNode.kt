package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.layout.ChildrenLoadState
import eu.alkismavridis.codescape.layout.CodeScapeNode
import java.awt.Color
import java.awt.Font
import java.awt.Rectangle
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
    ChildrenLoadState.SIZE_TOO_LARGE -> renderOpenDirectoryWithManyChildren(node, ctx)
    else -> {}
  }
}

private fun renderClosedDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  ctx.g.color = Color.ORANGE
  ctx.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
}

private fun renderOpenDirectoryWithManyChildren(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  ctx.g.color = Color.MAGENTA
  ctx.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
}

private fun renderOpenLoadedDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  ctx.g.color = Color.GRAY
  ctx.g.drawRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))

  if (node.children.isEmpty()) return

  val translateX = node.x.toPixelSpace(scale)
  val translateY = node.y.toPixelSpace(scale)

  ctx.g.translate(translateX, translateY)
  node.children.forEach { renderNode(it, ctx) }
  ctx.g.translate(-translateX, -translateY)
}

private fun renderLoadingDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  ctx.g.color = Color.GRAY
  ctx.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
}

private fun renderVisibleFile(node: CodeScapeNode, ctx: RenderContext) {
  val scale = ctx.scale
  ctx.g.color = Color.GREEN
  ctx.g.fillRect(node.x.toPixelSpace(scale), node.y.toPixelSpace(scale), node.width.toPixelSpace(scale), node.height.toPixelSpace(scale))
}

private fun renderNodeLabel(node: CodeScapeNode, ctx: RenderContext) {
  val widthPx = node.width * ctx.scale
  if (widthPx > SHOW_LABEL_THRESHOLD) {
    val nodeXPixel = node.x.toPixelSpace(ctx.scale)
    val nodeYPixel = node.y.toPixelSpace(ctx.scale)
    val fontSize = min(widthPx / 10, 20.0).roundToInt()

    val originalClip = ctx.g.clip
    ctx.g.clip = Rectangle(nodeXPixel, nodeYPixel - 30, node.width.toPixelSpace(ctx.scale), 30)

    ctx.g.color = LABEL_COLOR
    ctx.g.font = Font("serif", Font.PLAIN, fontSize)
    ctx.g.drawString(node.file.name, nodeXPixel, nodeYPixel - 4)

    ctx.g.clip = originalClip
  }
}

private fun Double.toPixelSpace(scale: Double): Int {
  return (this * scale).roundToInt()
}

private const val OPEN_DIR_THRESHOLD = 200
private const val SHOW_LABEL_THRESHOLD = 60
private val LABEL_COLOR = Color(200,200, 255)
