package eu.alkismavridis.archutils.integration

import eu.alkismavridis.archutils.project.ProjectAnalysisResult
import org.jetbrains.projector.common.misc.toString
import java.awt.Font
import java.awt.GridLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.border.EmptyBorder

class ProjectResultView(private val result: ProjectAnalysisResult): JPanel() {
  private val moduleTable: JPanel = JPanel()

  init {
    this.border = EmptyBorder(16, 16, 16, 16)
    this.moduleTable.add(JLabel("Loading..."))
    this.add(this.moduleTable)
  }

  fun onResultUpdated() {
    this.renderModuleTable()
  }

  private fun renderModuleTable() {
    this.moduleTable.removeAll()
    val modules = this.result.getModules()
    moduleTable.layout = GridLayout(modules.size + 1, 7, 8, 8)

    moduleTable.add(createBoldCell("Name"))
    moduleTable.add(createBoldCell("Cohesion factor"))
    moduleTable.add(createBoldCell("Instability factor"))
    moduleTable.add(createBoldCell("Incoming"))
    moduleTable.add(createBoldCell("Outgoing"))
    moduleTable.add(createBoldCell("Internal"))
    moduleTable.add(createBoldCell("Cross-module"))

    modules.forEach{
      val totalDependencies = it.internalDependencyCount + it.incomingDependencyCount + it.outgoingDependencyCount
      val crossModuleDependencies = it.incomingDependencyCount + it.outgoingDependencyCount

      moduleTable.add(createBoldCell(it.name))
      moduleTable.add(createCell(getRatioString(it.internalDependencyCount, it.outgoingDependencyCount + it.internalDependencyCount)))
      moduleTable.add(createCell(getRatioString(it.outgoingDependencyCount, crossModuleDependencies)))
      moduleTable.add(createRatioCell(it.incomingDependencyCount, totalDependencies))
      moduleTable.add(createRatioCell(it.outgoingDependencyCount, totalDependencies))
      moduleTable.add(createRatioCell(it.internalDependencyCount, totalDependencies))
      moduleTable.add(createRatioCell(crossModuleDependencies, totalDependencies))
    }
  }

  private fun createBoldCell(text: String): JLabel {
    return JLabel(text).also {
      it.font = it.font.deriveFont(it.font.style or Font.BOLD)
    }
  }

  private fun createCell(text: String): JLabel {
    return JLabel(text)
  }

  private fun createRatioCell(amount: Int, denominator: Int): JLabel {
    if (amount == 0) {
      return JLabel("0")
    }

    return JLabel("$amount (${getPercentString(amount, denominator)}%)")
  }

  private fun getPercentString(nominator: Int, denominator: Int): String = if (denominator == 0) {
    "---"
  } else {
    (100.0 * nominator / denominator).toString(1)
  }

  private fun getRatioString(nominator: Int, denominator: Int): String = if (denominator == 0) {
    "---"
  } else {
    (nominator.toDouble() / denominator).toString(1)
  }
}
