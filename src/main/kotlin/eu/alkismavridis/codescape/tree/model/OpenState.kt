package eu.alkismavridis.codescape.tree.model

enum class OpenState(val isOpen: Boolean) {
  OPEN(true),
  EXPLICITLY_OPEN(true),
  CLOSED(false),
  EXPLICITLY_CLOSED(false),
}
