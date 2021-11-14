package eu.alkismavridis.archutils.integration.ui

import java.awt.Color
import java.awt.Font
import javax.swing.JLabel
import javax.swing.border.EmptyBorder

class H2(text: String, error: Boolean = false, marginBottom: Int = 0) : JLabel() {
  init {
    this.text = text
    this.font = Font(this.font.fontName, Font.BOLD, 16)
    this.alignmentX = LEFT_ALIGNMENT
    this.border = EmptyBorder(0, 0, marginBottom, 0)

    if (error) {
      this.foreground = Color.RED
    }
  }
}
