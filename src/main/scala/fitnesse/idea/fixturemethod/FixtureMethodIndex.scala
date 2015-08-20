package fitnesse.idea.fixturemethod

import com.intellij.psi.stubs.{StubIndexKey, StringStubIndexExtension}


class FixtureMethodIndex extends StringStubIndexExtension[FixtureMethod] {
  override def getKey: StubIndexKey[String, FixtureMethod] = FixtureMethodIndex.KEY

  override def getVersion: Int = 2
}


object FixtureMethodIndex {
  val KEY: StubIndexKey[String, FixtureMethod] = StubIndexKey.createIndexKey("fitnesse.fixtureMethod.index")

  val INSTANCE = new FixtureMethodIndex
}
