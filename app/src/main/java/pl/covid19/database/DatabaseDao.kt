package pl.covid19.database
import androidx.lifecycle.LiveData
import androidx.room.*
import pl.covid19.util.getDistinct


@Dao
interface DatabaseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(night: AreaDB)

    @Update
    suspend fun update(night: AreaDB)
    //areas
    @Query("SELECT * from areas WHERE areaAutoId = :key")
    suspend fun get(key: Long): AreaDB?

    @Query("DELETE from areas WHERE areaAutoId = :key")
    suspend fun delete(key: Long): Int

    @Query("DELETE FROM areas")
    suspend fun clear()

    @Query("SELECT * FROM areas ORDER BY idGus ASC")
    fun getAllAreas(): LiveData<List<AreaDB>>

    @Query("SELECT * FROM areas ORDER BY areaAutoId DESC LIMIT 1")
    suspend fun getLast(): AreaDB?

    @Query("SELECT * from areas WHERE areaAutoId = :key")
    fun getAreaWithId(key: Long): LiveData<AreaDB>

    @Query("SELECT name from areas WHERE areaAutoId = :key")
    fun getAreaNameWithId(key: Long): String

    //INNER JOIN
    @Query("SELECT * from areas INTER JOIN govplx ON idGus = govplx.id  WHERE  areaAutoId = :key AND Date =:dat")
    fun getAreaGovplxWithIdDate(key: Long, dat: String): LiveData<AreaDBGOVPLXDB>

    //@Query("SELECT * from areas INTER JOIN govplx ON idGus = govplx.id JOIN fazy ON idFazy = fazy.idFazyKey  WHERE  Date = :dat")
    @Query("SELECT * from areas INTER JOIN govplx ON idGus = govplx.id JOIN fazy ON idFazy = fazy.idFazyKey  WHERE  Date IN ( SELECT  Max(Date)  FROM govplx)")
    fun getAreaGovplxFazaWithMaxdate(): LiveData<List<AreaDBGOVPLXDBFazyDB>>

    @Query("SELECT * from areas INTER JOIN govplx ON idGus = govplx.id JOIN fazy ON idFazy = fazy.idFazyKey  WHERE  areaAutoId = :key AND Date =:dat")
    fun getAreaGovplxFazaWithIdDate(key: Long, dat: String): LiveData<AreaDBGOVPLXDBFazyDB>

    //GOVPLXDB
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertGovlxAll(vararg pls: GOVPLXDB): LongArray?

    @Query("SELECT * FROM govplx  WHERE date = :dat")
    fun getGovplxWithData(dat: String): LiveData<GOVPLXDB>

    @Query("SELECT * FROM govplx  WHERE date = :dat")
    fun getAllGovplx(dat: String): LiveData<GOVPLXDB>

    @Query("SELECT  Max(Date)  FROM govplx")
    abstract fun getMaxDate(): LiveData<String?>
    fun getMaxDateDistinctLiveData(): LiveData<String?> = getMaxDate().getDistinct()

    @Query("SELECT  COUNT(autoId)  FROM govplx")
    fun getCountGovplx (): Int

    @Query("SELECT  COUNT(autoId)  FROM govplx WHERE date = :dat")
    fun getCountGovplxDay (dat: String): Int

    @Query("DELETE FROM govplx")
    fun clearGovplx()
    //FazyDB

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertFazyAll(vararg pls: FazyDB)

    @Query("SELECT * FROM fazy  WHERE DataOgloszenia = :dat")
    fun getFazyWithData(dat: String): LiveData<FazyDB>

    @Query("SELECT * FROM fazy")
    fun getAllFazy (): LiveData<FazyDB>

    @Query("SELECT  COUNT(idFazyKey)  FROM fazy")
    fun getCountFazy (): Int

    @Query("DELETE FROM fazy")
    fun clearFazy()

}

