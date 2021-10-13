package eu.alkismavridis.codescape.ui

import eu.alkismavridis.codescape.integration.CodeScapeActionHandler
import eu.alkismavridis.codescape.tree.TreeDataService
import eu.alkismavridis.codescape.tree.model.CodeScapeNode
import javax.swing.JComponent
import javax.swing.JMenuItem
import javax.swing.JPopupMenu

class CodeScapeMenu(
  private val node: CodeScapeNode?,
  private val actionHandler: CodeScapeActionHandler,
  private val treeDataService: TreeDataService,
  private val onUpdate: () -> Unit,
): JPopupMenu() {
  init {
    this.createItems().forEach {
      this.add(it)
    }
  }

  private fun createItems(): List<JComponent> {
    return listOfNotNull(
      createItem("Refresh", this.actionHandler::handleReload),

      createOptionalItem("Show in project") { this.actionHandler.showNodeInViewer(it.id)},

      if (this.node?.openState?.isOpen != true) null else {
        createOptionalItem("Close") { this.treeDataService.closeNode(it, true, this.onUpdate) }
      }
    )
  }

  private fun createOptionalItem(label: String, action: (CodeScapeNode) -> Unit): JMenuItem? {
    if (this.node == null) return null

    return JMenuItem(label).also {
      it.addActionListener{ action(this.node) }
    }
  }

  private fun createItem(label: String, action: () -> Unit): JMenuItem {
    return JMenuItem(label).also {
      it.addActionListener{ action() }
    }
  }
}
