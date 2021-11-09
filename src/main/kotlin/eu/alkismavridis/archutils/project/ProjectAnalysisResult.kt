package eu.alkismavridis.archutils.project

import com.intellij.openapi.diagnostic.thisLogger

class ProjectAnalysisResult(rootPackage: String) {
  private val rootPath = rootPackage
    .replace("\\", "/")
    .let { if(it.endsWith("/")) it else "$it/" }

  private val moduleMap = mutableMapOf<String, MutableModuleData>()

  fun getModules(): Collection<ModuleData> {
    return this.moduleMap.values.sortedBy { it.name }
  }

  fun addFile(file: String, usingFiles: Collection<String>) {
    val moduleName = this.getModuleOf(file) ?: return
    val module = getOrCreateModuleData(moduleName)

    var hasExternalDependencies = false
    var hasInternalDependencies = false
    for (usingFile in usingFiles) {
      val usingModuleName = this.getUsingModule(file, usingFile, moduleName) ?: continue
      val usingModule = getOrCreateModuleData(usingModuleName)
      addDependencyForModules(module, usingModule)

      if (moduleName != usingModuleName) {
        hasExternalDependencies = true
      } else {
        hasInternalDependencies = true
      }
    }

    module.files++
    if (hasExternalDependencies) {
      module.externallyUsedFiles++
    }

    if (hasInternalDependencies) {
      module.internallyUsedFiles++
    }
  }

  private fun addDependencyForModules(usingModule: MutableModuleData, usedModule: MutableModuleData) {
    if(usedModule.name == usingModule.name) {
      usedModule.internalDependencies++
    } else {
      usedModule.externalUsages++
      usingModule.externalDependencies++
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
    MutableModuleData(name)
  }

  private fun getUsingModule(usingFile: String, usedFile: String, usingModule: String): String? {
    return if (usingFile == usedFile) {
      usingModule
    } else{
      this.getModuleOf(usedFile) ?: return null
    }
  }



  private data class MutableModuleData(
    override val name: String,
    override var files: Int = 0,
    override var internallyUsedFiles: Int = 0,
    override var externallyUsedFiles: Int = 0,
    override var internalDependencies: Int = 0,
    override var externalDependencies: Int = 0,
    override var externalUsages: Int = 0,
  ): ModuleData
}


