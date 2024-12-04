package com.pms.rcuapp.view.menu
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.IntentSender
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pms.rcuapp.view.dialougs.ChangePasswordDialoug
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.PendingResult
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResult
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pms.rcuapp.BuildConfig
import com.pms.rcuapp.MainActivity
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.ActivityDashboardBinding
import com.pms.rcuapp.interfaces.OnItemSelected
import com.pms.rcuapp.model.getMenuListResponse.GetMenuListData
import com.pms.rcuapp.model.getMenuListResponse.GetMenuListResponse
import com.pms.rcuapp.network.CallbackObserver
import com.pms.rcuapp.network.Networking
import com.pms.rcuapp.room.InitDb
import com.pms.rcuapp.services.LocationUpdatesService
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.Session
import com.pms.rcuapp.uttils.Utility
import com.pms.rcuapp.uttils.Utils
import com.pms.rcuapp.view.adapter.MenuItemAdapter
import com.pms.rcuapp.view.base.BaseActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import io.reactivex.Observable;

@Suppress("DEPRECATION")
class DashboardActivity : BaseActivity(){


    private lateinit var binding: ActivityDashboardBinding
    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private var activityDashboard : DashboardActivity? = null

    // Session
    var session: Session? = null;

    val isLoading = MutableLiveData<Boolean>()
    var menuList: ArrayList<GetMenuListData> = ArrayList()



    // A reference to the service used to get location updates.
    private var mService: LocationUpdatesService? = null;
    // Tracks the bound state of the service.
    private var mBound: Boolean = false

    private val MY_PERMISSIONS_REQUEST_LOCATION = 68
    private val REQUEST_CHECK_SETTINGS = 129

    private var broadcastReceiver: BroadcastReceiver? = null

    companion object {
        public  var currentLat : Double = 0.0
        public  var currentLong : Double = 0.0
        public  var useraddress : String = ""
    }


    // For Location Request
    private var googleApiClient: GoogleApiClient? = null




    @SuppressLint("DiscouragedPrivateApi", "SimpleDateFormat", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityDashboard = this
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        session = Session(this);

        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)


        // Set up ActionBar
        setSupportActionBar(binding.toolbarDashboard)
        val actionBar = supportActionBar
        if (actionBar != null) {
            supportActionBar!!.setDisplayShowTitleEnabled(false)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
            supportActionBar!!.title ="Test"
        }

        getSessionData()
        setVersionName()

