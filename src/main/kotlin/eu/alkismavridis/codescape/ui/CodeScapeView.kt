package eu.alkismavridis.codescape.ui

import com.intellij.openapi.diagnostic.Logger
import eu.alkismavridis.codescape.config.StyleConfiguration
import eu.alkismavridis.codescape.integration.CodeScapeActionHandler
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import eu.alkismavridis.codescape.layout.model.MapArea
import eu.alkismavridis.codescape.tree.TreeDataService
import eu.alkismavridis.codescape.tree.actions.unloadChildren
import eu.alkismavridis.codescape.tree.model.NodeType
import java.awt.*
import java.awt.event.MouseEvent
import javax.swing.JMenuItem
import javax.swing.JPanel
import javax.swing.JPopupMenu
import kotlin.math.roundToInt

class CodeScapeView(
  private var rootNode: CodeScapeNode,
  private val treeDataService: TreeDataService,
  private val styleConfig: StyleConfiguration,
  private val imageProvider: ImageProvider,
  private val actionHandler: CodeScapeActionHandler,
): JPanel() {
  private var uiState = this.createInitialState()

  init { this.setupMouseListeners() }

  override fun paintComponent(g: Graphics) {
    if(g !is Graphics2D) return
    g.color = BACKGROUND_COLOR
    g.fillRect(0, 0, this.width, this.height)

    val originalTransform = g.transform
    this.translateAndScale(g)
    val camera = this.calculateCamera()
    val renderer = NodeRenderer(
      this.uiState.scale,
      camera,
      g,
      this.styleConfig,
      this::loadChildrenInNewTread,
      this::handleAutoOpen,
      this::handleAutoClose,
      this.imageProvider
    )

    renderer.render(this.rootNode)
    g.transform = originalTransform
  }


  private fun setState(state: CodeScapeViewState) {
    this.uiState = state
    this.repaint()
  }

  private fun translateAndScale(g: Graphics2D) {
    g.translate(-this.uiState.x.toPixelSpace(this.uiState.scale), -this.uiState.y.toPixelSpace(this.uiState.scale))
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

  private fun handleNodeClick(node: CodeScapeNode?, event: MouseEvent) {
    if (event.button == 3) {
      CodeScapeMenu(node, this.actionHandler, this.treeDataService, this::repaint).show(this, event.x, event.y)
      return
    }

    if (event.button == 1 && node != null) {
      when (node.type) {
        NodeType.LEAF -> this.actionHandler.openLeafNode(node.id)
        NodeType.SIMPLE_BRANCH,
        NodeType.AUTO_LOADING_BRANCH -> this.treeDataService.openNode(node, isExplicit = true, this::repaint)
        NodeType.LOCKED_BRANCH -> {}
      }
    }
  }

  private fun createInitialState() = CodeScapeViewState(0.0, 0.0, 1.0, null, null)

  private fun loadChildrenInNewTread(node: CodeScapeNode) {
    LOGGER.info("Loading children of ${node.id}")
    this.actionHandler.runReadOnlyTask { this.treeDataService.loadChildren(node, this::repaint) }
  }

  private fun handleAutoOpen(node: CodeScapeNode) {
    this.treeDataService.openNode(node, isExplicit = false) {}
  }

  private fun handleAutoClose(node: CodeScapeNode) {
    this.treeDataService.closeNode(node, isExplicit = false) {}
  }

  private fun debugState(camera: MapArea, g: Graphics2D) {
    g.color = Color.green
    g.font = DEBUG_FONT
    g.drawString("X: ${camera.getLeft().roundToInt()}, ${camera.getRight().roundToInt()}", 10.0f, 15.0f)
    g.drawString("Y: ${camera.getTop().roundToInt()}, ${camera.getBottom().roundToInt()}", 10.0f, 35.0f)
    g.drawString("Scale: ${this.uiState.scale}", 10.0f, 55.0f)
  }

  companion object {
    private val DEBUG_FONT = Font("Serif", Font.PLAIN, 14)
    private val BACKGROUND_COLOR = Color(100, 100, 100)
    private val LOGGER = Logger.getInstance(CodeScapeView::class.java)
  }
}
