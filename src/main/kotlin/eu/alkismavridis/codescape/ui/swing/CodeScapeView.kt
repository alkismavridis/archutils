package eu.alkismavridis.codescape.ui.swing

import eu.alkismavridis.codescape.project.CodeScapeObject
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel
import kotlin.math.roundToInt

class CodeScapeView(
  private val project: CodeScapeObject
): JPanel() {
  private var uiState = CodeScapeViewState(0.0, 0.0, 1.0, null, null)

  init { this.setupMouseListeners() }

  override fun paintComponent(g: Graphics) {
    if(g !is Graphics2D) return

    val originalTransform = g.transform
    g.clearRect(0, 0, this.width, this.height)

    this.translateAndScale(g)
    project.children.forEach{ this.renderObject(it, g) }

    g.transform = originalTransform
    this.debugState(g)
  }

  private fun renderObject(obj: CodeScapeObject, g: Graphics2D) {
    // TODO optimize rendering: skip objects that are out of view
    val scale = this.uiState.scale
    val widthPx = obj.width * scale
    val heightPx = obj.height * scale
    val shouldRenderChildren = widthPx > CHILDREN_THRESHOLD || heightPx > CHILDREN_THRESHOLD

    if (shouldRenderChildren) {
      g.color = Color.red
      g.drawRect(obj.left.toPixelSpace(scale), obj.top.toPixelSpace(scale), obj.width.toPixelSpace(scale), obj.height.toPixelSpace(scale))
    } else {
      g.color = Color.red
      g.fillRect(obj.left.toPixelSpace(scale), obj.top.toPixelSpace(scale), obj.width.toPixelSpace(scale), obj.height.toPixelSpace(scale))
    }

    if (shouldRenderChildren && obj.children.isNotEmpty()) {
      val translateX = obj.left.toPixelSpace(scale)
      val translateY = obj.top.toPixelSpace(scale)

      g.translate(translateX, translateY)
      obj.children.forEach { this.renderObject(it, g) }
      g.translate(-translateX, -translateY)
    }
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
