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
            counter++
        }
    } while (counter < limit)
}

private fun drawField(field: MutableList<MutableList<String>>) {
    for (line in field) {
        println(line.joinToString(""))
    }
}
