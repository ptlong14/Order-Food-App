package com.longpt.projectll1.presentation.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.longpt.projectll1.R
import com.longpt.projectll1.databinding.ActivityMainBinding
import com.longpt.projectll1.presentation.ui.UserFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.bottomNav.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.botHome->{
                    loadFragment(HomeFragment())
                    true
                }

                R.id.botFoodMenu->{
                    loadFragment(MenuFoodFragment())
                    true
                }
                R.id.botFavorite->{
                    loadFragment(FavoriteFragment())
                    true
                }
                R.id.botMyOrder->{
                    loadFragment(OrderHistoryContainerFragment())
                    true
                }
                R.id.userSetting->{
                    loadFragment(UserFragment())
                    true
                }
                else -> false
            }
        }
        loadFragment(HomeFragment())
    }
    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(R.id.frameContainer, fragment)
            .commit()
    }
}