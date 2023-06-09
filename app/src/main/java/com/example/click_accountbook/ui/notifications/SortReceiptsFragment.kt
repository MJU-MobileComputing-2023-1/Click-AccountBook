package com.example.click_accountbook.ui.notifications

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.DatabaseHandler
import com.example.click_accountbook.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SortReceiptsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ReceiptsAdapter
    private lateinit var db: DatabaseHandler
    private lateinit var root: View // root 변수 선언
    private lateinit var sortButton: Button

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

        // Initialize database handle
        sortButton = root.findViewById(R.id.sortButton)
        sortButton.setOnClickListener {
            adapter.sortReceiptsByTotalAmount()
        }

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
            val receipts = db.getAllReceipts()

            withContext(Dispatchers.Main) {
                if (receipts.isNotEmpty()) {
                    adapter.updateReceipts(receipts)

                } else {
                    // Show a message if no receipts are available
                    val tvEmpty = root.findViewById<TextView>(R.id.storeName)
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
            val receipts = db.getAllReceipts()
            withContext(Dispatchers.Main) {
                adapter.updateReceipts(receipts)
            }
        }
    }
}