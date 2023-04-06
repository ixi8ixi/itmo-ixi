package com.dinia.message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.FragmentManager

interface ImagePathClickListener {
    fun onChatClick(chat: String, root: View)
}

class FragmentActivity : AppCompatActivity(), ImagePathClickListener {
    private var fragmentExtraContainer: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_fragment)

        fragmentExtraContainer = findViewById(R.id.fragment_extra_container)

        val upEnabled = fragmentExtraContainer == null
        supportActionBar?.setDisplayShowHomeEnabled(upEnabled)
        supportActionBar?.setDisplayHomeAsUpEnabled(upEnabled)

        if (savedInstanceState == null) { // First launch
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, ChatListFragment(), TAG_LIST)
                .commit()
        } else if (fragmentExtraContainer != null) { // Portrait -> Landscape
            val imageFragment = supportFragmentManager.findFragmentByTag(TAG_FULLSCREEN)

            if (imageFragment != null) {
                supportFragmentManager.popBackStack(
                    FULLSCREEN_BACK_STACK,
                    FragmentManager.POP_BACK_STACK_INCLUSIVE
                )
                supportFragmentManager.executePendingTransactions()

                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_extra_container, imageFragment, TAG_FULLSCREEN)
                    .commit()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (supportFragmentManager.backStackEntryCount > 0) {
                    supportFragmentManager.popBackStack()
                } else {
                    finish()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onChatClick(chat: String, root: View) {
        val imageFragment = MessagesFragment.create(chat)

        if (fragmentExtraContainer != null) {
            imageFragment.enterTransition = Fade().apply { duration = 500L }
            imageFragment.exitTransition = Fade().apply { duration = 500L }

            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_extra_container,
                    imageFragment,
                    TAG_FULLSCREEN
                )
                .commit()
        } else {
            supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.fragment_container,
                    imageFragment,
                    TAG_FULLSCREEN
                )
                .addToBackStack(FULLSCREEN_BACK_STACK)
                .commit()
        }
    }

    companion object {
        private const val TAG_LIST = "list"
        private const val TAG_FULLSCREEN = "fullscreen"
        private const val FULLSCREEN_BACK_STACK = "fullscreen_back_stack"
    }
}