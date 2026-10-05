package com.ghardari.app

import com.ghardari.app.data.model.DailyExpense
import com.ghardari.app.data.model.MonthlyExpenditure
import com.ghardari.app.data.model.PartyRecord
import com.ghardari.app.data.model.RationItem
import com.ghardari.app.data.repository.BackupManager
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class BackupSanitizationTest {

    @Test
    fun testMalformedJsonWithNullValuesDeserializesSafely() {
        // Simulating JSON from an older version, incomplete fields, or explicit nulls
        val malformedJson = """
        {
            "appVersion": null,
            "exportedAt": null,
            "currentMonthKey": null,
            "parties": [
                {
                    "id": 1,
                    "name": null,
                    "phone": null,
                    "type": null,
                    "currentBalance": 0.0,
                    "updatedAt": null,
                    "notes": null
                }
            ],
            "monthlyExpenditures": [
                {
                    "id": 2,
                    "monthKey": null,
                    "title": null,
                    "amount": 2500.0,
                    "isPaid": true,
                    "paidDate": null,
                    "notes": null
                }
            ],
            "dailyExpenses": [
                {
                    "id": 3,
                    "date": null,
                    "monthKey": null,
                    "title": null,
                    "amount": 500.0,
                    "paymentMethod": null,
                    "notes": null
                }
            ],
            "rationItems": [
                {
                    "id": 4,
                    "monthKey": null,
                    "name": null,
                    "company": null,
                    "quantity": 2.0,
                    "unit": null,
                    "estimatedPrice": 100.0,
                    "actualPrice": 120.0,
                    "isPurchased": true,
                    "notes": null
                }
            ]
        }
        """.trimIndent()

        val gson = Gson()
        val backupData = gson.fromJson(malformedJson, BackupManager.BackupData::class.java)

        assertNotNull("BackupData must not be null", backupData)

        // Sanitize Parties
        val safeParties = backupData.parties?.map { p ->
            PartyRecord(
                id = p.id,
                name = (p.name as String?).orEmpty().ifBlank { "Party" },
                phone = (p.phone as String?).orEmpty(),
                type = (p.type as String?).orEmpty().ifBlank { "LENDER" },
                currentBalance = p.currentBalance,
                updatedAt = (p.updatedAt as String?).orEmpty(),
                notes = (p.notes as String?).orEmpty()
            )
        } ?: emptyList()

        assertEquals(1, safeParties.size)
        assertEquals("Party", safeParties[0].name)
        assertEquals("", safeParties[0].phone)
        assertEquals("LENDER", safeParties[0].type)
        assertEquals("", safeParties[0].notes)

        // Sanitize Monthly
        val safeMonthly = backupData.monthlyExpenditures?.map { m ->
            MonthlyExpenditure(
                id = m.id,
                monthKey = (m.monthKey as String?).orEmpty(),
                title = (m.title as String?).orEmpty().ifBlank { "Expense" },
                amount = m.amount,
                isPaid = m.isPaid,
                paidDate = (m.paidDate as String?).orEmpty(),
                notes = (m.notes as String?).orEmpty()
            )
        } ?: emptyList()

        assertEquals(1, safeMonthly.size)
        assertEquals("Expense", safeMonthly[0].title)
        assertEquals("", safeMonthly[0].paidDate)
        assertEquals(2500.0, safeMonthly[0].amount, 0.001)

        // Sanitize Ration
        val safeRation = backupData.rationItems?.map { r ->
            RationItem(
                id = r.id,
                monthKey = (r.monthKey as String?).orEmpty(),
                name = (r.name as String?).orEmpty().ifBlank { "Item" },
                company = (r.company as String?).orEmpty(),
                quantity = r.quantity,
                unit = (r.unit as String?).orEmpty().ifBlank { "piece" },
                estimatedPrice = r.estimatedPrice,
                actualPrice = r.actualPrice,
                isPurchased = r.isPurchased,
                notes = (r.notes as String?).orEmpty()
            )
        } ?: emptyList()

        assertEquals(1, safeRation.size)
        assertEquals("Item", safeRation[0].name)
        assertEquals("", safeRation[0].company)
        assertEquals("piece", safeRation[0].unit)
    }

    @Test
    fun testEmptyJsonDeserializesSafely() {
        val emptyJson = "{}"
        val gson = Gson()
        val backupData = gson.fromJson(emptyJson, BackupManager.BackupData::class.java)

        assertNotNull(backupData)
        assertNull(backupData.parties)
        assertNull(backupData.monthlyExpenditures)
        assertNull(backupData.dailyExpenses)
        assertNull(backupData.rationItems)
    }
}
