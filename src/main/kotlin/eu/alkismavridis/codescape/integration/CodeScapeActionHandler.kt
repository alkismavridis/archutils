package eu.alkismavridis.codescape.integration

interface CodeScapeActionHandler {
  fun openLeafNode(nodeId: String)
  fun runReadOnlyTask( runnable: () -> Unit )
  fun handleReload()
  fun showNodeInViewer(nodeId: String)
}
