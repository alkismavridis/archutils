package eu.alkismavridis.archutils.integration.ui

import javax.swing.JLabel

class ValueWithPercentCell(amount: Int, denominator: Int, tooltip: String? = null) : JLabel() {
  init {
    this.toolTipText = tooltip
    this.text = this.getText(amount, denominator)
  }

  private fun getText(nominator: Int, denominator: Int): String {
    if (nominator == 0) {
      return "0"
    } else if (denominator == 0) {
      return nominator.toString()
    } else {
      val percent = "%.1f".format(100.0 * nominator / denominator)
      return "$nominator ($percent%)"
    }
  }
}
