package com.pms.rcuapp.view.menu

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.FragmentLoadWebUrlBinding
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.viewmodel.WebViewViewModel
import java.net.URL


class WebViewFragment: BaseFragment() {

    private var _binding: FragmentLoadWebUrlBinding? = null
    private val binding get() = _binding!!
    private val webViewViewModel by lazy { WebViewViewModel(activity as Context) }
    var menuId: String = ""

    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null


    private val fileUploadActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val results = result.data?.let { WebChromeClient.FileChooserParams.parseResult(result.resultCode, it) }
            fileUploadCallback?.onReceiveValue(results)
        } else {
            fileUploadCallback?.onReceiveValue(null)
        }
        fileUploadCallback = null
    }
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLoadWebUrlBinding.inflate(inflater, container, false)
        binding.viewModel = webViewViewModel
        binding.lifecycleOwner = this
        menuId = requireArguments().getString("webURL").toString()
        context?.let { webViewViewModel.init(it,menuId) }

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

        Log.e("MenuId",menuId)

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), callback)

        val callback = requireActivity().onBackPressedDispatcher.addCallback(requireActivity()) {
            findNavController().navigate(R.id.action_webViewFragment_to_dashboardMenuFragment)
        }

        webViewViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        if(Build.VERSION.SDK_INT >= 21){
            webSettings.setMixedContentMode(0);
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT >= 19){
            binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }else if(Build.VERSION.SDK_INT < 19){
            binding.webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        // Enable file upload from the WebView
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                fileUploadCallback = filePathCallback ?: return false
                val intent = fileChooserParams?.createIntent()
                fileUploadActivityResultLauncher.launch(intent)
                return true
            }
        }

        // Set up a WebViewClient to handle page navigation and downloads
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                // Handle page navigation within the WebView
                view?.loadUrl(request?.url.toString())
                return true
            }

            override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                // Handle downloads from the WebView
                val url = request?.url.toString()
                if (url?.endsWith(".jpg") == true || url?.endsWith(".png") == true || url?.endsWith(".gif") == true) {
                    val connection = URL(url).openConnection()
                    connection.connect()
                    val inputStream = connection.inputStream
                    return WebResourceResponse("image/*", "UTF-8", inputStream)
                }
                return super.shouldInterceptRequest(view, request)
            }
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
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity?)!!.supportActionBar!!.show()
        (activity as AppCompatActivity?)!!.supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.setHomeAsUpIndicator(R.drawable.ic_nav_menup)
    }

    val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
             Log.e("OnBack","OnBack")

            }
        }
}