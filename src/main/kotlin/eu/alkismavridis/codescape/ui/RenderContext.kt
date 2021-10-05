package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.MapArea
import java.awt.Graphics2D

class RenderContext(
  val scale: Double,
  val mapArea: MapArea,
  val g: Graphics2D,
  val loadChildren: (node: CodeScapeNode) -> Unit
)
