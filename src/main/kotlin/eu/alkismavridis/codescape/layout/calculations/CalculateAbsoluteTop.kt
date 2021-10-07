package eu.alkismavridis.codescape.layout.calculations

import eu.alkismavridis.codescape.layout.model.MapArea

fun MapArea.calculateAbsoluteTop(): Double {
  val parent = this.getParent()
  return if(parent == null) {
    this.getTop()
  } else {
    this.getTop() + parent.getAbsTop()
  }
}
