package eu.alkismavridis.archutils.integration

import eu.alkismavridis.archutils.project.AnalysisResult
import org.jetbrains.projector.common.misc.toString
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

class ProjectResultView(private val result: AnalysisResult): JPanel() {

  init {
    this.border = EmptyBorder(16, 16, 16, 16)
    this.layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
    this.alignmentX = LEFT_ALIGNMENT
    this.renderModules()
  }

  private fun renderModules() {
    this.removeAll()

    this.add(createConfigLabel())
    this.add(createLabel("File Statistics"))
    this.add(createFileDataTable(this.result))
    this.add(createLabel("File Dependency Statistics"))
    this.add(createFileDependencyDataTable(this.result))
    this.add(createLabel("Module Dependency Statistics"))
    this.add(createModuleDependencyDataTable(this.result))
  }

  private fun createFileDataTable(result: AnalysisResult) : JPanel {
    val fileTable = JPanel()
    fileTable.alignmentX = LEFT_ALIGNMENT
    fileTable.border = EmptyBorder(0, 0, 40, 0)
    fileTable.layout = GridLayout(result.moduleStats.size + 1, 5, 8, 8)

    fileTable.add(createBoldCell("Module"))
    fileTable.add(createBoldCell("Number of Files"))
    fileTable.add(createBoldCell("Exposed Files", "Files used outside the module"))
    fileTable.add(createBoldCell("Private Files", "Files used only inside the module"))
    fileTable.add(createBoldCell("Internally used Files", "Files used inside the module"))

    result.moduleStats.forEach{
      fileTable.add(createBoldCell(it.name))
      fileTable.add(createCell(it.files.toString()))
      fileTable.add(createValueAndPercentCell(it.externallyUsedFiles, it.files))
      fileTable.add(createValueAndPercentCell(it.files - it.externallyUsedFiles, it.files))
      fileTable.add(createValueAndPercentCell(it.internallyUsedFiles, it.files))
    }

    fileTable.maximumSize = fileTable.preferredSize
    return fileTable
  }

  private fun createFileDependencyDataTable(result: AnalysisResult) : JPanel {
    val dependencyTable = JPanel()
    dependencyTable.alignmentX = LEFT_ALIGNMENT
    dependencyTable.border = EmptyBorder(0, 0, 40, 0)
    dependencyTable.layout = GridLayout(result.moduleStats.size + 1, 7, 8, 8)

    dependencyTable.add(createBoldCell("Module"))
    dependencyTable.add(createBoldCell("All Dep."))
    dependencyTable.add(createBoldCell("Internal Dep."))
    dependencyTable.add(createBoldCell("External Dep."))
    dependencyTable.add(createBoldCell("External Usages", "Number of Dependencies from the outside to the inside"))
    dependencyTable.add(createBoldCell("External Traffic", "External Dependencies + External Usages"))
    dependencyTable.add(createBoldCell("Instability Factor", "External Dependencies / External Traffic"))

    result.moduleStats.forEach{
      val dependencyCount = it.internalDependencies + it.dependenciesComingIn
      val externalTraffic = it.dependenciesGoingOut + it.dependenciesComingIn

      dependencyTable.add(createBoldCell(it.name))
      dependencyTable.add(createCell(dependencyCount.toString()))
      dependencyTable.add(createValueAndPercentCell(it.internalDependencies, dependencyCount))
      dependencyTable.add(createValueAndPercentCell(it.dependenciesComingIn, dependencyCount))
      dependencyTable.add(createCell(it.dependenciesGoingOut.toString()))
      dependencyTable.add(createCell(externalTraffic.toString()))
      dependencyTable.add(createRatioCell(it.dependenciesComingIn, externalTraffic))
    }

    dependencyTable.maximumSize = dependencyTable.preferredSize
    return dependencyTable
  }

  private fun createModuleDependencyDataTable(result: AnalysisResult) : JPanel {
    val dependencyTable = JPanel()
    dependencyTable.alignmentX = LEFT_ALIGNMENT
    dependencyTable.layout = GridLayout(result.moduleStats.size + 1, 4, 8, 8)

    dependencyTable.add(createBoldCell("Module"))
    dependencyTable.add(createBoldCell("All Dependencies"))
    dependencyTable.add(createBoldCell("Used by Modules"))
    dependencyTable.add(createBoldCell("Uses Modules"))

    result.moduleStats.forEach{
      val allDependencies = it.usedByModules.size + it.usesModules.size

      dependencyTable.add(createBoldCell(it.name))
      dependencyTable.add(createCell(allDependencies.toString()))
      dependencyTable.add(createValueAndPercentCell(it.usedByModules.size, allDependencies, it.usedByModules.joinToString(", ")))
      dependencyTable.add(createValueAndPercentCell(it.usesModules.size, allDependencies, it.usesModules.joinToString(", ")))
    }

    dependencyTable.maximumSize = dependencyTable.preferredSize
    return dependencyTable
  }

  private fun createBoldCell(text: String, tooltip: String? = null): JComponent {
    return createSelectableText(text).also {
      it.font = it.font.deriveFont(it.font.style or Font.BOLD)
      it.toolTipText = tooltip
    }
  }

  private fun createCell(text: String): JComponent {
    return createSelectableText(text)
  }

  private fun createValueAndPercentCell(amount: Int, denominator: Int, tooltip: String? = null): JComponent {
    if (amount == 0) {
      return createSelectableText("0")
    }

    return createSelectableText("$amount (${getPercentString(amount, denominator)}%)", tooltip)
  }

  private fun getPercentString(nominator: Int, denominator: Int): String = if (denominator == 0) {
    "---"
  } else {
    (100.0 * nominator / denominator).toString(1)
  }

  private fun createRatioCell(nominator: Int, denominator: Int): JComponent = if (denominator == 0) {
    createSelectableText("---")
  } else {
    createSelectableText((nominator.toDouble() / denominator).toString(1))
  }

  private fun createLabel(text: String): JComponent {
    return createSelectableText(text).also {
      it.font = Font(it.font.fontName, Font.BOLD, 16)
      it.alignmentX = LEFT_ALIGNMENT
      it.border = EmptyBorder(0, 0, 8, 0)
    }
  }

  private fun createConfigLabel(): JComponent {
    return createLabel("Configuration: ${this.result.analysisParams.name}").also {
      it.font = Font(it.font.fontName, Font.PLAIN, 12)
      it.foreground = Color.GRAY
    }
  }

  private fun createSelectableText(text: String, tooltip: String? = null): JLabel {
    return JLabel().also {
      it.text = text
      it.background = null
      it.border = null
      it.toolTipText = tooltip
    }
  }
}
