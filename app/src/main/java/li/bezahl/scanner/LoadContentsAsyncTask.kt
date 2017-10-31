package li.bezahl.scanner

import android.os.AsyncTask
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.DriveFile
import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import java.io.IOException
import java.io.InputStreamReader

class LoadContentsAsyncTask(private val googleApiClient: GoogleApiClient) : AsyncTask<DriveFile, Void, String>() {

    override fun doInBackground(vararg driveFiles: DriveFile): String? {
        val file = driveFiles[0]
        val driveContentsResult = file.open(
                googleApiClient, DriveFile.MODE_READ_ONLY, null).await()
        if (!driveContentsResult.status.isSuccess) {
            return null
        }
        val driveContents = driveContentsResult.driveContents
        try {
            InputStreamReader(driveContents.inputStream, Charsets.UTF_8).use { reader -> return CharStreams.toString(reader) }
        } catch (e: IOException) {
            return null
        }
    }
}
