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
    moduleTable.layout = GridLayout(modules.size + 1, 11, 8, 8)

    moduleTable.add(createBoldCell("Name"))
    moduleTable.add(createBoldCell("Files"))
    moduleTable.add(createBoldCell("Exposed files", "Files used outside the module"))
    moduleTable.add(createBoldCell("Private files", "Files used only inside the module"))
    moduleTable.add(createBoldCell("Internally used files", "Files used inside the module"))
    moduleTable.add(createBoldCell("Cohesion factor", "Internal dependencies / Internal+Outgoing"))
    moduleTable.add(createBoldCell("Instability factor", "Outgoing dependencies / Cross Module Dependencies"))
    moduleTable.add(createBoldCell("Incoming Deps."))
    moduleTable.add(createBoldCell("Outgoing Deps."))
    moduleTable.add(createBoldCell("Internal Deps."))
    moduleTable.add(createBoldCell("Cross-module", "Outgoing dependencies + Incoming dependencies"))

    modules.forEach{
      val totalDependencies = it.internalDependencies + it.incomingDependencies + it.outgoingDependencies
      val crossModuleDependencies = it.incomingDependencies + it.outgoingDependencies

      moduleTable.add(createBoldCell(it.name))
      moduleTable.add(createCell(it.files.toString()))
      moduleTable.add(createValueAndPercentCell(it.externallyUsedFiles, it.files))
      moduleTable.add(createValueAndPercentCell(it.files - it.externallyUsedFiles, it.files))
      moduleTable.add(createValueAndPercentCell(it.internallyUsedFiles, it.files))
      moduleTable.add(getRatioCell(it.internalDependencies, it.outgoingDependencies + it.internalDependencies))
      moduleTable.add(getRatioCell(it.outgoingDependencies, crossModuleDependencies))
      moduleTable.add(createValueAndPercentCell(it.incomingDependencies, totalDependencies))
      moduleTable.add(createValueAndPercentCell(it.outgoingDependencies, totalDependencies))
      moduleTable.add(createValueAndPercentCell(it.internalDependencies, totalDependencies))
      moduleTable.add(createValueAndPercentCell(crossModuleDependencies, totalDependencies))
    }
  }

  private fun createBoldCell(text: String, tooltip: String? = null): JLabel {
    return JLabel(text).also {
      it.font = it.font.deriveFont(it.font.style or Font.BOLD)
      it.toolTipText = tooltip
    }
  }

  private fun createCell(text: String): JLabel {
    return JLabel(text)
  }

  private fun createValueAndPercentCell(amount: Int, denominator: Int): JLabel {
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

  private fun getRatioCell(nominator: Int, denominator: Int): JLabel = if (denominator == 0) {
    JLabel("---")
  } else {
    JLabel((nominator.toDouble() / denominator).toString(1))
  }
}
