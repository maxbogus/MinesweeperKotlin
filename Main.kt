package minesweeper

import kotlin.random.Random

fun main() {
    println("How many mines do you want on the field?")
    val limit = readLine()!!.toInt()
    val field: MutableList<MutableList<Int>> = mutableListOf()
    repeat(9) {
        field.add(mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0))
    }
    var counter = 0

    do {
        val column = Random.nextInt(0, 9)
        val row = Random.nextInt(0, 9)
        if (field[column][row] != 1) {
            field[column][row] = 1
            counter++
        }
    } while (counter < limit)

    drawField(field)
}

private fun drawField(field: MutableList<MutableList<Int>>) {
    for (line in field) {
        var output: String = ""
        for (number in line) {
            output += if (number == 0) "." else "X"
        }
        println(output)
    }
}
