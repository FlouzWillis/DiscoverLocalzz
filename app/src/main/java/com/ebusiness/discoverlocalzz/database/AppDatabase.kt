package com.ebusiness.discoverlocalzz.database

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.database.dao.AccountDao
import com.ebusiness.discoverlocalzz.database.dao.AccountInterestDao
import com.ebusiness.discoverlocalzz.database.dao.AddressDao
import com.ebusiness.discoverlocalzz.database.dao.EventDao
import com.ebusiness.discoverlocalzz.database.dao.EventInterestDao
import com.ebusiness.discoverlocalzz.database.dao.InterestDao
import com.ebusiness.discoverlocalzz.database.dao.OrganizerDao
import com.ebusiness.discoverlocalzz.database.dao.ReviewDao
import com.ebusiness.discoverlocalzz.database.dao.TicketDao
import com.ebusiness.discoverlocalzz.database.dao.UserDao
import com.ebusiness.discoverlocalzz.database.models.Account
import com.ebusiness.discoverlocalzz.database.models.AccountInterest
import com.ebusiness.discoverlocalzz.database.models.Address
import com.ebusiness.discoverlocalzz.database.models.Event
import com.ebusiness.discoverlocalzz.database.models.EventInterest
import com.ebusiness.discoverlocalzz.database.models.Interest
import com.ebusiness.discoverlocalzz.database.models.Organizer
import com.ebusiness.discoverlocalzz.database.models.Review
import com.ebusiness.discoverlocalzz.database.models.Ticket
import com.ebusiness.discoverlocalzz.database.models.User
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Die Klasse AppDatabase repräsentiert die Room-Datenbank für die Anwendung.
 * Sie definiert die Datenbankentitäten, die Version und stellt Datenbankoperationen bereit.
 */
@Database(
    entities = [
        Account::class,
        AccountInterest::class,
        Address::class,
        Event::class,
        EventInterest::class,
        Interest::class,
        Organizer::class,
        Review::class,
        Ticket::class,
        User::class,
    ],
    version = 1,
    exportSchema = false,
)
@Suppress("TooManyFunctions")
abstract class AppDatabase : RoomDatabase() {
    /**
     * DAO zum Zugriff auf Ticket-Daten.
     */
    abstract fun ticketDao(): TicketDao

    /**
     * DAO zum Zugriff auf Event-Daten.
     */
    abstract fun eventDao(): EventDao

    /**
     * DAO zum Zugriff auf Interessen-Daten.
     */
    abstract fun interestDao(): InterestDao

    /**
     * DAO zum Zugriff auf EventInterest-Daten.
     */
    abstract fun eventInterestDao(): EventInterestDao

    /**
     * DAO zum Zugriff auf Review-Daten.
     */
    abstract fun reviewDao(): ReviewDao

    /**
     * DAO zum Zugriff auf Address-Daten.
     */
    abstract fun addressDao(): AddressDao

    /**
     * DAO zum Zugriff auf Organizer-Daten.
     */
    abstract fun organizerDao(): OrganizerDao

    /**
     * DAO zum Zugriff auf Account-Daten.
     */
    abstract fun accountDao(): AccountDao

    /**
     * DAO zum Zugriff auf AccountInterest-Daten.
     */
    abstract fun accountInterestDao(): AccountInterestDao

    /**
     * DAO zum Zugriff auf User-Daten.
     */
    abstract fun userDao(): UserDao

    companion object {
        private const val DATABASE_NAME = "event.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Holen Sie eine Instanz von AppDatabase mit einem bereitgestellten Kontext.
         *
         * @param context Der Anwendungskontext.
         * @return Eine Instanz von AppDatabase.
         */
        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: buildDummyDatabase(context).also { instance = it }
            }

