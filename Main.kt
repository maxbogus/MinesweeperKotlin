package minesweeper

import kotlin.random.Random

private const val EMPTY_FIELD_SYMBOL = "."
private const val FIELD_SIZE = 9
private const val MINE_SYMBOL = "X"
private const val MARKER_SYMBOL = "*"

enum class GameState {
    Playing,
    Won,
    Lost
}

data class Move(val x: Int, val y: Int, val moveType: String)

data class Field(val mineCount: Int) {
    private var mineField: MutableList<MutableList<String>> = generateField()
    init {
        fillFieldWithMines()
    }
    private val playerMoves: MutableList<MutableList<String>> = generateField()

    private fun generateField(): MutableList<MutableList<String>> {
        val field: MutableList<MutableList<String>> = mutableListOf()
        repeat(FIELD_SIZE) {
            val line = mutableListOf<String>()
            repeat(FIELD_SIZE) { line.add(EMPTY_FIELD_SYMBOL) }
            field.add(line)
        }
        return field
    }

    fun compareFields(): GameState {
        for ((index, line) in playerMoves.withIndex()) {
            var actualResult = ""
            for (ch in mineField[index]) {
                actualResult += if (ch == MARKER_SYMBOL) MINE_SYMBOL else ch
            }
            val expectedResult = line.joinToString("")
            if (actualResult != expectedResult) {
                return GameState.Playing
            }
        }
        return GameState.Won
    }

    private fun fillFieldWithMines() {
        var counter = 0
        do {
            val column = Random.nextInt(0, FIELD_SIZE)
            val row = Random.nextInt(0, FIELD_SIZE)
            if (mineField[column][row] != MINE_SYMBOL) {
                mineField[column][row] = MINE_SYMBOL
                val lowRowLimit = if (row >= 1) row - 1 else 0
                val highRowLimit = if (row <= 7) row + 1 else 8
                val leftColumnLimit = if (column >= 1) column - 1 else 0
                val rightColumnLimit = if (column <= 7) column + 1 else 8
                for (rowAroundMine in lowRowLimit..highRowLimit) {
                    for (columnAroundMine in leftColumnLimit..rightColumnLimit) {
                        if (mineField[columnAroundMine][rowAroundMine] == EMPTY_FIELD_SYMBOL) {
                            mineField[columnAroundMine][rowAroundMine] = "1"
                        } else if (mineField[columnAroundMine][rowAroundMine] != MINE_SYMBOL) {
                            mineField[columnAroundMine][rowAroundMine] =
                                "${mineField[columnAroundMine][rowAroundMine].toInt() + 1}"
                        }
                    }
                }

                counter++
            }
        } while (counter < mineCount)
    }

    fun drawField() {
        println(" │123456789│")
        println("—│—————————│")
        var counter = 1
        for (line in playerMoves) {
            println("$counter|${line.joinToString("")}|")
            counter++
        }
        println("—│—————————│")
    }

    fun drawMineField() {
        println(" │123456789│")
        println("—│—————————│")
        var counter = 1
        for (line in mineField) {
            println("$counter|${line.joinToString("")}|")
            counter++
        }
        println("—│—————————│")
    }

    fun setMark(move: Move) {
        val (x, y) = move
        val symbol = playerMoves[y][x]
        if (symbol in "1".."9") {
            println("There is a number here!")
        } else {
            playerMoves[y][x] =
                if (symbol == EMPTY_FIELD_SYMBOL) MARKER_SYMBOL else EMPTY_FIELD_SYMBOL
        }
    }

    fun exploreField(move: Move): GameState {
        TODO("Not yet implemented")
        return GameState.Playing
    }
}

fun main() {
    println("How many mines do you want on the field?")
    val limit = readLine()!!.toInt()

    val field = Field(limit)
    var gameState: GameState

    do {
        field.drawField()
        field.drawMineField()
        val move = getUserInput()
        if (move.moveType == "mine") {
            field.setMark(move)
        } else {
            gameState = field.exploreField(move)
        }
        gameState = field.compareFields()
    } while (gameState == GameState.Playing)

    println("Congratulations! You found all the mines!")
}

fun getUserInput(): Move {
    println("Set/unset mine marks or claim a cell as free:")
    val input = readLine()!!.split(" ").toList()
    val coordinates = input.subList(0, 2)
    val moveType = input[2]
    val x = coordinates[1].toInt() - 1
    val y = coordinates[0].toInt() - 1
    return Move(x, y, moveType)
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
