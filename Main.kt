package minesweeper

import kotlin.random.Random

private const val FIELD_SIZE = 9

fun main() {
    println("How many mines do you want on the field?")
    val limit = readLine()!!.toInt()
    val field: MutableList<MutableList<String>> = generateField()

    fillFieldWithMines(field, limit)
    drawField(field)
}

private fun generateField(): MutableList<MutableList<String>> {
    val field: MutableList<MutableList<String>> = mutableListOf()
    repeat(FIELD_SIZE) {
        field.add(mutableListOf(".", ".", ".", ".", ".", ".", ".", ".", "."))
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
        if (field[column][row] != "X") {
            field[column][row] = "X"
            // populate with numbers
            val lowRowLimit = if (row >= 1) row - 1 else 0
            val highRowLimit = if (row <= 7) row + 1 else 8
            val leftColumnLimit = if (column >= 1) column - 1 else 0
            val rightColumnLimit = if (column <= 7) column + 1 else 8
            for (rowAroundMine in lowRowLimit..highRowLimit) {
                for (columnAroundMine in leftColumnLimit..rightColumnLimit) {
                    if (field[columnAroundMine][rowAroundMine] == ".") {
                        field[columnAroundMine][rowAroundMine] = "1"
                    } else if (field[columnAroundMine][rowAroundMine] != "X") {
                        field[columnAroundMine][rowAroundMine] = "${field[columnAroundMine][rowAroundMine].toInt() + 1}"
                    }
                }
            }

            counter++
        }
    } while (counter < limit)
}

private fun drawField(field: MutableList<MutableList<String>>) {
    for (line in field) {
        println(line.joinToString(""))
    }
}
