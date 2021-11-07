package eu.alkismavridis.archutils.integration

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import eu.alkismavridis.archutils.project.ProjectAnalysisResult

class ProjectAnalysingPsiVisitor(
  private val result: ProjectAnalysisResult,
  private val searchScope: SearchScope
): PsiRecursiveElementWalkingVisitor() {
  override fun visitFile(file: PsiFile) {
    if (file.language.id in INCLUDED_LANGUAGE_IDS) {
      this.addFileToResult(file)
    }
  }

  private fun addFileToResult(file: PsiFile) {
    val filePath = file.virtualFile.path
    val usagePaths = mutableSetOf<String>()

    file.children.forEach { decl ->
      ReferencesSearch.search(decl, this.searchScope)
        .asSequence()
        .map { ref -> ref.element.containingFile.virtualFile.path }
        .forEach { usagePaths.add(it) }
    }

    usagePaths.forEach { this.addUsageToResult(it, filePath) }
  }

  private fun addUsageToResult(usingFilePath: String, usedFilePath: String) {
    result.addDependency(usingFilePath, usedFilePath)
  }

  companion object {
    private val INCLUDED_LANGUAGE_IDS = listOf("JAVA", "kotlin")
  }
}
