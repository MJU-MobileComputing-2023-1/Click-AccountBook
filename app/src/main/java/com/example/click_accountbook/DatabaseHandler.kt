package com.example.click_accountbook

// DatabaseHandler.kt
import Image
import Item
import Receipt
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class DatabaseHandler(context: Context) {
    private val db = DB.getDatabase(context)
    private val dbDao = db.DBDao()

    // Insert operations
    fun insertReceipt(receipt: Receipt) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertReceipt(receipt)
        }
    }

    fun insertImage(image: Image) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertImage(image)
        }
    }

    fun insertItem(item: Item) {
        CoroutineScope(Dispatchers.IO).launch {
            dbDao.insertItem(item)
        }
    }

    // Fetch operations
    suspend fun getAllReceipts(): List<Receipt> {
        return dbDao.getAllReceipts()
    }

    suspend fun getImagesForReceipt(receiptId: String): List<Image> {
        return dbDao.getImagesForReceipt(receiptId)
    }

    suspend fun getItemsForReceipt(receiptId: String): List<Item> {
        return dbDao.getItemsForReceipt(receiptId)
    }

    // Filter operations
    suspend fun getReceiptsByStore(storeName: String): List<Receipt> {
        return dbDao.getReceiptsByStore(storeName)
    }

    suspend fun getReceiptsByCardNumber(cardNumber: String): List<Receipt> {
        return dbDao.getReceiptsByCardNumber(cardNumber)
    }

    suspend fun getItemsByName(itemName: String): List<Item> {
        return dbDao.getItemsByName(itemName)
    }
}
