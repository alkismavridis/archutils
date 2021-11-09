package eu.alkismavridis.archutils.integration

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.progress.ProgressManager

class AnalyzeProjectAction: AnAction() {

  override fun actionPerformed(e: AnActionEvent) {
    val project = e.project ?: return
    val root = e.getData(CommonDataKeys.VIRTUAL_FILE) ?: return

    val task = ProjectAnalysisTask(project, root)
    ProgressManager.getInstance().run(task)
  }
}

