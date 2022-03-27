package com.github.matanper.commentsplugin.annotator

import com.intellij.diff.util.DiffUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.impl.LineStatusTrackerManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.psiUtil.getNextSiblingIgnoringWhitespaceAndComments


class CommentHighlighterAnnotator : Annotator {

    private val DISPLAY_MESSAGE = "Outdated Comment!"
    val TEXT_ATTRIBUTE = TextAttributesKey.createTextAttributesKey("COMMENT_OUTDATED")

    private fun next_related_element(element: PsiElement): PsiElement? {
        // If there are 2 successive whitespace then the comments are separated so does not return any element
        var startCodeElement = element.nextSibling
        while (isNoneCodeElement(startCodeElement)) {
            // If both are whitespace then the comment is not related to code
            if (isDoubleWhitespace(startCodeElement)) {
                return null
            }
            startCodeElement = startCodeElement.nextSibling
        }

        return startCodeElement
    }

    private fun isNoneCodeElement(element: PsiElement?): Boolean {
        return element != null && (element is PsiComment || element is PsiWhiteSpace)
    }

    private fun isDoubleWhitespace(element: PsiElement): Boolean {
        return element is PsiWhiteSpace && element.prevSibling is PsiWhiteSpace
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiComment) {
            // dog
            // bla bla
            // asdadasdasd

            val comment: String = element.text
            val nextSib = next_related_element(element) ?: return

            val lastCommittedDoc =
                PsiDocumentManager.getInstance(element.project).getLastCommittedDocument(element.containingFile)
                    ?: return

            val commentLineNumber = lastCommittedDoc.getLineNumber(element.textOffset)
            val nextSiblineNumber = lastCommittedDoc.getLineNumber(nextSib.textOffset)
            if (nextSiblineNumber - commentLineNumber > 2)
                return

            val lineStatusTracker = LineStatusTrackerManager.getInstance(element.project)
                .getLineStatusTracker(element.containingFile.virtualFile)
                ?: return
            val lineDiffRange = lineStatusTracker.getRangeForLine(nextSiblineNumber)
            if (lineDiffRange != null) {

                holder.newAnnotation(
                    HighlightSeverity.INFORMATION,
                    DISPLAY_MESSAGE
                )
                    .range(element)
                    .textAttributes(TEXT_ATTRIBUTE)
                    .create()
            }
        }
    }

}