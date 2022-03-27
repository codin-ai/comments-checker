package com.github.codinai.comments.annotator

class test {

    private fun max(x: List<Int>): Int {
        return 1
    }

    private fun sum(x: List<Int>): Int {
        return 1
    }

    private fun test(): Int {
        val lines = listOf<Int>()

        // Get sum of lines
        var linesSum = sum(lines)

        linesSum += 1
        return linesSum
    }
}