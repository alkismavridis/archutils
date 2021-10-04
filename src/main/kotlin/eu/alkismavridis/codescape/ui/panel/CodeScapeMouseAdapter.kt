package eu.alkismavridis.codescape.ui.panel

import java.awt.Point
import java.awt.event.*

class CodeScapeMouseAdapter(
  private val stateProvider: () -> CodeScapeViewState,
  private val onChange: (state: CodeScapeViewState) -> Unit
): MouseListener, MouseMotionListener, MouseWheelListener {

  override fun mouseDragged(e: MouseEvent){
    val state = this.stateProvider()
    val dragStart = state.dragStart ?: return
    val dragMapStart = state.dragMapStart ?: return

    val newLeft = dragMapStart.x + (e.point.x - dragStart.x)
    val newTop = dragMapStart.y + (e.point.y - dragStart.y)
    this.update { it.withLocation(newLeft, newTop) }
  }

  override fun mouseWheelMoved(e: MouseWheelEvent) = this.update {
    val newScale = if(e.wheelRotation < 0) it.scale * 1.1 else it.scale / 1.1
    it.withScale(newScale)
  }

  override fun mousePressed(e: MouseEvent) = this.update { it.withDragStart(e.point) }
  override fun mouseReleased(e: MouseEvent) = this.update { it.withDragStart(null) }
  override fun mouseExited(e: MouseEvent) = this.update { it.withDragStart(null) }

  override fun mouseClicked(e: MouseEvent) {}
  override fun mouseEntered(e: MouseEvent) {}
  override fun mouseMoved(e: MouseEvent) {}

  private fun update(newStateProvider: (current: CodeScapeViewState) -> CodeScapeViewState) {
    val state = stateProvider()
    val newState = newStateProvider(state)
    this.onChange(newState)
  }
}
