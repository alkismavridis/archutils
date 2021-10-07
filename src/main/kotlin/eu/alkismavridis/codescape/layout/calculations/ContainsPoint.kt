package eu.alkismavridis.codescape.layout.calculations

import eu.alkismavridis.codescape.layout.model.MapArea

fun MapArea.containsPoint(absX: Double, absY: Double): Boolean {
  return (absX in this.getAbsLeft() .. this.getAbsRight()) &&
    (absY in this.getAbsTop() .. this.getAbsBottom())
}
