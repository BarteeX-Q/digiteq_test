package com.test.digiteqtest.ui.theme

import androidx.recyclerview.widget.RecyclerView
import kotlin.math.ceil

class CustomLayoutManager(private var rows: Int, private var columns: Int) :
    RecyclerView.LayoutManager() {


    fun update(rows: Int = this.rows, columns: Int = this.columns) {
        this.rows = rows
        this.columns = columns
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

        val fullRowCount = rows * columns
        val innerRowsValue = itemCount / fullRowCount.toDouble()
        val innerRowsCount = ceil(innerRowsValue).toInt()


        var biggestVerticalOffset = 0
        var biggestHorizontalOffset = 0
        var totalHorizontalOffset = 0

        var offsetY = 0
        var offsetX: Int

        for (rowIndex in 0 until rows) {
            offsetX = totalHorizontalOffset
            for (innerRowIndex in 0 until innerRowsCount) {
                for (columnIndex in 0 until columns) {
                    val rowIndexOffset = rowIndex * columns * innerRowsCount
                    val innerColumnOffset = innerRowIndex * columns
                    val index = rowIndexOffset + innerColumnOffset + columnIndex
                    if (index >= itemCount) {
                        return
                    }

                    val view = recycler.getViewForPosition(index)

                    measureChildWithMargins(view, 0, 0)

                    val viewWidth = getDecoratedMeasuredWidth(view)
                    val viewHeight = getDecoratedMeasuredHeight(view)

                    val left = offsetX
                    val top = offsetY
                    val right = left + viewWidth
                    val bottom = top + viewHeight

                    layoutDecorated(view, left, top, right, bottom)

                    addView(view, index)

                    if (viewHeight > biggestVerticalOffset) {
                        biggestVerticalOffset = viewHeight
                    }
                    offsetX += viewWidth
                    if (offsetX > biggestHorizontalOffset) {
                        biggestHorizontalOffset = offsetX
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

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollVertically(): Boolean = false

    override fun canScrollHorizontally(): Boolean = true
}