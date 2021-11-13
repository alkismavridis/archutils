package eu.alkismavridis.archutils.analysis

import eu.alkismavridis.archutils.analysis.model.AnalysisRequest
import eu.alkismavridis.archutils.analysis.model.DependencyRules
import eu.alkismavridis.archutils.analysis.model.IllegalModuleDependency
import eu.alkismavridis.archutils.analysis.testutils.DummyModuleStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DependencyAnalysisServiceTest {

  @Test
  fun `zero modules should return no warnings`() {
    val request = AnalysisRequest("src", DependencyRules())
    val result = DependencyAnalysisService().findIllegalDependencies(request, listOf())
    assertThat(result).isEmpty()
  }

  @Test
  fun `should find illegal dependency from wildcard source rule`() {
    val modules = listOf(
      DummyModuleStats(name = "utils", usesModules = setOf("api"))
    )

    val request = AnalysisRequest("src", FORBID_ALL_RULE_BY_WILDCARD)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api")
    )
  }

  @Test
  fun `should find 2 illegal dependencies from wildcard source rule`() {
    val modules = listOf(
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db"))
    )

    val request = AnalysisRequest("src", FORBID_ALL_RULE_BY_WILDCARD)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "db")
    )
  }

  @Test
  fun `should mark all dependencies of a module that explicitly declares zero dependencies`() {
    val rules = DependencyRules(allowedDependencies = mapOf(
      "model" to emptyList(),
      "*" to listOf("*")
    ))

    val modules = listOf(
      DummyModuleStats(name = "model", usesModules = setOf("api", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db")),
    )

    val request = AnalysisRequest("src", rules)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "model", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "model", moduleTo = "services"),
    )
  }

  @Test
  fun `should allow explicitly stated dependency`() {
    val rules = DependencyRules(allowedDependencies = mapOf(
      "services" to listOf("model"),
      "*" to listOf("*")
    ))

    val modules = listOf(
      DummyModuleStats(name = "services", usesModules = setOf("model", "api")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db")),
    )

    val request = AnalysisRequest("src", rules)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "services", moduleTo = "api"),
    )
  }

  @Test
  fun `should allow everything to a module that explicitly states that everything is allowed`() {
    val rules = DependencyRules(allowedDependencies =  mapOf(
      "api" to listOf("*"),
      "*" to emptyList()
    ))

    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils", "db", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("api")),
    )

    val request = AnalysisRequest("src", rules)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
    )
  }

  @Test
  fun `should allow everything in an allow-all configuration`() {
    val rules = DependencyRules(allowedDependencies = mapOf(
      "*" to listOf("*")
    ))

    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils", "db", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("model", "api", "db", "services")),
      DummyModuleStats(name = "model", usesModules = setOf("api", "utils", "db", "services")),
      DummyModuleStats(name = "services", usesModules = setOf("model", "utils", "db", "api")),
    )

    val request = AnalysisRequest("src", rules)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).isEmpty()
  }

  @Test
  fun `should forbid everything in an allow-nothing configuration`() {
    val rules = DependencyRules(allowedDependencies =  mapOf())

    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "model")),
    )

    val request = AnalysisRequest("src", rules)
    val result = DependencyAnalysisService().findIllegalDependencies(request, modules)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "api", moduleTo = "model"),
      IllegalModuleDependency(moduleFrom = "api", moduleTo = "utils"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "model"),
    )
  }


  companion object {
    val FORBID_ALL_RULE_BY_WILDCARD = DependencyRules(allowedDependencies = mapOf("*" to emptyList()))
  }
}
