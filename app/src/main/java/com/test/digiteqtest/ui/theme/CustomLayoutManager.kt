package com.test.digiteqtest.ui.theme

import android.content.Context
import android.graphics.PointF
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil
import kotlin.math.min

class CustomLayoutManager(
    private var rows: Int,
    private var columns: Int,
    private var isRTL: Boolean = false,
) :
    RecyclerView.LayoutManager(), RecyclerView.SmoothScroller.ScrollVectorProvider {
    private var offsetVertical = 0
    private var offsetHorizontal = 0

    fun update(rows: Int = this.rows, columns: Int = this.columns, isRTL: Boolean = this.isRTL) {
        this.rows = rows
        this.columns = columns
        this.isRTL = isRTL
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)

        if (recycler == null) {
            return
        }

        if (itemCount == 0 || state?.isPreLayout == true) {
            removeAndRecycleAllViews(recycler)
            return
        }

        detachAndScrapAttachedViews(recycler)

        val innerRowsCount = getInnerRowsCount()

        var biggestVerticalOffset = 0
        var biggestHorizontalOffset = 0
        var totalHorizontalOffset = 0

        var offsetY = 0
        var offsetX: Int

        var rangeRows: Iterable<Int> = 0 until rows
        val rangeInnerColumns: Iterable<Int> = 0 until innerRowsCount
        var rangeColumns: Iterable<Int> = 0 until columns
        if (isRTL) {
            rangeRows = (rows - 1) downTo 0
            rangeColumns = (columns - 1) downTo 0
        }

        for (rowIndex in rangeRows) {
            offsetX = totalHorizontalOffset
            for (innerRowIndex in rangeInnerColumns) {
                for (columnIndex in rangeColumns) {
                    val index = calculateCurrentIndex(rowIndex, innerRowIndex, columnIndex)
                    if (index >= itemCount) {
                        continue
                    }

                    val view = recycler.getViewForPosition(index)
                    measureChildWithMargins(view, 0, 0)

                    val viewWidth = getDecoratedMeasuredWidth(view)
                    val viewHeight = getDecoratedMeasuredHeight(view)

                    renderItem(offsetX, offsetY, viewWidth, viewHeight, view, index)

                    offsetX += viewWidth
                    if (offsetX > biggestHorizontalOffset) {
                        biggestHorizontalOffset = offsetX
                    }
                    if (viewHeight > biggestVerticalOffset) {
                        biggestVerticalOffset = viewHeight
                    }
                }
                offsetX = totalHorizontalOffset
                offsetY += biggestVerticalOffset
                biggestVerticalOffset = 0
            }
            totalHorizontalOffset = biggestHorizontalOffset
            biggestHorizontalOffset = 0
            offsetY = 0
        }
    }

    private fun renderItem(
        offsetX: Int,
        offsetY: Int,
        viewWidth: Int,
        viewHeight: Int,
        view: View,
        index: Int
    ) {
        val left = offsetX + offsetHorizontal
        val top = offsetY + offsetVertical
        val right = left + viewWidth
        val bottom = top + viewHeight

        layoutDecorated(view, left, top, right, bottom)

        addView(view, index)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val innerRowsCount = getInnerRowsCount()
        val bottomViewIndex = innerRowsCount * columns - 1

        val topView = getChildAt(0)
        val bottomView = getChildAt(bottomViewIndex)

        val decoratedBottomView = getDecoratedBottom(bottomView!!)
        val decoratedTopView = getDecoratedTop(topView!!)
        val viewSpanHeight = decoratedBottomView - decoratedTopView
        val verticalSpace = getVerticalSpace()
        if (viewSpanHeight <= verticalSpace) {
            return 0
        }

        val delta: Int = if (dy > 0) {
            val scrollEndPosition = verticalSpace - decoratedTopView
            if (scrollEndPosition >= viewSpanHeight) {
                scrollEndPosition - viewSpanHeight
            } else {
                -dy
            }
        } else {
            val topOffset = -decoratedTopView + paddingTop
            min(-dy, topOffset)
        }
        offsetVertical += delta

        offsetChildrenVertical(delta)
        onLayoutChildren(recycler, state)

        return -delta
    }


    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        val innerRowsCount = getInnerRowsCount()
        var mostRightViewIndex = innerRowsCount * columns * (rows - 1) + columns - 1
        if (mostRightViewIndex >= itemCount - 1) {
            mostRightViewIndex = itemCount - 1
        }

        val leftViewIndex = if (isRTL) {
            mostRightViewIndex
        } else {
            0
        }
        val rightViewIndex = if (isRTL) {
            0
        } else {
            mostRightViewIndex
        }

        val leftView = getChildAt(leftViewIndex)
        val rightView = getChildAt(rightViewIndex)

        val decoratedRightView = getDecoratedRight(rightView!!)
        val decoratedLeftView = getDecoratedLeft(leftView!!)
        val viewSpanWidth = decoratedRightView - decoratedLeftView
        val horizontalSpace = getHorizontalSpace()
        if (viewSpanWidth <= horizontalSpace) {
            return 0
        }

        val delta: Int = if (dx > 0) {
            val scrollEndPosition = horizontalSpace - decoratedLeftView
            if (scrollEndPosition >= viewSpanWidth) {
                scrollEndPosition - viewSpanWidth
            } else {
                -dx
            }
        } else {
            val leftOffset = -decoratedLeftView + paddingLeft
            min(-dx, leftOffset)
        }
        offsetHorizontal += delta

        offsetChildrenHorizontal(delta)
        onLayoutChildren(recycler, state)

        return -delta
    }

    private fun getVerticalSpace(): Int = height - paddingTop - paddingBottom

    private fun getHorizontalSpace(): Int = width - paddingRight - paddingLeft

    override fun canScrollVertically(): Boolean = true

    override fun canScrollHorizontally(): Boolean = true

    override fun smoothScrollToPosition(
        recyclerView: RecyclerView?,
        state: RecyclerView.State?,
        position: Int
    ) {
        val smoothScroller = TopSnappedSmoothScroller(recyclerView!!.context)
        smoothScroller.targetPosition = position
        startSmoothScroll(smoothScroller)
    }

    override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
        if (childCount == 0) {
            return null
        }
        val firstChildPos = (getChildAt(targetPosition)!!)
        return PointF(getLeftDecorationWidth(firstChildPos).toFloat(), getTopDecorationHeight(firstChildPos).toFloat())
    }

    private fun getInnerRowsCount(): Int {
        val fullRowCount = rows * columns
        val innerRowsValue = itemCount / fullRowCount.toDouble()
        return ceil(innerRowsValue).toInt()
    }

    private fun calculateCurrentIndex(
        rowIndex: Int,
        innerRowIndex: Int,
        columnIndex: Int
    ): Int {
        val innerRowsCount = getInnerRowsCount()
        val rowIndexOffset = rowIndex * columns * innerRowsCount
        val innerColumnOffset = innerRowIndex * columns

        return rowIndexOffset + innerColumnOffset + columnIndex
    }

    private class TopSnappedSmoothScroller(context: Context) :
        LinearSmoothScroller(context) {

        override fun computeScrollVectorForPosition(targetPosition: Int): PointF? {
            return (layoutManager as CustomLayoutManager).computeScrollVectorForPosition(targetPosition)
        }

        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
}