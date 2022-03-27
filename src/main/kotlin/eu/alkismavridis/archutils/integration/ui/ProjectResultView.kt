package eu.alkismavridis.archutils.integration.ui

import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.ui.components.JBScrollPane
import eu.alkismavridis.archutils.analysis.AnalysisResult
import java.awt.Dimension
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

  fun showInWindow() {
    val scrollBar = JBScrollPane(this)

    JBPopupFactory.getInstance()
      .createComponentPopupBuilder(scrollBar, null)
      .setTitle("Project Analysis")
      .setNormalWindowLevel(true)
      .setCancelOnWindowDeactivation(false)
      .setFocusable(true)
      .setRequestFocus(true)
      .setResizable(true)
      .setMovable(true)
      .setMinSize(Dimension(500, 200))
      .createPopup()
      .showInFocusCenter()
  }

  private fun renderModules() {
    this.removeAll()

    this.add(H2("Configuration", marginBottom = 8))
    this.add(createConfigurationTable())
    this.add(H2("Illegal Dependencies", error = this.result.illegalDependencies.isNotEmpty(), marginBottom = 8))
    this.add(createIllegalDependenciesTable())
    this.add(H2("Cyclic Dependencies", error = this.result.cyclicDependencies.isNotEmpty(), marginBottom = 8))
    this.add(createCyclicDependenciesTable())
    this.add(H2("Module Statistics", marginBottom = 8))
    this.add(createModuleDependencyDataTable(this.result))
    this.add(H2("File Statistics", marginBottom = 8))
    this.add(createFileDataTable(this.result))
    this.add(H2("Dependency Statistics", marginBottom = 8))
    this.add(createFileDependencyDataTable(this.result))
  }

  private fun createConfigurationTable(): JComponent {
    val table = JPanel()
    table.alignmentX = LEFT_ALIGNMENT
    table.layout = GridLayout(3, 2, 24, 8)
    table.border = EmptyBorder(0, 0, 40, 0)

    table.add(SimpleCell("Path:", bold = true))
    table.add(SimpleCell(this.result.projectRelativePath))

    table.add(SimpleCell("Rules:", bold = true))
    table.add(SimpleCell(this.result.config.name))

    table.add(SimpleCell("Included suffixes:", bold = true))
    table.add(SimpleCell(this.result.config.includedSuffixes.joinToString(" ")))

    table.maximumSize = table.preferredSize
    return table
  }

  private fun createIllegalDependenciesTable(): JComponent {
    val table = JPanel()
    table.alignmentX = LEFT_ALIGNMENT
    table.layout = GridLayout(result.illegalDependencies.size + 1, 2, 24, 8)
    table.border = EmptyBorder(0, 0, 40, 0)

    if (this.result.illegalDependencies.isEmpty()) {
      table.add(SimpleCell("None"))
      return table
    }

    table.add(SimpleCell("From", bold = true))
    table.add(SimpleCell("To", bold = true))
    this.result.illegalDependencies.forEach { dep ->
      table.add(SimpleCell(dep.moduleFrom))
      table.add(SimpleCell(dep.moduleTo))
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
      cyclicDepPanel.add(SimpleCell("None"))
      return cyclicDepPanel
    }

    this.result.cyclicDependencies.forEach { path ->
      val text = path.joinToString(" -> ") + " -> " + path.first()
      cyclicDepPanel.add(SimpleCell(text))
    }

    cyclicDepPanel.maximumSize = cyclicDepPanel.preferredSize
    return cyclicDepPanel
  }

  private fun createFileDataTable(result: AnalysisResult) : JPanel {
    val fileTable = JPanel()
    fileTable.alignmentX = LEFT_ALIGNMENT
    fileTable.border = EmptyBorder(0, 0, 40, 0)
    fileTable.layout = GridLayout(result.moduleStats.size + 1, 5, 8, 8)

    fileTable.add(SimpleCell("Module", bold = true))
    fileTable.add(SimpleCell("Number of Files", bold = true))
    fileTable.add(SimpleCell("Exposed Files", tooltip = "Files used outside the module", bold = true))
    fileTable.add(SimpleCell("Private Files", tooltip = "Files used only inside the module", bold = true))
    fileTable.add(SimpleCell("Internally used Files", tooltip = "Files used inside the module", bold = true))

    result.moduleStats.forEach{ mod ->
      fileTable.add(SimpleCell(mod.name))
      fileTable.add(SimpleCell(mod.files.toString()))
      fileTable.add(ValueWithPercentCell(mod.externallyUsedFiles, mod.files))
      fileTable.add(ValueWithPercentCell(mod.files - mod.externallyUsedFiles, mod.files))
      fileTable.add(ValueWithPercentCell(mod.internallyUsedFiles, mod.files))
    }

    fileTable.maximumSize = fileTable.preferredSize
    return fileTable
  }

  private fun createFileDependencyDataTable(result: AnalysisResult) : JPanel {
    val dependencyTable = JPanel()
    dependencyTable.alignmentX = LEFT_ALIGNMENT
    dependencyTable.layout = GridLayout(result.moduleStats.size + 1, 7, 8, 8)

    dependencyTable.add(SimpleCell("Module", bold = true))
    dependencyTable.add(SimpleCell("Internal Dependencies", bold = true))
    dependencyTable.add(SimpleCell("External Dependencies", bold = true))
    dependencyTable.add(SimpleCell("Total", bold = true))
    dependencyTable.add(SimpleCell("External Usages", tooltip = "Number of Dependencies from the outside to the inside", bold = true))
    dependencyTable.add(SimpleCell("External Traffic", tooltip = "External Dependencies + External Usages", bold = true))
    dependencyTable.add(SimpleCell("Instability Factor", tooltip = "External Dependencies / External Traffic", bold = true))

    result.moduleStats.forEach{ mod ->
      val dependencyCount = mod.internalDependencies + mod.dependenciesComingIn
      val externalTraffic = mod.dependenciesGoingOut + mod.dependenciesComingIn

      dependencyTable.add(SimpleCell(mod.name))
      dependencyTable.add(ValueWithPercentCell(mod.internalDependencies, dependencyCount))
      dependencyTable.add(ValueWithPercentCell(mod.dependenciesComingIn, dependencyCount))
      dependencyTable.add(SimpleCell(dependencyCount.toString()))
      dependencyTable.add(SimpleCell(mod.dependenciesGoingOut.toString()))
      dependencyTable.add(SimpleCell(externalTraffic.toString()))
      dependencyTable.add(RatioCell(mod.dependenciesComingIn, externalTraffic))
    }

    dependencyTable.maximumSize = dependencyTable.preferredSize
    return dependencyTable
  }

  private fun createModuleDependencyDataTable(result: AnalysisResult) : JPanel {
    val dependencyTable = JPanel()
    dependencyTable.alignmentX = LEFT_ALIGNMENT
    dependencyTable.border = EmptyBorder(0, 0, 40, 0)
    dependencyTable.layout = GridLayout(result.moduleStats.size + 1, 4, 8, 8)

    dependencyTable.add(SimpleCell("Module", bold = true))
    dependencyTable.add(SimpleCell("Used by Modules", bold = true))
    dependencyTable.add(SimpleCell("Uses Modules", bold = true))
    dependencyTable.add(SimpleCell("Total", bold = true))

    result.moduleStats.forEach{ mod ->
      val allDependencies = mod.usedByModules.size + mod.usesModules.size
      val usedByTooltip = mod.usedByModules.joinToString(", ")
      val usesTooltip = mod.usesModules.joinToString(", ")

      dependencyTable.add(SimpleCell(mod.name, bold = true))
      dependencyTable.add(ValueWithPercentCell(mod.usedByModules.size, allDependencies, usedByTooltip))
      dependencyTable.add(ValueWithPercentCell(mod.usesModules.size, allDependencies, usesTooltip))
      dependencyTable.add(SimpleCell(allDependencies.toString()))
    }

    dependencyTable.maximumSize = dependencyTable.preferredSize
    return dependencyTable
  }
}
