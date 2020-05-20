package com.example.challange

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.challange.features.postcode.PostCodeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var postCodeFragment: PostCodeFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)


        val cached = supportFragmentManager.findFragmentByTag(PostCodeFragment.TAG)

        postCodeFragment = if (cached == null) {
            PostCodeFragment()
        } else {
            cached as PostCodeFragment
        }

        supportFragmentManager.beginTransaction()
            .run {
                replace(R.id.fragment_container, postCodeFragment, PostCodeFragment.TAG)
                commit()
            }
    }
}
