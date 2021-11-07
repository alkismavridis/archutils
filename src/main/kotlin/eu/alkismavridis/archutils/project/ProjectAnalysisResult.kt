package eu.alkismavridis.archutils.integration

class ProjectAnalysisResult() {
  private var rootPackage = ""
  private val foo = mutableListOf<String>()


  fun fooIt(str: String) {
    this.foo.add(str)
  }

  fun getFoo() = this.foo.joinToString(", ")
}
