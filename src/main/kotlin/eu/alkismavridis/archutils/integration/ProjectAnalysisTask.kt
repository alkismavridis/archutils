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
import eu.alkismavridis.archutils.project.ModuleStatsBuilder
import java.awt.Dimension

class ProjectAnalysisTask(
  project: Project,
  private val rootDirectory: VirtualFile,
) : Task.Modal(project, "Analyzing Dependencies", true) {
  private val builder = ModuleStatsBuilder(rootDirectory.path)

  override fun run(indicator: ProgressIndicator) {
    thisLogger().info("Analysis starts for ${rootDirectory.path}")

    ApplicationManager.getApplication().runReadAction{
      val rootPsi = PsiManager.getInstance(project).findDirectory(rootDirectory) ?: return@runReadAction
      val searchScope = GlobalSearchScopesCore.DirectoryScope(project, rootDirectory, true)
      rootPsi.accept(ProjectAnalysingPsiVisitor(this.builder, searchScope))
    }
  }

  override fun onSuccess() {
    val modules = this.builder.build()
    val view = ProjectResultView(modules)
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
