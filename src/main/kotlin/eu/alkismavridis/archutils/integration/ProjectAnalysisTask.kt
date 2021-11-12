package eu.alkismavridis.archutils.integration

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
import eu.alkismavridis.archutils.analysis.*
import eu.alkismavridis.archutils.analysis.model.AnalysisRequest
import eu.alkismavridis.archutils.analysis.model.AnalysisResult
import eu.alkismavridis.archutils.analysis.model.ModuleStats
import eu.alkismavridis.archutils.cycles.CyclicDependencyService
import eu.alkismavridis.archutils.integration.ui.ProjectResultView
import java.awt.Dimension
import java.io.FileNotFoundException

class ProjectAnalysisTask(
  project: Project,
  private val request: AnalysisRequest,
  private val rootDirectory: VirtualFile,
  private val analysisService: DependencyAnalysisService,
  private val cyclicDependencyService: CyclicDependencyService,
) : Task.Modal(project, "Analyzing Dependencies", true) {
  private var result: AnalysisResult? = null

  override fun run(indicator: ProgressIndicator) {
    thisLogger().info("Analysis starts for ${rootDirectory.path}")

    ApplicationManager.getApplication().runReadAction {
      this.title = "Collecting dependencies..."
      val moduleData = this.getModuleData()

      this.title = "Analysing dependencies..."
      val illegalDependencies = this.analysisService.findIllegalDependencies(request, moduleData)

      this.title = "Searching for cyclic dependencies..."
      val cyclicDependencies = this.cyclicDependencyService.detectCycles(moduleData)
      this.result = AnalysisResult(moduleData, illegalDependencies, cyclicDependencies)
    }
  }

  private fun getModuleData(): List<ModuleStats> {
    val rootPsi = PsiManager.getInstance(project).findDirectory(rootDirectory) ?: throw FileNotFoundException("Directory $rootDirectory not found")
    val searchScope = GlobalSearchScopesCore.DirectoryScope(project, rootDirectory, true)

    val builder = ModuleStatsBuilder(rootDirectory.path)
    rootPsi.accept(ProjectAnalysingPsiVisitor(builder, searchScope))

    return builder.build()
  }

  override fun onSuccess() {
    val result = this.result ?: return
    val view = ProjectResultView(this.request, result)
    val scrollBar = JBScrollPane(view)

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
}
