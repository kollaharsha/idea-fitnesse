package fitnesse.idea.fixtureclass

import com.intellij.openapi.application.QueryExecutorBase
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.searches.ReferencesSearch
import com.intellij.psi.{PsiElement, PsiClass, PsiReference, PsiReferenceBase}
import com.intellij.util.Processor
import fitnesse.idea.filetype.FitnesseFileType

import scala.collection.JavaConversions._

class FixtureClassReferencesSearch extends QueryExecutorBase[PsiReference, ReferencesSearch.SearchParameters](true) {

  override def processQuery(p: ReferencesSearch.SearchParameters, consumer: Processor[PsiReference]): Unit = {
    p.getElementToSearch match {
      case clazz: PsiClass =>
        p.getEffectiveSearchScope match {
          case scope: GlobalSearchScope =>
            val restrictedScope = GlobalSearchScope.getScopeRestrictedByFileTypes(scope, FitnesseFileType.INSTANCE)
            find(clazz.getName, clazz.getProject, restrictedScope)
              .map(fixtureClass => consumer.process(new FixtureClassReference(fixtureClass) {
                override def resolve(): PsiElement = clazz
              }))
          case _ =>
        }
      case _ =>
    }
  }

  def find(key: String, project: Project, scope: GlobalSearchScope): List[FixtureClass] = {
    FixtureClassIndex.INSTANCE.get(key, project, scope).toList
  }
}
