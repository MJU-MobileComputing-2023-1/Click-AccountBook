package com.example.click_accountbook.ui.notifications

import com.example.click_accountbook.Receipt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.R

class ReceiptsAdapter(private val context: Context) : RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {

    private var receipts: List<Receipt> = listOf()
    private val storeTotals: HashMap<String, Float> = hashMapOf()
    private val processedStores: HashSet<String> = hashSetOf()


    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.bind(receipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fragment_sort_receipts, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun getItemCount(): Int = receipts.size

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts = receipts
        calculateStoreTotals()
        notifyDataSetChanged()
    }

    inner class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val storeName: TextView = view.findViewById(R.id.storeName)
        private val totalPrice: TextView = view.findViewById(R.id.totalPrice)
        private val sortButton: Button = view.findViewById(R.id.sortButton)

        fun bind(receipt: Receipt) {
            storeName.text = receipt.storeName
            totalPrice.text = "총 구매금액: ${receipt.totalPrice}원"

            // Store totals
            val total = storeTotals[receipt.storeName]
            totalPrice.text = "총 구매금액: ${total ?: 0}원"

            sortButton.setOnClickListener {
                sortReceiptsByTotalAmount()
            }
            if (adapterPosition == 0) {
                sortButton.visibility = View.GONE
            } else {
                sortButton.visibility = View.GONE
            }
        }
    }

    private fun calculateStoreTotals() {
        storeTotals.clear()

        for (receipt in receipts) {
            val total = storeTotals[receipt.storeName]
            if (total == null) {
                storeTotals[receipt.storeName] = receipt.totalPrice
            } else {
                storeTotals[receipt.storeName] = total + receipt.totalPrice
            }
        }

        val uniqueReceipts: MutableList<Receipt> = mutableListOf()
        for (receipt in receipts) {
            if (uniqueReceipts.none { it.storeName == receipt.storeName }) {
                uniqueReceipts.add(receipt)
            }
        }

        receipts = uniqueReceipts
    }
    fun sortReceiptsByTotalAmount() {
        receipts = receipts.sortedByDescending { storeTotals[it.storeName] }
        notifyDataSetChanged()
    }

}