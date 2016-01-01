package li.klass.bezahlscanner;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class WriteContentsAsyncTask extends AsyncTask<DriveFile, Void, Void> {

    private final GoogleApiClient googleApiClient;
    private final String contentToWrite;

    public WriteContentsAsyncTask(GoogleApiClient googleApiClient, String contentToWrite) {
        this.googleApiClient = googleApiClient;
        this.contentToWrite = contentToWrite;
    }

    @Override
    protected Void doInBackground(DriveFile... driveFiles) {
        DriveFile file = driveFiles[0];
        DriveApi.DriveContentsResult driveContentsResult = file.open(
                googleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        try {
            OutputStream outputStream = driveContents.getOutputStream();
            outputStream.write(contentToWrite.getBytes(Charsets.UTF_8));
            driveContents.commit(googleApiClient, null).await();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
