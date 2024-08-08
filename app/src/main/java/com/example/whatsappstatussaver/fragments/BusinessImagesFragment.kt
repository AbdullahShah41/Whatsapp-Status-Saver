package com.example.whatsappstatussaver.fragments

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.Constants
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.adapters.ImageAdapter
import com.example.whatsappstatussaver.data.ModelImageUri
import com.example.whatsappstatussaver.databinding.FragmentImagesBinding
import com.example.whatsappstatussaver.viewmodels.BusinessImagesViewModel
import com.example.whatsappstatussaver.viewmodelsfactories.BussImagesViewModelFactory

class BusinessImagesFragment : Fragment() {

    private lateinit var binding: FragmentImagesBinding
    private lateinit var statusList: ArrayList<ModelImageUri>
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var b_viewModel: BusinessImagesViewModel
    private lateinit var getFolderPermissionLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(ContentValues.TAG, "onCreate Called")
        getFolderPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->

                        Log.i("permission", "Uri: $uri ")

                        requireActivity().contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )
                        val sh = requireActivity().getSharedPreferences(
                            "DATA_PATH",
                            Context.MODE_PRIVATE
                        )
                        with(sh.edit()) {
                            putString("BUSINESS_PATH", uri.toString())
                            apply()
                        }
                    }
                }

            }
    }

    private fun onBackPress() {
        parentFragmentManager.popBackStack()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(ContentValues.TAG, "onCreate View Called")
        binding = FragmentImagesBinding.inflate(inflater, container, false)
//        val factory = ImagesViewModelFactory(requireActivity().application)

        return binding.root
    }

    //    onViewCreated method initializes a list to store file URIs, reads a stored URI path from SharedPreferences, and attempts to access files at that path.
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(ContentValues.TAG, "onView Created Called")
        b_viewModel =
            ViewModelProvider(this, BussImagesViewModelFactory(requireActivity().application)).get(
                BusinessImagesViewModel::class.java
            )

        statusList = ArrayList()  // Initializes an empty list to store ImageUri objects.
        imageAdapter = ImageAdapter(
            ArrayList(),
            { imageUri -> imageClicked(imageUri) },
            { imageUri -> fabClicked(imageUri) })

        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = imageAdapter

        b_viewModel.statusList.observe(viewLifecycleOwner) { list ->
            imageAdapter.updateImages(list)
        }

        val sortSpinner: Spinner = binding.sortSpinner
        val sortOptions = arrayOf("New to Old", "Old to New")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
                    0 -> b_viewModel.sortByAscendingTime()
                    1 -> b_viewModel.sortByDescendingTime()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    //    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        Log.d(ContentValues.TAG, "onResume Called")
        super.onResume()
        loadImages()
    }

    //    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadImages() {
        statusList.clear()
        val result = readDataFromPrefs()  //  Calls a method to read data from SharedPreferences.
        Log.d(ContentValues.TAG, "onViewCreated: readDataFromPrefs result = $result")
        if (result) {
            val sh = requireActivity().getSharedPreferences(
                "DATA_PATH",
                Context.MODE_PRIVATE
            )  // Retrieves the URI path from SharedPreferences.
            val uriPath = sh.getString("BUSINESS_PATH", "")  // Retrieves the stored URI path as a string.
            Log.d(ContentValues.TAG, "load images: uriPath = $uriPath")
            // Handle URI path

            uriPath?.let {  // Checks if the URI path is not null.
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )  //Requests permission to read from the URI permanently.
                b_viewModel.loadImages(it)
                } catch (e: Exception) {
                    Log.e(ContentValues.TAG, "Error accessing URI : $uriPath", e)
                    getFolderPermission()
                }
            } ?: run {
                getFolderPermission()  //  If uriPath is null, calls getFolderPermission() to request permissions.
            }
        } else {
            getFolderPermission()
        }
    }

    private fun readDataFromPrefs(): Boolean {
        val sh = requireActivity().getSharedPreferences(
            "DATA_PATH",
            Context.MODE_PRIVATE
        )  //This line retrieves a SharedPreferences object named "DATA_PATH" in private mode, which means the file is accessible only by this application.
        val uriPath = sh.getString(
            "BUSINESS_PATH",
            ""
        )   // This line reads a string value associated with the key "PATH" from the SharedPreferences. If the key does not exist, it returns an empty string ("").
        Log.d(ContentValues.TAG, "readDataFromPrefs: uriPath = $uriPath")
        return !uriPath.isNullOrEmpty()
    }
    //The getFolderPermission function  is used to request access to a specific directory using the Storage Access Framework (SAF) on Android Q (API level 29) and higher.

    //    @RequiresApi(Build.VERSION_CODES.Q)
    private var backPressCancelled: Boolean = false
    private fun getFolderPermission() {

        Log.d(ContentValues.TAG, "getFolderPermission: Requesting folder permission")
        backPressCancelled = true
        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val storageManager =
                requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val initialUri =
                    intent.getParcelableExtra("android.provider.extra.INITIAL_URI", Uri::class.java)
                val targetDirectory = Constants.SIMPLE_WHATSAPP_PATH
                initialUri?.let {
                    var scheme = it.toString()
                    scheme = scheme.replace("/root", "/document")
                    scheme += "%3A$targetDirectory"
                    val newUri = Uri.parse(scheme)
                    intent.putExtra("android.provider.extra.INITIAL_URI", newUri)

                    Log.d(ContentValues.TAG, "Generated URI for API 33+: $newUri")
                }
            } else {
                val targetDirectory = Constants.SIMPLE_WHATSAPP_PATH
                val uri =
                    Uri.parse("content://com.android.externalstorage.documents/tree/primary%3A$targetDirectory")
                intent.putExtra("android.provider.extra.INITIAL_URI", Uri::class.java)
                Log.d(ContentValues.TAG, "Generated URI for API below 33: $uri")
            }
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            //            startActivityForResult(intent, 1001)
        } else {
            intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
//            startActivityForResult(intent, 1001)
        }
        getFolderPermissionLauncher.launch(intent)
