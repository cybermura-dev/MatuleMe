package ru.takeshiko.matuleme.presentation.utils

import android.content.Context
import ru.takeshiko.matuleme.domain.provider.StringResourceProvider

class AndroidStringResourceProvider(
    private val context: Context
) : StringResourceProvider {
    override fun getString(resId: Int, vararg formatArgs: Any): String {
        return context.getString(resId, *formatArgs)
    }
}