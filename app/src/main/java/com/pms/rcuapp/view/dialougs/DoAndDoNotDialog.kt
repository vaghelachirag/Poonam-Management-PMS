package com.pms.rcuapp.view.dialougs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.pms.rcuapp.R
import com.pms.rcuapp.databinding.DialogAddInventorySubitemBinding
import com.pms.rcuapp.databinding.DialougAcceptDoandDonotBinding

//
class DoAndDoNotDialog(private var mContext: Context, private val url: String) : Dialog(mContext, R.style.DialogTheme) {

    private lateinit var binding: DialougAcceptDoandDonotBinding
    private var listener: OkButtonListener? = null

    private var acceptRejectListSpinnerAdapter: ArrayAdapter<String?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialoug_accept_doand_donot,
            null,
            false
        )


        val lp = WindowManager.LayoutParams()
        lp.copyFrom(window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.CENTER


        window!!.setBackgroundDrawableResource(R.color.dialoug_main_bg);


        window!!.setAttributes(lp)

        setContentView(binding.root)


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


        if (url.isNotEmpty()) {
            binding.webView.getSettings().javaScriptEnabled = true;

            binding.webView.setWebViewClient(object : WebViewClient() {
                @Deprecated(
                    "Deprecated in Java", ReplaceWith(
                        "Toast.makeText(activity, description, Toast.LENGTH_SHORT).show()",
                        "android.widget.Toast",
                        "android.widget.Toast"
                    )
                )
                override fun onReceivedError(
                    view: WebView,
                    errorCode: Int,
                    description: String,
                    failingUrl: String
                ) {
                    Toast.makeText(context, description, Toast.LENGTH_SHORT).show()
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
                }
            })

            binding.webView.loadUrl(url)

            binding.ivClose.setOnClickListener {
                dismiss()
            }

            binding.btnSave.setOnClickListener {
                if (listener != null) {
                    listener?.onOkPressed(
                        this
                    )

                }
            }
        }
    }

    fun setListener(listener: OkButtonListener?): DoAndDoNotDialog {
        this.listener = listener
        return this
    }

    interface OkButtonListener {
        fun onOkPressed(
            doNotDialog: DoAndDoNotDialog,
        )
    }
}