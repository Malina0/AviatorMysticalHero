package com.aviatormysticalhero.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.aviatormysticalhero.R

@SuppressLint("ViewConstructor")
class PrimaryView(
  private val mhContext: Context,
  private val mhEnd: (Long) -> Unit,
  private val mhMoved: (Long) -> Unit
) : View(mhContext) {
  @JvmField
  val mNumCellTypes = 21
  private val mBitmapCell = arrayOfNulls<BitmapDrawable>(mNumCellTypes)

  val mGame: PrimaryGame by lazy {
    PrimaryGame(mhContext, this, mhMoved = { mhScore ->
      mhMoved(mhScore)
    })
  }
  private val mPaint = Paint()

  @JvmField
  var mStartingX = 0

  @JvmField
  var mStartingY = 0

  @JvmField
  var mEndingX = 0

  @JvmField
  var mEndingY = 0

  @JvmField
  var sYIcons = 0

  @JvmField
  var sXNewGame = 0

  @JvmField
  var sXUndo = 0

  @JvmField
  var sXRemoveTiles = 0

  @JvmField
  var sXSave = 0

  @JvmField
  var sXLoad = 0

  @JvmField
  var mIconSize = 0

  @JvmField
  var mRefreshLastTime = true
  private var mLastFPSTime = System.nanoTime()
  private var mTitleTextSize = 0f
  private var mBodyTextSize = 0f
  private var mHeaderTextSize = 0f
  private var mGameOverTextSize = 0f
  private var mCellSize = 0
  private var mTextSize = 0f
  private var mCellTextSize = 0f
  private var mGridWidth = 0
  private var mTextPaddingSize = 0
  private var mIconPaddingSize = 0
  private var mBackgroundRectangle: Drawable? = null
  private var mBackground: Bitmap? = null
  private var sYAll = 0
  private var mTitleStartYAll = 0
  private var mBodyStartYAll = 0
  private var eYAll = 0
  private var rowsCount = 4

  public override fun onDraw(canvas: Canvas) {
    canvas.drawBitmap(mBackground!!, 0f, 0f, mPaint)
    drawCells(canvas)
    if (!mGame.canContinue()) {
      mhEnd(mGame.mScore)
    }
    if (!mGame.isActive) {
      mhEnd(mGame.mScore)
      if (!mGame.aGrid!!.isAnimationActive) {
        mhEnd(mGame.mScore)
      }
    }
    if (mGame.aGrid!!.isAnimationActive) {
      invalidate(mStartingX, mStartingY, mEndingX, mEndingY)
      tick()
    } else if (!mGame.isActive && mRefreshLastTime) {
      invalidate()
      mRefreshLastTime = false
    }
  }

  override fun onSizeChanged(width: Int, height: Int, oldW: Int, oldH: Int) {
    super.onSizeChanged(width, height, oldW, oldH)
    getLayout(width, height)
    createBitmapCells()
    createBackgroundBitmap(width, height)
  }

  private fun drawBackground(canvas: Canvas) {
    drawDrawable(canvas, mBackgroundRectangle, mStartingX, mStartingY, mEndingX, mEndingY)
  }

  @SuppressLint("UseCompatLoadingForDrawables")
  private fun getDrawable(resId: Int): Drawable {
    return resources.getDrawable(resId)
  }

  private fun drawDrawable(
    canvas: Canvas,
    draw: Drawable?,
    startingX: Int,
    startingY: Int,
    endingX: Int,
    endingY: Int
  ) {
    draw!!.setBounds(startingX, startingY, endingX, endingY)
    draw.draw(canvas)
  }

  private fun drawBackgroundGrid(canvas: Canvas) {
    val backgroundCell = getDrawable(R.drawable.mh_cell_tint)
    for (xx in 0 until rowsCount) for (yy in 0 until rowsCount) {
      val sX = mStartingX + mGridWidth + (mCellSize + mGridWidth) * xx
      val eX = sX + mCellSize
      val sY = mStartingY + mGridWidth + (mCellSize + mGridWidth) * yy
      val eY = sY + mCellSize
      drawDrawable(canvas, backgroundCell, sX, sY, eX, eY)
    }
  }

  private fun drawCells(canvas: Canvas) {
    mPaint.textSize = mTextSize
    mPaint.textAlign = Paint.Align.CENTER
    for (xx in 0 until rowsCount) {
      for (yy in 0 until rowsCount) {
        val sX = mStartingX + mGridWidth + (mCellSize + mGridWidth) * xx
        val eX = sX + mCellSize
        val sY = mStartingY + mGridWidth + (mCellSize + mGridWidth) * yy
        val eY = sY + mCellSize
        val currentTile = mGame.mGrid!!.getCellContent(xx, yy)
        if (currentTile != null) {
          val value = currentTile.mValue
          val index = log2(value)
          val aArray = mGame.aGrid!!.getAnimationCell(xx, yy)
          var animated = false
          for (i in aArray.indices.reversed()) {
            val aCell = aArray[i]
            if (aCell.getmAnimationType() == PrimaryGame.SPAWN_ANIMATION) animated =
              true
            if (!aCell.isActive) continue
            if (aCell.getmAnimationType() == PrimaryGame.SPAWN_ANIMATION) {
              val percentDone = aCell.percentageDone
              val textScaleSize = percentDone.toFloat()
              mPaint.textSize = mTextSize * textScaleSize
              val cellScaleSize = mCellSize / 2 * (1 - textScaleSize)
              mBitmapCell[index]!!.setBounds(
                (sX + cellScaleSize).toInt(),
                (sY + cellScaleSize).toInt(),
                (eX - cellScaleSize).toInt(),
                (eY - cellScaleSize).toInt()
              )
              mBitmapCell[index]!!.draw(canvas)
            } else if (aCell.getmAnimationType() == PrimaryGame.MERGE_ANIMATION) {
              val percentDone = aCell.percentageDone
              val textScaleSize =
                (1 + INITIAL_VELOCITY * percentDone + MERGING_ACCELERATION * percentDone * percentDone / 2).toFloat()
              mPaint.textSize = mTextSize * textScaleSize
              val cellScaleSize = mCellSize / 2 * (1 - textScaleSize)
              mBitmapCell[index]!!.setBounds(
                (sX + cellScaleSize).toInt(),
                (sY + cellScaleSize).toInt(),
                (eX - cellScaleSize).toInt(),
                (eY - cellScaleSize).toInt()
              )
              mBitmapCell[index]!!.draw(canvas)
            } else if (aCell.getmAnimationType() == PrimaryGame.MOVE_ANIMATION) {
              val percentDone = aCell.percentageDone
              var tempIndex = index
              if (aArray.size >= 2) tempIndex = tempIndex - 1
              val previousX = aCell.mExtras[0]
              val previousY = aCell.mExtras[1]
              val currentX = currentTile.x
              val currentY = currentTile.y
              val dX =
                ((currentX - previousX) * (mCellSize + mGridWidth) * (percentDone - 1) * 1.0).toInt()
              val dY =
                ((currentY - previousY) * (mCellSize + mGridWidth) * (percentDone - 1) * 1.0).toInt()
              mBitmapCell[tempIndex]!!.setBounds(sX + dX, sY + dY, eX + dX, eY + dY)
              mBitmapCell[tempIndex]!!.draw(canvas)
            }
            animated = true
          }
          if (!animated) {
            mBitmapCell[index]!!.setBounds(sX, sY, eX, eY)
            mBitmapCell[index]!!.draw(canvas)
          }
        }
      }
    }
  }

  private fun createBackgroundBitmap(width: Int, height: Int) {
    mBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(mBackground!!)
    drawBackground(canvas)
    drawBackgroundGrid(canvas)
  }

  private fun createBitmapCells() {
    val resources = resources
    val cellRectangleIds = cellRectangleIds
    mPaint.textAlign = Paint.Align.CENTER
    for (xx in 1 until mBitmapCell.size) {
      val bitmap = Bitmap.createBitmap(mCellSize, mCellSize, Bitmap.Config.ARGB_8888)
      val canvas = Canvas(bitmap)
      drawDrawable(canvas, getDrawable(cellRectangleIds[xx]), 0, 0, mCellSize, mCellSize)
      mBitmapCell[xx] = BitmapDrawable(resources, bitmap)
    }
  }

  private val cellRectangleIds: IntArray
    get() {
      val cellRectangleIds = IntArray(mNumCellTypes)
      cellRectangleIds[0] = R.drawable.mh_plane_1
      cellRectangleIds[1] = R.drawable.mh_plane_2
      cellRectangleIds[2] = R.drawable.mh_plane_3
      cellRectangleIds[3] = R.drawable.mh_plane_4
      cellRectangleIds[4] = R.drawable.mh_plane_5
      cellRectangleIds[5] = R.drawable.mh_plane_6
      cellRectangleIds[6] = R.drawable.mh_plane_7
      cellRectangleIds[7] = R.drawable.mh_plane_8
      cellRectangleIds[8] = R.drawable.mh_plane_9
      cellRectangleIds[9] = R.drawable.mh_plane_10
      cellRectangleIds[10] = R.drawable.mh_plane_11
      cellRectangleIds[11] = R.drawable.mh_plane_12
      for (xx in 12 until cellRectangleIds.size) cellRectangleIds[xx] =
        R.drawable.mh_plane_13
      return cellRectangleIds
    }

  private fun tick() {
    val currentTime = System.nanoTime()
    mGame.aGrid!!.tickAll(currentTime - mLastFPSTime)
    mLastFPSTime = currentTime
  }

  fun resyncTime() {
    mLastFPSTime = System.nanoTime()
  }

  private fun getLayout(width: Int, height: Int) {
    mCellSize = (width / (rowsCount + 1)).coerceAtMost(height / (rowsCount + 3))
    mGridWidth = mCellSize / (rowsCount + 3)
    val screenMiddleX = width / 2
    val screenMiddleY = height / 2
    val boardMiddleY = screenMiddleY + mCellSize / 2
    mIconSize = mCellSize / 2
    val halfNumSquaresX = rowsCount / 2.0
    val halfNumSquaresY = rowsCount / 2.0
    mStartingX =
      (screenMiddleX - (mCellSize + mGridWidth) * halfNumSquaresX - mGridWidth / 2).toInt()
    mEndingX =
      (screenMiddleX + (mCellSize + mGridWidth) * halfNumSquaresX + mGridWidth / 2).toInt()
    mStartingY =
      (boardMiddleY - (mCellSize + mGridWidth) * halfNumSquaresY - mGridWidth / 2).toInt()
    mEndingY =
      (boardMiddleY + (mCellSize + mGridWidth) * halfNumSquaresY + mGridWidth / 2).toInt()
    val widthWithPadding = (mEndingX - mStartingX).toFloat()
    mPaint.textSize = mCellSize.toFloat()
    mTextSize =
      mCellSize * mCellSize / mCellSize.toFloat().coerceAtLeast(mPaint.measureText("0000"))
    mPaint.textAlign = Paint.Align.CENTER
    mPaint.textSize = 1000f
    mGameOverTextSize = (1000f * ((widthWithPadding - mGridWidth * 2) / mPaint.measureText(
      "GameOver"
    ))).coerceAtMost(mTextSize * 2)
      .coerceAtMost(1000f * ((widthWithPadding - mGridWidth * 2) / mPaint.measureText("win")))
    mPaint.textSize = mCellSize.toFloat()
    mCellTextSize = mTextSize
    mTitleTextSize = mTextSize / 3
    mBodyTextSize = (mTextSize / 1.5).toFloat()
    mHeaderTextSize = mTextSize * 2
    mTextPaddingSize = (mTextSize / 3).toInt()
    mIconPaddingSize = (mTextSize / 5).toInt()
    mPaint.textSize = mTitleTextSize
    var textShiftYAll = centerText()
    sYAll = (mStartingY - mCellSize * 1.5).toInt()
    mTitleStartYAll = (sYAll + mTextPaddingSize + mTitleTextSize / 2 - textShiftYAll).toInt()
    mBodyStartYAll =
      (mTitleStartYAll + mTextPaddingSize + mTitleTextSize / 2 + mBodyTextSize / 2).toInt()
    mPaint.textSize = mBodyTextSize
    textShiftYAll = centerText()
    eYAll = (mBodyStartYAll + textShiftYAll + mBodyTextSize / 2 + mTextPaddingSize).toInt()
    sYIcons = (mStartingY + eYAll) / 2 - mIconSize / 2
    sXNewGame = mEndingX - mIconSize
    sXUndo = sXNewGame - mIconSize - mIconPaddingSize
    sXRemoveTiles = sXUndo - mIconSize - mIconPaddingSize
    sXLoad = sXRemoveTiles - mIconSize - mIconPaddingSize
    sXSave = sXLoad - mIconSize - mIconPaddingSize
    resyncTime()
  }

  private fun centerText(): Int {
    return ((mPaint.descent() + mPaint.ascent()) / 2).toInt()
  }

  companion object {
    const val BASE_ANIMATION_TIME = 100000000
    private const val MERGING_ACCELERATION = (-0.5).toFloat()
    private const val INITIAL_VELOCITY = (1 - MERGING_ACCELERATION) / 4
    private fun log2(n: Int): Int {
      require(n > 0)
      return 31 - Integer.numberOfLeadingZeros(n)
    }
  }

  init {
    try {
      mBackgroundRectangle = getDrawable(R.drawable.mh_cell_bg)
      val font = ResourcesCompat.getFont(mhContext, R.font.montserrat_medium_mh)
      mPaint.typeface = font
      mPaint.isAntiAlias = true
    } catch (_: Exception) {
    }
    setOnTouchListener(GestureInputListener(this))
    mGame.newGame()
  }
}