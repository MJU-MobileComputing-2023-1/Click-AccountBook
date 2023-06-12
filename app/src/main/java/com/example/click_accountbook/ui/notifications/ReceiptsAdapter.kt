package com.example.click_accountbook.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.click_accountbook.Item
import com.example.click_accountbook.R
import java.text.NumberFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ReceiptsAdapter(private val context: Context) : RecyclerView.Adapter<ReceiptsAdapter.ItemViewHolder>() {

    var items: List<Item> = listOf()
    private val storeTotals: HashMap<String, Float> = hashMapOf()
    private val processedStores: HashSet<String> = hashSetOf()


    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.sort_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    fun updateItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun sortItemsByPriceAscending() {
        items = items.sortedBy { it.itemPrice }
        notifyDataSetChanged()
    }

    fun sortItemsByPriceDescending() {
        items = items.sortedByDescending { it.itemPrice }
        notifyDataSetChanged()
    }

    inner class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val itemName: TextView = view.findViewById(R.id.itemName)
        private val itemPrice: TextView = view.findViewById(R.id.itemPrice)

        fun bind(item: Item) {
            itemName.text = item.itemName

            val format = NumberFormat.getNumberInstance(Locale.US)
            itemPrice.text = "가격: ${format.format(item.itemPrice)}원"
        }
    }
}