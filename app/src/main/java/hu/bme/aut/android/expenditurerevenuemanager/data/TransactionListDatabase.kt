package hu.bme.aut.android.expenditurerevenuemanager.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [TransactionItem::class], version = 1)
@TypeConverters(value = [TransactionItemCategory::class, DateTypeConverter::class])
abstract class TransactionListDatabase : RoomDatabase() {

    companion object {
        private var INSTANCE: TransactionListDatabase? = null

        fun getInstance(context: Context): TransactionListDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                    TransactionListDatabase::class.java, "grade.db")
                    .build()
            }
            return INSTANCE!!
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }

    abstract fun transactionItemDao(): TransactionItemDao
}