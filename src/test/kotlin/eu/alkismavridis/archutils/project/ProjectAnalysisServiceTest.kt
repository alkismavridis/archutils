package eu.alkismavridis.archutils.project

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProjectAnalysisServiceTest {
  @Test
  fun `should be empty if no dependencies are registered`() {
    val result = ProjectAnalysisService("/foo/bar/")
    assertThat(result.getModules()).isEmpty()
  }

  @Test
  fun `should register internal dependency`() {
    val result = ProjectAnalysisService("/foo/bar/")
    result.addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module1/File1b"))
    result.addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module1/inner/File1c"))

    val modules = result.getModules().toList()
    assertThat(modules).hasSize(1)
    modules[0].assertStats("module1", 2, 2, 0, 2, 0, 0)
  }

  @Test
  fun `should register cross-module dependencies`() {
    val result = ProjectAnalysisService("/foo/bar/")
    result.addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
    result.addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2b"))

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertStats("module1", 1, 0, 1, 0, 1, 2)
    modules[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    modules[2].assertStats("module3", 0, 0, 0, 0, 1, 0)
  }

  @Test
  fun `should assign direct children of root module to root`() {
    val result = ProjectAnalysisService("/foo/bar/")
    result.addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2"))
    result.addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
    result.addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertStats("<ROOT>", 1, 1, 1,1, 1, 1)
    modules[1].assertStats("module1", 1, 0, 1, 0, 1, 1)
    modules[2].assertStats("module2", 1, 0, 1, 0, 1, 1)
  }

  @Test
  fun `root path with no leading slash should be treated the same`() {
    val result = ProjectAnalysisService("/foo/bar")
    result.addFile("/foo/bar/Direct1", listOf("/foo/bar/module1/File1a", "/foo/bar/Direct2", "/foo/bar/module2/File2a"))
    result.addFile("/foo/bar/module1/File1a", listOf("/foo/bar/module2/inner/File2b"))
    result.addFile("/foo/bar/module2/File2a", listOf("/foo/bar/Direct3"))

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    assertThat(modules[0].usedModules).containsExactly("module1", "module2")
    assertThat(modules[1].usedModules).containsExactly("module2")
    assertThat(modules[2].usedModules).containsExactly("<ROOT>")

    assertThat(modules[0].dependingModules).containsExactly("module2")
    assertThat(modules[1].dependingModules).containsExactly("<ROOT>")
    assertThat(modules[2].dependingModules).containsExactly("<ROOT>", "module1")
  }

  @Test
  fun `should track used and dependent modules`() {
    val result = ProjectAnalysisService("/foo/bar")
    result.addFile("/foo/bar/module2/File2a", listOf("/foo/bar/module1/File1a"))
    result.addFile("/foo/bar/module1/File1b", listOf("/foo/bar/module3/inner/File3a", "/foo/bar/module2/inner/File2c"))

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertStats("module1", 1, 0, 1, 0, 1, 2)
    modules[1].assertStats("module2", 1, 0, 1, 0, 1, 1)
    modules[2].assertStats("module3", 0, 0, 0, 0, 1, 0)
  }


  private fun ModuleData.assertStats(
    name: String,
    files: Int,
    internallyUsedFile: Int,
    externallyUsedFile: Int,
    internalDependencies: Int,
    incomingDependencies: Int,
    outgoingDependencies: Int
  ) {
    assertThat(this.name).isEqualTo(name)
    assertThat(this.files).isEqualTo(files)
    assertThat(this.internallyUsedFiles).isEqualTo(internallyUsedFile)
    assertThat(this.externallyUsedFiles).isEqualTo(externallyUsedFile)
    assertThat(this.internalDependencies).isEqualTo(internalDependencies)
    assertThat(this.externalUsages).isEqualTo(incomingDependencies)
    assertThat(this.externalDependencies).isEqualTo(outgoingDependencies)
  }
}
