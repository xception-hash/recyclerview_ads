package com.example.recyclerview_ads

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerview_ads.databinding.ActivityMainBinding
import com.google.android.gms.ads.*


class MainActivity : AppCompatActivity() {
    val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    private lateinit var adapter: AdsAdapter

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        MobileAds.initialize(this)
        setContentView(binding.root)
        adapter = AdsAdapter()
        binding.recyclerView.adapter = adapter
        refreshAd()

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                // Some code while the list is scrolling
                val lManager = recyclerView.layoutManager as LinearLayoutManager?
                val lastElementPosition = lManager!!.findLastVisibleItemPosition()
                val firstElementPosition = lManager.findFirstVisibleItemPosition()

                /** number of positions in recyclerview adapter we need to notify */
                adapter.lastElementPosition = lastElementPosition
                adapter.firstElementPosition = firstElementPosition

                super.onScrolled(recyclerView, dx, dy)
            }
        })
    }

    private fun refreshAd() {

        val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            /** below code is called only after adLoader.loadAd(AdRequest.Builder().build()) ad is loaded*/
            adapter.unifiedNativeAd = unifiedNativeAd

            /** after ad is ready refresh adapter since we dont want to refresh whole adapter only part
             *  that is visible on the screen */
            adapter.adsAddedItem()
        }


        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                val error =
                    """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
                Toast.makeText(
                    this@MainActivity, "Failed to load native ad with error $error",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}