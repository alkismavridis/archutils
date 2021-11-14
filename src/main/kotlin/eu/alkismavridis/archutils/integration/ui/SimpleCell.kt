package eu.alkismavridis.archutils.integration.ui

import java.awt.Font
import javax.swing.JLabel

class SimpleCell(text: String, tooltip: String? = null, bold: Boolean = false) : JLabel() {
  init {
    this.toolTipText = tooltip
    this.text = text
    this.toolTipText = tooltip
    if (bold) {
      this.font = this.font.deriveFont(this.font.style or Font.BOLD)

    }
  }
}
