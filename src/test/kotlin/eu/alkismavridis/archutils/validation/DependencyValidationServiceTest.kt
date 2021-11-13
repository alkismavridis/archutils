package eu.alkismavridis.archutils.validation

import eu.alkismavridis.archutils.testutils.DummyModuleStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class DependencyValidationServiceTest {

  @Test
  fun `zero modules should return no warnings`() {
    val result = DependencyValidationService().findIllegalDependencies(listOf(), emptyMap())
    assertThat(result).isEmpty()
  }

  @Test
  fun `should find illegal dependency from wildcard source rule`() {
    val modules = listOf(
      DummyModuleStats(name = "utils", usesModules = setOf("api"))
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, FORBID_ALL_RULE_BY_WILDCARD)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api")
    )
  }

  @Test
  fun `should find 2 illegal dependencies from wildcard source rule`() {
    val modules = listOf(
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db"))
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, FORBID_ALL_RULE_BY_WILDCARD)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "db")
    )
  }

  @Test
  fun `should mark all dependencies of a module that explicitly declares zero dependencies`() {
    val modules = listOf(
      DummyModuleStats(name = "model", usesModules = setOf("api", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db")),
    )

    val allowedDependencies = mapOf(
      "model" to emptyList(),
      "*" to listOf("*")
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, allowedDependencies)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "model", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "model", moduleTo = "services"),
    )
  }

  @Test
  fun `should allow explicitly stated dependency`() {
    val modules = listOf(
      DummyModuleStats(name = "services", usesModules = setOf("model", "api")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "db")),
    )

    val allowedDependencies = mapOf(
      "services" to listOf("model"),
      "*" to listOf("*")
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, allowedDependencies)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "services", moduleTo = "api"),
    )
  }

  @Test
  fun `should allow everything to a module that explicitly states that everything is allowed`() {
    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils", "db", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("api")),
    )

    val allowedDependencies = mapOf(
      "api" to listOf("*"),
      "*" to emptyList()
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, allowedDependencies)
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
    )
  }

  @Test
  fun `should allow everything in an allow-all configuration`() {
    val allowedDependencies = mapOf(
      "*" to listOf("*")
    )

    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils", "db", "services")),
      DummyModuleStats(name = "utils", usesModules = setOf("model", "api", "db", "services")),
      DummyModuleStats(name = "model", usesModules = setOf("api", "utils", "db", "services")),
      DummyModuleStats(name = "services", usesModules = setOf("model", "utils", "db", "api")),
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, allowedDependencies)
    assertThat(result).isEmpty()
  }

  @Test
  fun `should forbid everything in an allow-nothing configuration`() {
    val modules = listOf(
      DummyModuleStats(name = "api", usesModules = setOf("model", "utils")),
      DummyModuleStats(name = "utils", usesModules = setOf("api", "model")),
    )

    val result = DependencyValidationService().findIllegalDependencies(modules, emptyMap())
    assertThat(result).containsExactlyInAnyOrder(
      IllegalModuleDependency(moduleFrom = "api", moduleTo = "model"),
      IllegalModuleDependency(moduleFrom = "api", moduleTo = "utils"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "api"),
      IllegalModuleDependency(moduleFrom = "utils", moduleTo = "model"),
    )
  }


  companion object {
    private val FORBID_ALL_RULE_BY_WILDCARD =  mapOf("*" to emptyList<String>())
  }
}
