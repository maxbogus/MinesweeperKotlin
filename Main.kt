package minesweeper

import kotlin.random.Random

private const val EMPTY_FIELD_SYMBOL = "."
private const val FIELD_SIZE = 9
private const val MINE_SYMBOL = "X"
private const val MARKER_SYMBOL = "*"

fun main() {
    println("How many mines do you want on the field?")
    val limit = readLine()!!.toInt()
    val field: MutableList<MutableList<String>> = generateField()

    fillFieldWithMines(field, limit)
    val fieldWithHiddenMines = createFieldWithHiddenMines(field)

    do {
        drawField(fieldWithHiddenMines)
        println("Set/delete mines marks (x and y coordinates)")
        makeMove(fieldWithHiddenMines)
    } while (!checkWin(fieldWithHiddenMines, field))

    println("Congratulations! You found all the mines!")
}

fun checkWin(fieldWithHiddenMines: MutableList<MutableList<String>>, field: MutableList<MutableList<String>>): Boolean {
    var result = true
    for ((index, line) in field.withIndex()) {
        var actualResult = ""
        for (ch in fieldWithHiddenMines[index]) {
            actualResult += if (ch == MARKER_SYMBOL) MINE_SYMBOL else ch
        }
        val expectedResult = line.joinToString("")
        if (actualResult != expectedResult) {
            result = false
            break
        }
    }
    return result
}

private fun makeMove(fieldWithHiddenMines: MutableList<MutableList<String>>) {
    val input = readLine()!!.split(" ").map { it.toInt() }.toList()
    val symbol = fieldWithHiddenMines[input[0] - 1][input[1] - 1]
    if (symbol in "1".."9") {
        println("There is a number here!")
    } else {
        fieldWithHiddenMines[input[0] - 1][input[1] - 1] =
            if (symbol == EMPTY_FIELD_SYMBOL) MARKER_SYMBOL else EMPTY_FIELD_SYMBOL
    }
}

private fun createFieldWithHiddenMines(field: MutableList<MutableList<String>>): MutableList<MutableList<String>> {
    val copiedMinefield = mutableListOf<MutableList<String>>()
    for (line in field) {
        val modifiedLine = mutableListOf<String>()
        line.forEach { modifiedLine.add(if (it == MINE_SYMBOL) EMPTY_FIELD_SYMBOL else it) }
        copiedMinefield.add(modifiedLine)
    }
    return copiedMinefield
}

private fun generateField(): MutableList<MutableList<String>> {
    val field: MutableList<MutableList<String>> = mutableListOf()
    repeat(FIELD_SIZE) {
        val line = mutableListOf<String>()
        repeat(FIELD_SIZE) { line.add(EMPTY_FIELD_SYMBOL) }
        field.add(line)
    }
    return field
}

private fun fillFieldWithMines(
    field: MutableList<MutableList<String>>,
    limit: Int
) {
    var counter = 0
    do {
        val column = Random.nextInt(0, FIELD_SIZE)
        val row = Random.nextInt(0, FIELD_SIZE)
        if (field[column][row] != MINE_SYMBOL) {
            field[column][row] = MINE_SYMBOL
            // populate with numbers
            val lowRowLimit = if (row >= 1) row - 1 else 0
            val highRowLimit = if (row <= 7) row + 1 else 8
            val leftColumnLimit = if (column >= 1) column - 1 else 0
            val rightColumnLimit = if (column <= 7) column + 1 else 8
            for (rowAroundMine in lowRowLimit..highRowLimit) {
                for (columnAroundMine in leftColumnLimit..rightColumnLimit) {
                    if (field[columnAroundMine][rowAroundMine] == EMPTY_FIELD_SYMBOL) {
                        field[columnAroundMine][rowAroundMine] = "1"
                    } else if (field[columnAroundMine][rowAroundMine] != MINE_SYMBOL) {
                        field[columnAroundMine][rowAroundMine] = "${field[columnAroundMine][rowAroundMine].toInt() + 1}"
                    }
                }
            }

            counter++
        }
    } while (counter < limit)
}

private fun drawField(field: MutableList<MutableList<String>>) {
    println(" │123456789│")
    println("—│—————————│")
    var counter = 1
    for (line in field) {
        println("$counter|${line.joinToString("")}|")
        counter++
    }
    println("—│—————————│")
}
