package eu.alkismavridis.archutils.integration.ui

import eu.alkismavridis.archutils.analysis.model.AnalysisRequest
import eu.alkismavridis.archutils.analysis.model.AnalysisResult
import org.jetbrains.projector.common.misc.toString
import java.awt.Color
import java.awt.Font
import java.awt.GridLayout
import javax.swing.*
import javax.swing.border.EmptyBorder

class ProjectResultView(private val request: AnalysisRequest, private val result: AnalysisResult): JPanel() {

  init {
    this.border = EmptyBorder(16, 16, 16, 16)
    this.layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
    this.alignmentX = LEFT_ALIGNMENT
    this.renderModules()
  }

  private fun renderModules() {
    this.removeAll()

    this.add(createLabel("Analysis config"))
    this.add(createConfigLabel("Path: ${this.request.projectRelativePath}", 4))
    this.add(createConfigLabel("Dependency rules: ${this.request.rules.name}", 4))
    this.add(createConfigLabel("Included suffixes: ${this.request.rules.includedSuffixes.joinToString(" ")}", 40))

    this.add(createLabel("Illegal Dependencies", error = this.result.illegalDependencies.isNotEmpty()))
    this.add(createIllegalDependenciesTable())

    this.add(createLabel("Cyclic Dependencies", error = this.result.cyclicDependencies.isNotEmpty()))
    this.add(createCyclicDependenciesTable())

    this.add(createLabel("File Statistics"))
    this.add(createFileDataTable(this.result))
    this.add(createLabel("File Dependency Statistics"))
    this.add(createFileDependencyDataTable(this.result))
    this.add(createLabel("Module Dependency Statistics"))
    this.add(createModuleDependencyDataTable(this.result))
  }

  private fun createIllegalDependenciesTable(): JComponent {
    val table = JPanel()
    table.alignmentX = LEFT_ALIGNMENT
    table.layout = GridLayout(result.illegalDependencies.size + 1, 2, 24, 8)
    table.border = EmptyBorder(0, 0, 40, 0)

    if (this.result.illegalDependencies.isEmpty()) {
      val okMessage = JLabel("None", SwingUtilities.LEFT)
      table.add(okMessage)
      return table
    }

    table.add(createBoldCell("From"))
    table.add(createBoldCell("To"))
    this.result.illegalDependencies.forEach {
      table.add(createCell(it.moduleFrom))
      table.add(createCell(it.moduleTo))
    }

    table.maximumSize = table.preferredSize
    return table
  }

  private fun createCyclicDependenciesTable(): JComponent {
    val cyclicDepPanel = JPanel()
    cyclicDepPanel.alignmentX = LEFT_ALIGNMENT
    cyclicDepPanel.layout = BoxLayout(cyclicDepPanel, BoxLayout.PAGE_AXIS)
    cyclicDepPanel.border = EmptyBorder(0, 0, 40, 0)

    if (this.result.cyclicDependencies.isEmpty()) {
      val okMessage = JLabel("None", SwingUtilities.LEFT)
      cyclicDepPanel.add(okMessage)
      return cyclicDepPanel
    }

    this.result.cyclicDependencies.forEach {
      val text = it.path.joinToString(" -> ") + " -> " + it.path.first()
      cyclicDepPanel.add(createCell(text))
    }

    cyclicDepPanel.maximumSize = cyclicDepPanel.preferredSize
    return cyclicDepPanel
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

  private fun createLabel(text: String, error: Boolean = false): JComponent {
    return createSelectableText(text).also {
      it.font = Font(it.font.fontName, Font.BOLD, 16)
      it.alignmentX = LEFT_ALIGNMENT
      it.border = EmptyBorder(0, 0, 8, 0)

      if (error) {
        it.foreground = Color.RED
      }
    }
  }

  private fun createConfigLabel(text: String, marginBottom: Int = 0): JComponent {
    return createLabel(text).also {
      it.font = Font(it.font.fontName, Font.PLAIN, 12)
      it.foreground = Color.GRAY
      it.border = EmptyBorder(0, 0, marginBottom, 0)
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
