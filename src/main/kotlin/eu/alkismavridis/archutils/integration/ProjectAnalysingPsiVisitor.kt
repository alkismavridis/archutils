package eu.alkismavridis.archutils.integration

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import eu.alkismavridis.archutils.analysis.ModuleStatsBuilder

class ProjectAnalysingPsiVisitor(
  private val result: ModuleStatsBuilder,
  private val whitelistedSuffixes: Set<String>,
  private val searchScope: SearchScope
): PsiRecursiveElementWalkingVisitor() {
  override fun visitFile(file: PsiFile) {
    if (this.isWhiteListed(file)) {
      this.addFileToResult(file)
    }
  }

  private fun isWhiteListed(file: PsiFile) : Boolean {
    return this.whitelistedSuffixes.contains("*") ||
      this.whitelistedSuffixes.any { file.name.endsWith(it) }
  }

  private fun addFileToResult(file: PsiFile) {
    val usages = getFilesUsing(file)
    val filePath = file.virtualFile.path
    result.addFile(filePath, usages)
  }

  private fun getFilesUsing(file: PsiFile) : Set<String> {
    val referencesToFile = ReferencesSearch.search(file, this.searchScope).asSequence()
    val referencesToDeclarationsOfFile = file.children
      .asSequence()
      .flatMap { ReferencesSearch.search(it, this.searchScope) }
    val allReferences = referencesToFile + referencesToDeclarationsOfFile

    return allReferences
      .map{ it.element.containingFile.virtualFile.path }
      .toSet()
  }
}
