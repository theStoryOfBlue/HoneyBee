package com.example.receiptcareapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.receiptcareapp.R
import com.example.receiptcareapp.databinding.FragmentCameraBinding
import com.example.receiptcareapp.databinding.FragmentGalleryBinding
import com.example.receiptcareapp.databinding.FragmentShowPictureBinding
import com.example.receiptcareapp.fragment.base.BaseFragment
import com.example.receiptcareapp.fragment.viewModel.FragmentViewModel
import java.io.File
import kotlin.math.log

//class GalleryFragment : BaseFragment<FragmentGalleryBinding>(FragmentGalleryBinding::inflate) {
class GalleryFragment : Fragment() {

    private val binding by lazy { FragmentGalleryBinding.inflate(layoutInflater) }
    private val GALLERY = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private val GALLERY_CODE = 101

    private val viewModel : FragmentViewModel by viewModels({requireActivity()})


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("TAG", "onCreate: GalleryFragment", )
        CallGallery()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return binding.root
    }

    /** ????????? ?????? ?????? **/
    /* ????????? ?????? */
    fun CallGallery() {
        Log.e("TAG", "CallGallery ??????", )

        if(checkPermission(GALLERY)){
            Log.e("TAG", "?????? ?????? ??????", )

            val intent = Intent(Intent.ACTION_PICK)
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            activityResult.launch(intent)
        }
    }
    /* ????????? ?????? ?????? ?????? */
    private val activityResult: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK){
            Log.e("TAG", "onActivityResult: if ??????", )
            val imageUri: Uri? = it.data?.data
            if (imageUri != null) {
                Log.e("TAG", "data ??????", )
                viewModel.takeImage(imageUri)
                NavHostFragment.findNavController(this).navigate(R.id.action_galleryFragment_to_showFragment)
            }
            else{
                Log.e("TAG", "data ??????", )
            }
        }
        else{
            Log.e("TAG", "RESULT_OK if: else ??????", )
            findNavController().navigate(R.id.action_galleryFragment_to_homeFragment)
        }
    }


    /*** ?????? ?????? ?????? ***/
    fun checkPermission(permissions : Array<out String>) : Boolean{         // ?????? ????????? ???????????? ???
        Log.e("TAG", "checkPermission ??????", )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(), permissions, GALLERY_CODE)
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
            GALLERY_CODE -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(getActivity(), "?????? ????????? ????????? ?????????.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }
    }