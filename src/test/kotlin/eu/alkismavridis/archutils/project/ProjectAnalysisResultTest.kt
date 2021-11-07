package eu.alkismavridis.archutils.project

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class ProjectAnalysisResultTest {
  @Test
  fun `should be empty if no dependencies are registered`() {
    val result = ProjectAnalysisResult("/foo/bar/")
    assertThat(result.getModules()).isEmpty()
  }

  @Test
  fun `should register internal dependency`() {
    val result = ProjectAnalysisResult("/foo/bar/")
    result.addDependency("/foo/bar/module1/File1", "/foo/bar/module1/File2")
    result.addDependency("/foo/bar/module1/File2", "/foo/bar/module1/inner/File3")

    val modules = result.getModules().toList()
    assertThat(modules).hasSize(1)
    modules[0].assertEquals("module1", 2, 0, 0)
  }

  @Test
  fun `should register cross-module dependencies`() {
    val result = ProjectAnalysisResult("/foo/bar/")
    result.addDependency("/foo/bar/module2/File1", "/foo/bar/module1/File2")
    result.addDependency("/foo/bar/module1/File2", "/foo/bar/module3/inner/File3")
    result.addDependency("/foo/bar/module1/File2", "/foo/bar/module2/inner/File3")

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertEquals("module1", 0, 1, 2)
    modules[1].assertEquals("module2", 0, 1, 1)
    modules[2].assertEquals("module3", 0, 1, 0)
  }

  @Test
  fun `should assign direct children of root module to root`() {
    val result = ProjectAnalysisResult("/foo/bar/")
    result.addDependency("/foo/bar/Direct1", "/foo/bar/module1/File1a")
    result.addDependency("/foo/bar/Direct1", "/foo/bar/Direct2")
    result.addDependency("/foo/bar/module2/File2a", "/foo/bar/Direct3")
    result.addDependency("/foo/bar/module1/File1a", "/foo/bar/module2/inner/File2b")

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertEquals("<ROOT>", 1, 1, 1)
    modules[1].assertEquals("module1", 0, 1, 1)
    modules[2].assertEquals("module2", 0, 1, 1)
  }

  @Test
  fun `root path with no leading slash should be treated the same`() {
    val result = ProjectAnalysisResult("/foo/bar")
    result.addDependency("/foo/bar/module2/File1", "/foo/bar/module1/File2")
    result.addDependency("/foo/bar/module1/File2", "/foo/bar/module3/inner/File3")
    result.addDependency("/foo/bar/module1/File2", "/foo/bar/module2/inner/File3")

    val modules = result.getModules().toList().sortedBy { it.name }
    assertThat(modules).hasSize(3)
    modules[0].assertEquals("module1", 0, 1, 2)
    modules[1].assertEquals("module2", 0, 1, 1)
    modules[2].assertEquals("module3", 0, 1, 0)
  }


  private fun ModuleData.assertEquals(name: String, internal: Int, incoming: Int, outgoing: Int) {
    assertThat(this.name).isEqualTo(name)
    assertThat(this.internalDependencyCount).isEqualTo(internal)
    assertThat(this.incomingDependencyCount).isEqualTo(incoming)
    assertThat(this.outgoingDependencyCount).isEqualTo(outgoing)
  }
}
