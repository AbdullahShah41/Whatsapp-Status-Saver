package com.example.whatsappstatussaver.Fragments

import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import com.example.whatsappstatussaver.Adapters.VideoAdapter
import com.example.whatsappstatussaver.Data.VideoUri
import com.example.whatsappstatussaver.databinding.FragmentVideosBinding

class VideosFragment : Fragment() {

    private lateinit var binding: FragmentVideosBinding
    private lateinit var statusList: ArrayList<VideoUri>

    companion object {
        const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVideosBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AppCompatActivity)?.supportActionBar?.title = "Whatsapp Status Saver"
        statusList = ArrayList()

        val result = readDataFromPrefs()
        if (result) {
            val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
            val uriPath = sh.getString("PATH", "")

            uriPath?.let {
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        Uri.parse(it), Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                    val fileDoc = DocumentFile.fromTreeUri(requireContext(), Uri.parse(it))
                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name!!.endsWith(".mp4") || file.name!!.endsWith(".mkv") || file.name!!.endsWith(".avi")) {
                            val videoUri = VideoUri(file.uri)
                            statusList.add(videoUri)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && REQUEST_ACTION_OPEN_DOCUMENT_TREE) {
            val treeUri = data?.data

            val sharedPreferences =
                requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
            sharedPreferences.edit().putString("PATH", treeUri.toString()).apply()

            treeUri?.let {
                try {
                    requireActivity().contentResolver.takePersistableUriPermission(
                        it,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                    val fileDoc = DocumentFile.fromTreeUri(requireContext(), it)
                    val statusList = mutableListOf<VideoUri>()

                    fileDoc?.listFiles()?.forEach { file ->
                        if (file.name!!.endsWith(".mp4") || file.name!!.endsWith(".mkv") || file.name!!.endsWith(".avi")) {
                            val dataClass = VideoUri(file.uri)
                            statusList.add(dataClass)
                        }
                    }
                    setUpRecyclerView(statusList)
                } catch (e: Exception) {
                    Log.e(TAG, "Error Accessing URI: $treeUri", e)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getFolderPermission() {
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
        startActivityForResult(intent, 1234)
    }

    fun readDataFromPrefs(): Boolean {
        val sh = requireActivity().getSharedPreferences("DATA_PATH", Context.MODE_PRIVATE)
        val uriPath = sh.getString("PATH", "")
        return !uriPath.isNullOrEmpty()
    }

    private fun setUpRecyclerView(videosList: List<VideoUri>) {
        binding.recyclerView2.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView2.adapter = VideoAdapter(videosList)
    }
}
