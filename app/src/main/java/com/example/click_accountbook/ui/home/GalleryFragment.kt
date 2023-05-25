package com.example.click_accountbook.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.AddReceiptActivity
import com.example.click_accountbook.R
import com.example.click_accountbook.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GalleryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fab: FloatingActionButton
    private lateinit var adapter: ReceiptsAdapter
    private lateinit var db: DatabaseHandler

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        // Initialize RecyclerView, adapter
        recyclerView = root.findViewById(R.id.receipts_recycler_view)
        fab = root.findViewById(R.id.fab_add_receipt)
        adapter = ReceiptsAdapter(requireContext())

        // Set RecyclerView properties
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        recyclerView.adapter = adapter

        // Set FAB click listener to start AddReceiptActivity
        fab.setOnClickListener {
            val intent = Intent(context, AddReceiptActivity::class.java)
            startActivity(intent)
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
                    // This needs to be a TextView in your fragment_gallery.xml layout
                    val tvEmpty = view?.findViewById<TextView>(R.id.tv_empty)
                    tvEmpty?.text = "선택한 영수증이 없습니다"
                    tvEmpty?.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload the receipts when the fragment resumes
        loadReceipts()
    }
}

