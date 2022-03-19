package com.github.matanper.commentsplugin.services

import com.intellij.openapi.project.Project
import com.github.matanper.commentsplugin.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
