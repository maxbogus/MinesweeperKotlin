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
        do {
            val x = Random.nextInt(0, FIELD_SIZE)
            val y = Random.nextInt(0, FIELD_SIZE)
            if (mineField[x][y] != MINE_SYMBOL || (x == move.x && y == move.y)) {
                mineField[x][y] = MINE_SYMBOL
                counter += 1
            }
        } while (counter < mineCount)
        for (x in 0..FIELD_SIZE - 1) {
            for (y in 0..FIELD_SIZE - 1) {
                if (mineField[x][y] == MINE_SYMBOL) {
                    val mineXLowLimit = if (x == 0) 0 else x - 1
                    val mineXHighLimit = if (x == 8) 8 else x + 1
                    val mineYLowLimit = if (y == 0) 0 else y - 1
                    val mineYHighLimit = if (y == 8) 8 else y + 1
                    for (i in mineXLowLimit..mineXHighLimit) {
                        for (j in mineYLowLimit..mineYHighLimit) {
                            if (mineField[i][j] in "1".."9") {
                                mineField[i][j] = "${mineField[i][j].toInt() + 1}"
                            } else if (mineField[i][j] == UNEXPLORED_SYMBOL) {
                                mineField[i][j] = "1"
                            }
                        }
                    }
                }
            }
        }
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
        when (playerMoves[x][y]) {
            in "1".."9" -> {
                println("There is a number here!")
            }
            MARKER_SYMBOL -> {
                playerMoves[x][y] = UNEXPLORED_SYMBOL
            }
            UNEXPLORED_SYMBOL -> {
                playerMoves[x][y] = MARKER_SYMBOL
            }
            else -> {
            }
        }
    }

    fun openFreeSpaceFloodFill(move: Pair<Int, Int>) {
        val playerFieldValue = playerMoves[move.first][move.second]
        val mineFieldValue = mineField[move.first][move.second]
        if (playerFieldValue == EXPLORED_SYMBOL || playerFieldValue in "0".."9") {
            return
        }
        if (mineFieldValue == MINE_SYMBOL) {
            return
        }

        playerMoves[move.first][move.second] = when (mineFieldValue) {
            UNEXPLORED_SYMBOL -> EXPLORED_SYMBOL
            in "0".."9" -> mineFieldValue
            else -> {
                println(mineFieldValue)
                mineFieldValue
            }
        }
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
            else -> {
                openFreeSpaceFloodFill(move.getCoordinates())
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
