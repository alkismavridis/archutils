package eu.alkismavridis.archutils.integration

import com.intellij.psi.PsiFile
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.search.SearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import eu.alkismavridis.archutils.modules.ModuleStatsBuilder

class ProjectAnalysingPsiVisitor(
  private val builder: ModuleStatsBuilder,
  private val searchScope: SearchScope
): PsiRecursiveElementWalkingVisitor() {
  override fun visitFile(file: PsiFile) {
    if (this.builder.accepts(file.name)) {
      this.addFileToResult(file)
    }
  }

  private fun addFileToResult(file: PsiFile) {
    val usages = getFilesUsing(file)
    val filePath = file.virtualFile.path
    builder.addFile(filePath, usages)
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
