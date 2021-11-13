package eu.alkismavridis.archutils.validation

import eu.alkismavridis.archutils.validation.CyclicDependency
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


internal class CyclicDependencyTest {
  @Test
  fun `two empty paths should be equal`() {
    val path1 = CyclicDependency(emptyList())
    val path2 = CyclicDependency(emptyList())

    assertThat(path1).isEqualTo(path2)
    assertThat(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `should not be equal to an object of different class`() {
    val path = CyclicDependency(listOf("foo", "bar"))
    assertThat(path).isNotEqualTo("Hello")
  }

  @Test
  fun `same paths of size 1 should be equal`() {
    val path1 = CyclicDependency(listOf("foo"))
    val path2 = CyclicDependency(listOf("foo"))

    assertThat(path1).isEqualTo(path2)
    assertThat(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `different paths of size 1 should not be equal`() {
    val path1 = CyclicDependency(listOf("foo"))
    val path2 = CyclicDependency(listOf("bar"))

    assertThat(path1).isNotEqualTo(path2)
  }

  @Test
  fun `paths of same cyclic order  should be equal`() {
    val path1 = CyclicDependency(listOf("foo", "bar", "zed"))
    val path2 = CyclicDependency(listOf("bar", "zed", "foo"))
    val path3 = CyclicDependency(listOf("zed", "foo", "bar"))

    assertThat(path1).isEqualTo(path2).isEqualTo(path3)
    assertThat(path2).isEqualTo(path1).isEqualTo(path3)
    assertThat(path3).isEqualTo(path1).isEqualTo(path2)

    assertThat(path1.hashCode()).isEqualTo(path2.hashCode()).isEqualTo(path3.hashCode())
    assertThat(path2.hashCode()).isEqualTo(path1.hashCode()).isEqualTo(path3.hashCode())
    assertThat(path3.hashCode()).isEqualTo(path1.hashCode()).isEqualTo(path2.hashCode())
  }

  @Test
  fun `paths of different cyclic orders should not be equal`() {
    val path1 = CyclicDependency(listOf("foo", "bar", "zed"))
    val path2 = CyclicDependency(listOf("foo", "zed", "bar"))
    assertThat(path1).isNotEqualTo(path2)
  }

  @Test
  fun `paths of different size orders should not be equal`() {
    val path1 = CyclicDependency(listOf("foo", "bar", "zed"))
    val path2 = CyclicDependency(listOf("foo", "bar"))
    val path3 = CyclicDependency(listOf("bar", "zed"))
    assertThat(path1).isNotEqualTo(path2).isNotEqualTo(path3)
    assertThat(path2).isNotEqualTo(path1)
    assertThat(path3).isNotEqualTo(path1)
  }
}
