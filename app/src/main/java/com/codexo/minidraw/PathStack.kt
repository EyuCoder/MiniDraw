package com.codexo.minidraw

import android.util.Log
import android.graphics.Path


private val TAG = PathStack::class.java.simpleName

class PathStack {

    val elements: MutableList<Path> = mutableListOf()

    var isEmpty: Boolean = false
        get() = elements.isEmpty()

    val size: Int
        get() = elements.size

    fun push(item: Path) = elements.add(item)

    fun pop(): Unit {
        val item = elements.lastOrNull()
        if (!isEmpty) {
            elements.removeAt(elements.lastIndex)
        } else {
            Log.d(TAG, "pop: Stack is Empty!")
        }
    }

    val peek: Path?
        get() = elements.lastOrNull()

    val pathIterator = elements.iterator()

    override fun toString(): String = elements.toString()
}