//        handleBackPress()
    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (backPressCancelled) {
                Toast.makeText(requireContext(), "Folder Permission Cancelled ", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().finishAffinity()
            } else {
                isEnabled = false
                requireActivity().onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
                with(sh.edit()) {
                    putString("BUSINESS_PATH", uri.toString())
                    apply()
                }
                Log.d(
                    ContentValues.TAG,
                    "onActivityResult: URI permission granted and saved to SharedPreferences"
                )
            }
        } else {
            Toast.makeText(requireContext(), "Folder Permission Denied ", Toast.LENGTH_SHORT).show()
            return
//            requireActivity().finishAffinity()
        }
    }

    private fun imageClicked(status: ModelImageUri) {
//        val intent = Intent(requireContext(), ImageViewerActivity::class.java)
//        intent.putExtra("imageUri", status.imageUri)
//        startActivity(intent)
    }

    private fun fabClicked(status: ModelImageUri) {
        val dialog = android.app.Dialog(this.requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val downloadButton = dialog.findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            dialog.dismiss()
            b_viewModel.saveFile(
                status,
                onSuccess = {
                    Toast.makeText(requireContext(), "Image Saved", Toast.LENGTH_SHORT).show()
                    showDownloadNotification("${System.currentTimeMillis()}.jpg")
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                },
                activity = requireActivity())
        }
    }

    private fun showDownloadNotification(fileName: String) {

        val channelID = "download_channel"
        val notificationID = 2

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelID, "Download Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for download notifications"
            }
            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(requireContext(), channelID)
            .setSmallIcon(
                R.drawable.notifications
            )
            .setContentTitle("Download Completed")
            .setContentText("Image $fileName has been downloaded.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            with(NotificationManagerCompat.from(requireContext())) {
                notify(notificationID, builder.build())
            }
        } else {
            requestNotificationPermission()
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDownloadNotification("Image")
            } else {
                Toast.makeText(
                    requireContext(),
                    "Notification Permission Denied",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
