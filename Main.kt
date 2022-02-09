package minesweeper

import kotlin.random.Random

// Game Constraints
private const val FIELD_SIZE = 9

// Game Art
private const val UNEXPLORED_SYMBOL = "."
private const val MINE_SYMBOL = "X"
private const val MARKER_SYMBOL = "*"
private const val EXPLORED_SYMBOL = "/"

enum class GameState {
    Playing,
    Won,
    Lost
}

class Move {
    private val x: Int
    private val y: Int
    val moveType: String

    init {
        println("Set/unset mine marks or claim a cell as free:")
        val input = readLine()!!.split(" ").toList()
        val coordinates = input.subList(0, 2)
        moveType = input[2]
        x = coordinates[1].toInt() - 1
        y = coordinates[0].toInt() - 1
    }

    fun getCoordinates(): Pair<Int, Int> {
        return Pair(x, y)
    }
}

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
            repeat(FIELD_SIZE) { line.add(UNEXPLORED_SYMBOL) }
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
                        if (mineField[columnAroundMine][rowAroundMine] == UNEXPLORED_SYMBOL) {
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
        val (x, y) = move.getCoordinates()
        val symbol = playerMoves[y][x]
        if (symbol in "1".."9") {
            println("There is a number here!")
        } else {
            playerMoves[y][x] =
                if (symbol == UNEXPLORED_SYMBOL) MARKER_SYMBOL else UNEXPLORED_SYMBOL
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
        val move = Move()
        if (move.moveType == "mine") {
            field.setMark(move)
        } else {
            gameState = field.exploreField(move)
        }
        gameState = field.compareFields()
    } while (gameState == GameState.Playing)

    println("Congratulations! You found all the mines!")
}
