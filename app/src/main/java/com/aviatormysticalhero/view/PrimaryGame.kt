package com.aviatormysticalhero.view

import android.content.Context
import android.preference.PreferenceManager
import kotlin.math.pow

class PrimaryGame(
  private val mContext: Context,
  private val mView: PrimaryView,
  private val mhMoved: (Long) -> Unit
) {
  @JvmField
  var mGameState = GAME_NORMAL

  @JvmField
  var mGrid: GridItems? = null

  @JvmField
  var aGrid: AnimGridItems? = null

  @JvmField
  var mScore: Long = 0

  @JvmField
  var mHighScore: Long = 0
  private var rowsCount = 4

  fun newGame() {

    if (mGrid == null) mGrid = GridItems(rowsCount, rowsCount) else {
      mGrid!!.clearGrid()
    }
    aGrid = AnimGridItems(rowsCount, rowsCount)
    mHighScore = getHighScore()
    if (mScore >= mHighScore) {
      mHighScore = mScore
      recordHighScore()
    }
    mScore = 0
    mGameState = GAME_NORMAL
    addStartTiles()
    mView.mRefreshLastTime = true
    mView.resyncTime()
    mView.invalidate()
  }

  private fun addStartTiles() {
    val startTiles = 2
    for (xx in 0 until startTiles) addRandomTile()
  }

  private fun addRandomTile() {
    if (mGrid!!.isCellsAvailable) {
      val value = if (Math.random() < 0.9) 2 else 4
      val tile = GridTiles(mGrid!!.randomAvailableCell()!!, value)
      spawnTile(tile)
    }
  }

  private fun spawnTile(tile: GridTiles) {
    mGrid!!.insertTile(tile)
    aGrid!!.startAnimation(
      tile.x, tile.y, SPAWN_ANIMATION,
      SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null
    )
  }

  private fun recordHighScore() {

    val settings = PreferenceManager.getDefaultSharedPreferences(mContext)
    val editor = settings.edit()
    editor.putLong(HIGH_SCORE + rowsCount, mHighScore)
    editor.apply()
  }

  private fun getHighScore(): Long {

    val settings = PreferenceManager.getDefaultSharedPreferences(mContext)
    return settings.getLong(HIGH_SCORE + rowsCount, -1)
  }

  private fun prepareTiles() {
    for (array in mGrid!!.mField) for (tile in array) if (mGrid!!.isCellOccupied(tile)) tile!!.mMergedFrom =
      null
  }

  private fun moveTile(tile: GridTiles, cell: CellItem) {
    mGrid!!.mField[tile.x][tile.y] = null
    mGrid!!.mField[cell.x][cell.y] = tile
    tile.updatePosition(cell)
  }

  private fun gameWon(): Boolean {
    return mGameState > 0 && mGameState % 2 != 0
  }

  private fun gameLost(): Boolean {
    return mGameState == GAME_LOST
  }

  val isActive: Boolean
    get() = !(gameWon() || gameLost())

  fun move(direction: Int) {
    aGrid!!.cancelAnimations()
    if (!isActive) return
    val vector = getVector(direction)
    val traversalsX = buildTraversalsX(vector)
    val traversalsY = buildTraversalsY(vector)
    var moved = false
    prepareTiles()
    for (xx in traversalsX) {
      for (yy in traversalsY) {
        val cell = CellItem(xx, yy)
        val tile = mGrid!!.getCellContent(cell)
        if (tile != null) {
          val positions = findFarthestPosition(cell, vector)
          val next = mGrid!!.getCellContent(positions[1])
          if (next != null && next.mValue == tile.mValue && next.mMergedFrom == null) {
            val merged = GridTiles(positions[1], tile.mValue * 2)
            val temp = arrayOf(tile, next)
            merged.mMergedFrom = temp
            mGrid!!.insertTile(merged)
            mGrid!!.removeTile(tile)
            tile.updatePosition(positions[1])
            val extras = intArrayOf(xx, yy)
            aGrid!!.startAnimation(
              merged.x, merged.y, MOVE_ANIMATION,
              MOVE_ANIMATION_TIME, 0, extras
            )
            aGrid!!.startAnimation(
              merged.x, merged.y, MERGE_ANIMATION,
              SPAWN_ANIMATION_TIME, MOVE_ANIMATION_TIME, null
            )
            mScore += merged.mValue
            mHighScore = mScore.coerceAtLeast(mHighScore)

            if (merged.mValue >= winValue() && !gameWon()) {
              mGameState += GAME_WIN
              endGame()
            }
          } else {
            moveTile(tile, positions[0])
            val extras = intArrayOf(xx, yy, 0)
            aGrid!!.startAnimation(
              positions[0].x,
              positions[0].y,
              MOVE_ANIMATION,
              MOVE_ANIMATION_TIME,
              0,
              extras
            )
          }
          if (!positionsEqual(cell, tile)) {
            moved = true
            mhMoved(mScore)
          }
        }
      }
    }
    if (moved) {
      addRandomTile()
      checkLose()
    }
    mView.resyncTime()
    mView.invalidate()
  }

  private fun checkLose() {
    if (!movesAvailable() && !gameWon()) {
      mGameState = GAME_LOST
      endGame()
    }
  }

  private fun endGame() {
    aGrid!!.startAnimation(
      -1,
      -1,
      FADE_GLOBAL_ANIMATION,
      NOTIFICATION_ANIMATION_TIME,
      NOTIFICATION_DELAY_TIME,
      null
    )
    if (mScore >= mHighScore) {
      mHighScore = mScore
      recordHighScore()
    }
  }

  private fun getVector(direction: Int): CellItem {
    val map = arrayOf(
      CellItem(0, -1),
      CellItem(1, 0),
      CellItem(0, 1),
      CellItem(-1, 0)
    )
    return map[direction]
  }

  private fun buildTraversalsX(vector: CellItem): List<Int> {
    val traversals: MutableList<Int> = ArrayList()

    for (xx in 0 until rowsCount) traversals.add(xx)
    if (vector.x == 1) traversals.reverse()
    return traversals
  }

  private fun buildTraversalsY(vector: CellItem): List<Int> {
    val traversals: MutableList<Int> = ArrayList()

    for (xx in 0 until rowsCount) traversals.add(xx)
    if (vector.y == 1) traversals.reverse()
    return traversals
  }

  private fun findFarthestPosition(cell: CellItem, vector: CellItem): Array<CellItem> {
    var previous: CellItem
    var nextCell = CellItem(cell.x, cell.y)
    do {
      previous = nextCell
      nextCell = CellItem(
        previous.x + vector.x,
        previous.y + vector.y
      )
    } while (mGrid!!.isCellWithinBounds(nextCell) && mGrid!!.isCellAvailable(nextCell))
    return arrayOf(previous, nextCell)
  }

  private fun movesAvailable(): Boolean {
    return mGrid!!.isCellsAvailable || tileMatchesAvailable()
  }

  private fun tileMatchesAvailable(): Boolean {
    var tile: GridTiles?

    for (xx in 0 until rowsCount) {
      for (yy in 0 until rowsCount) {
        tile = mGrid!!.getCellContent(CellItem(xx, yy))
        if (tile != null) {
          for (direction in 0..3) {
            val vector = getVector(direction)
            val cell = CellItem(xx + vector.x, yy + vector.y)
            val other = mGrid!!.getCellContent(cell)
            if (other != null && other.mValue == tile.mValue) return true
          }
        }
      }
    }
    return false
  }

  private fun positionsEqual(first: CellItem, second: CellItem): Boolean {
    return first.x == second.x && first.y == second.y
  }

  private fun winValue(): Int {
    return if (!canContinue()) endingMaxValue else startingMaxValue
  }

  fun canContinue(): Boolean {
    return !(mGameState == GAME_ENDLESS || mGameState == GAME_ENDLESS_WON)
  }

  companion object {
    const val SPAWN_ANIMATION = -1
    const val MOVE_ANIMATION = 0
    const val MERGE_ANIMATION = 1
    const val FADE_GLOBAL_ANIMATION = 0
    private const val MOVE_ANIMATION_TIME = PrimaryView.BASE_ANIMATION_TIME.toLong()
    private const val SPAWN_ANIMATION_TIME = PrimaryView.BASE_ANIMATION_TIME.toLong()
    private const val NOTIFICATION_DELAY_TIME = MOVE_ANIMATION_TIME + SPAWN_ANIMATION_TIME
    private const val NOTIFICATION_ANIMATION_TIME = (PrimaryView.BASE_ANIMATION_TIME * 5).toLong()
    private const val startingMaxValue = 2048
    private const val GAME_WIN = 1
    private const val GAME_LOST = -1
    private const val GAME_NORMAL = 0
    private const val GAME_ENDLESS = 2
    private const val GAME_ENDLESS_WON = 3
    private const val HIGH_SCORE = "high score"
    private var endingMaxValue: Int = 0
  }

  init {
    endingMaxValue = 2.0.pow((mView.mNumCellTypes - 1).toDouble())
      .toInt()
  }
}