package com.pms.rcuapp.view.menu

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.DownloadListener
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.FragmentLoadWebUrlBinding
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.WebViewViewModel
import java.io.File
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date


class WebViewFragment: BaseFragment() {

    private var _binding: FragmentLoadWebUrlBinding? = null
    private val binding get() = _binding!!
    private val webViewViewModel by lazy { WebViewViewModel(activity as Context) }
    var menuId: String = ""

    private var mCM: String? = null
    private var mUM: ValueCallback<Uri>? = null
    private var mUMA: ValueCallback<Array<Uri>>? = null
    private
    val FCR: Int = 1


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoadWebUrlBinding.inflate(inflater, container, false)
        binding.viewModel = webViewViewModel
        binding.lifecycleOwner = this
        menuId = requireArguments().getString("webURL").toString()
        context?.let { webViewViewModel.init(it,menuId) }

        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                1
            )
        }
        val webSettings: WebSettings = binding.webView.getSettings()
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true
        webSettings.loadWithOverviewMode = false
        webSettings.allowFileAccess = true
        webSettings.builtInZoomControls = false
        webSettings.displayZoomControls = false
        webSettings.allowFileAccess = true;
        webSettings.allowContentAccess = true;
        webSettings.domStorageEnabled = true

        //        webSettings.setAppCacheEnabled(false);
        webSettings.loadsImagesAutomatically = true
        webSettings.useWideViewPort = false
        webSettings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW

        webSettings.mixedContentMode = 0;
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        webSettings.mixedContentMode = 0
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        binding.webView.webViewClient = Callback()

        Log.e("MenuId",menuId)


        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            findNavController().navigate(R.id.action_webViewFragment_to_dashboardMenuFragment)
        }

        binding.webView.webChromeClient = object : WebChromeClient() {
            //For Android 3.0+
            fun openFileChooser(uploadMsg: ValueCallback<Uri>) {
                mUM = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.setType("*/*")
               requireActivity().startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                    FCR
                )
            }

            //For Android 4.1+
            fun openFileChooser(
                uploadMsg: ValueCallback<Uri>,
                acceptType: String?,
                capture: String?
            ) {
                mUM = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.setType("*/*")
                requireActivity().startActivityForResult(
                    Intent.createChooser(i, "File Chooser"),
                    FCR
                )
            }

            //For Android 5.0+
            @SuppressLint("QueryPermissionsNeeded")
            override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                mUMA?.onReceiveValue(null)
                mUMA = filePathCallback
                var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (takePictureIntent!!.resolveActivity(requireActivity().packageManager) != null) {
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                        takePictureIntent.putExtra("PhotoPath", mCM)
                    } catch (ex: IOException) {
                        // Log.e(MainActivity.TAG, "Image file creation failed", ex)
                    }
                    if (photoFile != null) {
                        mCM = "file:" + photoFile.absolutePath
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile)
                        )
                    } else {
                        takePictureIntent = null
                    }
                }
                val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                contentSelectionIntent.setType("*/*")
                val intentArray = if (takePictureIntent != null) {
                    arrayOf<Intent?>(takePictureIntent)
                } else {
                    arrayOfNulls(0)
                }

                val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                startActivityForResult(chooserIntent, FCR)
                return true
            }
        }

        // Create an image file
        @Throws(IOException::class)
        fun createImageFiles(): File {
            @SuppressLint("SimpleDateFormat") val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(
                    Date()
                )
            val imageFileName = "img_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        webViewViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        binding.webView.setWebViewClient(object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (url.endsWith(".pdf")) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    // if want to download pdf manually create AsyncTask here
                    // and download file
                    return true
                }

                return false
            }
        })


        // Create an image file
        @Throws(IOException::class)
        fun createImageFile(): File {
            @SuppressLint("SimpleDateFormat") val timeStamp =
                SimpleDateFormat("yyyyMMdd_HHmmss").format(
                    Date()
                )
            val imageFileName = "img_" + timeStamp + "_"
            val storageDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        binding.webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            val request = DownloadManager.Request(Uri.parse(url))
            request.setMimeType(mimeType)
            request.addRequestHeader("cookie", CookieManager.getInstance().getCookie(url))
            request.addRequestHeader("User-Agent", userAgent)
            request.setDescription("Downloading file...")
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType))
            request.allowScanningByMediaScanner()
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            request.setDestinationInExternalFilesDir(
                requireActivity(),
                Environment.DIRECTORY_DOWNLOADS,
                ".png"
            )
            val dm = requireActivity().getSystemService(context.toString()) as DownloadManager
            dm.enqueue(request)
            Toast.makeText(requireActivity(), "Downloading File", Toast.LENGTH_LONG).show()
        }

        binding.webView.setDownloadListener(object : DownloadListener {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onDownloadStart(url: String, userAgent: String, contentDisposition: String, mimetype: String, contentLength: Long) {
                val request = DownloadManager.Request(Uri.parse(url))
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimetype))
                request.setDescription("Downloading file...")
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS,
                    URLUtil.guessFileName(url, contentDisposition, mimetype)
                )
                val dm = requireActivity().getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
                dm!!.enqueue(request)
                Toast.makeText(requireActivity().applicationContext, "Downloading...", Toast.LENGTH_SHORT).show()
                requireActivity().registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                    Context.RECEIVER_NOT_EXPORTED)
            }

            var onComplete: BroadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    Toast.makeText(
                        requireActivity().applicationContext,
                        "Downloading Complete",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })


        webViewViewModel.webViewURL.observeForever {
            if(it.isNotEmpty()){
                Log.e("URL",it.toString())
                binding.webView.getSettings().javaScriptEnabled = true;

                showProgressbar()
                binding.webView.setWebViewClient(object : WebViewClient() {
                    @Deprecated("Deprecated in Java", ReplaceWith("Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()", "android.widget.Toast", "android.widget.Toast"
                    )
                    )
                    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                        Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()
                    }

                    override fun onReceivedError(
                        view: WebView,
                        req: WebResourceRequest,
                        rerr: WebResourceError
                    ) {
                        // Redirect to deprecated method, so you can use it in all SDK versions
                        onReceivedError(
                            view,
                            rerr.errorCode,
                            rerr.description.toString(),
                            req.url.toString()
                        )
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        hideProgressbar()
                    }
                })

                binding.webView.loadUrl(webViewViewModel.webViewURL.value.toString())
            }


            // Create an image file
            @Throws(IOException::class)
            fun createImageFiles(): File {
                @SuppressLint("SimpleDateFormat") val timeStamp =
                    SimpleDateFormat("yyyyMMdd_HHmmss").format(
                        Date()
                    )
                val imageFileName = "img_" + timeStamp + "_"
                val storageDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                return File.createTempFile(imageFileName, ".jpg", storageDir)
            }

        }

        return binding.root
    }

    class Callback : WebViewClient() {
        @Deprecated("Deprecated in Java")
        override fun onReceivedError(
            view: WebView,
            errorCode: Int,
            description: String,
            failingUrl: String
        ) {

        }
    }

    // Create an image file
    @Throws(IOException::class)
    private fun createImageFile(): File {
        @SuppressLint("SimpleDateFormat") val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss").format(
                Date()
            )
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_nav_menup)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        var results: Array<Uri>? = null
        //Check if response is positive
        if (resultCode == RESULT_OK) {
            if (requestCode == FCR) {
                if (null == mUMA) {
                    return
                }
                if (intent == null) {
                    //Capture Photo if no image available
                    if (mCM != null) {
                        results = arrayOf(Uri.parse(mCM))
                    }
                } else {
                    val dataString = intent.dataString
                    if (dataString != null) {
                        results = arrayOf(Uri.parse(dataString))
                    }
                }
            }
        }
        mUMA!!.onReceiveValue(results)
        mUMA = null
    }


}
