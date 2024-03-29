package eu.alkismavridis.archutils.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.ui.components.JBScrollPane
import eu.alkismavridis.archutils.analysis.AnalysisResult
import eu.alkismavridis.archutils.analysis.ProjectAnalysisService
import eu.alkismavridis.archutils.analysis.config.ArchutilsConfiguration
import eu.alkismavridis.archutils.integration.io.IoService
import eu.alkismavridis.archutils.integration.psi.ProjectAnalysingPsiVisitor
import eu.alkismavridis.archutils.integration.ui.ProjectResultView
import eu.alkismavridis.archutils.modules.ModuleStatsBuilder
import java.awt.Dimension
import java.io.FileNotFoundException

class ProjectAnalysisTask(
  project: Project,
  private val rootDirectory: VirtualFile,
  private val projectRelativePath: String,
  private val analysisService: ProjectAnalysisService,
  private val ioService: IoService,
  private val configuration: ArchutilsConfiguration,
): Task.Modal(project, "Analyzing Dependencies", true) {
  private var result: AnalysisResult? = null

  override fun run(indicator: ProgressIndicator) {
    thisLogger().info("Analysis starts for $projectRelativePath")
    ApplicationManager.getApplication().runReadAction {
      this.result = analysisService.analyseProject(projectRelativePath, rootDirectory.path, ::buildDependencies) {
        this.title = it
      }
    }
  }

  private fun buildDependencies(builder: ModuleStatsBuilder) {
    val rootPsi = PsiManager.getInstance(project).findDirectory(rootDirectory)
      ?: throw FileNotFoundException("Directory ${rootDirectory.path} not found")

    val searchScope = GlobalSearchScopesCore.DirectoryScope(project, rootDirectory, true)
    rootPsi.accept(ProjectAnalysingPsiVisitor(builder, searchScope))
  }

  override fun onSuccess() {
    val result = this.result ?: return
    this.outputToFile(result)
    ProjectResultView(result).showInWindow()
  }

  private fun outputToFile(result: AnalysisResult) {
    if (configuration.outputFile.isNotEmpty()) {
      val contents = MAPPER.writeValueAsString(result)
      this.ioService.writeToFile(configuration.outputFile, contents)
    }
  }

  companion object {
    private val MAPPER = ObjectMapper().registerKotlinModule()
  }
}
