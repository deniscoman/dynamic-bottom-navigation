package com.comandenis.example

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val menuItems = listOf(
        MenuItem(
            label = "Home",
            image = "https://i.ibb.co/B4nR76t/home-24px.png",
            destinationId = R.id.home_dest
        ),
        MenuItem(
            label = "Chat",
            image = "https://i.ibb.co/6gG1M71/message-24px.png",
            destinationId = R.id.chat_dest
        ),
        MenuItem(
            label = "Profile",
            image = "https://i.ibb.co/FHP56wG/perm-identity-24px.png",
            destinationId = R.id.profile_dest
        )
    )

    private val subscriptions = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setMenuItems()
    }

    private fun setMenuItems() {
        data class Tuple(val menuItem: MenuItem, val bitmap: Bitmap)

        val picasso = Picasso.get()

        menuItems.forEachIndexed { index, menuItem ->
            nav_view.menu.add(Menu.NONE, menuItem.destinationId, index, menuItem.label)
        }

        subscriptions.add(
            Observable.fromIterable(menuItems)
                .switchMap {
                    Observable.just(
                        Tuple(it, picasso.load(it.image).get())
                    )
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        val menuItem = nav_view.menu.findItem(it.menuItem.destinationId)
                        menuItem.icon = BitmapDrawable(resources, it.bitmap)
                    },
                    {
                        // Handle errors here
                    },
                    {
                        // On complete we should setup nav controller
                        val navController = findNavController(R.id.nav_host_fragment)

                        nav_view.setupWithNavController(navController)
                    }
                )
        )
    }
}