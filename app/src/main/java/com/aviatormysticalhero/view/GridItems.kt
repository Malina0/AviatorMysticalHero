package com.aviatormysticalhero.view

import kotlin.math.floor

class GridItems(sizeX: Int, sizeY: Int) {
  val mField: Array<Array<GridTiles?>>

  fun randomAvailableCell(): CellItem? {
    val availableCells = getAvailableCells()
    return if (availableCells.size >= 1) availableCells[floor(Math.random() * availableCells.size)
      .toInt()] else null
  }

  private fun getAvailableCells(): ArrayList<CellItem> {
    val availableCells = ArrayList<CellItem>()
    for (xx in mField.indices) for (yy in 0 until mField[0].size)
      if (mField[xx][yy] == null) availableCells.add(
        CellItem(xx, yy)
      )
    return availableCells
  }

  val isCellsAvailable: Boolean
    get() = getAvailableCells().size >= 1

  fun isCellAvailable(cell: CellItem?): Boolean {
    return !isCellOccupied(cell)
  }

  fun isCellOccupied(cell: CellItem?): Boolean {
    return getCellContent(cell) != null
  }

  fun getCellContent(cell: CellItem?): GridTiles? {
    return if (cell != null && isCellWithinBounds(cell)) mField[cell.x][cell.y] else null
  }

  fun getCellContent(x: Int, y: Int): GridTiles? {
    return if (isCellWithinBounds(x, y)) mField[x][y] else null
  }

  fun isCellWithinBounds(cell: CellItem): Boolean {
    return 0 <= cell.x && cell.x < mField.size && 0 <= cell.y && cell.y < mField[0].size
  }

  private fun isCellWithinBounds(x: Int, y: Int): Boolean {
    return 0 <= x && x < mField.size && 0 <= y && y < mField[0].size
  }

  fun insertTile(tile: GridTiles) {
    mField[tile.x][tile.y] = tile
  }

  fun removeTile(tile: GridTiles) {
    mField[tile.x][tile.y] = null
  }

  fun clearGrid() {
    for (xx in mField.indices) for (yy in 0 until mField[0].size) mField[xx][yy] = null
  }

  init {
    mField = Array(sizeX) { arrayOfNulls(sizeY) }
    clearGrid()
  }
}