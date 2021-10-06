package eu.alkismavridis.codescape.integration

interface CodeScapeActionHandler {
  fun handleOpenFile(path: String)
  fun runReadOnlyTask( runnable: () -> Unit )
}
