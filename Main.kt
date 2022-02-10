package minesweeper

import java.util.*
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
    val x: Int
    val y: Int
    val moveType: String

    init {
        println("Set/unset mine marks or claim a cell as free:")
        val input = readLine()!!.split(" ").toList()
        val coordinates = input.subList(0, 2)
        x = coordinates[1].toInt() - 1
        y = coordinates[0].toInt() - 1
        moveType = input[2]
    }

    fun getCoordinates(): Pair<Int, Int> {
        return Pair(x, y)
    }
}

data class Field(val mineCount: Int) {
    private val mineField: MutableList<MutableList<String>> = generateField()
    private var playerMoves: MutableList<MutableList<String>> = generateField()
    var firstMove: Boolean = true

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
        for ((index, line) in mineField.withIndex()) {
            var actualResult = ""
            for (ch in playerMoves[index]) {
                actualResult += when (ch) {
                    EXPLORED_SYMBOL -> UNEXPLORED_SYMBOL
                    MARKER_SYMBOL -> MINE_SYMBOL
                    else -> ch
                }
            }
            val expectedResult = line.joinToString("")
            if (actualResult != expectedResult) {
                return GameState.Playing
            }
        }
        return GameState.Won
    }

    fun fillFieldWithMines(move: Move) {
        var counter = 0
        mineField[move.x][move.y] = MINE_SYMBOL
        do {
            val x = Random.nextInt(0, FIELD_SIZE)
            val y = Random.nextInt(0, FIELD_SIZE)
            if (mineField[x][y] != MINE_SYMBOL) {
                mineField[x][y] = MINE_SYMBOL
                val leftXLimit = if (x >= 1) x - 1 else 0
                val rightXLimit = if (x <= 7) x + 1 else 8
                val lowYLimit = if (y >= 1) y - 1 else 0
                val highYLimit = if (y <= 7) y + 1 else 8
                for (shiftedY in lowYLimit..highYLimit) {
                    for (shiftedX in leftXLimit..rightXLimit) {
                        if (mineField[shiftedX][shiftedY] == UNEXPLORED_SYMBOL) {
                            mineField[shiftedX][shiftedY] = "1"
                        } else if (mineField[shiftedX][shiftedY] != MINE_SYMBOL) {
                            mineField[shiftedX][shiftedY] =
                                "${mineField[shiftedX][shiftedY].toInt() + 1}"
                        }
                    }
                }
                counter++
            }
        } while (counter < mineCount)
        mineField[move.x][move.y] = UNEXPLORED_SYMBOL
        var countMines = 0
        val leftXLimit = if (move.x >= 1) move.x - 1 else 0
        val rightXLimit = if (move.x <= 7) move.x + 1 else 8
        val lowYLimit = if (move.y >= 1) move.y - 1 else 0
        val highYLimit = if (move.y <= 7) move.y + 1 else 8
        for (shiftedY in lowYLimit..highYLimit) {
            for (shiftedX in leftXLimit..rightXLimit) {
                if (mineField[shiftedX][shiftedY] in "2".."9") {
                    mineField[shiftedX][shiftedY] =
                        "${mineField[shiftedX][shiftedY].toInt() - 1}"
                } else if (mineField[shiftedX][shiftedY] == "1") {
                    mineField[shiftedX][shiftedY] = UNEXPLORED_SYMBOL
                } else if (mineField[shiftedX][shiftedY] == MINE_SYMBOL) {
                    countMines += 1
                }
            }
        }
        if (countMines > 0) {
            mineField[move.x][move.y] = "$countMines"
        }
        drawMineField()
        firstMove = false
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
        val symbol = playerMoves[x][y]
        if (symbol in "1".."9") {
            println("There is a number here!")
        } else {
            playerMoves[x][y] =
                if (symbol == UNEXPLORED_SYMBOL) MARKER_SYMBOL else UNEXPLORED_SYMBOL
        }
    }

    fun openFreeSpaceFloodFill(move: Pair<Int, Int>) {
        if (playerMoves[move.first][move.second] == EXPLORED_SYMBOL || playerMoves[move.first][move.second] in "1".."9") {
            return
        }
        if (mineField[move.first][move.second] == MINE_SYMBOL) {
            return
        }
        playerMoves[move.first][move.second] =
            if (mineField[move.first][move.second] == UNEXPLORED_SYMBOL) EXPLORED_SYMBOL else mineField[move.first][move.second]
        if (move.first + 1 < 9) {
            openFreeSpaceFloodFill(Pair(move.first + 1, move.second))
        }
        if (move.first - 1 >= 0) {
            openFreeSpaceFloodFill(Pair(move.first - 1, move.second))
        }
        if (move.second + 1 < 9) {
            openFreeSpaceFloodFill(Pair(move.first, move.second + 1))
        }
        if (move.second - 1 >= 0) {
            openFreeSpaceFloodFill(Pair(move.first, move.second - 1))
        }

        return
    }

    fun exploreField(move: Move): GameState {
        when (mineField[move.x][move.y]) {
            MINE_SYMBOL -> {
                return GameState.Lost
            }
            UNEXPLORED_SYMBOL -> {
                openFreeSpaceFloodFill(move.getCoordinates())
            }
            EXPLORED_SYMBOL -> {
            }
            else -> {
                playerMoves[move.x][move.y] = mineField[move.x][move.y]
            }
        }
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
        val move = Move()
        gameState = if (move.moveType == "mine") {
            field.setMark(move)
            field.compareFields()
        } else {
            if (field.firstMove) {
                field.fillFieldWithMines(move)
                field.openFreeSpaceFloodFill(move.getCoordinates())
                GameState.Playing
            } else {
                field.exploreField(move)
            }
        }
    } while (gameState == GameState.Playing)

    if (gameState == GameState.Lost) {
        println("You stepped on a mine and failed!")
    } else {
        println("Congratulations! You found all the mines!")
    }
}
