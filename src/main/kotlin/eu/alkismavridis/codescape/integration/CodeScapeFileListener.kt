package eu.alkismavridis.codescape.integration

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class CodeScapeFileListener(private val project: Project, private val onDetectChange: () -> Unit) : BulkFileListener {

  override fun after(events: MutableList<out VFileEvent>) {
    if (this.project.isDisposed) return

    val hasStructuralChanges = events.any(this::isStructuralChange)
    if (hasStructuralChanges) {
      this.onDetectChange()
    }
  }

  private fun isStructuralChange(event: VFileEvent): Boolean {
    return !event.isFromRefresh && !event.isFromSave
  }
}
