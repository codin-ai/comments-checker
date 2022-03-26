package com.github.matanper.commentsplugin.annotator

import com.intellij.diff.util.DiffUtil
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.psi.PsiComment
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement


class CommentHighlighterAnnotator : Annotator {

    private val DISPLAY_MESSAGE = "Outdated Comment!"
    val TEXT_ATTRIBUTE = TextAttributesKey.createTextAttributesKey("COMMENT_OUTDATED")

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        //ChangeListManager.getInstance(element.project
        var changeListManager = ChangeListManager.getInstance(element.project)
        var changes = changeListManager.getChangesIn(element.containingFile.virtualFile)
        var lastCommittedDoc = PsiDocumentManager.getInstance(element.project).getLastCommittedDocument(element.containingFile)
        var lineNumber = lastCommittedDoc?.getLineNumber(element.textOffset)
        var a = DiffUtil.getLinesContent(PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile)!!,12,13)
        //DiffUtil.getLinesContent(PsiDocumentManager.getInstance(element.project).getDocument(element.containingFile), 10,11)
        if (element is PsiComment) {
            // dog
            // bla bla
            // asdadasdasd
                
            val comment: String = element.text
            if (comment.contains("dog")) {

                holder.newAnnotation(HighlightSeverity.INFORMATION,
                                     DISPLAY_MESSAGE)
                    .range(element)
                    .textAttributes(TEXT_ATTRIBUTE)
                    .create()
            }
        }
    }

}