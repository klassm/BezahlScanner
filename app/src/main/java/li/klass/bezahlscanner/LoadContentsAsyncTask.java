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

public class LoadContentsAsyncTask extends AsyncTask<DriveFile, Void, String> {

    private final GoogleApiClient googleApiClient;

    public LoadContentsAsyncTask(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    protected String doInBackground(DriveFile... driveFiles) {
        DriveFile file = driveFiles[0];
        DriveApi.DriveContentsResult driveContentsResult = file.open(
                googleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        try (InputStreamReader reader = new InputStreamReader(driveContents.getInputStream(), Charsets.UTF_8)) {
            return CharStreams.toString(reader);
        } catch (IOException e) {
            return null;
        }
    }
}
