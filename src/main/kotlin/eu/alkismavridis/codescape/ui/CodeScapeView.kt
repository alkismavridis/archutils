package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.integration.CodeScapeActionHandler
import eu.alkismavridis.codescape.map.CodeScapeNode
import eu.alkismavridis.codescape.map.LayoutService
import eu.alkismavridis.codescape.map.MapArea
import org.jetbrains.rpc.LOG
import java.awt.*
import java.nio.file.Files
import java.nio.file.Paths
import java.util.concurrent.ConcurrentHashMap
import javax.imageio.ImageIO
import javax.swing.JPanel
import kotlin.math.roundToInt

class CodeScapeView(
  private val rootNode: CodeScapeNode,
  private val layoutService: LayoutService,
  private val actionHandler: CodeScapeActionHandler,
): JPanel() {
  private var uiState = CodeScapeViewState(0.0, 0.0, 1.0, null, null)
  private val imageCache = ImageCache()

  init { this.setupMouseListeners() }

  override fun paintComponent(g: Graphics) {
    if(g !is Graphics2D) return
    g.clearRect(0, 0, this.width, this.height)

    val originalTransform = g.transform
    this.translateAndScale(g)
    val mapArea = this.calculateMapArea()
    val ctx = RenderContext(
      this.uiState.scale,
      mapArea,
      g,
      { this.layoutService.loadChildren(it, this::repaint) },
      { this.imageCache.getImage(it) }
    )

    renderNode(this.rootNode, ctx)

    g.transform = originalTransform
    this.debugState(mapArea, g)
  }


  private fun setState(state: CodeScapeViewState) {
    this.uiState = state
    this.repaint()
  }

  private fun translateAndScale(g: Graphics2D) {
    g.translate(-this.uiState.x.toPixelSpace(this.uiState.scale), -this.uiState.y.toPixelSpace(this.uiState.scale))
  }

  private fun debugState(mapArea: MapArea, g: Graphics2D) {
    g.color = Color.green
    g.font = DEBUG_FONT
    g.drawString("X: ${mapArea.left.roundToInt()}, ${mapArea.right.roundToInt()}", 10.0f, 15.0f)
    g.drawString("Y: ${mapArea.top.roundToInt()}, ${mapArea.bottom.roundToInt()}", 10.0f, 35.0f)
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

  private fun calculateMapArea() = MapArea(
    this.uiState.x,
    this.uiState.x + this.width / this.uiState.scale,
    this.uiState.y,
    this.uiState.y + this.height / this.uiState.scale,
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

  companion object {
    private val DEBUG_FONT = Font("Serif", Font.PLAIN, 14)
  }
}



class ImageCache {
  private val map = ConcurrentHashMap<String, Image>()

  fun getImage(path: String): Image {
    return map.computeIfAbsent(path) {
      LOG.info("Loading image \"$path\"")
      ImageIO.read(Files.newInputStream(Paths.get(path)))
    }
  }
}
