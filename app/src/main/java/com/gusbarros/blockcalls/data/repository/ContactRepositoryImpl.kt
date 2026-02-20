package com.gusbarros.blockcalls.data.repository

import android.content.ContentResolver
import android.provider.ContactsContract
import android.util.Log
import com.gusbarros.blockcalls.data.util.PhoneNumberNormalizer
import com.gusbarros.blockcalls.domain.repository.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of ContactRepository using Android's ContentProvider.
 *
 * Queries the device's contact database to validate phone numbers.
 */
class ContactRepositoryImpl(
    private val contentResolver: ContentResolver
) : ContactRepository {

    override suspend fun isNumberInContacts(phoneNumber: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Normalizar remove +55 pois ContentProvider compara sem código de país
                val normalizedNumber = PhoneNumberNormalizer.normalize(phoneNumber)

                // Query usando PhoneLookup para matching inteligente
                val uri = ContactsContract.PhoneLookup.CONTENT_FILTER_URI
                    .buildUpon()
                    .appendPath(normalizedNumber)
                    .build()

                contentResolver.query(
                    uri,
                    arrayOf(ContactsContract.PhoneLookup._ID),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    cursor.count > 0
                } ?: false

            } catch (e: SecurityException) {
                // Permissão READ_CONTACTS negada
                Log.e(TAG, "Permission denied when querying contacts", e)
                false
            } catch (e: Exception) {
                // Qualquer outro erro
                Log.e(TAG, "Error querying contacts", e)
                false
            }
        }
    }

    companion object {
        private const val TAG = "ContactRepositoryImpl"
    }
}
