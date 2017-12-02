package com.example.luismiguel.pdmrockpaperscissors

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var buttonSinglePlayer: Button
    lateinit var buttonMultiPlayer: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSinglePlayer = findViewById(R.id.buttonSinglePlayer)
        buttonSinglePlayer.setOnClickListener(this)

        buttonMultiPlayer = findViewById(R.id.buttonMultiPlayer)
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
