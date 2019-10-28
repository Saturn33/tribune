package ru.netology.saturn33.kt1.diploma.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import ru.netology.saturn33.kt1.diploma.R
import ru.netology.saturn33.kt1.diploma.dto.AttachmentDto
import ru.netology.saturn33.kt1.diploma.model.UserBadge
import ru.netology.saturn33.kt1.diploma.repositories.Repository
import java.io.IOException

class ProfileActivity : AppCompatActivity(), CoroutineScope by MainScope() {
    private val REQUEST_IMAGE_CAPTURE = 1
    private var attachmentModel: AttachmentDto? = null
    private var job: Job? = null
    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.profile_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_done -> {
                doSaveProfile()
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

        dialog = AlertDialog.Builder(this@ProfileActivity)
            .setCancelable(false)
            .setView(R.layout.progressbar).create()

        job = launch {
            dialog.show()
            try {
                val result = Repository.getProfile()
                if (result.isSuccessful) {
                    result.body()?.let { profileInfo ->
                        username.text = profileInfo.username
                        readOnly.isVisible = profileInfo.isReadOnly
                        if (profileInfo.badge != null) {
                            badge.visibility = View.VISIBLE
                            when (profileInfo.badge) {
                                UserBadge.PROMOTER -> {
                                    badge.setTextColor(
                                        resources.getColor(
                                            R.color.badge_promoter,
                                            null
                                        )
                                    )
                                    badge.text = getString(R.string.badge_promoter)
                                }
                                UserBadge.HATER -> {
                                    badge.setTextColor(
                                        resources.getColor(
                                            R.color.badge_hater,
                                            null
                                        )
                                    )
                                    badge.text = getString(R.string.badge_hater)
                                }
                            }
                        } else {
                            badge.visibility = View.GONE
                        }

                        attachmentModel = profileInfo.avatar
                        redrawImage()
                    }
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@ProfileActivity,
                    getString(R.string.profile_load_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
            dialog.dismiss()
        }

        avatarImg.setOnClickListener {
            dispatchTakePictureIntent()
        }

        removeImg.setOnClickListener {
            attachmentModel = null
            redrawImage()
        }
    }

    private fun doSaveProfile() {
        job = launch {
            dialog.show()
            try {
                val result = Repository.saveProfile(attachmentModel)
                if (result.isSuccessful) {
                    Toast.makeText(
                        this@ProfileActivity,
                        getString(R.string.profile_saved),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    Toast.makeText(
                        this@ProfileActivity,
                        getString(R.string.profile_save_error),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: IOException) {
                Toast.makeText(
                    this@ProfileActivity,
                    getString(R.string.profile_save_error),
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                dialog.dismiss()
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
                                        this@ProfileActivity,
                                        getString(R.string.profile_avatar_upload_error),
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            } catch (e: IOException) {
                                Toast.makeText(
                                    this@ProfileActivity,
                                    getString(R.string.profile_avatar_upload_error),
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
            Glide.with(this@ProfileActivity)
                .load(attachmentModel?.url)
                .centerInside()
                .into(avatarImg)
        } else {
            avatarImg.setImageResource(R.drawable.ic_person)
        }

    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
        job?.cancel()
    }
}
