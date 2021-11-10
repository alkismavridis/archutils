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
import eu.alkismavridis.archutils.project.AnalysisParameters
import eu.alkismavridis.archutils.project.ProjectAnalysisService
import java.nio.file.Files
import java.nio.file.Paths

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val root = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    val configuration = this.getConfiguration(project)
    val analysisService = ProjectAnalysisService(configuration)
    val task = ProjectAnalysisTask(project, root, analysisService)
    ProgressManager.getInstance().run(task)
  }

  private fun getConfiguration(project: Project): AnalysisParameters {
    val pathString = PropertiesComponent
      .getInstance(project)
      .getValue(SettingsConfigurable.STORAGE_KEY)
      ?.ifEmpty { null }
      ?: return AnalysisParameters()

    try {
      val path = Paths.get(pathString)
      return ObjectMapper()
        .registerKotlinModule()
        .readValue(Files.newInputStream(path))
    } catch (e: Exception) {
      Messages.showMessageDialog(project, e.message, "Invalid Archutils Configuration ", Messages.getWarningIcon())
      return AnalysisParameters()
    }
  }
}

