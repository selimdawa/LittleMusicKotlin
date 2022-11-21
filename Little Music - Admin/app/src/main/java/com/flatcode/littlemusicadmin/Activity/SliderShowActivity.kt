package com.flatcode.littlemusicadmin.Activity

import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemusicadmin.R
import com.flatcode.littlemusicadmin.Unit.DATAv
import com.flatcode.littlemusicadmin.Unit.THEME
import com.flatcode.littlemusicadmin.Unit.VOID
import com.flatcode.littlemusicadmin.databinding.ActivitySliderShowBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.theartofdev.edmodo.cropper.CropImage
import java.text.MessageFormat

class SliderShowActivity : AppCompatActivity() {

    private var binding: ActivitySliderShowBinding? = null
    private var activity: Activity? = null
    private val context: Context = also { activity = it }
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null
    private var IMAGE_NUMBER = 0
    private var item = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(context)
        super.onCreate(savedInstanceState)
        binding = ActivitySliderShowBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.slider_show)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        dialog = ProgressDialog(context)
        dialog!!.setTitle("Please wait...")
        dialog!!.setCanceledOnTouchOutside(false)
        binding!!.addOne.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 1
        }
        binding!!.addTwo.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 2
        }
        binding!!.addThree.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 3
        }
        binding!!.addFour.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 4
        }
        binding!!.addFive.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 5
        }
        binding!!.addSix.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 6
        }
        binding!!.addSeven.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 7
        }
        binding!!.addEight.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 8
        }
        binding!!.addNine.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 9
        }
        binding!!.addTeen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 10
        }
        binding!!.addEleven.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 11
        }
        binding!!.addTwelfth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 12
        }
        binding!!.addThirteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 13
        }
        binding!!.addFourteenth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 14
        }
        binding!!.addFifteenth.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 15
        }
        binding!!.addSixteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 16
        }
        binding!!.addSeventeen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 17
        }
        binding!!.addEighteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 18
        }
        binding!!.addNineteen.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 19
        }
        binding!!.addTwenty.setOnClickListener {
            VOID.CropImageSlider(activity)
            IMAGE_NUMBER = 20
        }
        nrSliderShow
        sliderShow()
    }

    private val nrSliderShow: Unit
        get() {
            val reference = FirebaseDatabase.getInstance().getReference(DATAv.SLIDER_SHOW)
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    item = dataSnapshot.childrenCount.toInt()
                    binding!!.toolbar.nameSpace.text =
                        MessageFormat.format("Slider Show ( {0} )", item)
                    if (item >= 0) {
                        binding!!.linearOne.visibility = View.VISIBLE
                    } else {
                        binding!!.linearOne.visibility = View.GONE
                    }
                    if (item >= 1) {
                        binding!!.linearTwo.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwo.visibility = View.GONE
                    }
                    if (item >= 2) {
                        binding!!.linearThree.visibility = View.VISIBLE
                    } else {
                        binding!!.linearThree.visibility = View.GONE
                    }
                    if (item >= 3) {
                        binding!!.linearFour.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFour.visibility = View.GONE
                    }
                    if (item >= 4) {
                        binding!!.linearFive.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFive.visibility = View.GONE
                    }
                    if (item >= 5) {
                        binding!!.linearSix.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSix.visibility = View.GONE
                    }
                    if (item >= 6) {
                        binding!!.linearSeven.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSeven.visibility = View.GONE
                    }
                    if (item >= 7) {
                        binding!!.linearEight.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEight.visibility = View.GONE
                    }
                    if (item >= 8) {
                        binding!!.linearNine.visibility = View.VISIBLE
                    } else {
                        binding!!.linearNine.visibility = View.GONE
                    }
                    if (item >= 9) {
                        binding!!.linearTeen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTeen.visibility = View.GONE
                    }
                    if (item >= 10) {
                        binding!!.linearEleven.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEleven.visibility = View.GONE
                    }
                    if (item >= 11) {
                        binding!!.linearTwelfth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwelfth.visibility = View.GONE
                    }
                    if (item >= 12) {
                        binding!!.linearThirteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearThirteen.visibility = View.GONE
                    }
                    if (item >= 13) {
                        binding!!.linearFourteenth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFourteenth.visibility = View.GONE
                    }
                    if (item >= 14) {
                        binding!!.linearFifteenth.visibility = View.VISIBLE
                    } else {
                        binding!!.linearFifteenth.visibility = View.GONE
                    }
                    if (item >= 15) {
                        binding!!.linearSixteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearSixteen.visibility = View.GONE
                    }
                    if (item >= 16) {
                        binding!!.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 17) {
                        binding!!.linearEighteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearEighteen.visibility = View.GONE
                    }
                    if (item >= 18) {
                        binding!!.linearNineteen.visibility = View.VISIBLE
                    } else {
                        binding!!.linearNineteen.visibility = View.GONE
                    }
                    if (item >= 19) {
                        binding!!.linearTwenty.visibility = View.VISIBLE
                    } else {
                        binding!!.linearTwenty.visibility = View.GONE
                    }
                    binding!!.bar.visibility = View.GONE
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    private fun sliderShow() {
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.SLIDER_SHOW)
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val one = DATAv.EMPTY + dataSnapshot.child("1").value
                val two = DATAv.EMPTY + dataSnapshot.child("2").value
                val three = DATAv.EMPTY + dataSnapshot.child("3").value
                val four = DATAv.EMPTY + dataSnapshot.child("4").value
                val five = DATAv.EMPTY + dataSnapshot.child("5").value
                val six = DATAv.EMPTY + dataSnapshot.child("6").value
                val seven = DATAv.EMPTY + dataSnapshot.child("7").value
                val eight = DATAv.EMPTY + dataSnapshot.child("8").value
                val nine = DATAv.EMPTY + dataSnapshot.child("9").value
                val teen = DATAv.EMPTY + dataSnapshot.child("10").value
                val eleven = DATAv.EMPTY + dataSnapshot.child("11").value
                val twelfth = DATAv.EMPTY + dataSnapshot.child("12").value
                val thirteen = DATAv.EMPTY + dataSnapshot.child("13").value
                val fourteenth = DATAv.EMPTY + dataSnapshot.child("14").value
                val fifteenth = DATAv.EMPTY + dataSnapshot.child("15").value
                val sixteen = DATAv.EMPTY + dataSnapshot.child("16").value
                val seventeen = DATAv.EMPTY + dataSnapshot.child("17").value
                val eighteen = DATAv.EMPTY + dataSnapshot.child("18").value
                val nineteen = DATAv.EMPTY + dataSnapshot.child("19").value
                val twenty = DATAv.EMPTY + dataSnapshot.child("20").value
                VOID.Glide(false, context, one, binding!!.imageOne)
                VOID.Glide(false, context, two, binding!!.imageTwo)
                VOID.Glide(false, context, three, binding!!.imageThree)
                VOID.Glide(false, context, four, binding!!.imageFour)
                VOID.Glide(false, context, five, binding!!.imageFive)
                VOID.Glide(false, context, six, binding!!.imageSix)
                VOID.Glide(false, context, seven, binding!!.imageSeven)
                VOID.Glide(false, context, eight, binding!!.imageEight)
                VOID.Glide(false, context, nine, binding!!.imageNine)
                VOID.Glide(false, context, teen, binding!!.imageTeen)
                VOID.Glide(false, context, eleven, binding!!.imageEleven)
                VOID.Glide(false, context, twelfth, binding!!.imageTwelfth)
                VOID.Glide(false, context, thirteen, binding!!.imageThirteen)
                VOID.Glide(false, context, fourteenth, binding!!.imageFourteenth)
                VOID.Glide(false, context, fifteenth, binding!!.imageFifteenth)
                VOID.Glide(false, context, sixteen, binding!!.imageSixteen)
                VOID.Glide(false, context, seventeen, binding!!.imageSeventeen)
                VOID.Glide(false, context, eighteen, binding!!.imageEighteen)
                VOID.Glide(false, context, nineteen, binding!!.imageNineteen)
                VOID.Glide(false, context, twenty, binding!!.imageTwenty)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun uploadImage(name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val filePathAndName = "Images/SliderShow/" + (DATAv.EMPTY + name)
        val reference = FirebaseStorage.getInstance()
            .getReference(filePathAndName + DATAv.DOT + VOID.getFileExtension(imageUri, context))
        reference.putFile(imageUri!!)
            .addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = DATAv.EMPTY + uriTask.result
                updateImage(uploadedImageUrl, DATAv.EMPTY + name)
            }.addOnFailureListener { e: Exception ->
                dialog!!.dismiss()
                Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateImage(imageUrl: String, name: String) {
        dialog!!.setMessage("Posting photo...")
        dialog!!.show()
        val hashMap = HashMap<String, Any>()
        if (imageUri != null) {
            hashMap[DATAv.EMPTY + name] = DATAv.EMPTY + imageUrl
        }
        val reference = FirebaseDatabase.getInstance().getReference(DATAv.SLIDER_SHOW)
        reference.updateChildren(hashMap).addOnSuccessListener {
            dialog!!.dismiss()
            Toast.makeText(context, "The photo has been posted", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e: Exception ->
            dialog!!.dismiss()
            Toast.makeText(context, "Error! " + e.message, Toast.LENGTH_SHORT).show()
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri = CropImage.getPickImageResultUri(context, data)
            if (CropImage.isReadExternalStoragePermissionsRequired(context, uri)) {
                imageUri = uri
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
            } else {
                VOID.CropImageSlider(activity)
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                imageUri = result.uri
                uploadImage(DATAv.EMPTY + IMAGE_NUMBER)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
                Toast.makeText(this, "Error! $error", Toast.LENGTH_SHORT).show()
            }
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