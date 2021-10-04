package eu.alkismavridis.codescape.ui.panel

import eu.alkismavridis.codescape.project.CodeScapeProject
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JPanel

class CodeScapeView(
  private val project: CodeScapeProject
): JPanel() {
  private var uiState = CodeScapeViewState(0, 0, 1.0, null, null)

  init { this.setupMouseListeners() }

  override fun paintComponent(g: Graphics) {
    val g2d = g as? Graphics2D ?: return

    g2d.clearRect(0, 0, this.width, this.height)
    g2d.translate(this.uiState.left, this.uiState.top)
    g2d.scale(this.uiState.scale, this.uiState.scale)

    g2d.color = Color.red
    g2d.drawRect(0, 0, 100, 100)
  }

  private fun setState(state: CodeScapeViewState) {
    this.uiState = state
    this.repaint()
  }

  private fun setupMouseListeners() {
    val mouseAdapter = CodeScapeMouseAdapter(this::uiState, this::setState)
    this.addMouseListener(mouseAdapter)
    this.addMouseMotionListener(mouseAdapter)
    this.addMouseWheelListener(mouseAdapter)

  }
}
