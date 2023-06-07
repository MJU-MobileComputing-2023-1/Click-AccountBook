package com.example.click_accountbook.ui.home

import com.example.click_accountbook.Receipt
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.R
import com.bumptech.glide.Glide
import com.example.click_accountbook.DB
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReceiptsAdapter(private val context: Context) : RecyclerView.Adapter<ReceiptsAdapter.ReceiptViewHolder>() {

    private var receipts: List<Receipt> = listOf()
    private val db = DB.getDatabase(context)
    private val dbDao = db.DBDao()

    override fun onBindViewHolder(holder: ReceiptViewHolder, position: Int) {
        val receipt = receipts[position]
        val imageId = receipt.imageId
        GlobalScope.launch(Dispatchers.IO) {
            val image = imageId?.let { dbDao.getImage(it) }
            if (image != null) {
                withContext(Dispatchers.Main) {
                    Glide.with(context).load(image.path).into(holder.imageView)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReceiptViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.receipt_item, parent, false)
        return ReceiptViewHolder(view)
    }

    override fun getItemCount(): Int = receipts.size

    fun updateReceipts(receipts: List<Receipt>) {
        this.receipts = receipts
        notifyDataSetChanged()
    }

    inner class ReceiptViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.receipt_image)

        init {
            imageView.rotation = 0f
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        }
    }
}