package eu.alkismavridis.archutils.validation

import com.intellij.openapi.diagnostic.thisLogger
import eu.alkismavridis.archutils.modules.ModuleStats
import java.util.*


class CyclicDependencyService {

  fun detectCycles(modules: List<ModuleStats>, limit: Int = 100): Set<List<String>> {
    val ctx = DependencyCycleContext(modules, limit)
    while (!ctx.isFull()) {
      val notVisited = this.getNextUnvisited(ctx) ?: break
      this.visit(notVisited, ctx)
    }

    return ctx.getResult()
  }

  private fun visit(module: ModuleStats, ctx: DependencyCycleContext) {
    if (ctx.isFull()) return

    ctx.pushToStack(module)
    if (ctx.isPathCyclic()) {
      ctx.markPathAsCyclic()
    } else {
      this.visitDependencies(module, ctx)
    }

    ctx.popFromStack()
    ctx.markAsVisited(module)
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


  private class DependencyCycleContext(val modules: List<ModuleStats>, private val limit: Int) {
    private val moduleMap = this.modules.associateBy { it.name }
    private val visitedNames = mutableSetOf<String>()
    private val currentPath = Stack<String>()
    private val cycles = mutableSetOf<List<String>>()

    fun isFull(): Boolean = this.cycles.size >= this.limit
    fun getResult(): Set<List<String>> = this.cycles
    fun getVisitedNames(): Set<String> = this.visitedNames
    fun getModule(name: String) = moduleMap[name]

    fun isPathCyclic(): Boolean {
      return this.firstIndexOfTail() != this.currentPath.lastIndex
    }

    fun pushToStack(module: ModuleStats): String = this.currentPath.push(module.name)
    fun popFromStack(): String = this.currentPath.pop()
    fun markAsVisited(module: ModuleStats): Boolean = this.visitedNames.add(module.name)

    fun markPathAsCyclic() {
      val cycleStart = this.firstIndexOfTail()
      val cyclicSlice = currentPath.slice(cycleStart until currentPath.lastIndex)
      thisLogger().info("Cyclic dep no. ${this.cycles.size}. Path size: ${this.currentPath.size} ")

      this.cycles.add(cyclicSlice.rotateMinimumToStart())
    }

    private fun firstIndexOfTail() : Int {
      val lastElement = this.currentPath.lastElement()
      return this.currentPath.indexOf(lastElement)
    }
  }
}
