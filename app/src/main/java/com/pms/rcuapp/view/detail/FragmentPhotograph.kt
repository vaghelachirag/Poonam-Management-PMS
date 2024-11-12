package com.pms.rcuapp.view.detail

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.view.forEach
import androidx.lifecycle.lifecycleScope
import com.karumi.dexter.BuildConfig
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.pms.rcuapp.databinding.FragmentPhotographBinding
import com.pms.rcuapp.R
import com.pms.rcuapp.interfaces.FragmentLifecycleInterface
import com.pms.rcuapp.uttils.AppConstants
import com.pms.rcuapp.uttils.ImagePickerDialog
import com.pms.rcuapp.uttils.Utility
import com.pms.rcuapp.uttils.Utility.Companion.setAllEnabled
import com.pms.rcuapp.uttils.onItemClick
import com.pms.rcuapp.view.base.BaseFragment
import com.pms.rcuapp.view.menu.DashboardActivity
import com.pms.rcuapp.viewmodel.verificationDetail.PictureViewModel
import id.zelory.compressor.Compressor
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date
import java.util.Objects


class FragmentPhotograph: BaseFragment(), FragmentLifecycleInterface {


    private var _binding: FragmentPhotographBinding? = null
    // This property is only valid between onCreateView and
    private val binding get() = _binding!!
    var data : String = ""
    private val photoVerificationViewModel by lazy { PictureViewModel( context as Activity,binding) }

    private var imgFile: File? = null
    private var imagePath: Uri? = null
    private val cameraCode: Int = 0x50
    val galleryCode: Int = 0x51
    private var compressedImage: File? = null

