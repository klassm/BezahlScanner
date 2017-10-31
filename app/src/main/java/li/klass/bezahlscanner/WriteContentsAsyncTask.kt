package li.klass.bezahlscanner

import android.os.AsyncTask
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.DriveFile
import com.google.common.base.Charsets
import java.io.IOException

class WriteContentsAsyncTask(private val googleApiClient: GoogleApiClient, private val contentToWrite: String) : AsyncTask<DriveFile, Void, Void>() {

    override fun doInBackground(vararg driveFiles: DriveFile): Void? {
        val file = driveFiles[0]
        val driveContentsResult = file.open(
                googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await()
        if (!driveContentsResult.status.isSuccess) {
            return null
        }
        val driveContents = driveContentsResult.driveContents
        try {
            val outputStream = driveContents.outputStream
            outputStream.write(contentToWrite.toByteArray(Charsets.UTF_8))
            driveContents.commit(googleApiClient, null).await()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }

        return null
    }
}
