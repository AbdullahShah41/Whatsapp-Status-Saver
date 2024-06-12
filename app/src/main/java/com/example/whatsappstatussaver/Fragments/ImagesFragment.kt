package com.example.whatsappstatussaver.Fragments

import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.storage.StorageManager
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.application
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.documentfile.provider.DocumentFile
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whatsappstatussaver.Adapters.ImageAdapter
import com.example.whatsappstatussaver.Data.ImageUri
import com.example.whatsappstatussaver.R
import com.example.whatsappstatussaver.databinding.FragmentImagesBinding
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.util.Arrays

class ImagesFragment : Fragment() {

    private lateinit var binding: FragmentImagesBinding
    private lateinit var statusList: ArrayList<ImageUri>
    private lateinit var imageAdapter: ImageAdapter

    companion object {
        const val REQUEST_ACTION_OPEN_DOCUMENT_TREE = true
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagesBinding.inflate(inflater, container, false)
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
                        if (file.name!!.endsWith(".jpg") || file.name!!.endsWith(".png") || file.name!!.endsWith(".jpeg")) {
                            val imageUri = ImageUri(file.uri)
                            statusList.add(imageUri)
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
                    val statusList = mutableListOf<ImageUri>()

                    fileDoc?.listFiles()?.forEach { file ->
                        if (!file.name!!.endsWith(".nomedia")) {
                            val dataClass = ImageUri(file.uri)
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


    private fun setUpRecyclerView(imagesList: List<ImageUri>) {
        imageAdapter = ImageAdapter(imagesList) { selectedStatus: ImageUri ->
            listItemClicked(selectedStatus)
        }


        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 3)
        binding.recyclerView.adapter = imageAdapter


    }
    private fun listItemClicked(status:ImageUri){
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
        val inputStream=requireActivity().contentResolver.openInputStream(status.imageUri)
        val fileName = "${System.currentTimeMillis()}.jpg"
        try {
            val values=ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME,fileName)
            values.put(MediaStore.MediaColumns.MIME_TYPE,"images/jpg")
            values.put(MediaStore.MediaColumns.RELATIVE_PATH,Environment.DIRECTORY_DOCUMENTS+
            "/Images/")
            val uri = requireActivity().contentResolver.insert(MediaStore.Files.getContentUri("external"),values
            )
            val outputStream : OutputStream =uri?.let {
                requireActivity().contentResolver.openOutputStream(it)
            }!!
            if (inputStream!=null){
                outputStream.write(inputStream.readBytes())
            }
            outputStream.close()
            Toast.makeText(requireContext(), "Image Saved", Toast.LENGTH_SHORT).show()
        }
        catch (e: IOException){
            Toast.makeText(requireContext(), "Failed", Toast.LENGTH_SHORT).show()
        }
    }
}
