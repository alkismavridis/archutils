package eu.alkismavridis.archutils.integration.ui

import org.apache.commons.lang.StringEscapeUtils
import java.awt.Font
import javax.swing.Box
import javax.swing.BoxLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class ValueWithPercentCell(amount: Int, denominator: Int, tooltip: String? = null): JPanel() {
  init {
    this.toolTipText = tooltip?.let { StringEscapeUtils.escapeHtml(it) }
    this.layout = BoxLayout(this, BoxLayout.LINE_AXIS)
    this.add(createAmountLabel(amount), Box.LEFT_ALIGNMENT)
    if (amount != 0 && denominator != 0) {
      this.add(createPercentLabel(amount, denominator), Box.LEFT_ALIGNMENT)
    }
  }

  private fun createAmountLabel(amount: Int): JLabel {
    return JLabel(amount.toString())
  }

  private fun createPercentLabel(nominator: Int, denominator: Int): JLabel {
    val percent = "%.1f".format(100.0 * nominator / denominator)
    return JLabel("($percent%)").also {
      it.border = EmptyBorder(0, 8, 0, 0)
      it.font = Font(it.font.fontName, Font.PLAIN, 10)
    }
  }
}
