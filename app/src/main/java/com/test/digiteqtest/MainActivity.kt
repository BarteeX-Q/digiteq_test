package com.test.digiteqtest

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.RecyclerView
import com.test.digiteqtest.ui.theme.CustomAdapter
import com.test.digiteqtest.ui.theme.CustomLayoutManager

class MainActivity : ComponentActivity() {

    private lateinit var columnsInput: EditText
    private lateinit var rowsInput: EditText
    private lateinit var itemsCountInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)

        columnsInput = findViewById(R.id.columns)
        rowsInput = findViewById(R.id.rows)
        itemsCountInput = findViewById(R.id.items)

        val initialDataCount = 100
        val initialRows = 3
        val initialColumns = 5

        columnsInput.setText(initialColumns.toString())
        rowsInput.setText(initialRows.toString())
        itemsCountInput.setText(initialDataCount.toString())


        val data = List(initialDataCount) { index -> "${index + 1}" }.toTypedArray()

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.adapter = CustomAdapter(data)
        recyclerView.layoutManager = CustomLayoutManager(rows = initialRows, columns = initialColumns)


        columnsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                val text = input.toString()
                if (text.isEmpty()) {
                    return
                }
                val columns = text.toInt()
                (recyclerView.layoutManager as CustomLayoutManager).update(columns = columns)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })


        rowsInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                val text = input.toString()
                if (text.isEmpty()) {
                    return
                }
                val rows = text.toInt()
                (recyclerView.layoutManager as CustomLayoutManager).update(rows = rows)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        itemsCountInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(input: Editable?) {
                val text = input.toString()
                if (text.isEmpty()) {
                    return
                }
                val count = text.toInt()
                val generatedData = List(count) { index -> "${index + 1}" }.toTypedArray()
                (recyclerView.adapter as CustomAdapter).updateData(generatedData)
                recyclerView.adapter!!.notifyDataSetChanged()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }
}