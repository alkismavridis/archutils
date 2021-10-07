package eu.alkismavridis.codescape.layout.calculations

import eu.alkismavridis.codescape.layout.model.MapArea

fun MapArea.calculateAbsoluteLeft(): Double {
  val parent = this.getParent()
  return if(parent == null) {
    this.getLeft()
  } else {
    this.getLeft() + parent.getAbsLeft()
  }
}
