/*
 * Copyright 2018 Google LLC All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetoothkeyboard;

import androidx.annotation.IntDef;

import com.example.bluetoothkeyboard.KeyboardReport.KeyboardDataSender;
import com.google.common.collect.ImmutableMap;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Helper class that allows sending less key press states, keeps some handy constants and translates
 * characters to scan codes.
 */
public class KeyboardHelper {


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Modifier.NONE,
            Modifier.LEFT_CTRL,
            Modifier.LEFT_SHIFT,
            Modifier.LEFT_ALT,
            Modifier.LEFT_GUI,    // GUI key is windows key, super key
            Modifier.RIGHT_CTRL,
            Modifier.RIGHT_SHIFT,
            Modifier.RIGHT_ALT,
            Modifier.RIGHT_GUI
    })
    public @interface Modifier {
        int NONE = 0;
        int LEFT_CTRL = (1 << 0);
        int LEFT_SHIFT = (1 << 1);
        int LEFT_ALT = (1 << 2);
        int LEFT_GUI = (1 << 3);
        int RIGHT_CTRL = (1 << 4);
        int RIGHT_SHIFT = (1 << 5);
        int RIGHT_ALT = (1 << 6);
        int RIGHT_GUI = (1 << 7);
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            Key.ENTER,
            Key.ESCAPE,
            Key.BACKSPACE,
            Key.TAB,
            Key.SPACE,
            Key.RIGHT,
            Key.LEFT,
            Key.DOWN,
            Key.UP,
            Key.DELETE,
            Key.INSERT,
            Key.PAGEUP,
            Key.PAGEDOWN,
            Key.PRINTSCREEN,
            Key.MENU
    })
    public @interface Key {
        int ENTER = 40;
        int ESCAPE = 41;
        int BACKSPACE = 42;
        int TAB = 43;
        int SPACE = 44;
        int RIGHT = 79;
        int LEFT = 80;
        int DOWN = 81;
        int UP = 82;
        int DELETE = 76; // this is scan code, not key code
        int INSERT = 73;
        int PAGEUP = 75;
        int PAGEDOWN = 78;
        int PRINTSCREEN = 70;
        int MENU = 118;
        int F1 = 58;
        int F2 = 59;
        int F3 = 60;
        int F4 = 61;
        int F5 = 62;
        int F6 = 63;
        int F7 = 64;
        int F8 = 65;
        int F9 = 66;
        int F10 = 67;
        int F11 = 68;
        int F12 = 69;
        int SCROLLLOCK = 71;
        int PAUSE = 72;
        int HOME = 74;
        int END = 77;
    }
    // https://www.usb.org/sites/default/files/documents/hut1_12v2.pdf

    public static final Map<Character, Integer> keyMap =
            new ImmutableMap.Builder<Character, Integer>()
                    .put('a', 0x04)
                    .put('b', 0x05)
                    .put('c', 0x06)
                    .put('d', 0x07)
                    .put('e', 0x08)
                    .put('f', 0x09)
                    .put('g', 0x0A)
                    .put('h', 0x0B)
                    .put('i', 0x0C)
                    .put('j', 0x0D)
                    .put('k', 0x0E)
                    .put('l', 0x0F)
                    .put('m', 0x10)
                    .put('n', 0x11)
                    .put('o', 0x12)
                    .put('p', 0x13)
                    .put('q', 0x14)
                    .put('r', 0x15)
                    .put('s', 0x16)
                    .put('t', 0x17)
                    .put('u', 0x18)
                    .put('v', 0x19)
                    .put('w', 0x1A)
                    .put('x', 0x1B)
                    .put('y', 0x1C)
                    .put('z', 0x1D)
                    .put('1', 0x1E)
                    .put('2', 0x1F)
                    .put('3', 0x20)
                    .put('4', 0x21)
                    .put('5', 0x22)
                    .put('6', 0x23)
                    .put('7', 0x24)
                    .put('8', 0x25)
                    .put('9', 0x26)
                    .put('0', 0x27)
                    .put(' ', 0x2C)
                    .put('-', 0x2D)
                    .put('=', 0x2E)
                    .put('[', 0x2F)
                    .put(']', 0x30)
                    .put('\\', 0x31)
                    .put(';', 0x33)
                    .put('\'', 0x34)
                    .put('`', 0x35)
                    .put(',', 0x36)
                    .put('.', 0x37)
                    .put('/', 0x38)
                    .build();

    public static final Map<Character, Integer> shiftKeyMap =
            new ImmutableMap.Builder<Character, Integer>()
                    .put('A', 0x04)
                    .put('B', 0x05)
                    .put('C', 0x06)
                    .put('D', 0x07)
                    .put('E', 0x08)
                    .put('F', 0x09)
                    .put('G', 0x0A)
                    .put('H', 0x0B)
                    .put('I', 0x0C)
                    .put('J', 0x0D)
                    .put('K', 0x0E)
                    .put('L', 0x0F)
                    .put('M', 0x10)
                    .put('N', 0x11)
                    .put('O', 0x12)
                    .put('P', 0x13)
                    .put('Q', 0x14)
                    .put('R', 0x15)
                    .put('S', 0x16)
                    .put('T', 0x17)
                    .put('U', 0x18)
                    .put('V', 0x19)
                    .put('W', 0x1A)
                    .put('X', 0x1B)
                    .put('Y', 0x1C)
                    .put('Z', 0x1D)
                    .put('!', 0x1E)
                    .put('@', 0x1F)
                    .put('#', 0x20)
                    .put('$', 0x21)
                    .put('%', 0x22)
                    .put('^', 0x23)
                    .put('&', 0x24)
                    .put('*', 0x25)
                    .put('(', 0x26)
                    .put(')', 0x27)
                    .put('_', 0x2D)
                    .put('+', 0x2E)
                    .put('{', 0x2F)
                    .put('}', 0x30)
                    .put('|', 0x31)
                    .put(':', 0x33)
                    .put('"', 0x34)
                    .put('~', 0x35)
                    .put('<', 0x36)
                    .put('>', 0x37)
                    .put('?', 0x38)
                    .build();

    private final KeyboardDataSender dataSender;

    public ArrayList<String> pressedModifier = new ArrayList<String>();

    /**
     * @param dataSender Interface to send the Keyboard data with.
     */
    public KeyboardHelper(KeyboardDataSender dataSender) {
        this.dataSender = checkNotNull(dataSender);
    }

    /**
     * Send Keyboard data to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     * @param key1     Scan code of the 1st button that is currently pressed (or 0 if none).
     * @param key2     Scan code of the 2nd button that is currently pressed (or 0 if none).
     * @param key3     Scan code of the 3rd button that is currently pressed (or 0 if none).
     * @param key4     Scan code of the 4th button that is currently pressed (or 0 if none).
     * @param key5     Scan code of the 5th button that is currently pressed (or 0 if none).
     */
    public void sendKeysDown(@Modifier int modifier, int key1, int key2, int key3, int key4, int key5) {
        sendKeysDown(modifier, key1, key2, key3, key4, key5, 0);
    }

    /**
     * Send Keyboard data to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     * @param key1     Scan code of the 1st button that is currently pressed (or 0 if none).
     * @param key2     Scan code of the 2nd button that is currently pressed (or 0 if none).
     * @param key3     Scan code of the 3rd button that is currently pressed (or 0 if none).
     * @param key4     Scan code of the 4th button that is currently pressed (or 0 if none).
     */
    public void sendKeysDown(@Modifier int modifier, int key1, int key2, int key3, int key4) {
        sendKeysDown(modifier, key1, key2, key3, key4, 0, 0);
    }

    /**
     * Send Keyboard data to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     * @param key1     Scan code of the 1st button that is currently pressed (or 0 if none).
     * @param key2     Scan code of the 2nd button that is currently pressed (or 0 if none).
     * @param key3     Scan code of the 3rd button that is currently pressed (or 0 if none).
     */
    public void sendKeysDown(@Modifier int modifier, int key1, int key2, int key3) {
        sendKeysDown(modifier, key1, key2, key3, 0, 0, 0);
    }

    /**
     * Send Keyboard data to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     * @param key1     Scan code of the 1st button that is currently pressed (or 0 if none).
     * @param key2     Scan code of the 2nd button that is currently pressed (or 0 if none).
     */
    public void sendKeysDown(@Modifier int modifier, int key1, int key2) {
        sendKeysDown(modifier, key1, key2, 0, 0, 0, 0);
    }

    /**
     * Send Keyboard data to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     * @param key      Scan code of the button that is currently pressed (or 0 if none).
     */
    public void sendKeyDown(@Modifier int modifier, int key) {
        sendKeysDown(modifier, key, 0, 0, 0, 0, 0);
    }

    /**
     * Send "all keys are not pressed" event to the connected HID Host device.
     *
     * @param modifier Modifier keys bit mask (Ctrl/Shift/Alt/GUI).
     */
    public void sendKeysUp(@Modifier int modifier) {
        sendKeysDown(modifier, 0, 0, 0, 0, 0, 0);
    }

    /**
     * Send a key press event, followed by an immediate release event, for the specified character.
     *
     * @param key Character to send.
     */


    public static final Map<String,Integer> scancode = new HashMap<String, Integer>(){
        {
            put("Enter", Key.ENTER);
            put("Esc", Key.ESCAPE);
            put("Back", Key.BACKSPACE);
            put("Tab", Key.TAB);
            put("Space", Key.SPACE);
            put("Right", Key.RIGHT);
            put("Left", Key.LEFT);
            put("Down", Key.DOWN);
            put("Up", Key.UP);
            put("Ins", Key.INSERT);
            put("Del", Key.DELETE);
            put("PgUp", Key.PAGEUP);
            put("PgDn", Key.PAGEDOWN);
            put("PRINTSCREEN", Key.PRINTSCREEN);
            put("MENU",Key.MENU);
            put("SCROLLLOCK",Key.SCROLLLOCK);
            put("PAUSE",Key.PAUSE);
            put("HOME",Key.HOME);
            put("END",Key.END);
            put("F1",Key.F1);
            put("F2",Key.F2);
            put("F3",Key.F3);
            put("F4",Key.F4);
            put("F5",Key.F5);
            put("F6",Key.F6);
            put("F7",Key.F7);
            put("F8",Key.F8);
            put("F9",Key.F9);
            put("F10",Key.F10);
            put("F11",Key.F11);
            put("F12",Key.F12);
            put("Ctrl", Modifier.LEFT_CTRL);
            put("Shift", Modifier.LEFT_SHIFT);
            put("Alt", Modifier.LEFT_ALT);
            put("Win", Modifier.LEFT_GUI);
        }
    };

    public void sendChar(char key) {
        boolean shift = false;
        Integer code = keyMap.get(key);
        if (code == null) {    // if not found then find it in shiftKeyMap
            shift = true;
            code = shiftKeyMap.get(key);
            if (code == null) return;
        }
        // if shift is true, then send shift and shiftKeyMap code
        sendKeyDown(shift ? Modifier.LEFT_SHIFT : Modifier.NONE, code);
        sendKeysUp(Modifier.NONE);
    }

    public void sendSpecialKey(String key){
        Integer code = scancode.get(key);
        if (code == null) return;
        sendKeyDown(Modifier.NONE,code);
        sendKeysUp(Modifier.NONE);
    }

    public void sendModifierKey(String specialChar){
        Integer code = scancode.get(specialChar);
        if (code == null) return;
        ArrayList<Integer> ai = new ArrayList<>();
        for (String value: pressedModifier) ai.add(scancode.get(value));
        if (ai.size() == 1) {
            if (specialChar.equals("Del")){
                // Shift Del just send Shift, Win Del just send Win.
                if (pressedModifier.get(0).equals("Shift")) sendKeyDown(ai.get(0),0);
                if (pressedModifier.get(0).equals("Win")) sendKeyDown(ai.get(0),0);
            }
            else sendKeyDown(ai.get(0), code);
        }
        if (ai.size() == 2) sendKeysDown(ai.get(0), ai.get(1), code);
        if (ai.size() == 3) sendKeysDown(ai.get(0), ai.get(1), ai.get(2), code);
        sendKeysUp(Modifier.NONE);
        pressedModifier.clear();
    }

    public void sendSingalModifierKey(String key){
        sendKeyDown(scancode.get(key),0);
    }

    public void sendModifierKey(char key){
        Integer code = keyMap.get(key);
        if (code == null) code = shiftKeyMap.get(key);
        if (code == null) return;
        ArrayList<Integer> ai = new ArrayList<>();
        for (String value: pressedModifier){
            ai.add(scancode.get(value));
            if (value.equals( "Shift")) {
                if (Character.isLetter(key)) code = shiftKeyMap.get(Character.toUpperCase(key));
            }
        }
        if (ai.size() == 1) sendKeyDown(ai.get(0), code);
        if (ai.size() == 2) sendKeysDown(ai.get(0), ai.get(1), code);
        if (ai.size() == 3) sendKeysDown(ai.get(0), ai.get(1), ai.get(2), code);
        sendKeysUp(Modifier.NONE);
        pressedModifier.clear();
    }

    private void sendKeysDown(
            @Modifier int modifier, int key1, int key2, int key3, int key4, int key5, int key6) {
        // modifier use Modifier.None if no, other key1 use 0 if not press
        dataSender.sendKeyboard(modifier, key1, key2, key3, key4, key5, key6);
    }
}
