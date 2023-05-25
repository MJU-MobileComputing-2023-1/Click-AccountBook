package com.example.click_accountbook

// DatabaseHandler.kt
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.click_accountbook.DB
import com.example.click_accountbook.Receipt
import com.example.click_accountbook.Image
import com.example.click_accountbook.Item
import kotlinx.coroutines.withContext


import java.util.*

class DatabaseHandler(context: Context) {
    private val db = DB.getDatabase(context)
    private val dbDao = db.DBDao()

    // Insert operations
    fun insertReceipt(receipt: Receipt) {
        CoroutineScope(Dispatchers.IO).launch {
            // Generate a new receipt ID
            val newReceiptId = getNextReceiptId()

            // Assign the generated ID to the receipt object
            val receiptWithId = receipt.copy(id = newReceiptId)

            // Insert the receipt with the assigned ID into the database
            dbDao.insertReceipt(receiptWithId)
        }
    }

    private suspend fun getNextReceiptId(): String {
        return withContext(Dispatchers.IO) {
            val receipts = dbDao.getAllReceipts()
            val maxId = receipts.maxByOrNull { it.id.toIntOrNull() ?: 0 }?.id ?: "-1"
            val newId = maxId.toInt() + 1
            newId.toString()
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

    suspend fun getImages(): List<Image> {
        return dbDao.getImages()
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