        private fun buildDummyDatabase(context: Context): AppDatabase =
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DATABASE_NAME,
            ).addCallback(
                object : Callback() {
                    @Suppress("MagicNumber", "LongMethod")
                    private suspend fun fillDatabase(database: AppDatabase) {
                        database.apply {
                            ticketDao().insertAll(
                                Ticket(1, 1, 0),
                                Ticket(2, 1, 0),
                                Ticket(3, 1, 0),
                                Ticket(4, 1, 1_706_299_000_000),
                                Ticket(5, 1, 1_706_297_700_000),
                                Ticket(6, 1, 1_706_199_000_000),
                                Ticket(7, 1, 1_705_999_000_000),
                            )
                            eventDao().insertAll(
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_706_227_200_000,
                                    1_706_211_000_000,
                                    1,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_706_299_900_000,
                                    1_704_088_800_000,
                                    1,
                                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_714_557_600_000,
                                    1_719_770_400_000,
                                    1,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_721_037_600_000,
                                    1_723_744_800_000,
                                    1,
                                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_725_184_800_000,
                                    1_727_719_200_000,
                                    1,
                                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Stuttgart"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_727_776_800_000,
                                    1_730_397_600_000,
                                    1,
                                    "Nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Stuttgart"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_733_076_000_000,
                                    1_735_682_400_000,
                                    1,
                                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_719_835_200_000,
                                    1_722_463_200_000,
                                    1,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Berlin"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_722_506_400_000,
                                    1_725_141_600_000,
                                    1,
                                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_725_220_800_000,
                                    1_727_740_740_000,
                                    1,
                                    "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_717_279_200_000,
                                    1_717_300_800_000,
                                    1,
                                    "Nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_721_080_800_000,
                                    1_721_102_400_000,
                                    1,
                                    "Excepteur sint occaecat cupidatat non proident, sunt in culpa.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Mannheim"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_724_184_000_000,
                                    1_724_205_600_000,
                                    1,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                ),
                                Event(
                                    1,
                                    "Lorem Ipsum",
                                    1_726_005_600_000,
                                    1_726_027_200_000,
                                    1,
                                    "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                    "Karlsruhe"
                                )

                            )
                            interestDao().insertAll(
                                Interest("Kultur", Base64.getFromAssets(context, "sample_bar.jpg")),
                                Interest("Tanzen", Base64.getFromAssets(context, "sample_bar.jpg")),
                                Interest(
                                    "Alkohol",
                                    Base64.getFromAssets(context, "sample_bar.jpg"),
                                ),
                                Interest("Essen", Base64.getFromAssets(context, "sample_bar.jpg")),
                                Interest("Party", Base64.getFromAssets(context, "sample_bar.jpg")),
                            )

                            eventInterestDao().insertAll(
                                EventInterest(1, 2),
                                EventInterest(2, 4),
                                EventInterest(3, 1),
                                EventInterest(4, 2),
                                EventInterest(5, 3),
                                EventInterest(6, 1),
                                EventInterest(7, 1),
                                EventInterest(8, 4),
                                EventInterest(9, 1),
                                EventInterest(10, 3),
                                EventInterest(11, 2),
                                EventInterest(12, 5),
                                EventInterest(13, 2),
                                EventInterest(14, 4),
                                EventInterest(15, 1),
                                EventInterest(16, 2),
                                EventInterest(17, 5),
                                )


//                            eventInterestDao().insertAll(
//                                EventInterest(1, 2),
//                                EventInterest(1, 3),
//                                EventInterest(2, 4),
//                                EventInterest(2, 3),
//                                EventInterest(3, 1),
//                                EventInterest(3, 2),
//                                EventInterest(4, 2),
//                                EventInterest(4, 3),
//                                EventInterest(5, 3),
//                                EventInterest(5, 2),
//                                EventInterest(5, 4),
//                                EventInterest(5, 5),
//                                EventInterest(6, 1),
//                                EventInterest(7, 1),
//                                EventInterest(8, 4),
//                                EventInterest(9, 1),
//                                EventInterest(10, 2),
//                                EventInterest(10, 3),
//                                EventInterest(10, 5),
//                                EventInterest(11, 2),
//                                EventInterest(11, 3),
//                                EventInterest(11, 5),
//                                EventInterest(12, 2),
//                                EventInterest(12, 3),
//                                EventInterest(12, 5),
//                                EventInterest(13, 2),
//                                EventInterest(13, 3),
//                                EventInterest(13, 5),
//                            )
                            reviewDao().insertAll(
                                Review(1, 1, "", 2.5f, 1_000_000_000),
                                Review(2, 1, "", 3.5f, 2_000_000_000),
                                Review(3, 1, "", 4.5f, 3_000_000_000),
                                Review(4, 1, "", 5.0f, 2_090_000_000),
                                Review(5, 1, "", 4.5f, 2_440_000_000),
                                Review(6, 1, "", 4.0f, 2_570_000_000),
                                Review(7, 1, "", 1.5f, 2_650_000_000),
                            )
                            organizerDao().insertAll(
                                Organizer("Jan"),
                            )
                            addressDao().insertAll(
                                Address("Kußmaulstraße", "76133", "1", "Karlsruhe"),
                            )
                            accountDao().insertAll(
                                Account(
                                    "maxmustermann@gmx.de",
                                    "0800 897378423",
                                    BCrypt.hashpw("123456789", BCrypt.gensalt()),
                                ),
                            )
                            userDao().insertAll(
                                User(
                                    1,
                                    "Max",
                                    "Mustermann",
                                    0,
                                ),
                            )
                            accountInterestDao().insertAll(
                                AccountInterest(1, 1),
                                AccountInterest(1, 2),
                                AccountInterest(1, 3),
                            )
                        }
                    }

                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            fillDatabase(getInstance(context))
                            ContextCompat.getMainExecutor(context).execute {
                                MaterialAlertDialogBuilder(context).setTitle(R.string.dummy_database)
                                    .setMessage(R.string.dummy_database_summary)
                                    .setPositiveButton(R.string.ok) { _, _ ->
                                        context.startActivity(
                                            Intent(
                                                context,
                                                MainActivity::class.java,
                                            ).addFlags(
                                                Intent.FLAG_ACTIVITY_NEW_TASK or
                                                        Intent.FLAG_ACTIVITY_CLEAR_TASK,
                                            ),
                                        )
                                    }
                                    .show()
                            }
                        }
                    }
                },
            ).build()
    }
}
