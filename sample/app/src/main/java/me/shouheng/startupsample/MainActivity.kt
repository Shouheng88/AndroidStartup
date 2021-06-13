package me.shouheng.startupsample

import android.content.Intent
import android.os.Bundle
import me.shouheng.startupsample.databinding.ActivityMainBinding
import me.shouheng.utils.ktx.onDebouncedClick
import me.shouheng.vmlib.base.ViewBindingActivity
import me.shouheng.vmlib.comn.EmptyViewModel

class MainActivity : ViewBindingActivity<EmptyViewModel, ActivityMainBinding>() {

    override fun doCreateView(savedInstanceState: Bundle?) {
        setMenu(R.menu.menu_main) {
            when (it.itemId) {
                R.id.action_settings -> { }
                else -> super.onOptionsItemSelected(it)
            }
        }
        // Start remote service.
        binding.btnStartService.onDebouncedClick {
            startService(Intent(this, RemoteService::class.java))
        }
    }
}
