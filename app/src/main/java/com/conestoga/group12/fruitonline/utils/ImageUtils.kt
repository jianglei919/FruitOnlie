package com.conestoga.group12.fruitonline.utils

import android.content.Context
import android.net.Uri
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.conestoga.group12.fruitonline.R
import com.conestoga.group12.fruitonline.constant.CommonConstant
import com.google.firebase.storage.FirebaseStorage

object ImageUtils {
    private val STORAGE = FirebaseStorage.getInstance()

    fun getThumbnailImageResourceByName(name: String?): Int {
        var resourceId = R.drawable.placeholder_image
        when (name) {
            "Apple" -> resourceId = R.drawable.product_apple_thumbnail
            "Banana" -> resourceId = R.drawable.product_banana_thumbnail
            "Grape" -> resourceId = R.drawable.product_grape_thumbnail
            "Orange" -> resourceId = R.drawable.product_orange_thumbnail
            "Peach" -> resourceId = R.drawable.product_peach_thumbnail
            "Pear" -> resourceId = R.drawable.product_pear_thumbnail
            "Pineapple" -> resourceId = R.drawable.product_pineapple_thumbnail
            "Watermelon" -> resourceId = R.drawable.product_watermelon_thumbnail
            else -> {}
        }
        return resourceId
    }

    fun getDetailImageResourceByName(name: String?): Int {
        var resourceId = R.drawable.placeholder_image
        when (name) {
            "Apple" -> resourceId = R.drawable.product_apple_detail
            "Banana" -> resourceId = R.drawable.product_banana_detail
            "Grape" -> resourceId = R.drawable.product_grape_detail
            "Orange" -> resourceId = R.drawable.product_orange_detail
            "Peach" -> resourceId = R.drawable.product_peach_detail
            "Pear" -> resourceId = R.drawable.product_pear_detail
            "Pineapple" -> resourceId = R.drawable.product_pineapple_detail
            "Watermelon" -> resourceId = R.drawable.product_watermelon_detail
            else -> {}
        }
        return resourceId
    }

    fun getImageResource(productName: String?, type: String?): Int {
        when (type) {
            CommonConstant.IMAGE_DETAIL_TYPE -> return getDetailImageResourceByName(productName)
            CommonConstant.IMAGE_THUMBNAIL_TYPE -> return getThumbnailImageResourceByName(
                productName
            )
        }
        return R.drawable.placeholder_image
    }

    /**
     * load image from firebase storage
     *
     * @param context
     * @param imageView
     * @param imageUri  - Storage uri: gs://ecommerceapplication-ba926.firebasestorage.app/product_pear_thumbnail.jpg
     * @param name
     * @param type
     */
    fun loadImageFromStorage(
        context: Context?,
        imageView: ImageView?,
        imageUri: String?,
        name: String?,
        type: String?
    ) {
        val storageReference = STORAGE.getReferenceFromUrl(imageUri!!)
        storageReference.downloadUrl.addOnSuccessListener { uri: Uri? ->
            // Use Glide loading image
            Glide.with(context!!)
                .load(uri) //                    .apply(RequestOptions.bitmapTransform(new RoundedCorners(8)))
                .placeholder(R.drawable.placeholder_image) // loading placeholder image
                .error(getImageResource(name, type))
                .into(imageView!!)
        }.addOnFailureListener { exception: Exception ->
            // process error
            Toast.makeText(
                context,
                "Failed to load image: " + exception.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
