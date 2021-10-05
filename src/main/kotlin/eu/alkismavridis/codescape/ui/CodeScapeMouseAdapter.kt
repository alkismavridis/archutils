package eu.alkismavridis.codescape.ui

import java.awt.event.*

class CodeScapeMouseAdapter(
  private val getState: () -> CodeScapeViewState,
  private val onChange: (state: CodeScapeViewState) -> Unit
): MouseListener, MouseMotionListener, MouseWheelListener {

  override fun mouseDragged(e: MouseEvent){
    val state = this.getState()
    val dragStart = state.dragStart ?: return
    val dragMapStart = state.dragMapStart ?: return

    val newLeft = dragMapStart.x + (dragStart.x - e.point.x) / state.scale
    val newTop = dragMapStart.y + (dragStart.y - e.point.y) / state.scale
    this.update { it.withLocation(newLeft, newTop) }
  }

  override fun mouseWheelMoved(e: MouseWheelEvent) = this.update {
    val scrollMapX = it.x + e.point.x / it.scale
    val scrollMapY = it.y + e.point.y / it.scale

    val zoomFactor = if(e.wheelRotation < 0)  ZOOM_STEP else 1 / ZOOM_STEP
    val newLeft = scrollMapX + (it.x - scrollMapX) / zoomFactor
    val newTop = scrollMapY + (it.y - scrollMapY) / zoomFactor
    it.withLocationAndScale(newLeft, newTop, it.scale * zoomFactor)
  }

  override fun mousePressed(e: MouseEvent) = this.update { it.withDragStart(e.point) }
  override fun mouseReleased(e: MouseEvent) = this.update { it.withDragStart(null) }
  override fun mouseExited(e: MouseEvent) = this.update { it.withDragStart(null) }

  override fun mouseClicked(e: MouseEvent) {}
  override fun mouseEntered(e: MouseEvent) {}
  override fun mouseMoved(e: MouseEvent) {}

  private fun update(newStateProvider: (current: CodeScapeViewState) -> CodeScapeViewState) {
    val state = getState()
    val newState = newStateProvider(state)
    this.onChange(newState)
  }

  companion object {
    private const val ZOOM_STEP = 1.1
  }
}
