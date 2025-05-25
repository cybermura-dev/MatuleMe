package ru.takeshiko.matuleme.domain.provider

interface StringResourceProvider {
    fun getString(resId: Int, vararg formatArgs: Any): String
}