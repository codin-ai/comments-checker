package com.github.matanper.commentsplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vcs.impl.LineStatusTrackerManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.psiUtil.endOffset
import org.jetbrains.kotlin.psi.psiUtil.startOffset


class CommentHighlighterAnnotator : Annotator {

    private val DISPLAY_MESSAGE = "Outdated Comment!"
    val TEXT_ATTRIBUTE = TextAttributesKey.createTextAttributesKey("COMMENT_OUTDATED")

    /**
     * Return list of related code elements, and a related code element"""
     */
    private fun next_related_element(element: PsiElement): Pair<List<PsiElement>, PsiElement>? {
        val relatedElements = mutableListOf(element)
        var nextElement = element.nextSibling
        while (isNoneCodeElement(nextElement)) {
            // If double whitespace then there is no related code, return null
            if (isDoubleWhitespace(nextElement)) {
                return null
            }
            if (nextElement !is PsiWhiteSpace) {
                relatedElements.add(nextElement)
            }
            nextElement = nextElement.nextSibling
        }

        // If last element is null return null
        nextElement ?: return null

        // Get back related code block
        var prevElement = element.prevSibling
        while (isNoneCodeElement(prevElement)) {
            // If double whitespace then there is no more related comments
            if (isDoubleWhitespace(prevElement)) {
                break
            }
            // list should contain all related code elements
            if (prevElement !is PsiWhiteSpace) {
                relatedElements.add(0, prevElement)
            }
            prevElement = prevElement.prevSibling
        }

        return Pair(relatedElements, nextElement)
    }

    private fun isNoneCodeElement(element: PsiElement?): Boolean {
        return element != null && (element is PsiComment || element is PsiWhiteSpace)
    }

    private fun isDoubleWhitespace(element: PsiElement): Boolean {
        return element is PsiWhiteSpace && element.text.split("\n").size > 3
    }

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiComment) {
            // dog
            // bla bla
            // asdadasdasd

            val (relatedComments, codeElement) = next_related_element(element) ?: return

            val lastCommittedDoc =
                PsiDocumentManager.getInstance(element.project).getLastCommittedDocument(element.containingFile)
                    ?: return

            val firstCommentLineNumber = lastCommittedDoc.getLineNumber(relatedComments.first().textOffset)
            val lastCommentLineNumber = lastCommittedDoc.getLineNumber(relatedComments.last().textOffset)

            val codelineNumber = lastCommittedDoc.getLineNumber(codeElement.textOffset)

            val lineStatusTracker = LineStatusTrackerManager.getInstance(element.project)
                .getLineStatusTracker(element.containingFile.virtualFile)
                ?: return

            val commentsModified = lineStatusTracker.isRangeModified(firstCommentLineNumber, lastCommentLineNumber + 1)
            val codeLineModified = lineStatusTracker.isLineModified(codelineNumber)

            val vcsLineNumber = lineStatusTracker.transferLineToVcs(codelineNumber, true)
            val vcsLineText = lineStatusTracker.vcsDocument.getText(TextRange.create(lineStatusTracker.vcsDocument.getLineStartOffset(vcsLineNumber), lineStatusTracker.vcsDocument.getLineEndOffset(vcsLineNumber)))
            //vcsLineText.split(element.text)
            //lineStatusTracker.vcsDocument.text.slice(setOf(element.startOffset,element.endOffset))
            if (codeLineModified && !commentsModified) {
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