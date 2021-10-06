package eu.alkismavridis.codescape.map.calculations

import eu.alkismavridis.codescape.map.model.MapArea

fun MapArea.containsPoint(absX: Double, absY: Double): Boolean {
  return (absX in this.getAbsLeft() .. this.getAbsRight()) &&
    (absY in this.getAbsTop() .. this.getAbsBottom())
}
