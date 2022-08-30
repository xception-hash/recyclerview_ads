package com.example.recyclerview_ads

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview_ads.databinding.AdItemBinding
import com.example.recyclerview_ads.databinding.TextItemBinding
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView

class AdsAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val textList: List<Int> = (0..50).toList()
    var lastElementPosition: Int = -1
    var firstElementPosition: Int = -1

    var unifiedNativeAd: UnifiedNativeAd? = null

    inner class AdsViewHolder(private val binding: AdItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (unifiedNativeAd != null) {
                binding.adFrame.visibility = View.VISIBLE

                /** we need to inflate ad layout everytime */
                val adView = LayoutInflater.from(binding.root.context)
                    .inflate(R.layout.native_ad, null) as UnifiedNativeAdView

                populateUnifiedNativeAdView(unifiedNativeAd!!, adView)
                binding.adFrame.removeAllViews()
                binding.adFrame.addView(adView)
            } else {
                binding.adFrame.removeAllViews()
                binding.adFrame.visibility = View.GONE
            }
        }
    }

    inner class TextViewHolder(private val binding: TextItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(text: Int) {
            binding.textView.text = text.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return if (viewType == Type.Text.ordinal) {
            TextViewHolder(TextItemBinding.inflate(layoutInflater, parent, false))
        } else
            AdsViewHolder(AdItemBinding.inflate(layoutInflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == Type.Ad.ordinal) {
            (holder as AdsViewHolder).bind()
        } else {
            val realPosition = getRealPosition(position)
            (holder as TextViewHolder).bind(textList[realPosition])
        }
    }

    override fun getItemCount(): Int {
        return if (textList.size > AD_POSITION && textList.isNotEmpty())
            textList.size + textList.size / AD_POSITION
        else
            textList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position > 0 && position % AD_POSITION == 0) {
            Type.Ad.ordinal
        } else Type.Text.ordinal
    }

    private fun getRealPosition(position: Int): Int {
        return if (position > AD_POSITION - 1)
            position - (position / AD_POSITION)
        else
            position
    }

    /** itemToChangedPosition is a total number of item display on the screen*/
    fun adsAddedItem() {
        if (lastElementPosition != -1 && firstElementPosition != -1) {
            notifyItemRangeChanged(
                firstElementPosition,
                lastElementPosition - firstElementPosition + 1
            )
        }
        Log.d(TAG, "adsAddedItem: ${lastElementPosition - firstElementPosition}")
    }

    fun populateUnifiedNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Set the media view.
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline and media content are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        adView.mediaView.setMediaContent(nativeAd.mediaContent)

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            adView.bodyView.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            adView.callToActionView.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                nativeAd.icon.drawable
            )
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd)
    }

    enum class Type {
        Text,
        Ad
    }

    companion object {
        //you can change AD_POSITION to any number you want
        const val AD_POSITION = 7
        private const val TAG = "AdsAdapter"
    }
}