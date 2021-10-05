package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.layout.CodeScapeNode
import eu.alkismavridis.codescape.layout.MapArea
import org.jetbrains.rpc.LOG
import java.awt.Font
import java.awt.Graphics2D
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class RenderContext(
  val scale: Double,
  val mapArea: MapArea,
  val g: Graphics2D,
  val loadChildren: (node: CodeScapeNode) -> Unit
)
