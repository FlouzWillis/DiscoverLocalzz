package com.ebusiness.discoverlocalzz.data

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.ebusiness.discoverlocalzz.R
import com.ebusiness.discoverlocalzz.activities.MainActivity
import com.ebusiness.discoverlocalzz.data.dao.AccountDao
import com.ebusiness.discoverlocalzz.data.dao.AccountInterestDao
import com.ebusiness.discoverlocalzz.data.dao.AddressDao
import com.ebusiness.discoverlocalzz.data.dao.EventDao
import com.ebusiness.discoverlocalzz.data.dao.EventInterestDao
import com.ebusiness.discoverlocalzz.data.dao.InterestDao
import com.ebusiness.discoverlocalzz.data.dao.OrganizerDao
import com.ebusiness.discoverlocalzz.data.dao.ReviewDao
import com.ebusiness.discoverlocalzz.data.dao.TicketDao
import com.ebusiness.discoverlocalzz.database.dao.UserDao
import com.ebusiness.discoverlocalzz.data.dao.ZipCodeDao
import com.ebusiness.discoverlocalzz.data.models.Account
import com.ebusiness.discoverlocalzz.data.models.AccountInterest
import com.ebusiness.discoverlocalzz.data.models.Address
import com.ebusiness.discoverlocalzz.data.models.Event
import com.ebusiness.discoverlocalzz.data.models.EventInterest
import com.ebusiness.discoverlocalzz.data.models.Interest
import com.ebusiness.discoverlocalzz.data.models.Organizer
import com.ebusiness.discoverlocalzz.data.models.Review
import com.ebusiness.discoverlocalzz.data.models.Ticket
import com.ebusiness.discoverlocalzz.data.models.User
import com.ebusiness.discoverlocalzz.data.models.ZipCode
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Die Klasse AppDatabase repräsentiert die Room-Datenbank für die Event Radar-Anwendung.
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
        ZipCode::class,
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
     * DAO zum Zugriff auf ZipCode-Daten.
     */
    abstract fun zipCodeDao(): ZipCodeDao

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
                                    29.00,
                                    "Lucas Kochabend",
                                    1_706_227_200_000,
                                    1_706_211_000_000,
                                    1,
                                    "Alle mögen gutes Essen, deshalb werden Sie " +
                                            "auch Luca mögen!",
                                    Base64.getFromAssets(context, "kochen.jpg"),
                                ),
                                Event(
                                    1,
                                    8.50,
                                    "Domis Dampflockausstellung",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Unser Dampflockexperte Domi lädt zu einem " +
                                            "sinnlichen Abentuer in seine Privataussletung ein.",
                                    Base64.getFromAssets(context, "bahn.jpg"),
                                ),
                                Event(
                                    1,
                                    11.50,
                                    "Musical: Der große Joel alleine im Wald?",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Eine unvergessleicher Abend voller Tanz und " +
                                            "Stimmung",
                                    Base64.getFromAssets(context, "musical.jpg"),
                                ),
                                Event(
                                    1,
                                    8.50,
                                    "Tanz mit Tim",
                                    1_706_299_900_000,
                                    1_706_300_000_000,
                                    1,
                                    "Wer sein Tanzbein mal wieder so richtig " +
                                            "schwingen will, darf diesen Event nicht " +
                                            "verpassen!",
                                    Base64.getFromAssets(context, "tanz.jpg"),
                                ),
                                Event(
                                    1,
                                    8.50,
                                    "Silvesterparty",
                                    1_706_299_900_000,
                                    1_704_088_800_000,
                                    1,
                                    "Feier mit uns gemeinsam in das neue Jahr!",
                                    Base64.getFromAssets(context, "silvester.jpg"),
                                ),
                                Event(
                                    1,
                                    12.00,
                                    "Zeitreise: Kunst durch die Jahrhunderte",
                                    1_714_557_600_000,
                                    1_719_770_400_000,
                                    1,
                                    "Eine Ausstellung, die Kunstwerke aus verschiedenen " +
                                            "Epochen präsentiert, von der Renaissance bis zur modernen Kunst.",
                                    Base64.getFromAssets(context, "zeitreise.jpg"),
                                ),
                                Event(
                                    1,
                                    10.00,
                                    "Fotografie Heute: Eine globale Perspektive",
                                    1_721_037_600_000,
                                    1_723_744_800_000,
                                    1,
                                    "Eine Fotografie Ausstellung, die Werke von zeitgenössischen " +
                                            "Fotografen aus der ganzen Welt zeigt.",
                                    Base64.getFromAssets(context, "fotografie.jpg"),
                                ),
                                Event(
                                    1,
                                    15.00,
                                    "Design des 21. Jahrhunderts",
                                    1_725_184_800_000,
                                    1_727_719_200_000,
                                    1,
                                    "Eine Ausstellung, die innovative Designkonzepte aus den Bereichen Mode, " +
                                            "Möbel, Grafik und Industriedesign präsentiert.",
                                    Base64.getFromAssets(context, "design.jpg"),
                                ),
                                Event(
                                    1,
                                    18.00,
                                    "Natur in der Kunst",
                                    1_727_776_800_000,
                                    1_730_397_600_000,
                                    1,
                                    "Eine Ausstellung, die Kunstwerke präsentiert, die die " +
                                            "Schönheit und Vielfalt der Natur darstellen.",
                                    Base64.getFromAssets(context, "natur.jpg"),
                                ),
                                Event(
                                    1,
                                    20.00,
                                    "Lichter der Welt",
                                    1_733_076_000_000,
                                    1_735_682_400_000,
                                    1,
                                    "Ein Festival, das Lichtinstallationen von internationalen " +
                                            "Künstlern in der ganzen Stadt präsentiert. ",
                                    Base64.getFromAssets(context, "lichter.jpg"),
                                ),
                                Event(
                                    1,
                                    35.00,
                                    "Rhythmus der Nationen",
                                    1_719_835_200_000,
                                    1_722_463_200_000,
                                    1,
                                    "Ein Musikfestival, das Bands und Musiker aus verschiedenen " +
                                            "Ländern und Kulturen zusammenbringt. ",
                                    Base64.getFromAssets(context, "rhythmus.jpg"),
                                ),
                                Event(
                                    1,
                                    10.00,
                                    "Straßenkunst Spektakel",
                                    1_722_506_400_000,
                                    1_725_141_600_000,
                                    1,
                                    "Ein Festival, das Straßenkünstler, Akrobaten und Performer " +
                                            "aus der ganzen Welt in einer Stadt versammelt. ",
                                    Base64.getFromAssets(context, "strassenkunst.jpg"),
                                ),
                                Event(
                                    1,
                                    15.00,
                                    "Filmnächte unter den Sternen",
                                    1_725_220_800_000,
                                    1_727_740_740_000,
                                    1,
                                    "Ein Open-Air-Filmfestival, das klassische und zeitgenössische " +
                                            "Filme unter dem Sternenhimmel zeigt. ",
                                    Base64.getFromAssets(context, "filmnaechte.jpg"),
                                ),
                                Event(
                                    1,
                                    25.00,
                                    "Elektronische Ekstase",
                                    1_717_279_200_000,
                                    1_717_300_800_000,
                                    1,
                                    "Eine Party, die sich auf elektronische Musik konzentriert " +
                                            "und Top-DJs aus der ganzen Welt einlädt. ",
                                    Base64.getFromAssets(context, "elektronische.jpg"),
                                ),
                                Event(
                                    1,
                                    20.00,
                                    "Retro Revival Nacht",
                                    1_721_080_800_000,
                                    1_721_102_400_000,
                                    1,
                                    "Eine Themenparty, die die Musik und Mode der 80er und 90er Jahre feiert.",
                                    Base64.getFromAssets(context, "retro.jpg"),
                                ),
                                Event(
                                    1,
                                    30.00,
                                    "Tanz unter den Sternen",
                                    1_724_184_000_000,
                                    1_724_205_600_000,
                                    1,
                                    "Eine Open-Air-Party im Mannheimer Stadtpark mit Live-Musik und Tanzflächen. ",
                                    Base64.getFromAssets(context, "tanz_unter.jpg"),
                                ),
                                Event(
                                    1,
                                    15.00,
                                    "Club der Kulturen",
                                    1_726_005_600_000,
                                    1_726_027_200_000,
                                    1,
                                    "Eine Partyreihe, die Musikstile aus verschiedenen " +
                                            "Kulturen und Ländern präsentiert.  ",
                                    Base64.getFromAssets(context, "club_der.jpg"),
                                ),
                            )
                            interestDao().insertAll(
                                Interest("Kultur", Base64.getFromAssets(context, "kultur.jpg")),
                                Interest("Tanzen", Base64.getFromAssets(context, "musicalm.jpg")),
                                Interest(
                                    "Alkohol",
                                    Base64.getFromAssets(context, "bar.jpg"),
                                ),
                                Interest("Essen", Base64.getFromAssets(context, "essen.jpg")),
                                Interest("Party", Base64.getFromAssets(context, "club.jpg")),
                            )
                            eventInterestDao().insertAll(
                                EventInterest(1, 2),
                                EventInterest(1, 3),
                                EventInterest(2, 4),
                                EventInterest(2, 3),
                                EventInterest(3, 1),
                                EventInterest(3, 2),
                                EventInterest(4, 2),
                                EventInterest(4, 3),
                                EventInterest(5, 3),
                                EventInterest(5, 2),
                                EventInterest(5, 4),
                                EventInterest(5, 5),
                                EventInterest(6, 1),
                                EventInterest(7, 1),
                                EventInterest(8, 1),
                                EventInterest(9, 1),
                                EventInterest(10, 2),
                                EventInterest(10, 3),
                                EventInterest(10, 5),
                                EventInterest(11, 2),
                                EventInterest(11, 3),
                                EventInterest(11, 5),
                                EventInterest(12, 2),
                                EventInterest(12, 3),
                                EventInterest(12, 5),
                                EventInterest(13, 2),
                                EventInterest(13, 3),
                                EventInterest(13, 5),
                            )
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
                                Organizer("Luca"),
                            )
                            zipCodeDao().insertAll(
                                ZipCode("76133", "Karlsruhe"),
                            )
                            addressDao().insertAll(
                                Address("Moltkestraße", "76133", "30"),
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
