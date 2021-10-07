package eu.alkismavridis.codescape.integration

interface CodeScapeActionHandler {
  fun handleNodeClick(nodeId: String)
  fun runReadOnlyTask( runnable: () -> Unit )
}
