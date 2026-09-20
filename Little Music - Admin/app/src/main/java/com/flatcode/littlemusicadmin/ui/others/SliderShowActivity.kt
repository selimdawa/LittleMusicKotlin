package com.flatcode.littlemusicadmin.ui.others

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.utils.*
import com.flatcode.littlemusicadmin.databinding.ActivitySliderShowBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.text.MessageFormat
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class SliderShowActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySliderShowBinding
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var IMAGE_NUMBER = 0
    private var item = 0

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            uploadImage(DATA.EMPTY + IMAGE_NUMBER)
        } else {
            val error = result.error
            Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startSliderCrop(num: Int) {
        IMAGE_NUMBER = num
        cropImage.launch(
            CropImageContractOptions(
                uri = null,
                cropImageOptions = CropImageOptions(
                    minCropResultWidth = DATA.MIX_SLIDER_X,
                    minCropResultHeight = DATA.MIX_SLIDER_Y,
                    aspectRatioX = 16,
                    aspectRatioY = 9,
                    fixAspectRatio = true,
                    cropShape = CropImageView.CropShape.OVAL,
                    guidelines = CropImageView.Guidelines.ON,
                    multiTouchEnabled = true
                )
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.slider_show)
        binding.toolbar.back.setOnClickListener { onBackPressed() }

        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)

        binding.addOne.setOnClickListener { startSliderCrop(1) }
        binding.addTwo.setOnClickListener { startSliderCrop(2) }
        binding.addThree.setOnClickListener { startSliderCrop(3) }
        binding.addFour.setOnClickListener { startSliderCrop(4) }
        binding.addFive.setOnClickListener { startSliderCrop(5) }
        binding.addSix.setOnClickListener { startSliderCrop(6) }
        binding.addSeven.setOnClickListener { startSliderCrop(7) }
        binding.addEight.setOnClickListener { startSliderCrop(8) }

        binding.addNine.setOnClickListener { startSliderCrop(9) }
        binding.addTeen.setOnClickListener { startSliderCrop(10) }
        binding.addEleven.setOnClickListener { startSliderCrop(11) }
        binding.addTwelfth.setOnClickListener { startSliderCrop(12) }
        binding.addThirteen.setOnClickListener { startSliderCrop(13) }
        binding.addFourteenth.setOnClickListener { startSliderCrop(14) }
        binding.addFifteenth.setOnClickListener { startSliderCrop(15) }
        binding.addSixteen.setOnClickListener { startSliderCrop(16) }
        binding.addSeventeen.setOnClickListener { startSliderCrop(17) }
        binding.addEighteen.setOnClickListener { startSliderCrop(18) }
        binding.addNineteen.setOnClickListener { startSliderCrop(19) }
        binding.addTwenty.setOnClickListener { startSliderCrop(20) }

        nrSliderShow
        sliderShow()
    }

    private val nrSliderShow: Unit
        get() {
            val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    item = dataSnapshot.childrenCount.toInt()
                    binding.toolbar.nameSpace.text =
                        MessageFormat.format("Slider Show ( {0} )", item)
                    if (item >= 0) {
                        binding.linearOne.visibility = View.VISIBLE
                    } else {
                        binding.linearOne.visibility = View.GONE
                    }
                    if (item >= 1) {
                        binding.linearTwo.visibility = View.VISIBLE
                    } else {
                        binding.linearTwo.visibility = View.GONE
                    }
                    if (item >= 2) {
                        binding.linearThree.visibility = View.VISIBLE
                    } else {
                        binding.linearThree.visibility = View.GONE
                    }
                    if (item >= 3) {
                        binding.linearFour.visibility = View.VISIBLE
                    } else {
                        binding.linearFour.visibility = View.GONE
                    }
                    if (item >= 4) {
                        binding.linearFive.visibility = View.VISIBLE
                    } else {
                        binding.linearFive.visibility = View.GONE
                    }
                    if (item >= 5) {
                        binding.linearSix.visibility = View.VISIBLE
                    } else {
                        binding.linearSix.visibility = View.GONE
                    }
                    if (item >= 6) {
                        binding.linearSeven.visibility = View.VISIBLE
                    } else {
                        binding.linearSeven.visibility = View.GONE
                    }
                    if (item >= 7) {
                        binding.linearEight.visibility = View.VISIBLE
                    } else {
                        binding.linearEight.visibility = View.GONE
                    }
                    if (item >= 8) {
                        binding.linearNine.visibility = View.VISIBLE
                    } else {
                        binding.linearNine.visibility = View.GONE
                    }
                    if (item >= 9) {
                        binding.linearTeen.visibility = View.VISIBLE
                    } else {
                        binding.linearTeen.visibility = View.GONE
                    }
                    if (item >= 10) {
                        binding.linearEleven.visibility = View.VISIBLE
                    } else {
                        binding.linearEleven.visibility = View.GONE
                    }
                    if (item >= 11) {
                        binding.linearTwelfth.visibility = View.VISIBLE
                    } else {
                        binding.linearTwelfth.visibility = View.GONE
                    }
                    if (item >= 12) {
                        binding.linearThirteen.visibility = View.VISIBLE
                    } else {
                        binding.linearThirteen.visibility = View.GONE
                    }
                    if (item >= 13) {
                        binding.linearFourteenth.visibility = View.VISIBLE
                    } else {
                        binding.linearFourteenth.visibility = View.GONE
                    }
                    if (item >= 14) {
                        binding.linearFifteenth.visibility = View.VISIBLE
                    } else {
                        binding.linearFifteenth.visibility = View.GONE
                    }
                    if (item >= 15) {
                        binding.linearSixteen.visibility = View.VISIBLE
                    } else {
                        binding.linearSixteen.visibility = View.GONE
                    }
                    if (item >= 16) {
                        binding.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 17) {
                        binding.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 18) {
                        binding.linearNineteen.visibility = View.VISIBLE
                    } else {
                        binding.linearNineteen.visibility = View.GONE
                    }
                    if (item >= 19) {
                        binding.linearTwenty.visibility = View.VISIBLE
                    } else {
                        binding.linearTwenty.visibility = View.GONE
                    }
                    binding.bar.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun sliderShow() {
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val one = DATA.EMPTY + dataSnapshot.child("1").value
                val two = DATA.EMPTY + dataSnapshot.child("2").value
                val three = DATA.EMPTY + dataSnapshot.child("3").value
                val four = DATA.EMPTY + dataSnapshot.child("4").value
                val five = DATA.EMPTY + dataSnapshot.child("5").value
                val six = DATA.EMPTY + dataSnapshot.child("6").value
                val seven = DATA.EMPTY + dataSnapshot.child("7").value
                val eight = DATA.EMPTY + dataSnapshot.child("8").value
                val nine = DATA.EMPTY + dataSnapshot.child("9").value
                val teen = DATA.EMPTY + dataSnapshot.child("10").value
                val eleven = DATA.EMPTY + dataSnapshot.child("11").value
                val twelfth = DATA.EMPTY + dataSnapshot.child("12").value
                val thirteen = DATA.EMPTY + dataSnapshot.child("13").value
                val fourteenth = DATA.EMPTY + dataSnapshot.child("14").value
                val fifteenth = DATA.EMPTY + dataSnapshot.child("15").value
                val sixteen = DATA.EMPTY + dataSnapshot.child("16").value
                val seventeen = DATA.EMPTY + dataSnapshot.child("17").value
                val eighteen = DATA.EMPTY + dataSnapshot.child("18").value
                val nineteen = DATA.EMPTY + dataSnapshot.child("19").value
                val twenty = DATA.EMPTY + dataSnapshot.child("20").value

                binding.imageOne.glide(false, one)
                binding.imageTwo.glide(false, two)
                binding.imageThree.glide(false, three)
                binding.imageFour.glide(false, four)
                binding.imageFive.glide(false, five)
                binding.imageSix.glide(false, six)
                binding.imageSeven.glide(false, seven)
                binding.imageEight.glide(false, eight)
                binding.imageNine.glide(false, nine)
                binding.imageTeen.glide(false, teen)
                binding.imageEleven.glide(false, eleven)
                binding.imageTwelfth.glide(false, twelfth)
                binding.imageThirteen.glide(false, thirteen)
                binding.imageFourteenth.glide(false, fourteenth)
                binding.imageFifteenth.glide(false, fifteenth)
                binding.imageSixteen.glide(false, sixteen)
                binding.imageSeventeen.glide(false, seventeen)
                binding.imageEighteen.glide(false, eighteen)
                binding.imageNineteen.glide(false, nineteen)
                binding.imageTwenty.glide(false, twenty)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun uploadImage(name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val filePathAndName = "Images/SliderShow/" + (DATA.EMPTY + name)

        try {
            MediaManager.get().upload(imageUri)
                .unsigned(DATA.CLOUDINARY_UPLOAD_PRESET)
                .option("public_id", filePathAndName)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}
                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedImageUrl = resultData["secure_url"]?.toString() ?: ""
                        updateImage(uploadedImageUrl, DATA.EMPTY + name)
                    }
                    override fun onError(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                        Toast.makeText(context, "Error! " + error?.description, Toast.LENGTH_SHORT).show()
                    }
                    override fun onReschedule(requestId: String, error: ErrorInfo?) {
                        dialog!!.dismiss()
                    }
                }).dispatch()
        } catch (e: Exception) {
            dialog!!.dismiss()
            Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateImage(imageUrl: String, name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val hashMap = HashMap<String, Any>()
        if (imageUri != null) {
            hashMap[DATA.EMPTY + name] = DATA.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
        reference.updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(context, "The photo has been posted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRestart() {
        nrSliderShow
        sliderShow()
        super.onRestart()
    }

    override fun onResume() {
        nrSliderShow
        sliderShow()
        super.onResume()
    }
}
