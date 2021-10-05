package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.map.CodeScapeNode
import eu.alkismavridis.codescape.map.MapArea
import java.awt.Graphics2D
import java.awt.Image

class RenderContext(
  val scale: Double,
  val mapArea: MapArea,
  val g: Graphics2D,
  val loadChildren: (node: CodeScapeNode) -> Unit,
  val getImage: (path: String) -> Image,
)
