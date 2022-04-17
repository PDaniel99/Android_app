package hu.bme.aut.android.expenditurerevenuemanager.data

import androidx.room.*

@Dao
interface TransactionItemDao {

    //Ezeket a metódusokat a MainActivity-ben használom.

    @Query("SELECT * FROM transactionitem")
    suspend fun getAll(): List<TransactionItem>

    @Insert
    suspend fun insert(transactionItems: TransactionItem): Long

    @Update
    suspend fun update(transactionItems: TransactionItem)

    @Delete
    suspend fun deleteItem(transactionItems: TransactionItem)


    //Ezeket a metódusokat bárhol máshol ahol a szálkezelésről gondoskodni kell!
    @Query("SELECT COUNT(*) FROM transactionitem")
    fun getNum(): Int

    @Query("SELECT COUNT(*) FROM transactionitem WHERE  category=1")
    fun getNumRev(): Int

    @Query("SELECT SUM(value) FROM transactionitem WHERE  category=1")
    fun getSumRev(): Long

    @Query("SELECT SUM(value) FROM transactionitem WHERE  category=0")
    fun getSumExp(): Long

    @Query("SELECT * FROM transactionitem")
    fun getAllonThread(): List<TransactionItem>

    @Query("SELECT * FROM transactionitem WHERE  category=0 ORDER BY value DESC LIMIT 5" )
    fun getTop5Exp(): List<TransactionItem>

    @Query("SELECT * FROM transactionitem WHERE  category=1 ORDER BY value DESC LIMIT 5" )
    fun getTop5Rev(): List<TransactionItem>

    @Query("SELECT * FROM transactionitem WHERE id = :id")
    fun getItemById(id: Long): TransactionItem
}