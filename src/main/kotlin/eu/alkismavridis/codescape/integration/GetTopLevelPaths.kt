package eu.alkismavridis.codescape.integration


fun getTopLevelPaths(paths: Collection<String>): Collection<String> {
  return paths.filter { !isChildOfPaths(paths, it) }
}

private fun isChildOfPaths(paths: Collection<String>, pathToTest: String): Boolean {
  return paths.any {
    it != pathToTest && pathToTest.startsWith(it.withSlashSuffix())
  }
}

private fun String.withSlashSuffix(): String {
  return if(this.endsWith("/")) this else "$this/"
}
