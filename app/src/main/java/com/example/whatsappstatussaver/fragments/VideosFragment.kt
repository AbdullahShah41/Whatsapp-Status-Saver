package com.example.whatsappstatussaver.fragments

import android.Manifest
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
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
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.Constants
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.activities.VideoViewActivity
import com.example.whatsappstatussaver.adapters.VideoAdapter
import com.example.whatsappstatussaver.data.ModelVideoUri
import com.example.whatsappstatussaver.databinding.FragmentVideosBinding
import com.example.whatsappstatussaver.viewmodels.ImagesViewModel
import com.example.whatsappstatussaver.viewmodels.VideosViewModel
import com.example.whatsappstatussaver.viewmodelsfactories.ImagesViewModelFactory
import com.example.whatsappstatussaver.viewmodelsfactories.VideosViewModelFactory

class VideosFragment : Fragment() {

    private lateinit var binding: FragmentVideosBinding
    private lateinit var statusList: ArrayList<ModelVideoUri>
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var viewModel: VideosViewModel
    private lateinit var getFolderPermissionLauncher: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getFolderPermissionLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {result ->
            if (result.resultCode == Activity.RESULT_OK){
                result.data?.data?.let {uri ->
                    requireActivity().contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                     )
                    val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
                    with(sh.edit()){
                        putString("PATH",uri.toString())
                        apply()
                    }
                }
            }
        }
//        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                parentFragmentManager.popBackStack()
//            }
//        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, VideosViewModelFactory(requireActivity().application))[VideosViewModel::class.java]
        statusList = ArrayList()
        videoAdapter = VideoAdapter(
            statusList,
            { videoUri -> fabClicked(videoUri) },
            { videoUri -> listItemClicked(videoUri) })

        binding.recyclerView2.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView2.adapter = videoAdapter

        viewModel.statusList.observe(viewLifecycleOwner){list ->
            videoAdapter.updateVideos(list)
        }

        val factory = VideosViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory).get(VideosViewModel::class.java)
        val sortSpinner : Spinner = binding.sortSpinner
        val sortOptions = arrayOf("New to Old","Old to New")
        val adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item,sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = adapter

        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ){
                when (position){
                    0 -> viewModel.sortByAscendingTime()
                    1 -> viewModel.sortByDescendingTime()
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>){
                // Do nothing
                }
            }
        viewModel.statusList.observe(viewLifecycleOwner) { videos ->
            videoAdapter.updateVideos(videos)
            }
        }


    override fun onResume() {
        super.onResume()
        loadVideos()
    }

    private fun loadVideos(){
        statusList.clear()
        val result = readDataFromPrefs()
        if (result) {
            val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
            val uriPath = sh.getString("PATH", "")

            uriPath?.let {
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    viewModel.loadVideos(it)
                    val fileDoc = DocumentFile.fromTreeUri(requireContext(), Uri.parse(it))
                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name!!.endsWith(".mp4") || file.name!!.endsWith(".mkv") || file.name!!.endsWith(
                                ".avi"
                            )
                        ) {
                            val modelVideoUri = ModelVideoUri(
                                file.uri,
                                file.lastModified())
                            statusList.add(modelVideoUri)
                        }
                    }
                    setUpRecyclerView(statusList)
                } catch (e: Exception) {
                    Log.e(TAG, "Error accessing URI : $uriPath", e)
                    getFolderPermission()
                }
            } ?: run {
                getFolderPermission()
            }
        } else {
            getFolderPermission()
        }
    }

//    private fun sortByAscendingTime(){
//        statusList.sortBy {it.timeStamp}
//        videoAdapter.notifyDataSetChanged()
//    }
//    private fun sortByDescendingTime(){
//        statusList.sortByDescending {it.timeStamp}
//        videoAdapter.notifyDataSetChanged()
//    }

    private fun getFolderPermission() {
        handleBackPress()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val storageManager =
                requireActivity().getSystemService(Context.STORAGE_SERVICE) as StorageManager
            val intent = storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
            val targetDirectory = Constants.SIMPLE_WHATSAPP_PATH
            var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
            var scheme = uri.toString()
            scheme = scheme.replace("/root", "/document")
            scheme += "%3A$targetDirectory"
            uri = Uri.parse(scheme)
            intent.putExtra("android.provider.extra.INITIAL_URI", uri)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            startActivityForResult(intent, 1234)
        } else {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            intent.putExtra("android.content.extra.SHOW_ADVANCED", true)
            startActivityForResult(intent, 1234)
        }
    }
    private fun handleBackPress(){
        Log.d(TAG, "handleBackPress: Adding callback")
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            Log.d(TAG, "onBackPressedDispatcher: Back pressed detected")
            Toast.makeText(requireContext(),"Folder Permission Cancelled ",Toast.LENGTH_SHORT).show()
            requireActivity().finishAffinity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1234 && resultCode == Activity.RESULT_OK) {
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
            }
        }
    }

    private fun readDataFromPrefs(): Boolean {
        val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
        val uriPath = sh.getString("PATH", "")
        return !uriPath.isNullOrEmpty()
    }

    private fun setUpRecyclerView(videosList: List<ModelVideoUri>) {

        videoAdapter = VideoAdapter(
            videosList,
            { videoUri -> fabClicked(videoUri) },
            { videoUri -> listItemClicked(videoUri) }
        )
        binding.recyclerView2.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView2.adapter = videoAdapter
    }

    private fun listItemClicked(status: ModelVideoUri) {

        val intent = Intent(this@VideosFragment.requireContext(), VideoViewActivity::class.java)
        intent.putExtra("videoUri", status.videoUri)
        startActivity(intent)
    }

    private fun fabClicked(status: ModelVideoUri) {
        val dialog = android.app.Dialog(this@VideosFragment.requireContext())
        dialog.setContentView(R.layout.custom_dialog)
        dialog.show()
        val downloadButton = dialog.findViewById<Button>(R.id.downloadButton)
        downloadButton.setOnClickListener {
            dialog.dismiss()
            viewModel.saveFile(
                activity=requireActivity(),
                status = status,
                onSuccess = {
                    Toast.makeText(requireContext(), "Video Saved", Toast.LENGTH_SHORT).show()
                    showDownloadNotification("${System.currentTimeMillis()}.mp4")
                },
                onFailure = {
                    Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
                })
        }
    }

//    private fun saveFile(status: VideoUri) {
//        val inputStream = requireActivity().contentResolver.openInputStream(status.videoUri)
//        val fileName = "${System.currentTimeMillis()}.mp4"
//        try {
//            val values = ContentValues()
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "videos/mp4")
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "videos/x-matroska")
//            values.put(MediaStore.MediaColumns.MIME_TYPE, "videos/avi")
//            values.put(
//                MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS +
//                        "/Whatsapp Statuses/"
//            )
//            val uri = requireActivity().contentResolver.insert(
//                MediaStore.Files.getContentUri("external"), values
//            )
//            val outputStream: OutputStream = uri?.let {
//                requireActivity().contentResolver.openOutputStream(it)
//            }!!
//            if (inputStream != null) {
//                outputStream.write(inputStream.readBytes())
//            }
//            outputStream.close()
//            Toast.makeText(requireContext(), "Video Saved", Toast.LENGTH_SHORT).show()
//
//            showDownloadNotification(fileName)
//        } catch (e: IOException) {
//            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
//        }
//    }

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
            .setSmallIcon(R.drawable.notifications)
            .setContentTitle("Download Completed")
            .setContentText("Video $fileName has been downloaded.")
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
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showDownloadNotification("Video")
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