    companion object {
        fun newInstance(): FragmentPhotograph {
            val bundle = Bundle()
            val fragmentPhotograph = FragmentPhotograph()
            fragmentPhotograph.arguments = bundle
            return fragmentPhotograph
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPhotographBinding.inflate(inflater, container, false)
        binding.viewModel = photoVerificationViewModel
        binding.lifecycleOwner = this
        photoVerificationViewModel.init(context as Activity)

        setView()

        Log.e("OnCrete","Photo")

        photoVerificationViewModel.isLoading.observe(requireActivity()) { isLoading ->
            if (isLoading && isAdded) showProgressbar()
            else if (!isLoading && isAdded) hideProgressbar()
        }

        binding.layoutCamera.setOnClickListener {
            checkImagePickerPermission()
        }
        return binding.root
    }

    private fun setView() {
        if(ActivityDetail.selectedData!!.getStatus() != null){
            if(ActivityDetail.selectedData!!.getStatus() == AppConstants.statusPending){
                //   binding.constraintLayout.forEach { child -> child.setAllEnabled(false) }
            }
            else{
                binding.constraintLayout.forEach { child -> child.setAllEnabled(true) }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == cameraCode && resultCode == Activity.RESULT_OK) {
            uploadImage()
        }
        if (requestCode == galleryCode && resultCode == Activity.RESULT_OK) {
            imagePath = Objects.requireNonNull(data!!).data
            imgFile = File(Objects.requireNonNull(imagePath?.let {
                Utility.getPath(requireActivity(), it)
            }))
            uploadImage()
        }
    }


    // For Check Image permission
    private fun checkImagePickerPermission() {
        Dexter.withActivity(requireActivity())
            .withPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // openImagePickerDialog()
                    displayCamera()

                    /* if (report!!.areAllPermissionsGranted()) {
                         openImagePickerDialog()
                     } else {
                         Utility.showSettingsDialog(requireActivity())
                     }
                     if (report.isAnyPermissionPermanentlyDenied) {
                         Utility.showSettingsDialog(requireActivity())
                     }*/
                }

                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<com.karumi.dexter.listener.PermissionRequest>?, token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }
            }).withErrorListener { }.onSameThread().check()
    }

    // For Check Image permission
    private fun openImagePickerDialog() {
        ImagePickerDialog(requireActivity(), object : onItemClick {
            override fun onCameraClicked() {
                displayCamera()
            }

            override fun onGalleryClicked() {
                val pickPhoto =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(pickPhoto, galleryCode)
            }
        }).show()
    }

    // Display Camera Pic
    fun displayCamera() {
        val destPath: String? = Objects.requireNonNull(Objects.requireNonNull(requireActivity()).getExternalFilesDir(null)!!).absolutePath
        val imagesFolder = File(destPath, this.resources.getString(R.string.app_name))
        try {
            imagesFolder.mkdirs()
            imgFile = File(imagesFolder, Date().time.toString() + ".jpg")
            imagePath = FileProvider.getUriForFile(
                requireActivity(),
                BuildConfig.APPLICATION_ID + ".fileProvider",
                imgFile!!
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePath)
            startActivityForResult(intent, cameraCode)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    // For Upload Image
    @RequiresApi(Build.VERSION_CODES.Q)
    @SuppressLint("DefaultLocale")
    private fun uploadImage() {
        if (imgFile != null) {

            val filePath: String = imgFile!!.absolutePath
            var bitmap = BitmapFactory.decodeFile(filePath)


            val ei = ExifInterface(filePath)
            val orientation: Int = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
            Log.e("Rotation",orientation.toString())


            var angle  =  Utility.rotateImageAngle(orientation)



            bitmap = Utility.rotateImage(bitmap,angle)
            binding.ivImage.setImageBitmap(bitmap)


            val workingBitmap: Bitmap = Bitmap.createBitmap(bitmap)
            val mutableBitmap = workingBitmap.copy(Bitmap.Config.ARGB_8888, true)

            val builder = StringBuilder()
            builder.append(Utility.getCurrentDate()).append("\n")
            builder.append("Latitude: ").append(String.format("%.6f", DashboardActivity.currentLat)).append("\n")
            builder.append("Longitude: ").append(String.format("%.6f", DashboardActivity.currentLong)).append("\n")
            builder.append(DashboardActivity.useraddress)

            val scale = resources.displayMetrics.density

            //  saveToInternalStorage(mutableBitmap)

            val canvas1 = Canvas(mutableBitmap)
            val paint1 = TextPaint(Paint.ANTI_ALIAS_FLAG)

            // text color - #3D3D3D
            paint1.color = Color.WHITE

            // text size in pixels
            paint1.textSize = 100F

            // text shadow
            ///   paint1.setShadowLayer(1f, 0f, 0f, Color.WHITE)


            // set text width to canvas width minus 16dp padding
            val textWidth = canvas1.width - (16 * scale).toInt()

            // init StaticLayout for text
            val textLayout = StaticLayout(builder.toString(), paint1, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false)


            // get height of multiline text
            val textHeight = textLayout.height

            // get position of text's top left corner
            val x = ((mutableBitmap.width - textWidth) - 10).toFloat()
            val y = ((mutableBitmap.height - textHeight) - 40).toFloat()

            //  paint1.setShadowLayer(100F, 100F, 100F,Color.RED);

            paint1.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

            // draw text to the Canvas center
            canvas1.save()
            canvas1.translate(x, y)
            textLayout.draw(canvas1)

            //  binding.ivImage.setImageBitmap(mutableBitmap)
            saveImage(mutableBitmap)
        }

    }

    private fun saveImage(mutableBitmap: Bitmap?) {
        val destPath: String? = Objects.requireNonNull(Objects.requireNonNull(requireActivity()).getExternalFilesDir(null)!!).absolutePath
        val imagesFolder = File(destPath, this.resources.getString(R.string.app_name))

        //create a file to write bitmap data
        val locationImageFile: File = File(requireActivity().cacheDir, this.resources.getString(R.string.app_name) + ".jpg")
        locationImageFile.createNewFile()
        var bitmap: Bitmap = mutableBitmap!!
        val bos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos);
        var bitmapdata = bos.toByteArray()
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(locationImageFile)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }


        try {
            fos!!.write(bitmapdata)
            fos!!.flush()
            fos!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        lifecycleScope.launch {
            compressedImage = Compressor.compress(requireActivity(), locationImageFile)
            bitmap = BitmapFactory.decodeFile(compressedImage!!.absolutePath)
            setCompressedImage()
        }

    }

    private fun setCompressedImage() {
        compressedImage?.let {
            photoVerificationViewModel.saveSurveyPicture(compressedImage!!)
        }
    }

    fun getResizedBitmap(image: Bitmap, maxSize: Int): Bitmap {

        val nh = (image.height * (512.0 / image.width)).toInt()
        val scaled = Bitmap.createScaledBitmap(image, 512, nh, true)

        return scaled
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onPauseFragment() {
    }

    override fun onResumeFragment(s: String?) {
        Log.e("OnResume","Photo")
    }
}