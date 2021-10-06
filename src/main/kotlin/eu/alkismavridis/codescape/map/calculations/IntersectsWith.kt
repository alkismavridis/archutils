package eu.alkismavridis.codescape.map.calculations

import eu.alkismavridis.codescape.map.model.MapArea

fun MapArea.intersectsWith(other: MapArea): Boolean {
    return other.getAbsLeft() <= this.getAbsRight() &&
      other.getAbsRight() >= this.getAbsLeft() &&
      other.getAbsTop() <= this.getAbsBottom() &&
      other.getAbsBottom() >= this.getAbsTop()
}
