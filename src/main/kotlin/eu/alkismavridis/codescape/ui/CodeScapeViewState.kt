package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.layout.model.MapPoint
import java.awt.Point

class CodeScapeViewState(
  val x: Double,
  val y: Double,
  val scale: Double,
  val dragStart: Point?,
  val dragMapStart: MapPoint?,
) {
  fun withLocation(newX: Double, newY: Double) = CodeScapeViewState(
    newX,
    newY,
    this.scale,
    this.dragStart,
    this.dragMapStart
  )

  fun withScale(newScale: Double) = CodeScapeViewState(
    this.x,
    this.y,
    newScale,
    this.dragStart,
    this.dragMapStart
  )

  fun withLocationAndScale(newX: Double, newY: Double, newScale: Double) = CodeScapeViewState(
    newX,
    newY,
    newScale,
    this.dragStart,
    this.dragMapStart
  )

  fun withDragStart(newDragStart: Point?) = CodeScapeViewState(
    this.x,
    this.y,
    this.scale,
    newDragStart,
    if (newDragStart == null) null else MapPoint(this.x, this.y)
  )
}

