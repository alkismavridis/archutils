package eu.alkismavridis.archutils.cycles

import eu.alkismavridis.archutils.analysis.model.ModuleStats
import java.util.*


typealias CyclicDependency = List<String>

class CyclicDependencyService {

  fun detectCycles(modules: List<ModuleStats>): List<CyclicDependency> {
    val ctx = DependencyCycleContext(modules)
    while (true) {
      val notVisited = this.getNextUnvisited(ctx) ?: break
      this.visit(notVisited, ctx)
    }

    return ctx.getResult()
  }

  private fun visit(module: ModuleStats, ctx: DependencyCycleContext): Boolean {
    ctx.pushToStack(module)
    this.visitDependencies(module, ctx)

    val isCyclic = ctx.isPathCyclic()
    if (isCyclic) {
      ctx.markPathAsCyclic()
    }

    ctx.popFromStack()
    ctx.markAsVisited(module)
    return isCyclic
  }

  private fun visitDependencies(module: ModuleStats, ctx: DependencyCycleContext) {
    module.usesModules.forEach { usedModuleName ->
      val usedModule = ctx.getModule(usedModuleName) ?: return@forEach
      this.visit(usedModule, ctx)
    }
  }

  private fun getNextUnvisited(ctx: DependencyCycleContext): ModuleStats? {
    val visitedNames = ctx.getVisitedNames()
    return ctx.modules.find { it.name !in visitedNames }
  }


  private class DependencyCycleContext(val modules: List<ModuleStats>) {
    private val moduleMap = this.modules.associateBy { it.name }
    private val visitedNames = mutableSetOf<String>()
    private val currentPath = Stack<String>()
    private val cycles = mutableListOf<CyclicDependency>()

    fun getResult(): List<CyclicDependency> = this.cycles
    fun getVisitedNames(): Set<String> = this.visitedNames
    fun getModule(name: String) = moduleMap[name]

    fun isPathCyclic(): Boolean {
      return this.currentPath.size > 1 &&
        this.currentPath.firstElement() == this.currentPath.lastElement()
    }

    fun pushToStack(module: ModuleStats): String = this.currentPath.push(module.name)
    fun popFromStack(): String = this.currentPath.pop()
    fun markAsVisited(module: ModuleStats): Boolean = this.visitedNames.add(module.name)

    fun markPathAsCyclic() {
      val cycle = currentPath.toList()
      this.cycles.add(cycle)
    }
  }
}
