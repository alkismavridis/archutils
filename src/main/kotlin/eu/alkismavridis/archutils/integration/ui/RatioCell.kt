package eu.alkismavridis.archutils.integration.ui

import javax.swing.JLabel

class RatioCell(nominator: Int, denominator: Int) : JLabel() {
  init {
    if (denominator == 0) {
      this.text = "---"
    } else {
      this.text = "%.1f".format(nominator.toDouble() / denominator)
    }
  }
}
