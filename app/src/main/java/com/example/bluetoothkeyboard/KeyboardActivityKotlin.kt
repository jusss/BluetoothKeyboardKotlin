package com.example.bluetoothkeyboard

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Button
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetoothkeyboard.HidDataSender.ProfileListener
import kotlinx.coroutines.*

class KeyboardActivityKotlin : AppCompatActivity() {
    val TAG = "BluetoothKeyboard"
    lateinit var TARGET_DEVICE_NAME: String
    var hidDataSender: HidDataSender? = null
    var keyboardHelper: KeyboardHelper? = null

//    private val profileListener : ProfileListener = object : ProfileListener {   will make the second ProfileListener not solved, weird! convert by AS
    private val profileListener = object : ProfileListener {
        @MainThread
        override fun onDeviceStateChanged(device: BluetoothDevice, state: Int) {
            // 0 = disconnected, 1 = connecting, 2 = connected
            Log.d(TAG, "device state changed to $state")
        }

        @MainThread
        override fun onAppUnregistered() {
            Log.v(TAG, "app unregistered")
            // unregister once app switch to background
            hidDataSender!!.unregister(this)
        }

        @MainThread
        override fun onServiceStateChanged(proxy: BluetoothProfile) {
            Log.v(TAG, "service state changed to$proxy")
        }
    }

    // press back button return home
    override fun onBackPressed() { moveTaskToBack(true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = intent
        val name = intent.getSerializableExtra("name") as ArrayList<String>?
        TARGET_DEVICE_NAME = name!![0]
        val screenSize = name[1]
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        if (screenSize == "1280x720") setContentView(R.layout.activity_keyboard_1280x720)
        if (screenSize == "2340x1080") setContentView(R.layout.activity_keyboard_2340x1080)
        if (screenSize == "1280x720_large") setContentView(R.layout.activity_keyboard_1280x720_large)
        hidDataSender = HidDataSender.getInstance()
        val hidDeviceProfile = hidDataSender!!.register(applicationContext, profileListener)
        keyboardHelper = KeyboardHelper(hidDataSender)

        val regularKey = arrayListOf<Int>(
            R.id.char_comma, R.id.char_z,R.id.char_x,R.id.char_c,R.id.char_v,
            R.id.char_b,R.id.char_n,R.id.char_m,R.id.char_period,R.id.char_a,
            R.id.char_s,R.id.char_d,R.id.char_f,R.id.char_g,R.id.char_h,R.id.char_j,
            R.id.char_k,R.id.char_l,R.id.char_q,R.id.char_w,R.id.char_e,R.id.char_r,
            R.id.char_t,R.id.char_y,R.id.char_u,R.id.char_i,R.id.char_o,R.id.char_p,
            R.id.char_1,R.id.char_2,R.id.char_3,R.id.char_4,R.id.char_5,R.id.char_6,
            R.id.char_7,R.id.char_8,R.id.char_9,R.id.char_0,R.id.question,R.id.backquote,
            R.id.char_slash,R.id.semicolon,R.id.single_quote,R.id.left_square,R.id.right_square,
            R.id.back_slach,R.id.hyphen,R.id.equal
        )
        val specialKey = arrayListOf<Int>(
                R.id.space,R.id.enter,
                R.id.left,R.id.right,R.id.down,R.id.up,R.id.tab,R.id.back,R.id.esc
        )

        val modifierKey = arrayListOf<Int>(
                R.id.ctrl,R.id.window,R.id.alt,R.id.shift
        )

        val regularView = regularKey.map { findViewById<Button>(it) }
        val specialView = specialKey.map { findViewById<Button>(it) }
        val modifierView = modifierKey.map { findViewById<Button>(it) }


            regularView.map {
                it.setOnTouchListener { view, motionEvent ->
                    val event = motionEvent.action
                    RepeatControl.mutableMap.put(view, true)
                    if (event == MotionEvent.ACTION_DOWN) {
                        CoroutineScope(Dispatchers.Main).launch {
                            while (RepeatControl.mutableMap.getValue(view)) {
                                sendChar(view)
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                delay(360)
                            }
                        }
                    }
                    if (event == MotionEvent.ACTION_UP) {
                        RepeatControl.mutableMap.put(view, false)
                    }
                    false
                }
            }

            specialView.map {
                it.setOnTouchListener { view, motionEvent ->
                    val event = motionEvent.action
                    RepeatControl.mutableMap.put(view, true)
                    if (event == MotionEvent.ACTION_DOWN) {
                        CoroutineScope(Dispatchers.Main).launch {
                            while (RepeatControl.mutableMap.getValue(view)) {
                                sendSpecialChar(view)
                                view.playSoundEffect(SoundEffectConstants.CLICK)
                                delay(120)
                            }
                        }
                    }
                    if (event == MotionEvent.ACTION_UP) {
                        RepeatControl.mutableMap.put(view, false)
                    }
                    false
                }
            }

            modifierView.map {
                it.setOnTouchListener { view, motionEvent ->
                    val event = motionEvent.action
                    RepeatControl.mutableMap.put(view, true)
                    if (event == MotionEvent.ACTION_DOWN) {
                        CoroutineScope(Dispatchers.Main).launch {
//                            while (RepeatControl.mutableMap.getValue(view)) {
//                                keyboardHelper.sendSingalModifierKey((view as Button).text.toString())
                                sendModifierChar(view)
                                view.playSoundEffect(SoundEffectConstants.CLICK)
//                                delay(90)
//                            }
                        }
                    }
                    if (event == MotionEvent.ACTION_UP) {
                        RepeatControl.mutableMap.put(view, false)
//                        keyboardHelper.sendKeysUp(KeyboardHelper.Modifier.NONE)
                    }
                    false
                }
            }
    }

    // send normal char like a,b,c
    fun sendChar(view: View) {
        val b = view as Button
        val c = b.text.toString()[0]
        if (hidDataSender!!.isConnected) {
            Log.d(TAG, "Sending message: $c")
            if (keyboardHelper!!.pressedModifier.isEmpty()) {
                keyboardHelper!!.sendChar(c)
            } else {
                keyboardHelper!!.sendModifierKey(c)
            }
        } else {
            for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) if (TARGET_DEVICE_NAME == device.name) {
                Log.d(TAG, "Requesting connection to " + device.name)
                // register again when app switch back from background
                hidDataSender!!.register(applicationContext, profileListener)
                hidDataSender!!.requestConnect(device)
            }
            //            {
//                hidDataSender.register(getApplicationContext(), profileListener);
//                hidDataSender.requestConnect(device);
//            }
        }
    }

