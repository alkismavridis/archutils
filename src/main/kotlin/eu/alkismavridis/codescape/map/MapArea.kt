package eu.alkismavridis.codescape.map

class MapArea(
  val left: Double,
  val right: Double,
  val top: Double,
  val bottom: Double,
) {
  fun intersectsWith(node: CodeScapeNode): Boolean {
    val nodeAbsoluteX = node.getAbsoluteX()
    val nodeAbsoluteY = node.getAbsoluteY()

    return nodeAbsoluteX <= this.right &&
      nodeAbsoluteX + node.width >= this.left &&
      nodeAbsoluteY <= this.bottom &&
      nodeAbsoluteY + node.height >= this.top
  }
}
