package com.aviatormysticalhero.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import kotlin.math.abs

internal class GestureInputListener(private val mView: PrimaryView) : OnTouchListener {
  private var x = 0f
  private var y = 0f
  private var mLastDx = 0f
  private var mLastDy = 0f
  private var mPreviousX = 0f
  private var mPreviousY = 0f
  private var mStartingX = 0f
  private var mStartingY = 0f
  private var mPreviousDirection = 1
  private var mVeryLastDirection = 1
  private var mHasMoved = false

  @SuppressLint("ClickableViewAccessibility")
  override fun onTouch(view: View, event: MotionEvent): Boolean {
    when (event.action) {
      MotionEvent.ACTION_DOWN -> {
        x = event.x
        y = event.y
        mStartingX = x
        mStartingY = y
        mPreviousX = x
        mPreviousY = y
        mLastDx = 0f
        mLastDy = 0f
        mHasMoved = false
        return true
      }

      MotionEvent.ACTION_MOVE -> {
        x = event.x
        y = event.y
        if (mView.mGame.isActive) {
          val dx = x - mPreviousX
          if (abs(mLastDx + dx) < abs(mLastDx) + abs(dx) && abs(dx) > RESET_STARTING && abs(
              x - mStartingX
            ) > SWIPE_MIN_DISTANCE
          ) {
            mStartingX = x
            mStartingY = y
            mLastDx = dx
            mPreviousDirection = mVeryLastDirection
          }
          if (mLastDx == 0f) {
            mLastDx = dx
          }
          val dy = y - mPreviousY
          if (abs(mLastDy + dy) < abs(mLastDy) + abs(dy) && abs(dy) > RESET_STARTING && abs(
              y - mStartingY
            ) > SWIPE_MIN_DISTANCE
          ) {
            mStartingX = x
            mStartingY = y
            mLastDy = dy
            mPreviousDirection = mVeryLastDirection
          }
          if (mLastDy == 0f) {
            mLastDy = dy
          }
          if (pathMoved() > SWIPE_MIN_DISTANCE * SWIPE_MIN_DISTANCE && !mHasMoved) {
            var moved = false
            if ((dy >= SWIPE_THRESHOLD_VELOCITY && abs(dy) >= abs(dx) || y - mStartingY >= MOVE_THRESHOLD) && mPreviousDirection % 2 != 0) {
              moved = true
              mPreviousDirection *= 2
              mVeryLastDirection = 2
              mView.mGame.move(2)
            } else if ((dy <= -SWIPE_THRESHOLD_VELOCITY && abs(dy) >= abs(dx) || y - mStartingY <= -MOVE_THRESHOLD) && mPreviousDirection % 3 != 0) {
              moved = true
              mPreviousDirection *= 3
              mVeryLastDirection = 3
              mView.mGame.move(0)
            }
            if ((dx >= SWIPE_THRESHOLD_VELOCITY && abs(dx) >= abs(dy) || x - mStartingX >= MOVE_THRESHOLD) && mPreviousDirection % 5 != 0) {
              moved = true
              mPreviousDirection *= 5
              mVeryLastDirection = 5
              mView.mGame.move(1)
            } else if ((dx <= -SWIPE_THRESHOLD_VELOCITY && abs(dx) >= abs(dy) || x - mStartingX <= -MOVE_THRESHOLD) && mPreviousDirection % 7 != 0) {
              moved = true
              mPreviousDirection *= 7
              mVeryLastDirection = 7
              mView.mGame.move(3)
            }
            if (moved) {
              mHasMoved = true
              mStartingX = x
              mStartingY = y
            }
          }
        }
        mPreviousX = x
        mPreviousY = y
        return true
      }

      MotionEvent.ACTION_UP -> {
        x = event.x
        y = event.y
        mPreviousDirection = 1
        mVeryLastDirection = 1
      }
    }
    return true
  }

  private fun pathMoved(): Float {
    return (x - mStartingX) * (x - mStartingX) + (y - mStartingY) * (y - mStartingY)
  }

  companion object {
    private const val SWIPE_MIN_DISTANCE = 0
    private const val SWIPE_THRESHOLD_VELOCITY = 25
    private const val MOVE_THRESHOLD = 250
    private const val RESET_STARTING = 10
  }
}