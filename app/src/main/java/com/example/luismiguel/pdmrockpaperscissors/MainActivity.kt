package com.example.luismiguel.pdmrockpaperscissors

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSinglePlayer.setOnClickListener(this)
        buttonMultiPlayer.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.buttonSinglePlayer -> callSingleActivity()
            R.id.buttonMultiPlayer -> callMultiActivity()
        }
    }

    private fun callSingleActivity() {
        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", true)
        startActivity(it)
    }

    private fun callMultiActivity() {
        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", false)
        startActivity(it)
    }


}
