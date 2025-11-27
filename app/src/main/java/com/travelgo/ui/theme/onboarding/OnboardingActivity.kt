package com.travelgo.ui.theme.onboarding


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.travelgo.R
import com.google.android.material.button.MaterialButton
import com.travelgo.ui.theme.login.LoginActivity
class OnboardingActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_onboarding)

        viewPager = findViewById(R.id.viewPager)
        val btnNext: MaterialButton = findViewById(R.id.btnNext)

        val items = listOf(
            OnboardingItem(
                R.drawable.onboarding1,
                "Explora destinos",
                "Encuentra los mejores lugares turísticos del mundo."
            ),
            OnboardingItem(
                R.drawable.onboarding2,
                "Planifica tu viaje",
                "Organiza tus destinos favoritos y crea tu propia ruta."
            ),
            OnboardingItem(
                R.drawable.onboarding3,
                "Disfruta la experiencia",
                "Descubre y comparte tus aventuras."
            )
        )

        adapter = OnboardingAdapter(items)
        viewPager.adapter = adapter

        btnNext.setOnClickListener {
            if (viewPager.currentItem + 1 < items.size) {
                viewPager.currentItem += 1
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }

        }
    }
}