package com.example.whatsappstatussaver.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.activities.ImageViewerActivity
import com.example.whatsappstatussaver.adapters.ImageAdapter
import com.example.whatsappstatussaver.data.ImageUri
import com.example.whatsappstatussaver.databinding.FragmentImagesBinding
import java.io.IOException
import java.io.OutputStream

class ImagesFragment : Fragment() {

    private lateinit var binding: FragmentImagesBinding
    private lateinit var statusList: ArrayList<ImageUri>
    private lateinit var imageAdapter: ImageAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    //    onViewCreated method initializes a list to store file URIs, reads a stored URI path from SharedPreferences, and attempts to access files at that path.
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusList = ArrayList()  // Initializes an empty list to store ImageUri objects.
        imageAdapter = ImageAdapter(
            statusList,
            { imageUri -> imageClicked(imageUri) },
            { imageUri -> fabClicked(imageUri) })

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
                    0 -> sortByAscendingTime()
                    1 -> sortByDescendingTime()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onResume() {
        super.onResume()
        loadImages()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun loadImages(){
        statusList.clear()
        val result = readDataFromPrefs()  //  Calls a method to read data from SharedPreferences.
        Log.d(TAG, "onViewCreated: readDataFromPrefs result = $result")
        if (result) {
            val sh = requireActivity().getSharedPreferences(
                "DATA_PATH",
                Context.MODE_PRIVATE
            )  // Retrieves the URI path from SharedPreferences.
            val uriPath = sh.getString("PATH", "")  // Retrieves the stored URI path as a string.
            Log.d(TAG, "onViewCreated: uriPath = $uriPath")
            // Handle URI path

            uriPath?.let {  // Checks if the URI path is not null.
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )  //Requests permission to read from the URI permanently.
                    val fileDoc = DocumentFile.fromTreeUri(
                        requireContext(),
                        Uri.parse(it)
                    )  // Retrieves the DocumentFile object for the specified URI.
                    fileDoc?.listFiles()
                        ?.forEach { file ->  //  Iterates over the files in the directory.
                            if (file.name!!.endsWith(".jpg") || file.name!!.endsWith(".png") || file.name!!.endsWith(
                                    ".jpeg"
                                )
                            ) {
                                val imageUri = ImageUri(
                                    file.uri,
                                    file.lastModified())
                                statusList.add(imageUri)
                                Log.d(TAG, "onViewCreated: Added imageUri = $imageUri")

                            }  // Creates an ImageUri object for the file.
                            // Adds the ImageUri to statusList.
                        }
                    setUpRecyclerView(statusList)
                }
                catch (e: Exception) {
                    Log.e(TAG, "Error accessing URI : $uriPath", e)
                    getFolderPermission()
                }
            } ?: run {
                getFolderPermission()  //  If uriPath is null, calls getFolderPermission() to request permissions.
            }
        } else {
            getFolderPermission()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByAscendingTime() {
        statusList.sortBy { it.timeStamp }
        imageAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun sortByDescendingTime() {
        statusList.sortByDescending { it.timeStamp }
        imageAdapter.notifyDataSetChanged()
    }

    private fun readDataFromPrefs(): Boolean {
        val sh = requireActivity().getSharedPreferences(
            "DATA_PATH",
            Context.MODE_PRIVATE
        )  //This line retrieves a SharedPreferences object named "DATA_PATH" in private mode, which means the file is accessible only by this application.
        val uriPath = sh.getString(
            "PATH",
            ""
        )   // This line reads a string value associated with the key "PATH" from the SharedPreferences. If the key does not exist, it returns an empty string ("").
        Log.d(TAG, "readDataFromPrefs: uriPath = $uriPath")
        return !uriPath.isNullOrEmpty()
    }

    //The getFolderPermission function  is used to request access to a specific directory using the Storage Access Framework (SAF) on Android Q (API level 29) and higher.
    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFolderPermission() {
        Log.d(TAG, "getFolderPermission: Requesting folder permission")
        val storageManager =
            requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
        val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
        val targetDirectory = "Android%2Fmedia%2Fcom.whatsapp%2FWhatsApp%2FMedia%2F.Statuses"
        var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
        var scheme = uri.toString()
        scheme = scheme.replace("/root", "/document")
        scheme += "%3A$targetDirectory"
        uri = Uri.parse(scheme)
        intent.putExtra("android.provider.extra.INITIAL_URI", uri)
        intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
//        startActivityForResult(intent, 1001)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1001 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                requireActivity().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
                with(sh.edit()) {
                    putString("PATH", uri.toString())
                    apply()
                }
                Log.d(TAG, "onActivityResult: URI permission granted and saved to SharedPreferences")
            }
        }
    }

    private fun setUpRecyclerView(imagesList: List<ImageUri>) {
        imageAdapter = ImageAdapter(
            imagesList,
            { imageUri -> imageClicked(imageUri) },
            { imageUri -> fabClicked(imageUri) }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = imageAdapter
    }

    private fun imageClicked(status: ImageUri) {
        val intent = Intent(requireContext(), ImageViewerActivity::class.java)
        intent.putExtra("imageUri", status.imageUri)
        startActivity(intent)
    }

    private fun fabClicked(status: ImageUri) {
        val dialog = android.app.Dialog(this@ImagesFragment.requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val downloadButton = dialog.findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            dialog.dismiss()
            saveFile(status)
        }
    }

    private fun saveFile(status: ImageUri) {
        val inputStream = requireActivity().contentResolver.openInputStream(status.imageUri)
        val fileName = "${System.currentTimeMillis()}.jpg"
        try {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, "images/jpg")
            values.put(MediaStore.MediaColumns.MIME_TYPE, "images/png")
            values.put(MediaStore.MediaColumns.MIME_TYPE, "images/jpeg")
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS +
                        "/Whatsapp Statuses/"
            )
            val uri = requireActivity().contentResolver.insert(
                MediaStore.Files.getContentUri("external"), values
            )
            val outputStream: OutputStream = uri?.let {
                requireActivity().contentResolver.openOutputStream(it)
            }!!
            if (inputStream != null) {
                outputStream.write(inputStream.readBytes())
            }
            outputStream.close()
            Toast.makeText(requireContext(), "Image Saved", Toast.LENGTH_SHORT).show()
            showDownloadNotification(fileName)

        } catch (e: IOException) {
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
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








