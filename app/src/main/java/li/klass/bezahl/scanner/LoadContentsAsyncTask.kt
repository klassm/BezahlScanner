package li.klass.bezahl.scanner

import android.os.AsyncTask
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.DriveFile

class LoadContentsAsyncTask(private val googleApiClient: GoogleApiClient) : AsyncTask<DriveFile, Void, String>() {

    override fun doInBackground(vararg driveFiles: DriveFile): String? {
        val file = driveFiles[0]
        val driveContentsResult = file.open(
                googleApiClient, DriveFile.MODE_READ_ONLY, null).await()
        if (!driveContentsResult.status.isSuccess) {
            return null
        }
        return driveContentsResult.driveContents.inputStream.use { it.readBytes().toString(Charsets.UTF_8) }
    }
}
