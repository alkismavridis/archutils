package eu.alkismavridis.codescape.config


interface CodeScapeConfigurationService {
  fun getOptionsFor(projectPath: String): NodeOptions
  fun getRootNodePath(): String
  fun getColorPalette(): StyleConfiguration
  fun reload()
}
