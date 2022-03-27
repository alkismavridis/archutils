package eu.alkismavridis.archutils.integration

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager
import eu.alkismavridis.archutils.analysis.ProjectAnalysisService
import eu.alkismavridis.archutils.analysis.config.ArchutilsConfiguration
import eu.alkismavridis.archutils.integration.io.IoService
import eu.alkismavridis.archutils.validation.DependencyValidationService
import eu.alkismavridis.archutils.validation.CyclicDependencyService

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val rootDir = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    val ioService = IoService(project)
    val configuration = ioService.loadConfiguration()
    val analysisService = createAnalysisService(configuration)
    val projectRelativePath = ioService.relativizeToProjectRoot(rootDir.path)
    val task = ProjectAnalysisTask(project, rootDir, projectRelativePath, analysisService, ioService, configuration)

    ProgressManager.getInstance().run(task)
  }


  private fun createAnalysisService(configuration: ArchutilsConfiguration): ProjectAnalysisService {
    val validationService = DependencyValidationService()
    val cyclicDependencyService = CyclicDependencyService()
    return ProjectAnalysisService(configuration, validationService, cyclicDependencyService)
  }
}

