package eu.alkismavridis.archutils.cycles

import eu.alkismavridis.archutils.analysis.model.ModuleStats
import eu.alkismavridis.archutils.analysis.testutils.DummyModuleStats
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CyclicDependencyServiceTest {

  @Test
  fun `zero modules should return zero cycles`() {
    val modules = emptyList<ModuleStats>()
    val service = createService()
    val result = service.detectCycles(modules)

    assertThat(result).isEmpty()
  }

  @Test
  fun `should detect basic cycle`() {
    val modules = listOf(
      DummyModuleStats("module1", usesModules = setOf("module2")),
      DummyModuleStats("module2", usesModules = setOf("module1")),
    )

    val result = createService().detectCycles(modules)
    assertThat(result).hasSize(1)
    assertThat(result[0]).containsExactly("module1", "module2")
  }

  @Test
  fun `should detect 4 step cycle`() {
    val modules = listOf(
      DummyModuleStats("module1", usesModules = setOf("module2")),
      DummyModuleStats("module2", usesModules = setOf("module3")),
      DummyModuleStats("module3", usesModules = setOf("module4")),
      DummyModuleStats("module4", usesModules = setOf("module1")),
    )

    val result = createService().detectCycles(modules)
    assertThat(result).hasSize(1)
    assertThat(result[0]).containsExactly("module1", "module2", "module3", "module4")
  }

  @Test
  fun `should detect isolated cycles`() {
    val modules = listOf(
      DummyModuleStats("module1", usesModules = setOf("module2")),
      DummyModuleStats("module2", usesModules = setOf("module3", "irrelevant1", "irrelevant2")),
      DummyModuleStats("module3", usesModules = setOf("module4")),
      DummyModuleStats("module4", usesModules = setOf("module1")),

      DummyModuleStats("isolated1", usesModules = setOf("isolated2")),
      DummyModuleStats("isolated2", usesModules = setOf("isolated1")),
    )

    val result = createService().detectCycles(modules)
    assertThat(result).hasSize(2)
    assertThat(result[0]).containsExactly("module1", "module2", "module3", "module4")
    assertThat(result[1]).containsExactly("isolated1", "isolated2")
  }

  @Test
  fun `should handle large graph`() {
    val modules = listOf(
      DummyModuleStats("module1", usesModules = setOf("module7")),
      DummyModuleStats("module2", usesModules = setOf("module4", "module7", "module9")),
      DummyModuleStats("module3", usesModules = emptySet()),
      DummyModuleStats("module4", usesModules = setOf("module5", "module6", "module10")),
      DummyModuleStats("module5", usesModules = setOf("module4")),
      DummyModuleStats("module6", usesModules = emptySet()),
      DummyModuleStats("module7", usesModules = setOf("module8")),
      DummyModuleStats("module8", usesModules = setOf("module1", "module2")),
      DummyModuleStats("module9", usesModules = emptySet()),
      DummyModuleStats("module10", usesModules = setOf("module6")),
      DummyModuleStats("module11", usesModules = setOf("module3")),
    )

    val result = createService().detectCycles(modules)
    assertThat(result)
      .hasSize(3)
      .anyMatch { it.toSet() == setOf("module1", "module7", "module8") }
      .anyMatch { it.toSet() == setOf("module4", "module5") }
      .anyMatch { it.toSet() == setOf("module2", "module7", "module8") }
  }


  private fun createService() = CyclicDependencyService()
}
