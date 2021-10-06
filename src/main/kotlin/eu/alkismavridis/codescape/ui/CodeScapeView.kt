package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.integration.CodeScapeActionHandler
import eu.alkismavridis.codescape.map.CodeScapeNode
import eu.alkismavridis.codescape.map.LayoutService
import eu.alkismavridis.codescape.map.model.MapArea
import org.jetbrains.rpc.LOG
import java.awt.*
import javax.swing.JPanel
import kotlin.math.roundToInt

class CodeScapeView(
  private var rootNode: CodeScapeNode,
  private val layoutService: LayoutService,
  private val imageCache: ImageCache,
  private val actionHandler: CodeScapeActionHandler,
): JPanel() {
  private var uiState = this.createInitialState()

  init { this.setupMouseListeners() }

  fun reload(newRoot: CodeScapeNode) {
    this.rootNode = newRoot
    this.uiState = this.createInitialState()
    this.repaint()
  }

  override fun paintComponent(g: Graphics) {
    if(g !is Graphics2D) return
    g.clearRect(0, 0, this.width, this.height)

    val originalTransform = g.transform
    this.translateAndScale(g)
    val camera = this.calculateCamera()
    val renderer = NodeRenderer(
      this.uiState.scale,
      camera,
      g,
      { this.layoutService.loadChildren(it, this::repaint) },
      { this.imageCache.getImage(it) }
    )

    renderer.render(this.rootNode)

    g.transform = originalTransform
    this.debugState(camera, g)
  }


  private fun setState(state: CodeScapeViewState) {
    this.uiState = state
    this.repaint()
  }

  private fun translateAndScale(g: Graphics2D) {
    g.translate(-this.uiState.x.toPixelSpace(this.uiState.scale), -this.uiState.y.toPixelSpace(this.uiState.scale))
  }

  private fun debugState(camera: MapArea, g: Graphics2D) {
    g.color = Color.green
    g.font = DEBUG_FONT
    g.drawString("X: ${camera.getLeft().roundToInt()}, ${camera.getRight().roundToInt()}", 10.0f, 15.0f)
    g.drawString("Y: ${camera.getTop().roundToInt()}, ${camera.getBottom().roundToInt()}", 10.0f, 35.0f)
    g.drawString("Scale: ${this.uiState.scale}", 10.0f, 55.0f)
  }

  private fun setupMouseListeners() {
    val mouseAdapter = CodeScapeMouseAdapter(this::uiState, this::rootNode, this::setState, this::handleNodeClick)
    this.addMouseListener(mouseAdapter)
    this.addMouseMotionListener(mouseAdapter)
    this.addMouseWheelListener(mouseAdapter)
  }

  private fun Double.toPixelSpace(scale: Double): Int {
    return (this * scale).roundToInt()
  }

  private fun calculateCamera() = MapArea(
    this.uiState.x,
    this.uiState.y,
    this.width / this.uiState.scale,
    this.height / this.uiState.scale,
    null
  )

  private fun handleNodeClick(node: CodeScapeNode, mouseButton: Int) {
    if (mouseButton == 3) {
      LOG.info("TODO alkis - open menu for ${node.file.path}")
    } else if (node.file.isDirectory) {
      LOG.info("TODO alkis open directory")
      return
    } else if (mouseButton == 1) {
      this.actionHandler.handleOpenFile(node.file.path)
    }
  }

  private fun createInitialState() = CodeScapeViewState(0.0, 0.0, 1.0, null, null)

  companion object {
    private val DEBUG_FONT = Font("Serif", Font.PLAIN, 14)
  }
}
