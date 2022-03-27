package eu.alkismavridis.archutils.validation


fun <T: Comparable<T>> List<T>.rotateMinimumToStart(): List<T> {
  if (this.isEmpty()) return this
  val indexOfMin = this.asSequence()
    .withIndex()
    .minByOrNull { it.value }
    ?.index
    ?: -1

  if (indexOfMin == 0) return this
  return this.subList(indexOfMin, this.size) + this.subList(0, indexOfMin)
}
