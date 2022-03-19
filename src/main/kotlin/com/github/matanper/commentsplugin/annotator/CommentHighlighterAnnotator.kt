package com.github.matanper.commentsplugin.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiElement


class CommentHighlighterAnnotator : Annotator {

    private val DISPLAY_MESSAGE = "Outdated Comment!"

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is PsiComment) {
            // dog
            // bla bla
            val comment: String = element.text
            if (comment.contains("dog")) {
                val TEXT_ATTRIBUTE = TextAttributesKey.createTextAttributesKey("COMMENT_OUTDATED")

                holder.newAnnotation(HighlightSeverity.INFORMATION,
                                     DISPLAY_MESSAGE)
                    .range(element)
                    .textAttributes(TEXT_ATTRIBUTE)
                    .create()
            }
        }
    }

}