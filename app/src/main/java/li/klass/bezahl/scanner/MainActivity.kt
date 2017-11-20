package li.klass.bezahl.scanner

import android.app.AlertDialog
import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.widget.ListView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.drive.Drive
import com.google.zxing.integration.android.IntentIntegrator

class MainActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {

    private var googleApiClient: GoogleApiClient? = null
    private var driveFileAccessor: DriveFileAccessor? = null
    private val qrCodeTextParser = QrCodeTextParser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val scanButton = findViewById<FloatingActionButton>(R.id.scanAction)
        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this@MainActivity)
            integrator.initiateScan()
        }

        (findViewById<View>(R.id.payments) as ListView).adapter = PaymentAdapter(this)
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        val scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent)
        val msg: String
        if (scanResult != null && driveFileAccessor != null) {
            val payment = qrCodeTextParser.parse(scanResult.contents)
            if (payment != null) {
                driveFileAccessor!!.addPayment(payment)
                updatePaymentsView()
                msg = String.format(getString(R.string.payment_added_message), payment.name)
            } else {
                msg = getString(R.string.could_not_find_payment_in_qr_code)
            }
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        } else if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(intent)
            if (result.isSuccess) {
                Log.e(TAG, "sign in success")
                if (googleApiClient != null) {
                    connect()
                }
            } else {
                AlertDialog.Builder(this)
                        .setTitle(R.string.google_login_failed_title)
                        .setMessage(R.string.google_login_failed_text)
                        .setCancelable(false)
                        .setOnDismissListener { finish() }
                        .show()
                Log.e(TAG, result.status.toString())
            }
        }
    }

    private fun connect() {
        Log.e(TAG, "sign in success")
        driveFileAccessor = DriveFileAccessor(googleApiClient!!)
        driveFileAccessor!!.start(object : DriveFileAccessor.InitDone {
            override fun onInitDone() {
                updatePaymentsView()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        if (googleApiClient == null) {
            val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestScopes(Drive.SCOPE_FILE, Drive.SCOPE_APPFOLDER)
                    .build()

            googleApiClient = GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                    .addApi(Drive.API)
                    .build()

            val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
            startActivityForResult(signInIntent, RC_SIGN_IN)
        } else {
            googleApiClient!!.connect()
        }
    }

    override fun onStop() {
        if (googleApiClient != null && googleApiClient!!.isConnected) {
            googleApiClient!!.stopAutoManage(this)
            googleApiClient!!.disconnect()
        }
        super.onStop()
    }

    private fun updatePaymentsView() {
        ((findViewById<View>(R.id.payments) as ListView).adapter as PaymentAdapter).setData(driveFileAccessor!!.payments)
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        Log.e(TAG, result.toString())
        if (result.hasResolution()) {
            try {
                result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION)
            } catch (e: IntentSender.SendIntentException) {
                showError()
            }

        } else {
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.errorCode, 0).show()
        }
    }

    private fun showError() {
        Snackbar.make(findViewById(android.R.id.content), R.string.google_drive_connection_failed, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    companion object {

        private val REQUEST_CODE_RESOLUTION = 1
        private val RC_SIGN_IN = 9001

        private val TAG = MainActivity::class.java.name
    }
}
