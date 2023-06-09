package com.example.click_accountbook.ui.notifications

import com.example.click_accountbook.Receipt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.R
import java.text.NumberFormat
import java.util.Locale

class ReceiptsAdapter(private val context: Context) : RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {

    var receipts: List<Receipt> = listOf()
    private val storeTotals: HashMap<String, Float> = hashMapOf()
    private val processedStores: HashSet<String> = hashSetOf()


    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = receipts[position]
        holder.bind(receipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sort_item, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun getItemCount(): Int = receipts.size

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts = receipts
        calculateStoreTotals()
        notifyDataSetChanged()
    }

    fun sortReceiptsByTotalAmountAscending() {
        receipts = receipts.sortedBy { storeTotals[it.storeName] }
        notifyDataSetChanged()
    }

    fun sortReceiptsByTotalAmountDescending() {
        receipts = receipts.sortedByDescending { storeTotals[it.storeName] }
        notifyDataSetChanged()
    }

    inner class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val storeName: TextView = view.findViewById(R.id.storeName)
        private val totalPrice: TextView = view.findViewById(R.id.totalPrice)

        fun bind(receipt: Receipt) {
            storeName.text = receipt.storeName
            totalPrice.text = "총 구매금액: ${receipt.totalPrice}원"

            // Store totals
            val total = storeTotals[receipt.storeName]?.toInt()
            val formatter = NumberFormat.getNumberInstance(Locale.US)
            totalPrice.text = "총 구매금액: ${formatter.format(total ?: 0)}원"
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

    fun sortReceiptsByTotalAmount(descending: Boolean) {
        receipts = if (descending) {
            receipts.sortedByDescending { storeTotals[it.storeName] }
        } else {
            receipts.sortedBy { storeTotals[it.storeName] }
        }
        notifyDataSetChanged()
    }
    fun sortReceiptsByTotalAmount() {
        receipts = receipts.sortedByDescending { storeTotals[it.storeName] }
        notifyDataSetChanged()
    }

}