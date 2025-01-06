package com.ebusiness.discoverlocalzz.data.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

/**
 * Die Klasse UserWithAccount stellt eine Beziehung zwischen einem User und einem Account in der Room-Datenbank dar.
 *
 * @property user Der User, der mit dem Account verknüpft ist.
 * @property account Der Account, mit dem der User verknüpft ist.
 */
@Entity
data class UserWithAccount(
    @Embedded val user: User,
    @Relation(
        parentColumn = "account_id",
        entityColumn = "account_id",
    )
    val account: Account,
)