        supportActionBar!!.title = "Test"
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_nav_menup)

        navController = findNavController(R.id.navHostFragmentPickford)

        appBarConfiguration = AppBarConfiguration.Builder(
            R.id.dashboardMenuFragment,
            R.id.settingFragment,
            R.id.dashboardMenuFragment,
        ).setDrawerLayout(binding.drawer).build()


        setupActionBarWithNavController(
            navController,
            appBarConfiguration
        )

        setupNavControl()
        setDrawerAction()
        setupToolbarWithMenu(getString(R.string.dashboard),binding)
        getMenuListResponse()
        askNotificationPermission()
        Log.e("Token", getToken(this).toString())

        googleApiClient = getAPIClientInstance();
        if (googleApiClient != null) {
            googleApiClient!!.connect();
        }



        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (getLocationMode() == 3) {
                        Log.e("MainActivity:","Already set High Accuracy Mode")
                        initializeService()
                    } else {
                        Log.e("MainActivity:","Alert Dialog Shown")
                        // showAlertDialog(this@DashboardActivity)
                        requestGPSSettings()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()


        broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(contxt: Context?, intent: Intent?) {

                when (intent?.action) {
                    "NotifyUser" -> {
                        try {
                            val name = intent.getStringExtra("pinned_location_name")
                            val lat = intent.getStringExtra("pinned_location_lat")
                            val long = intent.getStringExtra("pinned_location_long")

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }
    }


    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return false
    }

    private fun getAPIClientInstance(): GoogleApiClient {
        return GoogleApiClient.Builder(this)
            .addApi(LocationServices.API).build()
    }

    private fun requestGPSSettings() {
        val locationRequest: LocationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
        locationRequest.setInterval(2000)
        locationRequest.setFastestInterval(500)
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        builder.setAlwaysShow(true)
        val result: PendingResult<LocationSettingsResult> =
            LocationServices.SettingsApi.checkLocationSettings(googleApiClient!!, builder.build())
        result.setResultCallback { result ->
            val status: Status = result.status
            when (status.statusCode) {
                LocationSettingsStatusCodes.SUCCESS -> {
                    Log.i("", "All location settings are satisfied.")
                }

                LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                    Log.i(
                        "",
                        "Location settings are not satisfied. Show the user a dialog to" + "upgrade location settings "
                    )
                    try { status.startResolutionForResult(
                        this, REQUEST_CHECK_SETTINGS)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e("Applicationsett", e.toString())
                    }
                }

                LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                    Log.i(
                        "",
                        "Location settings are inadequate, and cannot be fixed here. Dialog " + "not created."
                    )
                }
            }
        }
    }


    private fun setVersionName() {
        binding.txtVersion.text = "Version : " +BuildConfig.VERSION_NAME
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter()
        intentFilter.addAction("NotifyUser")
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).registerReceiver(it, intentFilter)
        }
        navController = findNavController(R.id.navHostFragmentPickford)

        if (title != null) binding.tvTitle.text = "Dashboard"
        Log.e("OnResume","OnResume")
    }

    fun  setTitle(){
        if (title != null) binding.tvTitle.text = "Dashboard"
    }

    override fun onPause() {
        broadcastReceiver?.let {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(it)
        }
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Log.e("MainActivity:","Location Permission Granted")
                    if (getLocationMode() == 3) {
                        Log.e("MainActivity:","Already set High Accuracy Mode")
                        initializeService()
                    } else {
                        Log.e("MainActivity:","Alert Dialog Shown")
                        showAlertDialog(this@DashboardActivity)
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }


    private fun showAlertDialog(context: Context?) {
        try {
            context?.let {
                val builder = androidx.appcompat.app.AlertDialog.Builder(it)
                builder.setTitle(it.resources.getString(R.string.app_name))
                    .setMessage("Please select High accuracy Location Mode from Mode Settings")
                    .setPositiveButton(it.resources.getString(android.R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                        startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CHECK_SETTINGS)
                    }
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun getLocationMode(): Int {
        return Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            initializeService()
        }
    }

    // Monitors the state of the connection to the service.
    private var mServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: LocationUpdatesService.LocalBinder = service as LocationUpdatesService.LocalBinder
            mService = binder.service
            mBound = true
            // Check that the user hasn't revoked permissions by going to Settings.

            mService?.requestLocationUpdates()

        }

        override fun onServiceDisconnected(name: ComponentName?) {
            mService = null
            mBound = false
        }
    }

    private fun initializeService(){
        bindService(Intent(this, LocationUpdatesService::class.java), mServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        if (mBound) {
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
       //     unbindService(mServiceConnection)
        //    mBound = false
        }
        super.onStop()
    }

    private fun getToken(context: Context): String? {
        return context.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }

    private fun getSessionData() {
        val viewMenu = findViewById<View>(R.id.layoutMenu)
        val userName = viewMenu.findViewById<View>(R.id.txt_UserName) as TextView
        val profileImage = viewMenu.findViewById<View>(R.id.navHeaderLogo) as AppCompatImageView
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.ic_launcher_round)
            .circleCrop()
            .error(R.mipmap.ic_launcher_round)

        Glide.with(this).load(session!!.getUserProfileImageKey()).apply(options).into(profileImage)
        userName.text = session!!.getUserNameKey()
    }

    private fun setDrawerAction() {
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }

            when (menuItem.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardMenuFragment)
                    binding.toolbarDashboard.setTitle("Test")
                    supportActionBar!!.title = "test"
                    supportActionBar?.setDisplayShowHomeEnabled(false);
                    binding.toolbarDashboard.setNavigationIcon(null);

                }
                R.id.settingFragment -> {
                    navController.navigate(R.id.settingFragment)
                    binding.toolbarDashboard.setTitle(R.string.action_settings)
                    binding.toolbarDashboard.setNavigationIcon(null);

                }
                R.id.logout -> {
                    Utils().showAlertDialog(this,resources.getString(R.string.logoutAlert))
                }
            }
            true
        }
        binding.ivMenu.setOnClickListener {
            Log.e("Menu","MenuClicked")
            if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
                binding.drawer.closeDrawer(GravityCompat.START)
            }else{
                binding.drawer.openDrawer(GravityCompat.START)
            }
        }

    }

    private fun showLogoutAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Are You Sure Want To Logout?")

        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            session!!.clearSession()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            Toast.makeText(applicationContext,
                android.R.string.no, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun setupNavControl() {
        binding.navigationView.setupWithNavController(navController) //Setup Drawer navigation with navController
    }

    private fun getMenuListResponse() {
        if (Utility.isNetworkConnected(this)){
            isLoading.postValue(true)
            Networking.with(this)
                .getServices()
                .getMenuListResponse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : CallbackObserver<GetMenuListResponse>() {
                    override fun onSuccess(response: GetMenuListResponse) {
                        isLoading.postValue(false)
                    }

                    override fun onFailed(code: Int, message: String) {
                        isLoading.postValue(false)
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onNext(t: GetMenuListResponse) {
                        Log.e("Status",t.getStatusCode().toString())
                        isLoading.postValue(false)
                        if(t.getStatusCode() == 200){
                            if(t.getData() != null){
                                menuList  =  t.getData()!!
                                menuList.sortBy { it.getOrderNo() }
                                setMenuAdapter()
                            }
                        }else{
                            Utils().showToast(this@DashboardActivity ,t.getMessage().toString())
                        }
                    }
                })
        }else{

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public fun setMenuAdapter() {
        binding.rvMenu.layoutManager = LinearLayoutManager(this)
        binding.rvMenu.adapter = MenuItemAdapter(this, menuList,object :
            OnItemSelected<GetMenuListData> {

            override fun onItemSelected(t: GetMenuListData?, position: Int) {
                clickMenuEvent(t)
            }

        })

        binding.rvMenu.addItemDecoration(
            DividerItemDecoration(
                binding.rvMenu.context,
                (binding.rvMenu.layoutManager as LinearLayoutManager).orientation
            )
        )
    }

    private fun clickMenuEvent(menuData: GetMenuListData?) {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        if (menuData!!.getIsWebView() == true){

            val bundle = Bundle()
            bundle.putString("webURL", menuData.getMenuId().toString())

            if (title != null) binding.tvTitle.text = menuData.getName()

            binding.toolbarDashboard.setTitle(menuData.getName())
            navController.navigate(R.id.webViewFragment,bundle)
            supportActionBar?.setDisplayShowHomeEnabled(false);
            binding.toolbarDashboard.setNavigationIcon(null);
        }else{

            if (menuData.getMenuId() == AppConstants.home){
                navController.navigate(R.id.dashboardMenuFragment)
                supportActionBar?.setDisplayShowHomeEnabled(false);
                binding.toolbarDashboard.setNavigationIcon(null);
                if (title != null) binding.tvTitle.text = "Dashboard"
            }
            if (menuData.getMenuId() == AppConstants.changePassword){
                /*  navController.navigate(R.id.changePasswordFragment)
                  binding.toolbarDashboard.setTitle(R.string.changePassword_Txt)
                  binding.toolbarDashboard.setNavigationIcon(null);*/
                //showChangePasswordDialoug()
                Utils().showChangePasswordDialog(this,binding)
            }
            if (menuData.getMenuId() == AppConstants.logout){
                Utils().showAlertDialog(this,resources.getString(R.string.logoutAlert))
            }
        }
    }

    private fun showChangePasswordDialoug() {
        ChangePasswordDialoug(this)
            .setListener(object : ChangePasswordDialoug.OkButtonListener {
                override fun onOkPressed(
                    acceptRejectFIDialog: ChangePasswordDialoug
                ) {

                }

            })
            .show()
    }

    fun redirectToLogin(){
     Log.e("Login","Login")
        session!!.clearSession()
        CoroutineScope(Dispatchers.IO).launch {
            InitDb.appDatabase.clearAllTables()
        }
        val intentLogin = Intent(this, MainActivity()::class.java)
        startActivity(intentLogin)
        finish()
    }

}