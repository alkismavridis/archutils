package eu.alkismavridis.archutils.integration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import eu.alkismavridis.archutils.analysis.model.AnalysisConfiguration
import eu.alkismavridis.archutils.analysis.model.AnalysisRequest
import eu.alkismavridis.archutils.analysis.model.DependencyRules
import eu.alkismavridis.archutils.analysis.DependencyAnalysisService
import java.lang.IllegalArgumentException
import java.nio.file.Files
import java.nio.file.Paths

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val root = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val projectRelativePath = relativizeToProjectRoot(root.path, project)

    val configuration = this.getConfiguration(project)
    val rules = configuration.rules.find { it.path == projectRelativePath } ?: DependencyRules.allowAll()
    val analysisService = DependencyAnalysisService()
    val analysisRequest = AnalysisRequest(projectRelativePath, rules)
    val task = ProjectAnalysisTask(project, analysisRequest, root, analysisService)

    ProgressManager.getInstance().run(task)
  }

  private fun getConfiguration(project: Project): AnalysisConfiguration {
    val pathString = PropertiesComponent
      .getInstance(project)
      .getValue(SettingsConfigurable.STORAGE_KEY)
      ?.ifEmpty { null }
      ?: return AnalysisConfiguration()

    try {
      val path = getAbsolutePath(pathString, project)
      return ObjectMapper()
        .registerKotlinModule()
        .readValue(Files.newInputStream(path))
    } catch (e: Exception) {
      Messages.showMessageDialog(project, e.message, "Invalid Archutils Configuration ", Messages.getWarningIcon())
      return AnalysisConfiguration()
    }
  }
}

