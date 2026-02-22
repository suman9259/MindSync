package com.example.mindsync.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.mindsync.data.local.database.dao.MeditationDao
import com.example.mindsync.data.local.database.dao.MedicineDao
import com.example.mindsync.data.local.database.dao.ReminderDao
import com.example.mindsync.data.local.database.dao.SkincareDao
import com.example.mindsync.data.local.database.dao.WorkoutDao
import com.example.mindsync.data.local.database.entity.ExerciseEntity
import com.example.mindsync.data.local.database.entity.MeditationEntity
import com.example.mindsync.data.local.database.entity.MedicineEntity
import com.example.mindsync.data.local.database.entity.ReminderEntity
import com.example.mindsync.data.local.database.entity.SkincareRoutineEntity
import com.example.mindsync.data.local.database.entity.SkincareStepEntity
import com.example.mindsync.data.local.database.entity.WorkoutEntity
import com.example.mindsync.data.local.database.converter.Converters
import com.example.mindsync.data.local.security.SecurePreferences
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import java.security.SecureRandom

@Database(
    entities = [
        MeditationEntity::class,
        WorkoutEntity::class,
        ExerciseEntity::class,
        ReminderEntity::class,
        MedicineEntity::class,
        SkincareRoutineEntity::class,
        SkincareStepEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MindSyncDatabase : RoomDatabase() {

    abstract fun meditationDao(): MeditationDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun reminderDao(): ReminderDao
    abstract fun medicineDao(): MedicineDao
    abstract fun skincareDao(): SkincareDao

    companion object {
        private const val DATABASE_NAME = "mindsync_encrypted.db"

        @Volatile
        private var INSTANCE: MindSyncDatabase? = null

        fun getDatabase(context: Context, securePreferences: SecurePreferences): MindSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val passphrase = getOrCreatePassphrase(securePreferences)
                val factory = SupportFactory(passphrase)

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindSyncDatabase::class.java,
                    DATABASE_NAME
                )
                    .openHelperFactory(factory)
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun getOrCreatePassphrase(securePreferences: SecurePreferences): ByteArray {
            val storedPassphrase = securePreferences.getDatabasePassphrase()
            
            return if (storedPassphrase != null) {
                storedPassphrase.toByteArray(Charsets.UTF_8)
            } else {
                val newPassphrase = generateSecurePassphrase()
                securePreferences.saveDatabasePassphrase(newPassphrase)
                newPassphrase.toByteArray(Charsets.UTF_8)
            }
        }

        private fun generateSecurePassphrase(): String {
            val random = SecureRandom()
            val bytes = ByteArray(32)
            random.nextBytes(bytes)
            return bytes.joinToString("") { "%02x".format(it) }
        }

        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
