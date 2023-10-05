package com.test.digiteqtest

import android.os.Bundle
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.test.digiteqtest.ui.theme.CustomAdapter
import com.test.digiteqtest.ui.theme.CustomLayoutManager

class MainActivity : ComponentActivity() {

    private lateinit var columnsInput: SeekBar
    private lateinit var rowsInput: SeekBar
    private lateinit var itemsCountInput: SeekBar
    private lateinit var rtlInput: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        columnsInput = findViewById(R.id.columns)
        rowsInput = findViewById(R.id.rows)
        itemsCountInput = findViewById(R.id.items)
        rtlInput = findViewById(R.id.rtl)

        val initialDataCount = 100
        val initialRows = 3
        val initialColumns = 5

        columnsInput.progress = initialColumns
        rowsInput.progress = initialRows
        itemsCountInput.progress = initialDataCount

        val data = List(initialDataCount) { index -> "${index + 1}" }.toTypedArray()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = CustomAdapter(data, baseContext)
        recyclerView.layoutManager =
            CustomLayoutManager(rows = initialRows, columns = initialColumns)


        columnsInput.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                (recyclerView.layoutManager as CustomLayoutManager).update(columns = progress)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })


        rowsInput.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                (recyclerView.layoutManager as CustomLayoutManager).update(rows = progress)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        rtlInput.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                (recyclerView.layoutManager as CustomLayoutManager).update(isRTL = isChecked)
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        })

        itemsCountInput.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val generatedData = List(progress) { index -> "${index + 1}" }.toTypedArray()
                (recyclerView.adapter as CustomAdapter).updateData(generatedData)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }
}