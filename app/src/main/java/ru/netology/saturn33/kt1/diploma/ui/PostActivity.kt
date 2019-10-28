package ru.netology.saturn33.kt1.diploma.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_post.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.AttachmentDto
import ru.netology.saturn33.kt1.diploma.helpers.Validator
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class PostActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val REQUEST_IMAGE_CAPTURE = 2
    private var attachmentModel: AttachmentDto? = null
    private var job: Job? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.post_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                doAddPost()
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStart() {
        super.onStart()

        dialog = AlertDialog.Builder(this@PostActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()

        postImg.setOnClickListener {
            dispatchTakePictureIntent()
        }

        removeImg.setOnClickListener {
            attachmentModel = null
            redrawImage()
        }
    }

    private fun doAddPost() {
        val textValidationResult = Validator.isPostTextValid(postText.text.toString())
        if (!textValidationResult.first) {
            postText.error = textValidationResult.second
            return
        }

        val link = if (postLink.text.toString().isEmpty()) null else postLink.text.toString()
        val linkValidationResult = Validator.isPostLinkValid(link)
        if (!linkValidationResult.first) {
            postLink.error = linkValidationResult.second
            return
        }

        if (attachmentModel == null) {
            Toast.makeText(this, "Прикладывание изображения обязательно", Toast.LENGTH_LONG).show()
            return
        } else {
            job = launch {
                dialog.show()
                try {
                    val result =
                        Repository.addPost(postText.text.toString(), link, attachmentModel!!)
                    if (result.isSuccessful) {
                        Toast.makeText(
                            this@PostActivity,
                            getString(R.string.post_added),
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@PostActivity,
                            getString(R.string.post_add_error),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } catch (e: IOException) {
                    Toast.makeText(
                        this@PostActivity,
                        getString(R.string.post_add_error),
                        Toast.LENGTH_LONG
                    ).show()
                } finally {
                    dialog.dismiss()
                }
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK)
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap?
                    imageBitmap?.let {
                        launch {
                            dialog.show()
                            try {
                                val imageUploadResult = Repository.upload(it)
                                if (imageUploadResult.isSuccessful) {
                                    attachmentModel = imageUploadResult.body()
                                    redrawImage()
                                } else {
                                    Toast.makeText(
                                        this@PostActivity,
                                        getString(R.string.post_image_upload_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this@PostActivity,
                                    getString(R.string.post_image_upload_error),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            dialog.dismiss()
                        }
                    }
                }
            }
    }

    private fun redrawImage() {
        if (attachmentModel != null) {
            Glide.with(this@PostActivity)
                .load(attachmentModel?.url)
                .centerInside()
                .into(postImg)
        } else {
            postImg.setImageResource(R.drawable.image_stub)
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
        job?.cancel()
    }
}
