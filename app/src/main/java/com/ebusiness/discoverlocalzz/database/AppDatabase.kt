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
import com.ebusiness.discoverlocalzz.database.dao.LocationDao
import com.ebusiness.discoverlocalzz.database.dao.LocationInterestDao
import com.ebusiness.discoverlocalzz.database.dao.InterestDao
import com.ebusiness.discoverlocalzz.database.dao.OrganizerDao
import com.ebusiness.discoverlocalzz.database.dao.ReviewDao
import com.ebusiness.discoverlocalzz.database.dao.CouponDao
import com.ebusiness.discoverlocalzz.database.dao.UserDao
import com.ebusiness.discoverlocalzz.database.models.Account
import com.ebusiness.discoverlocalzz.database.models.AccountInterest
import com.ebusiness.discoverlocalzz.database.models.Address
import com.ebusiness.discoverlocalzz.database.models.Location
import com.ebusiness.discoverlocalzz.database.models.LocationInterest
import com.ebusiness.discoverlocalzz.database.models.Interest
import com.ebusiness.discoverlocalzz.database.models.Organizer
import com.ebusiness.discoverlocalzz.database.models.Review
import com.ebusiness.discoverlocalzz.database.models.Coupon
import com.ebusiness.discoverlocalzz.database.models.User
import com.ebusiness.discoverlocalzz.helpers.Base64
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

/**
 * Die Klasse AppDatabase beinhaltet die Room-Datenbank für die Anwendung.
 * Hier sind die Datenbankentitäten, die Version und stellt Datenbankoperationen bereit.
 */
@Database(
    entities = [
        Account::class,
        AccountInterest::class,
        Address::class,
        Location::class,
        LocationInterest::class,
        Interest::class,
        Organizer::class,
        Review::class,
        Coupon::class,
        User::class,
    ],
    version = 1,
    exportSchema = false,
)
@Suppress("TooManyFunctions")
abstract class AppDatabase : RoomDatabase() {
    /**
     * DAO zum Zugriff auf Coupon-Daten.
     */
    abstract fun couponDao(): CouponDao

    /**
     * DAO zum Zugriff auf Location-Daten.
     */
    abstract fun locationDao(): LocationDao

    /**
     * DAO zum Zugriff auf Interessen-Daten.
     */
    abstract fun interestDao(): InterestDao

    /**
     * DAO zum Zugriff auf LocationInterest-Daten.
     */
    abstract fun locationInterestDao(): LocationInterestDao

