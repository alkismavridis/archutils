package eu.alkismavridis.codescape.map.calculations

import eu.alkismavridis.codescape.map.model.MapArea

fun MapArea.calculateAbsoluteLeft(): Double {
  val parent = this.getParent()
  return if(parent == null) {
    this.getLeft()
  } else {
    this.getLeft() + parent.getAbsLeft()
  }
}
