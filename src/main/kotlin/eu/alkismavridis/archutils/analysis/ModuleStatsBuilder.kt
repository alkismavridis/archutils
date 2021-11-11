package eu.alkismavridis.archutils.analysis

import com.intellij.openapi.diagnostic.thisLogger
import eu.alkismavridis.archutils.analysis.model.ModuleStats

class ModuleStatsBuilder(rootPackage: String) {
  private val rootPath = rootPackage
    .replace("\\", "/")
    .let { if(it.endsWith("/")) it else "$it/" }

  private val moduleMap = mutableMapOf<String, MutableModuleStats>()

  fun build(): List<ModuleStats> {
    return this.moduleMap.values.sortedBy { it.name }
  }

  fun addFile(currentFile: String, filesUsingCurrent: Collection<String>): ModuleStatsBuilder {
    val moduleName = this.getModuleOf(currentFile) ?: return this
    val module = getOrCreateModuleData(moduleName)

    var hasExternalDependencies = false
    var hasInternalDependencies = false
    for (fileUsingCurrent in filesUsingCurrent) {
      val nameOfModuleUsingCurrent = this.getUsingModule(currentFile, fileUsingCurrent, moduleName) ?: continue
      val moduleUsingCurrent = getOrCreateModuleData(nameOfModuleUsingCurrent)
      addDependencyForModules(module, moduleUsingCurrent)

      if (moduleName == nameOfModuleUsingCurrent) {
        hasInternalDependencies = true
      } else {
        hasExternalDependencies = true
      }
    }

    module.files++
    if (hasExternalDependencies) module.externallyUsedFiles++
    if (hasInternalDependencies) module.internallyUsedFiles++
    return this
  }

  private fun addDependencyForModules(currentModule: MutableModuleStats, moduleUsingCurrent: MutableModuleStats) {
    if(currentModule.name == moduleUsingCurrent.name) {
      currentModule.internalDependencies++
    } else {
      currentModule.dependenciesComingIn++
      currentModule.usedByModules.add(moduleUsingCurrent.name)

      moduleUsingCurrent.dependenciesGoingOut++
      moduleUsingCurrent.usesModules.add(currentModule.name)
    }
  }

  private fun getModuleOf(path: String): String? {
    if (!path.startsWith(this.rootPath)) {
      thisLogger().warn("Unknown module found for file $path")
      return null
    }

    val moduleNameEnd = path.indexOf("/", this.rootPath.length)
    return if (moduleNameEnd == -1) {
      "<ROOT>"
    } else {
      path.substring(this.rootPath.length, moduleNameEnd)
    }
  }

  private fun getOrCreateModuleData(name: String) = this.moduleMap.computeIfAbsent(name) {
    MutableModuleStats(name)
  }

  private fun getUsingModule(currentFile: String, fileUsingCurrent: String, usingModule: String): String? {
    return if (currentFile == fileUsingCurrent) {
      usingModule
    } else{
      this.getModuleOf(fileUsingCurrent) ?: return null
    }
  }


  private data class MutableModuleStats(
    override val name: String,
    override var files: Int = 0,
    override var internallyUsedFiles: Int = 0,
    override var externallyUsedFiles: Int = 0,
    override var internalDependencies: Int = 0,
    override var dependenciesComingIn: Int = 0,
    override var dependenciesGoingOut: Int = 0,
    override val usesModules: MutableSet<String> = mutableSetOf(),
    override val usedByModules: MutableSet<String> = mutableSetOf(),
  ): ModuleStats
}


