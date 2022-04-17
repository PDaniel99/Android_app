package hu.bme.aut.android.expenditurerevenuemanager.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.*

@Entity(tableName = "transactionitem")
data class TransactionItem (
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) var id: Long = 0,


    @ColumnInfo(name = "category") var category: TransactionItemCategory,
    @ColumnInfo(name = "partner") var partner: String,
    @ColumnInfo(name = "description") var description: String = "",
    @ColumnInfo(name = "value") var value: Long = 0,
    @ColumnInfo(name = "date") var date: Date
        )

enum class TransactionItemCategory {
    EXPENDITURE, REVENUE;

    companion object {
        @TypeConverter
        @JvmStatic
        fun getByOrdinal(ordinal: Int): TransactionItemCategory? {
            var ret: TransactionItemCategory? = null
            for (cat in values()) {
                if (cat.ordinal == ordinal) {
                    ret = cat
                    break
                }
            }
            return ret
        }

        @TypeConverter
        @JvmStatic
        fun toInt(category: TransactionItemCategory): Int {
            return category.ordinal
        }

    }
}

class DateTypeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}