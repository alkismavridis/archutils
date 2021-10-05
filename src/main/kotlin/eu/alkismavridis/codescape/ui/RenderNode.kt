package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.layout.ChildrenLoadState
import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.MapArea
import java.awt.Color
import java.awt.Graphics2D
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

  ctx.g.color = Color.BLUE
  ctx.g.drawString(node.file.name, node.x.toPixelSpace(ctx.scale), node.y.toPixelSpace(ctx.scale))
}

private fun renderVisibleDirectory(node: CodeScapeNode, ctx: RenderContext) {
  val widthPx = node.width * ctx.scale
  val heightPx = node.height * ctx.scale
  val shouldRenderOpen = node.file.isDirectory && widthPx > CHILDREN_THRESHOLD || heightPx > CHILDREN_THRESHOLD

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

private fun Double.toPixelSpace(scale: Double): Int {
  return (this * scale).roundToInt()
}

class RenderContext(
  val scale: Double,
  val mapArea: MapArea,
  val g: Graphics2D,
  val loadChildren: (node: CodeScapeNode) -> Unit
)

private const val CHILDREN_THRESHOLD = 200
