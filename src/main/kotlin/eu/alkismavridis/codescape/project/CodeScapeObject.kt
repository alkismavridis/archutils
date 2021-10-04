package eu.alkismavridis.codescape.project

interface CodeScapeObject {
  fun getX()
  fun getY()
  fun getWidth()
  fun getHeight()
  fun getChildren(): List<CodeScapeObject>
}
