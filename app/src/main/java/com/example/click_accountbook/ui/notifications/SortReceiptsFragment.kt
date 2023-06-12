package com.example.click_accountbook.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.DatabaseHandler
import com.example.click_accountbook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

class SortReceiptsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptsAdapter
    private lateinit var db: DatabaseHandler
    private lateinit var root: View
    private lateinit var sortSpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_sort_receipts, container, false)

        // Initialize RecyclerView, adapter
        recyclerView = root.findViewById(R.id.recyclerView)
        adapter = ReceiptsAdapter(requireContext())
        // Set RecyclerView properties
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Initialize Spinner
        sortSpinner = root.findViewById(R.id.sortSpinner)
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.sort_array,
            R.layout.spinner_item // 커스텀 레이아웃을 사용합니다.
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            sortSpinner.adapter = adapter
        }

        sortSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                when (position) {
                    0 -> adapter.updateItems(adapter.items)
                    1 -> adapter.sortItemsByPriceAscending()
                    2 -> adapter.sortItemsByPriceDescending()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        })

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize database handler
        db = DatabaseHandler(requireContext())
        // Load the receipts
        loadReceipts()
    }

    private fun loadReceipts() {
        GlobalScope.launch(Dispatchers.IO) {
            val items = db.getAllItems()

            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    adapter.updateItems(items)

                } else {
                    // Show a message if no receipts are available
                    val tvEmpty = root.findViewById<TextView>(R.id.itemName)
                    tvEmpty?.text = "데이터가 없습니다"
                    tvEmpty?.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun loadItems() {
        GlobalScope.launch(Dispatchers.IO) {
            val items = db.getAllItems()
            withContext(Dispatchers.Main) {
                if (items.isNotEmpty()) {
                    adapter.updateItems(items)
                } else {
                    // Show a message if no items are available
                    val tvEmpty = root.findViewById<TextView>(R.id.itemName)
                    tvEmpty?.text = "데이터가 없습니다"
                    tvEmpty?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload the receipts when the fragment resumes
        GlobalScope.launch(Dispatchers.IO) {
            val items = db.getAllItems()
            withContext(Dispatchers.Main) {
                adapter.updateItems(items)
            }
        }
    }
}