    /**
     * DAO zum Zugriff auf Bewertung-Daten.
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
        private const val DATABASE_NAME = "Location.db"

        @Volatile
        private var instance: AppDatabase? = null

        /**
         * Holen Sie eine Instanz von AppDatabase mit bereitgestellten Kontext.
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
                            couponDao().insertAll(
                                Coupon(1, 1, 1_767_139_200_000),
                                Coupon(2, 1, 1_767_139_200_000),
                                Coupon(3, 1, 1_767_139_200_000),
                                Coupon(4, 1, 1_767_139_200_000),
                                Coupon(5, 1, 1_767_139_200_000),
                                Coupon(6, 1, 1_767_139_200_000),
                                Coupon(7, 1, 1_767_139_200_000),
                                Coupon(1, 1, 1_706_227_200_000),
                                Coupon(2, 1, 1_706_227_200_000),
                                Coupon(3, 1, 1_706_227_200_000),
                                Coupon(4, 1, 1_706_227_200_000),
                            )
                            addressDao().insertAll(
                                Address("Hermann-Levi-Platz", "76137", "1", "Karlsruhe"),
                                Address("Erbprinzenstraße", "76133", "13", "Karlsruhe"),
                                Address("Rheinstraße", "76185", "77", "Karlsruhe"),
                                Address("Zähringerstraße", "76133", "96", "Karlsruhe"),
                                Address("Baumeisterstraße", "76137", "3", "Karlsruhe"),
                                Address("Am Wriezener bhf", "10243", "1", "Berlin"),
                                Address("Friedrichstraße", "70174", "27", "Stuttgart"),
                                Address("Douglasstraße", "76133", "10", "Karlsruhe"),
                                Address("Bürgerstraße", "76133", "16", "Karlsruhe"),
                                Address("Rheinstraße", "76185", "18", "Karlsruhe"),
                                Address("Rüppurrer Str.", "76137", "1", "Karlsruhe"),
                                Address("Hirschstraße", "70173", "14", "Stuttgart"),
                            )
                            locationDao().insertAll(
                                Location(
                                    1,
                                    "Badisches Staatstheater",
                                    1,
                                    "Das Badische Staatstheater Karlsruhe ist ein renommiertes Theater in Karlsruhe, das für seine vielseitigen Aufführungen in den Bereichen Schauspiel, Oper, Ballett und Konzert bekannt ist.",
                                    Base64.getFromAssets(context, "Badisches-Staatstheater-Karlsruhe.jpg"),
                                ),
                                Location(
                                    1,
                                    "Naturkundemuseum",
                                    2,
                                    "Das Naturkundemuseum Karlsruhe präsentiert eine faszinierende Sammlung aus den Bereichen Naturgeschichte, Zoologie und Paläontologie.",
                                    Base64.getFromAssets(context, "Naturkundemuseum.jpg"),
                                ),
                                Location(
                                    1,
                                    "Rheingold",
                                    3,
                                    "Der Ort bietet eine besondere Form der Unterhaltung, bei der Tanz und Musik in einer einzigartigen Atmosphäre kombiniert werden.",
                                    Base64.getFromAssets(context, "Karlsruhe_Rheingold.jpg"),
                                ),
                                Location(
                                    1,
                                    "Marktlücke",
                                    4,
                                    "Marktlücke Karlsruhe ist ein modernes Café und Restaurant, das für seine kreative Küche und gemütliche Atmosphäre bekannt ist. ",
                                    Base64.getFromAssets(context, "marktluecke-karlsruhe.jpg"),
                                ),
                                Location(
                                    1,
                                    "Die Stadtmitte",
                                    5,
                                    "\n" +
                                            "Die Stadtmitte in Karlsruhe ist ein beliebtes Restaurant, das für seine moderne Küche und gemütliche Atmosphäre bekannt ist, ideal für kulinarische Genüsse in zentraler Lage.",
                                    Base64.getFromAssets(context, "die-stadtmitte.jpg"),
                                ),
                                Location(
                                    1,
                                    "Berghain",
                                    6,
                                    "Berghain in Berlin ist ein legendärer Club, der für seine einzigartige Atmosphäre, elektronischen Musik und legendären Partys bekannt ist.",
                                    Base64.getFromAssets(context, "berghain-club1.jpg"),
                                ),
                                Location(
                                    1,
                                    "Palast der Republik",
                                    7,
                                    "Der Palast der Republik in Stuttgart ist ein historisches Gebäude, das für seine kulturellen Veranstaltungen und Veranstaltungen im Bereich Kunst und Musik bekannt ist.",
                                    Base64.getFromAssets(context, "Palast-der-Republik.jpg"),
                                ),
                                Location(
                                    1,
                                    "Bierakademie",
                                    8,
                                    "Die Bierakademie Karlsruhe ist ein einzigartiger Ort, der Bierliebhabern die Möglichkeit bietet, mehr über die Kunst des Bierbrauens und die Vielfalt der Biersorten zu erfahren.",
                                    Base64.getFromAssets(context, "Bierakademie.jpg"),
                                ),
                                Location(
                                    1,
                                    "OISHII",
                                    9,
                                    "Oishii Karlsruhe ist ein beliebtes Restaurant, das authentische japanische Küche in einem modernen und einladenden Ambiente serviert.",
                                    Base64.getFromAssets(context, "oishii-karlsruhe.jpg"),
                                ),
                                Location(
                                    1,
                                    "Kebab Factory",
                                    10,
                                    "Kebab Factory Karlsruhe ist ein trendiges Restaurant, das für seine köstlichen Kebabs und frischen Zutaten bekannt ist.",
                                    Base64.getFromAssets(context, "Kebab-Factory-KA.jpg"),
                                ),
                                Location(
                                    1,
                                    "Agostea",
                                    11,
                                    "Agostea Karlsruhe ist ein beliebter Club und Veranstaltungsort, der für seine abwechslungsreichen Partys und Live-Events bekannt ist.",
                                    Base64.getFromAssets(context, "Agostea.jpg"),
                                ),
                                Location(
                                    1,
                                    "Schräglage Club",
                                    12,
                                    "Schräglage Club in Stuttgart ist ein angesagter Veranstaltungsort, der für seine unkonventionelle Atmosphäre und aufregenden Partys bekannt ist.",
                                    Base64.getFromAssets(context, "Schraeglage-Club.jpg"),
                                ),
                                Location(
                                    1,
                                    "Berghain",
                                    6,
                                    "Berghain in Berlin ist ein legendärer Club, der für seine einzigartige Atmosphäre, elektronischen Musik und legendären Partys bekannt ist.",
                                    Base64.getFromAssets(context, "berghain-club1.jpg"),
                                ),

                            )
                            interestDao().insertAll(
                                Interest("Kultur", Base64.getFromAssets(context, "kultur.jpg")),
                                Interest("Tanzen", Base64.getFromAssets(context, "tanzen.jpg")),
                                Interest(
                                    "Drinks",
                                    Base64.getFromAssets(context, "drinks.jpg"),
                                ),
                                Interest("Essen", Base64.getFromAssets(context, "essen.jpg")),
                                Interest("Party", Base64.getFromAssets(context, "party.jpg")),
                            )

                            locationInterestDao().insertAll(
                                LocationInterest(1, 1),
                                LocationInterest(2, 1),
                                LocationInterest(3, 1),
                                LocationInterest(4, 2),
                                LocationInterest(5, 2),
                                LocationInterest(6, 2),
                                LocationInterest(7, 3),
                                LocationInterest(8, 3),
                                LocationInterest(9, 4),
                                LocationInterest(10, 4),
                                LocationInterest(11, 5),
                                LocationInterest(12, 5),
                                LocationInterest(13, 5),
                                )

                            reviewDao().insertAll(
                                Review(1, 1, "", 2.5f, 1736982000000),
                                Review(2, 1, "", 3.5f, 1736726400000),
                                Review(3, 1, "", 4.5f, 1735084800000),
                                Review(4, 1, "Super!", 5.0f, 1736208000000),
                                Review(5, 1, "", 4.5f, 1735171200000),
                                Review(6, 1, "", 4.0f, 1736380800000),
                                Review(7, 1, "Schlecht!", 1.5f, 1735948800000),
                            )
                            organizerDao().insertAll(
                                Organizer(""),
                            )
                            accountDao().insertAll(
                                Account(
                                    "maxmustermann@gmx.de",
                                    "0800 897378423",
                                    BCrypt.hashpw("12345678", BCrypt.gensalt()),
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
