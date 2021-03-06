package com.example.bluetoothkeyboard

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import com.example.bluetoothkeyboard.HidDataSender.ProfileListener
import com.example.bluetoothkeyboard.KeyboardHelper.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class KeyboardActivityKotlin : AppCompatActivity() {
    val TAG = "BluetoothKeyboard"
    lateinit var TARGET_DEVICE_NAME: String
    var hidDataSender: HidDataSender? = null
    var keyboardHelper: KeyboardHelper? = null
    var modifierKeyEventRepeatControl :Boolean = true
    var latestSentTime = System.currentTimeMillis()

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
        if (screenSize == "otg_keyboard") setContentView(R.layout.activity_otg_keyboard)

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

        val regularView = regularKey.map { findViewById<Button>(it) }.filter { it != null }
        val specialView = specialKey.map { findViewById<Button>(it) }.filter { it != null }
        val modifierView = modifierKey.map { findViewById<Button>(it) }.filter { it != null }
//        val searchView = findViewById<SearchView>(R.id.search)
        val editTextView = findViewById<EditText>(R.id.edit_text)

//        searchView.setIconifiedByDefault(true);
//        searchView.setQuery("", false)
//        searchView.setFocusable(false);

        // 1. editTextView!!.requestFocus() will pop KotlinNullPointerException if it's null, use requireNotNull or checkNotNull for debug message
        // 2. a?.b?:c if a is not null, run b, otherwise run c, elvis operator
        editTextView?.requestFocus()

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

//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
////                searchView.setFocusable(false);
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })

        val regularPhysicsKey = mutableMapOf<Int, Char>(
                KeyEvent.KEYCODE_COMMA to ',', KeyEvent.KEYCODE_Z to 'z', KeyEvent.KEYCODE_X to 'x', KeyEvent.KEYCODE_C to 'c', KeyEvent.KEYCODE_V to 'v',
                KeyEvent.KEYCODE_B to 'b', KeyEvent.KEYCODE_N to 'n', KeyEvent.KEYCODE_M to 'm', KeyEvent.KEYCODE_PERIOD to '.', KeyEvent.KEYCODE_A to 'a',
                KeyEvent.KEYCODE_S to 's', KeyEvent.KEYCODE_D to 'd', KeyEvent.KEYCODE_F to 'f', KeyEvent.KEYCODE_G to 'g', KeyEvent.KEYCODE_H to 'h', KeyEvent.KEYCODE_J to 'j',
                KeyEvent.KEYCODE_K to 'k', KeyEvent.KEYCODE_L to 'l', KeyEvent.KEYCODE_Q to 'q', KeyEvent.KEYCODE_W to 'w', KeyEvent.KEYCODE_E to 'e', KeyEvent.KEYCODE_R to 'r',
                KeyEvent.KEYCODE_T to 't', KeyEvent.KEYCODE_Y to 'y', KeyEvent.KEYCODE_U to 'u', KeyEvent.KEYCODE_I to 'i', KeyEvent.KEYCODE_O to 'o', KeyEvent.KEYCODE_P to 'p',
                KeyEvent.KEYCODE_1 to '1', KeyEvent.KEYCODE_2 to '2', KeyEvent.KEYCODE_3 to '3', KeyEvent.KEYCODE_4 to '4', KeyEvent.KEYCODE_5 to '5', KeyEvent.KEYCODE_6 to '6',
                KeyEvent.KEYCODE_7 to '7', KeyEvent.KEYCODE_8 to '8', KeyEvent.KEYCODE_9 to '9', KeyEvent.KEYCODE_0 to '0', KeyEvent.KEYCODE_GRAVE to '`',
                KeyEvent.KEYCODE_SLASH to '/', KeyEvent.KEYCODE_SEMICOLON to ';', KeyEvent.KEYCODE_APOSTROPHE to '\'', KeyEvent.KEYCODE_LEFT_BRACKET to '[', KeyEvent.KEYCODE_RIGHT_BRACKET to ']',
                KeyEvent.KEYCODE_BACKSLASH to '\\', KeyEvent.KEYCODE_MINUS to '-', KeyEvent.KEYCODE_EQUALS to '='
        )

        val specialPhysicsKey = mutableMapOf<Int, String>(
                KeyEvent.KEYCODE_SPACE to "Space", KeyEvent.KEYCODE_ENTER to "Enter", KeyEvent.KEYCODE_DEL to "Back",
                KeyEvent.KEYCODE_TAB to "Tab", KeyEvent.KEYCODE_ESCAPE to "Esc",
                KeyEvent.KEYCODE_DPAD_LEFT to "Left", KeyEvent.KEYCODE_DPAD_DOWN to "Down",
                KeyEvent.KEYCODE_DPAD_UP to "Up", KeyEvent.KEYCODE_DPAD_RIGHT to "Right",
                KeyEvent.KEYCODE_FORWARD_DEL to "Del", KeyEvent.KEYCODE_INSERT to "Ins",
                KeyEvent.KEYCODE_PAGE_DOWN to "PgDn", KeyEvent.KEYCODE_PAGE_UP to "PgUp",
                KeyEvent.KEYCODE_SYSRQ to "PRINTSCREEN", KeyEvent.KEYCODE_MENU to "MENU", KeyEvent.KEYCODE_SCROLL_LOCK to "SCROLLLOCK",
                KeyEvent.KEYCODE_BREAK to "PAUSE", KeyEvent.KEYCODE_MOVE_HOME to "HOME", KeyEvent.KEYCODE_MOVE_END to "END",
                KeyEvent.KEYCODE_F1 to "F1", KeyEvent.KEYCODE_F2 to "F2", KeyEvent.KEYCODE_F3 to "F3",
                KeyEvent.KEYCODE_F4 to "F4", KeyEvent.KEYCODE_F5 to "F5", KeyEvent.KEYCODE_F6 to "F6",
                KeyEvent.KEYCODE_F7 to "F7", KeyEvent.KEYCODE_F8 to "F8", KeyEvent.KEYCODE_F9 to "F9",
                KeyEvent.KEYCODE_F10 to "F10", KeyEvent.KEYCODE_F11 to "F11", KeyEvent.KEYCODE_F12 to "F12"
        )
        // https://elementalx.org/button-mapper/android-key-codes/

        editTextView?.setOnKeyListener { v: View?, keyCode: Int, event: KeyEvent? ->

//            if (event!!.isShiftPressed()){
//                if (regularPhysicsKey.containsKey(keyCode)) {
//                    if (modifierKeyEventRepeatControl) {
//                        // shift space, it will send twice key event, reduce one
//                        modifierKeyEventRepeatControl = modifierKeyEventRepeatControl.not()
//                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_SHIFT,
//                                keyMap.get(regularPhysicsKey.getOrDefault(keyCode, 'a')) ?: 0
////                                    ?: shiftKeyMap.getOrDefault(regularPhysicsKey.getOrDefault(keyCode,'a'),0)
//                        )
//                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
//                    }
//                    else {modifierKeyEventRepeatControl = modifierKeyEventRepeatControl.not()}
//                    return@setOnKeyListener true
//                }
//
//                if (specialPhysicsKey.containsKey(keyCode)){
//                    if (modifierKeyEventRepeatControl) {
//                        // shift space, it will send twice key event, reduce one
//                        modifierKeyEventRepeatControl = modifierKeyEventRepeatControl.not()
//                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_SHIFT,
//                                scancode.getOrDefault(specialPhysicsKey.get(keyCode), 0))
//                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
//                    }
//                    else {modifierKeyEventRepeatControl = modifierKeyEventRepeatControl.not()}
//                    return@setOnKeyListener true
//                }
////                event.getUnicodeChar()
//                return@setOnKeyListener true
//            }

            if (event!!.isShiftPressed()){
                if (regularPhysicsKey.containsKey(keyCode)) {
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        // shift space, it will send twice key event, reduce one
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_SHIFT,
                                keyMap.get(regularPhysicsKey.getOrDefault(keyCode, 'a')) ?: 0
//                                    ?: shiftKeyMap.getOrDefault(regularPhysicsKey.getOrDefault(keyCode,'a'),0)
                        )
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }

                if (specialPhysicsKey.containsKey(keyCode)){
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        // shift space, it will send twice key event, reduce one
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_SHIFT,
                                scancode.getOrDefault(specialPhysicsKey.get(keyCode), 0))
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }
//                event.getUnicodeChar()
                return@setOnKeyListener true
            }

            if (event!!.isCtrlPressed()){
                if (regularPhysicsKey.containsKey(keyCode)) {
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_CTRL,
                                keyMap.get(regularPhysicsKey.getOrDefault(keyCode, 'a')) ?: 0
//                                    ?: shiftKeyMap.getOrDefault(regularPhysicsKey.getOrDefault(keyCode,'a'),0)
                        )
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }

                if (specialPhysicsKey.containsKey(keyCode)){
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_CTRL,
                                scancode.getOrDefault(specialPhysicsKey.get(keyCode), 0))
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }
//                event.getUnicodeChar()
                return@setOnKeyListener true
            }

            if (event!!.isAltPressed()){
                if (regularPhysicsKey.containsKey(keyCode)) {
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_ALT,
                                keyMap.get(regularPhysicsKey.getOrDefault(keyCode, 'a')) ?: 0
//                                    ?: shiftKeyMap.getOrDefault(regularPhysicsKey.getOrDefault(keyCode,'a'),0)
                        )
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }

                if (specialPhysicsKey.containsKey(keyCode)){
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_ALT,
                                scancode.getOrDefault(specialPhysicsKey.get(keyCode), 0))
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }
//                event.getUnicodeChar()
                return@setOnKeyListener true
            }

            // window key is event!!.isSymPressed or event!!.isMetaPressed or event!!.isFunctionPressed
            if (event!!.isSymPressed()){
                if (regularPhysicsKey.containsKey(keyCode)) {
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_GUI,
                                keyMap.get(regularPhysicsKey.getOrDefault(keyCode, 'a')) ?: 0
//                                    ?: shiftKeyMap.getOrDefault(regularPhysicsKey.getOrDefault(keyCode,'a'),0)
                        )
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }

                if (specialPhysicsKey.containsKey(keyCode)){
                    val pressedTime = System.currentTimeMillis()
                    if ((pressedTime - latestSentTime) > 160L) {
                        keyboardHelper!!.sendKeyDown(KeyboardHelper.Modifier.LEFT_GUI,
                                scancode.getOrDefault(specialPhysicsKey.get(keyCode), 0))
                        keyboardHelper!!.sendKeysUp(KeyboardHelper.Modifier.NONE)
                        latestSentTime = pressedTime
                    }
                    return@setOnKeyListener true
                }
//                event.getUnicodeChar()
                return@setOnKeyListener true
            }

            if (event?.action == KeyEvent.ACTION_DOWN) {
                if (hidDataSender!!.isConnected) {
                    Log.d(TAG, "Sending message: $keyCode")
                    if (keyboardHelper!!.pressedModifier.isEmpty()) {
                        if (regularPhysicsKey.containsKey(keyCode)){
                            keyboardHelper!!.sendChar(regularPhysicsKey.getOrDefault(keyCode,'a'))
                            return@setOnKeyListener true
                        }
                        if (specialPhysicsKey.containsKey(keyCode)){
                            keyboardHelper!!.sendSpecialKey(specialPhysicsKey.getOrDefault(keyCode,"Space"))
                            return@setOnKeyListener true
                        }
                    }
//                    else {
//                        keyboardHelper!!.sendModifierKey(c)
//                    }
                } else {
                    for (device in BluetoothAdapter.getDefaultAdapter().bondedDevices) if (TARGET_DEVICE_NAME == device.name) {
                        Log.d(TAG, "Requesting connection to " + device.name)
                        // register again when app switch back from background
                        hidDataSender!!.register(applicationContext, profileListener)
                        hidDataSender!!.requestConnect(device)
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
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

