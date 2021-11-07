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

  fun addDependency(usingFilePath: String, usedFilePath: String) {
    val usingModule = this.getModuleOf(usingFilePath) ?: return
    val usedModule = if (usingFilePath == usedFilePath) {
      usingModule
    } else{
      this.getModuleOf(usedFilePath) ?: return
    }

    addDependencyForModules(usingModule, usedModule)
  }

  private fun addDependencyForModules(usingModule: String, usedModule: String) {
    if(usedModule == usingModule) {
      getOrCreateModuleData(usedModule).internalDependencyCount++
    } else {
      getOrCreateModuleData(usedModule).incomingDependencyCount++
      getOrCreateModuleData(usingModule).outgoingDependencyCount++
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
    MutableModuleData(name, 0, 0, 0)
  }

  private data class MutableModuleData(
    override val name: String,
    override var internalDependencyCount: Int,
    override var incomingDependencyCount: Int,
    override var outgoingDependencyCount: Int,
  ): ModuleData
}


