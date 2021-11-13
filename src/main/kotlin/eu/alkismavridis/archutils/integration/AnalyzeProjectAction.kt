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
import eu.alkismavridis.archutils.analysis.ProjectAnalysisService
import eu.alkismavridis.archutils.analysis.config.ArchutilsConfiguration
import eu.alkismavridis.archutils.analysis.config.PathConfiguration
import eu.alkismavridis.archutils.garbage.integration.getAbsolutePath
import eu.alkismavridis.archutils.garbage.integration.relativizeToProjectRoot
import eu.alkismavridis.archutils.validation.DependencyValidationService
import eu.alkismavridis.archutils.validation.CyclicDependencyService
import java.nio.file.Files

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val rootDir = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    val configuration = this.getConfiguration(project)
    val analysisService = createAnalysisService(configuration)
    val task = ProjectAnalysisTask(project, rootDir, analysisService)

    ProgressManager.getInstance().run(task)
  }


  private fun createAnalysisService(configuration: ArchutilsConfiguration): ProjectAnalysisService {
    val validationService = DependencyValidationService()
    val cyclicDependencyService = CyclicDependencyService()
    return ProjectAnalysisService(configuration, validationService, cyclicDependencyService)
  }

  private fun getConfiguration(project: Project): ArchutilsConfiguration {
    val pathString = PropertiesComponent
      .getInstance(project)
      .getValue(SettingsConfigurable.STORAGE_KEY)
      ?.ifEmpty { null }
      ?: return ArchutilsConfiguration()

    try {
      val path = getAbsolutePath(pathString, project)
      return ObjectMapper()
        .registerKotlinModule()
        .readValue(Files.newInputStream(path))
    } catch (e: Exception) {
      Messages.showMessageDialog(project, e.message, "Invalid Archutils Configuration ", Messages.getWarningIcon())
      return ArchutilsConfiguration()
    }
  }
}

