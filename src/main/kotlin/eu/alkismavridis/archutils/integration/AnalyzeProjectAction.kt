package eu.alkismavridis.archutils.integration

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.application.Application
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ReadAction
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import com.intellij.psi.search.LocalSearchScope
import com.intellij.psi.search.scope.ProjectProductionScope
import com.intellij.ui.components.JBScrollPane
import eu.alkismavridis.archutils.project.ProjectAnalysisResult
import java.awt.Dimension

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val root = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return
    val rootPsi = PsiManager.getInstance(project).findDirectory(root) ?: return

    if (!rootPsi.isDirectory) {
      this.printWarningMessage(project)
      return
    }

    val result = ProjectAnalysisResult(root.path)
    val view = ProjectResultView(result)

    this.showPopup(view)
    this.startLoading(project, root, rootPsi, result, view)
  }

  private fun showPopup(view: ProjectResultView) {
    val scrollBar = JBScrollPane(view)
    JBPopupFactory.getInstance()
      .createComponentPopupBuilder(scrollBar, null)
      .setTitle("Project Analysis")
      .setResizable(true)
      .setMovable(true)
      .setMinSize(Dimension(500, 200))
      .createPopup()
      .showInFocusCenter()
  }

  private fun startLoading(project: Project, root: VirtualFile, rootPsi: PsiDirectory, result: ProjectAnalysisResult, view: ProjectResultView) {
    // TODO alkis run in separate thread
    val searchScope = GlobalSearchScopesCore.DirectoryScope(project, root, true)
    rootPsi.accept(ProjectAnalysingPsiVisitor(result, searchScope))

    view.onResultUpdated()
  }

  private fun printWarningMessage(project: Project) {
    Messages.showMessageDialog(project, "Please select a directory", "Warning", Messages.getWarningIcon())
  }
}

