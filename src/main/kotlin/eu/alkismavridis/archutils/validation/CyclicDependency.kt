package eu.alkismavridis.archutils.validation

class CyclicDependency(val path: List<String>) {
  private val sortedByName = path.sorted()

  override fun equals(other: Any?): Boolean {
    if (other !is CyclicDependency) return false

    val pathSize = this.path.size
    if (pathSize != other.path.size) return false
    if (this.path.isEmpty()) return true

    val offset = other.path.indexOf(this.path[0])
    if (offset < 0) return false
    else if (offset == 0) return this.path == other.path
    else return this.path.indices.all { ind ->
      this.path[ind] == other.path[(ind + offset) % pathSize]
    }
  }

  override fun hashCode(): Int {
    return this.sortedByName.hashCode()
  }
}
