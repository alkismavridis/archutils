package eu.alkismavridis.codescape.layout.calculations

import eu.alkismavridis.codescape.layout.model.MapArea

fun MapArea.intersectsWith(other: MapArea): Boolean {
    return other.getAbsLeft() <= this.getAbsRight() &&
      other.getAbsRight() >= this.getAbsLeft() &&
      other.getAbsTop() <= this.getAbsBottom() &&
      other.getAbsBottom() >= this.getAbsTop()
}
