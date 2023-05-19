import android.content.Context
import androidx.room.*
import java.util.*

// Receipt Table
@Entity(tableName = "receipts",
    indices = [Index(value = ["storeName", "paymentCardNumber"])]
)
data class Receipt(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "storeName") val storeName: String,
    @ColumnInfo(name = "storeSubName") val storeSubName: String?,
    @ColumnInfo(name = "storeBizNum") val storeBizNum: String?,
    @ColumnInfo(name = "storeAddress") val storeAddress: String?,
    @ColumnInfo(name = "storeTel") val storeTel: String?,
    @ColumnInfo(name = "paymentDate") val paymentDate: Date,
    @ColumnInfo(name = "paymentTime") val paymentTime: Date,
    @ColumnInfo(name = "paymentCardCompany") val paymentCardCompany: String?,
    @ColumnInfo(name = "paymentCardNumber") val paymentCardNumber: String?,
    @ColumnInfo(name = "paymentConfirmNum") val paymentConfirmNum: String?,
    @ColumnInfo(name = "totalPrice") val totalPrice: Float,
    @ColumnInfo(name = "estimatedLanguage") val estimatedLanguage: String
)

// ImageDataTable
@Entity(tableName = "images",
    foreignKeys = [ForeignKey(entity = Receipt::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("receiptId"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["receiptId"])]
)
data class Image(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "format") val format: String,
    @ColumnInfo(name = "data") val data: ByteArray,
    @ColumnInfo(name = "timestamp") val timestamp: Date,
    @ColumnInfo(name = "receiptId") val receiptId: String
)

// ItemDataTable
@Entity(tableName = "items",
    foreignKeys = [ForeignKey(entity = Receipt::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("receiptId"),
        onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["receiptId"])]
)
data class Item(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "receiptId") val receiptId: String,
    @ColumnInfo(name = "itemName") val itemName: String,
    @ColumnInfo(name = "itemCode") val itemCode: String?,
    @ColumnInfo(name = "itemCount") val itemCount: Float?,
    @ColumnInfo(name = "itemPrice") val itemPrice: Float?,
    @ColumnInfo(name = "itemUnitPrice") val itemUnitPrice: Float?
)

@Dao
interface DBDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceipt(receipt: Receipt)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item)

    @Query("SELECT * FROM receipts")
    suspend fun getAllReceipts(): List<Receipt>

    @Query("SELECT * FROM images WHERE receiptId = :receiptId")
    suspend fun getImagesForReceipt(receiptId: String): List<Image>

    @Query("SELECT * FROM items WHERE receiptId = :receiptId")
    suspend fun getItemsForReceipt(receiptId: String): List<Item>

    // For FilterReceiptsActivity
    @Query("SELECT * FROM receipts WHERE storeName = :storeName")
    suspend fun getReceiptsByStore(storeName: String): List<Receipt>

    @Query("SELECT * FROM receipts WHERE paymentCardNumber = :cardNumber")
    suspend fun getReceiptsByCardNumber(cardNumber: String): List<Receipt>

    @Query("SELECT * FROM items WHERE itemName = :itemName")
    suspend fun getItemsByName(itemName: String): List<Item>
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time?.toLong()
    }
}


@Database(entities = [Receipt::class, Image::class, Item::class], version = 1)
@TypeConverters(Converters::class)
abstract class DB : RoomDatabase() {
    abstract fun DBDao(): DBDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: DB? = null

        fun getDatabase(context: Context): DB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DB::class.java,
                    "receipt_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
