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
        moveType = input[2]
        x = coordinates[1].toInt() - 1
        y = coordinates[0].toInt() - 1
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

    fun fillFieldWithMines(move: Move) {
        var counter = 0
        do {
            val column = Random.nextInt(0, FIELD_SIZE)
            val row = Random.nextInt(0, FIELD_SIZE)
            if (mineField[column][row] != MINE_SYMBOL && column != move.x && row != move.y) {
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

    fun openFreeSpace(move: Pair<Int, Int>) {
        val queue: Queue<Pair<Int, Int>> = LinkedList()
        queue.add(move)
        val traversed: MutableList<Pair<Int, Int>> = mutableListOf(move)
        do {
            val (x, y) = queue.peek()
            when (val playerFound = mineField[y][x]) {
                in "1".."9" -> {
                    playerMoves[y][x] = playerFound
                }
                EXPLORED_SYMBOL, MINE_SYMBOL -> {
                }
                else -> {
                    val lowRowLimit = if (y >= 1) y - 1 else 0
                    val highRowLimit = if (y <= 7) y + 1 else 8
                    val leftColumnLimit = if (x >= 1) x - 1 else 0
                    val rightColumnLimit = if (x <= 7) x + 1 else 8
                    for (rowAroundMine in lowRowLimit..highRowLimit) {
                        for (columnAroundMine in leftColumnLimit..rightColumnLimit) {
                            val foundSymbol = mineField[columnAroundMine][rowAroundMine]
                            if (foundSymbol == UNEXPLORED_SYMBOL) {
                                playerMoves[columnAroundMine][rowAroundMine] = EXPLORED_SYMBOL
                            } else if (foundSymbol in "1".."9") {
                                playerMoves[columnAroundMine][rowAroundMine] = foundSymbol
                            }
                            if (!traversed.contains(Pair(columnAroundMine, rowAroundMine))) {
                                queue.add(Pair(columnAroundMine, rowAroundMine))
                            }
                        }
                    }
                }
            }
            traversed.add(Pair(x, y))
            queue.remove()
        } while (queue.size != 0)
    }

    fun exploreField(move: Move): GameState {
        when (mineField[move.y][move.x]) {
            MINE_SYMBOL -> {
                return GameState.Lost
            }
            UNEXPLORED_SYMBOL -> {
                openFreeSpace(move.getCoordinates())
            }
            EXPLORED_SYMBOL -> {
            }
            else -> {
                playerMoves[move.y][move.x] = mineField[move.y][move.x]
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
                field.openFreeSpace(move.getCoordinates())
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
