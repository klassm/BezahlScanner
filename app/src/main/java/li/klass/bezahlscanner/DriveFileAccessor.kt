package li.klass.bezahlscanner

import android.util.Log
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.ResultCallback
import com.google.android.gms.drive.Drive
import com.google.android.gms.drive.DriveApi
import com.google.android.gms.drive.DriveId
import com.google.android.gms.drive.MetadataChangeSet
import com.google.android.gms.drive.query.Filters
import com.google.android.gms.drive.query.Query
import com.google.android.gms.drive.query.SearchableField
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import org.jetbrains.anko.coroutines.experimental.bg

class DriveFileAccessor(private val googleApiClient: GoogleApiClient) {
    private var driveId: DriveId? = null
    private var dataHolder = DataHolder("")

    val payments: List<Payment>
        get() = dataHolder.getPayments()

    fun start(initDone: InitDone) {
        Log.e(TAG, "start() - starting accessor")
        val query = Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, FILE_TITLE)).build()
        Drive.DriveApi.query(googleApiClient, query)
                .setResultCallback(queryCallback(initDone))
    }

    private fun queryCallback(initDone: InitDone): ResultCallback<DriveApi.MetadataBufferResult> {
        return ResultCallback { result ->
            if (!result.status.isSuccess) {
                Log.e(TAG, "queryCallback() - query result is error")
                return@ResultCallback
            }

            if (result.metadataBuffer.count == 0) {
                Log.i(TAG, "queryCallback() - creating new file")
                createNewFile(object : FileLoadedCallback {

                    override fun onLoaded(driveId: DriveId) {
                        loadFileContent(driveId)
                        initDone.onInitDone()
                        result.release()
                    }
                })
            } else {
                Log.i(TAG, "queryCallback() - using existing file")
                loadFileContent(result.metadataBuffer.get(0).driveId)
                result.release()
                initDone.onInitDone()
            }
        }
    }

    private fun createNewFile(callback: FileLoadedCallback) {

        Drive.DriveApi.newDriveContents(googleApiClient)
                .setResultCallback({ result ->
                    if (result.status.isSuccess) {
                        async(UI) {
                            bg {
                                val changeSet = MetadataChangeSet.Builder()
                                        .setTitle(FILE_TITLE)
                                        .setMimeType("text/csv")
                                        .setStarred(false).build()

                                // create a file on root folder
                                Drive.DriveApi.getRootFolder(googleApiClient)
                                        .createFile(googleApiClient, changeSet, result.driveContents)
                                        .setResultCallback({ driveFileResult ->
                                            if (driveFileResult.status.isSuccess) {
                                                callback.onLoaded(driveFileResult.driveFile.driveId)
                                            }
                                        })
                            }
                        }
                    }
                })
    }

    private fun loadFileContent(driveId: DriveId) {
        this.driveId = driveId

        try {
            val content = LoadContentsAsyncTask(googleApiClient).execute(driveId.asDriveFile()).get()
            dataHolder = DataHolder(content)
        } catch (e: Exception) {
            throw RuntimeException(e)
        }

    }

    fun addPayment(payment: Payment) {
        dataHolder.addPayment(payment)

        WriteContentsAsyncTask(googleApiClient, dataHolder.toCsv()).execute(driveId!!.asDriveFile())
    }

    private interface FileLoadedCallback {
        fun onLoaded(driveId: DriveId)
    }

    interface InitDone {
        fun onInitDone()
    }

    companion object {

        val FILE_TITLE = "bezahlScannerData"
        private val TAG = DriveFileAccessor::class.java.name
    }
}
