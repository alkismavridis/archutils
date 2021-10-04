package eu.alkismavridis.codescape.ui.swing

import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.CodeScapeNodeLoadingState
import eu.alkismavridis.codescape.layout.LayoutService
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.roundToInt

class CodeScapeView(
  private val rootNode: CodeScapeNode,
  private val layoutService: LayoutService,
): JPanel() {
  private var uiState = CodeScapeViewState(0.0, 0.0, 1.0, null, null)

  init { this.setupMouseListeners() }

  override fun paintComponent(g: Graphics) {
    if(g !is Graphics2D) return

    val originalTransform = g.transform
    g.clearRect(0, 0, this.width, this.height)

    this.translateAndScale(g)
    this.renderObject(this.rootNode, g)

    g.transform = originalTransform
    this.debugState(g)
  }

  private fun renderObject(obj: CodeScapeNode, g: Graphics2D) {
    // TODO optimize rendering: skip objects that are out of view
    val scale = this.uiState.scale
    val widthPx = obj.width * scale
    val heightPx = obj.height * scale
    val shouldRenderChildren = widthPx > CHILDREN_THRESHOLD || heightPx > CHILDREN_THRESHOLD

    if (shouldRenderChildren) {
      g.color = Color.RED
      g.drawRect(obj.x.toPixelSpace(scale), obj.y.toPixelSpace(scale), obj.width.toPixelSpace(scale), obj.height.toPixelSpace(scale))
    } else if (obj.loadingState == CodeScapeNodeLoadingState.LOADING) {
      g.color = Color.GRAY
      g.fillRect(obj.x.toPixelSpace(scale), obj.y.toPixelSpace(scale), obj.width.toPixelSpace(scale), obj.height.toPixelSpace(scale))
    } else {
      g.color = Color.RED
      g.fillRect(obj.x.toPixelSpace(scale), obj.y.toPixelSpace(scale), obj.width.toPixelSpace(scale), obj.height.toPixelSpace(scale))
    }

    if (shouldRenderChildren && obj.loadingState == CodeScapeNodeLoadingState.UNCHECKED) {
      this.layoutService.loadChildren(obj, this::repaint)
    }

    if (shouldRenderChildren && obj.children.isNotEmpty()) {
      val translateX = obj.x.toPixelSpace(scale)
      val translateY = obj.y.toPixelSpace(scale)

      g.translate(translateX, translateY)
      obj.children.forEach { this.renderObject(it, g) }
      g.translate(-translateX, -translateY)
    }


    g.color = Color.BLUE
    g.drawString(obj.file.name, obj.x.toPixelSpace(scale), obj.y.toPixelSpace(scale))
  }

  private fun setState(state: CodeScapeViewState) {
    this.uiState = state
    this.repaint()
  }

  private fun translateAndScale(g: Graphics2D) {
    g.translate(-this.uiState.x.toPixelSpace(this.uiState.scale), -this.uiState.y.toPixelSpace(this.uiState.scale))
  }

  private fun debugState(g: Graphics2D) {
    g.color = Color.green
    g.drawString("${this.uiState.x.roundToInt()}, ${this.uiState.y.roundToInt()}, ${this.uiState.scale}", 10.0f, 10.0f)
  }

  private fun setupMouseListeners() {
    val mouseAdapter = CodeScapeMouseAdapter(this::uiState, this::setState)
    this.addMouseListener(mouseAdapter)
    this.addMouseMotionListener(mouseAdapter)
    this.addMouseWheelListener(mouseAdapter)
  }

  private fun Double.toPixelSpace(scale: Double): Int {
    return (this * scale).roundToInt()
  }

  companion object {
    private const val CHILDREN_THRESHOLD = 200
  }
}
