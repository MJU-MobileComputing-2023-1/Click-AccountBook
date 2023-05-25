package com.example.click_accountbook.ui.home

import com.example.click_accountbook.Receipt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.R

class ReceiptsAdapter(private val context: Context) : RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {

    private var receipts: List<Receipt> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.receipt_item, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun getItemCount(): Int = receipts.size

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = receipts[position]
        // TODO: Load the image data from the receipt into the ImageView
    }

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts = receipts
        notifyDataSetChanged()
    }

    inner class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.receipt_image)
    }
}