    // send special char like enter, esc
    fun sendSpecialChar(view: View) {
        val b = view as Button
        val string = b.text.toString()
        if (hidDataSender!!.isConnected) {
            Log.d(TAG, "Sending message: $string")
            if (keyboardHelper != null) {
                if (keyboardHelper!!.pressedModifier.isEmpty()) {
                    keyboardHelper!!.sendSpecialKey(string)
                } else {
                    keyboardHelper!!.sendModifierKey(string)
                }
            }
        } else {
            for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) if (TARGET_DEVICE_NAME == device.name) {
                Log.d(TAG, "Requesting connection to " + device.name)
                // register again when app switch back from background
                hidDataSender!!.register(applicationContext, profileListener)
                hidDataSender!!.requestConnect(device)
            }
            //            {
//                hidDataSender.register(getApplicationContext(), profileListener);
//                hidDataSender.requestConnect(device);
//            }
        }
    }

    // send modifier like ctrl,shift and alt
    fun sendModifierChar(view: View) {
        val b = view as Button
        val string = b.text.toString()
        if (hidDataSender!!.isConnected) {
            Log.d(TAG, "Sending message: $string")
            if (keyboardHelper != null) keyboardHelper!!.pressedModifier.add(string)


        } else {
            for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) if (TARGET_DEVICE_NAME == device.name) {
                Log.d(TAG, "Requesting connection to " + device.name)
                // register again when app switch back from background
                hidDataSender!!.register(applicationContext, profileListener)
                hidDataSender!!.requestConnect(device)
            }
            //            {
//                hidDataSender.register(getApplicationContext(), profileListener);
//                hidDataSender.requestConnect(device);
//            }
        }
    }

    override fun onResume() {
        super.onResume()
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }
}

