package com.example.receiptcareapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.receiptcareapp.R
import com.example.receiptcareapp.databinding.FragmentCameraBinding
import com.example.receiptcareapp.databinding.FragmentHomeBinding
import com.example.receiptcareapp.fragment.base.BaseFragment
import com.example.receiptcareapp.fragment.viewModel.FragmentViewModel
import com.example.receiptcareapp.viewModel.MainViewModel
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class CameraFragment : BaseFragment<FragmentCameraBinding>(FragmentCameraBinding::inflate) {
    private val CAMERA = arrayOf(android.Manifest.permission.CAMERA)
    private val CAMERA_CODE = 98
    private var photoURI : Uri? = null
    private val fragmentViewModel : FragmentViewModel by viewModels({requireActivity()})
    //private val activityViewModel : MainViewModel by viewModels({ requireActivity() })


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        CallCamera()
    }


    override fun onResume() {
        Log.e("TAG", "onResume: ", )
        super.onResume()
    }

    /** ????????? ?????? ?????? **/
    /* ????????? ?????? */
    fun CallCamera() {
        Log.e("TAG", "CallCamera ??????", )
        if (checkPermission(CAMERA)) {  // ????????? ?????? ?????? ??? ????????? ?????????
            Log.e("TAG", "????????? ?????? ??????", )
            dispatchTakePictureIntentEx()
        }
    }

    private fun dispatchTakePictureIntentEx() {
        Log.e("TAG", "dispatchTakePictureIntentEx: ??????", )
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())

        //????????? ????????????
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        //?????? ????????? ????????? ???????????????
        val uri : Uri? = createImageUri("JPEG_${timeStamp}_", "image/jpeg")
        println("my uri : $uri")

        photoURI = uri
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        activityResult.launch(takePictureIntent)
    }

    fun createImageUri(filename:String, mimeType:String):Uri? {
        Log.e("TAG", "createImageUri: ??????", )
        var values = ContentValues()
        values.put(MediaStore.Images.Media.DISPLAY_NAME,filename)
        values.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        return getActivity()?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }


    //????????? ?????? ??? ?????? ???
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            Log.e("TAG", "onActivityResult: if ??????", )
            if(photoURI != null) {

                fragmentViewModel.takeImage(photoURI!!)
//                fragmentViewModel.getMultiPartPicture(body)

                photoURI = null
                NavHostFragment.findNavController(this).navigate(R.id.action_cameraFragment_to_showFragment)
            }
        }
        else {
            Log.e("TAG", "RESULT_OK if: else ??????", )
            findNavController().navigate(R.id.action_cameraFragment_to_homeFragment)
        }
    }

    fun absolutelyPath(path: Uri?, context : Context): String {
        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = context.contentResolver.query(path!!, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        var result = c?.getString(index!!)
        return result!!
    }

    /*** ?????? ?????? ?????? ***/
    fun checkPermission(permissions : Array<out String>) : Boolean{         // ?????? ????????? ???????????? ???
        Log.e("TAG", "checkPermission ??????", )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), permissions, CAMERA_CODE)
                    return false;
                }
            }
        }
        return true
    }
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {  // ?????? ?????? ?????? ?????? ?????????
        Log.e("TAG", "onRequestPermissionsResult ??????", )

        when(requestCode) {
            CAMERA_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "????????? ????????? ????????? ?????????.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
}
