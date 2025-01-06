package com.ebusiness.discoverlocalzz.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Data-Klasse für die Verknüpfung von Accounts und Interessen.
 * @property accountId ID des Accounts.
 * @property interestId ID der Interesse.
 */
@Entity(
    tableName = "account_interest",
    primaryKeys = ["account_id", "interest_id"],
)
data class AccountInterest(
    @ColumnInfo(name = "account_id") val accountId: Long,
    @ColumnInfo(name = "interest_id") val interestId: Long,
)
