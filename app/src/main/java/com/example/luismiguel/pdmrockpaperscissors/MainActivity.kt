package com.example.luismiguel.pdmrockpaperscissors

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import app.akexorcist.bluetotohspp.library.DeviceList
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import app.akexorcist.bluetotohspp.library.BluetoothState
import java.io.IOException
import java.util.*
import android.content.BroadcastReceiver
import android.content.Context
import java.nio.charset.Charset
import android.content.IntentFilter
import android.support.v4.content.LocalBroadcastManager
import android.util.Log


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private val TAG = "MainActivity"

    private val REQUEST_ENABLE_BLUETOOTH = 1
    private val REQUEST_CONNECT_DEVICE = 2

    private var connectionActive = false

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetoothDevice : BluetoothDevice

    private var hisMAC = ""
    private var MY_UUID_INSECURE = UUID.fromString("8d3e8f01-3ae9-46d0-97d4-5836237a90ef")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonSinglePlayer.setOnClickListener(this)
        buttonMultiPlayer.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.buttonSinglePlayer -> callSingleActivity()
            R.id.buttonMultiPlayer -> multiPlayerClick()
        }
    }

    private fun callSingleActivity() {
        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", true)
        startActivity(it)
    }

    private fun multiPlayerClick() {
        val askBluetoothAlert = AlertDialog.Builder(this@MainActivity).create()
        askBluetoothAlert.setTitle("Selecionar Modo")
        askBluetoothAlert.setMessage("Deseja jogar local ou via bluetooth?")

        askBluetoothAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Bluetooth", {
            dialogInterface, i -> callBluetoothMultiActivity()
        })

        askBluetoothAlert.setButton(AlertDialog.BUTTON_POSITIVE, "Local", {
            dialogInterface, i -> callLocalMultiActivity()
        })

        askBluetoothAlert.show()


    }

    private fun callLocalMultiActivity() {
        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", false)
        startActivity(it)
    }

    private fun callBluetoothMultiActivity() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        if (mBluetoothAdapter == null){
            Toast.makeText(this, "not ROCK BABY! :(", Toast.LENGTH_SHORT).show()
            return
        }
        else if (!mBluetoothAdapter.isEnabled){
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BLUETOOTH)
        }
        else {
            requestConnectDevice()
        }
    }

    private fun requestConnectDevice() {
        val intent = Intent(applicationContext, DeviceList::class.java)
        startActivityForResult(intent, REQUEST_CONNECT_DEVICE)
    }

    private fun startConnection() {
        startBTConnection(mBluetoothDevice, MY_UUID_INSECURE)
    }

    private fun startBTConnection(device: BluetoothDevice, uuid: UUID) {
        BluetoothManagement.mBluetoothConnection.startClient(device, uuid)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode){
            REQUEST_ENABLE_BLUETOOTH -> {
                if (resultCode == Activity.RESULT_OK){
                    Toast.makeText(this, "Bluetooth ativado", Toast.LENGTH_SHORT).show()
                    if (!connectionActive){
                        requestConnectDevice()
                    }
                } else {
                    Toast.makeText(this, "Bluetooth não foi ativado", Toast.LENGTH_SHORT).show()
                    return
                }
            }

            REQUEST_CONNECT_DEVICE -> {
                if (resultCode == Activity.RESULT_OK){
                    hisMAC = data?.extras?.getString(BluetoothState.EXTRA_DEVICE_ADDRESS)?: ""
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(hisMAC)
                    Log.d(TAG, "Trying to pair with " + mBluetoothDevice.name)
                    mBluetoothDevice.createBond()
                    BluetoothManagement.mBluetoothConnection = BluetoothConnectionService(this)

                    val filter = IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
                    registerReceiver(mBroadcastReceiver4, filter)
                    LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, IntentFilter("incomingMessageIntent"))

                    askServerOrClientBluetooth()
                } else {
                    Toast.makeText(this, "Falha ao conectar", Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }

    }

    private fun askServerOrClientBluetooth(){
        val ask = AlertDialog.Builder(this@MainActivity).create()
        ask.setTitle("Servidor ou Cliente")
        ask.setMessage("Selecione se você está sendo o servidor ou se conectando como cliente:")

        ask.setButton(AlertDialog.BUTTON_NEGATIVE, "Cliente", {
            dialogInterface, i -> connectToProvider()
        })

        ask.setButton(AlertDialog.BUTTON_POSITIVE, "Servidor", {
            dialogInterface, i -> connectAsProvider()
        })

        ask.show()
    }

    private fun connectToProvider() {
        startConnection()

        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", false)
        it.putExtra("isBluetooth", true)
        it.putExtra("isProvider", false)
        startActivity(it)
    }

    private fun connectAsProvider() {
        BluetoothManagement.mBluetoothConnection.start()

        val it = Intent(this, GameActivity::class.java)
        it.putExtra("singlePlayer", false)
        it.putExtra("isBluetooth", true)
        it.putExtra("isProvider", true)
        startActivity(it)
    }

    private val mReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var text = intent.getStringExtra("theMessage")
            //Toast.makeText(context, text, Toast.LENGTH_LONG).show()
        }
    }

    private val mBroadcastReceiver4 = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            if (action == BluetoothDevice.ACTION_BOND_STATE_CHANGED) {
                val mDevice = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                //3 cases:
                //case1: bonded already
                if (mDevice.bondState == BluetoothDevice.BOND_BONDED) {
                    Toast.makeText(context, "BroadcastReceiver: BOND_BONDED.", Toast.LENGTH_LONG).show()
                    //inside BroadcastReceiver4
                    mBluetoothDevice = mDevice
                }
                //case2: creating a bone
                if (mDevice.bondState == BluetoothDevice.BOND_BONDING) {
                    Toast.makeText(context, "BroadcastReceiver: BOND_BONDING.", Toast.LENGTH_LONG).show()
                }
                //case3: breaking a bond
                if (mDevice.bondState == BluetoothDevice.BOND_NONE) {
                    Toast.makeText(context, "BroadcastReceiver: BOND_NONE.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }






}
