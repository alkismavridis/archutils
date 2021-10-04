package eu.alkismavridis.codescape.ui.panel

import java.awt.Point

class CodeScapeViewState(
  val left: Int,
  val top: Int,
  val scale: Double,
  val dragStart: Point?,
  val dragMapStart: Point?,
) {
  fun withLocation(newLeft: Int, newTop: Int) = CodeScapeViewState(
    newLeft,
    newTop,
    this.scale,
    this.dragStart,
    this.dragMapStart
  )

  fun withScale(newScale: Double) = CodeScapeViewState(
    this.left,
    this.top,
    newScale,
    this.dragStart,
    this.dragMapStart
  )

  fun withDragStart(newDragStart: Point?) = CodeScapeViewState(
    this.left,
    this.top,
    this.scale,
    newDragStart,
    if (newDragStart == null) null else Point(this.left, this.top)
  )
}
