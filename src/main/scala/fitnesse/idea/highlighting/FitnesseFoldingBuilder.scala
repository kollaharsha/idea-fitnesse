package fitnesse.idea.highlighting

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.{FoldingBuilderEx, FoldingDescriptor}
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.search.PsiElementProcessor
import com.intellij.psi.util.PsiTreeUtil
import fitnesse.idea.parser.FitnesseElementType
import fitnesse.idea.psi.FitnesseFile

class FitnesseFoldingBuilder extends FoldingBuilderEx {

  override def isCollapsedByDefault(astNode: ASTNode): Boolean = true

  override def buildFoldRegions(psiElement: PsiElement, document: Document, quick: Boolean): Array[FoldingDescriptor] = {
    psiElement match {
      case g2: FitnesseFile if !quick =>
        val processor: FitnesseFileElementprocessor = new FitnesseFileElementprocessor
        PsiTreeUtil.processElements(processor, g2)
        processor.descriptors.toArray
      case _ => FoldingDescriptor.EMPTY
    }
  }

  override def getPlaceholderText(astNode: ASTNode): String =
    astNode.getElementType match {
      case FitnesseElementType.COLLAPSIBLE =>
        astNode.getText.split("\n").toList match {
          case head :: Nil => null
          case head :: (_ :+ last) => head.trim + " ... " + last.trim
          case _ => null
        }
      case _ => null
    }

  private class FitnesseFileElementprocessor extends PsiElementProcessor[PsiElement] {

    var descriptors = List[FoldingDescriptor]()

    override def execute(t: PsiElement): Boolean = {
      if (t.getNode.getElementType == FitnesseElementType.COLLAPSIBLE) {
        descriptors = new FoldingDescriptor(t, t.getTextRange) :: descriptors
      }
      true
    }
  }

}