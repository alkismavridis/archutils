package eu.alkismavridis.archutils.integration.ui

import org.apache.commons.lang.StringEscapeUtils.escapeHtml
import java.awt.Font
import javax.swing.JLabel

class SimpleCell(text: String, tooltip: String? = null, bold: Boolean = false) : JLabel() {
  init {
    this.text = text
    this.toolTipText = tooltip?.let { escapeHtml(it) }
    if (bold) {
      this.font = this.font.deriveFont(this.font.style or Font.BOLD)

    }
  }
}
