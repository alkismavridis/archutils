package eu.alkismavridis.codescape.config


interface CodeScapeConfigurationService {
  fun getOptionsFor(absolutePath: String): NodeOptions
}
