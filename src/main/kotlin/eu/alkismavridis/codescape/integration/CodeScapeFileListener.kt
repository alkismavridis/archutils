package eu.alkismavridis.codescape.integration

import com.intellij.openapi.vfs.newvfs.BulkFileListener
import com.intellij.openapi.vfs.newvfs.events.VFileEvent

class CodeScapeFileListener(private val onDetectChange: () -> Unit) : BulkFileListener {

  override fun after(events: MutableList<out VFileEvent>) {
    val hasStructuralChanges = events.any(this::isStructuralChange)
    if (hasStructuralChanges) {
      this.onDetectChange()
    }
  }

  private fun isStructuralChange(event: VFileEvent): Boolean {
    return !event.isFromRefresh && !event.isFromSave
  }
}
