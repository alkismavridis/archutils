package eu.alkismavridis.codescape.map.calculations

import eu.alkismavridis.codescape.map.model.MapArea

fun MapArea.calculateAbsoluteTop(): Double {
  val parent = this.getParent()
  return if(parent == null) {
    this.getTop()
  } else {
    this.getTop() + parent.getAbsTop()
  }
}
