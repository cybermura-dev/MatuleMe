package ru.takeshiko.matuleme.presentation.utils

object UniqueImageNumberProvider {
    private val availableNumbers = (1..25).shuffled().toMutableList()

    fun next(): Int {
        if (availableNumbers.isEmpty()) {
            availableNumbers.addAll((1..25).shuffled())
        }
        return availableNumbers.removeAt(0)
    }
}