# BluetoothKeyboard
Use An Android Device As A Bluetooth Keyboard 


# :)

# Copy from
https://github.com/ginkage/wearmouse<br/>
https://github.com/domi1294/BluetoothHidDemo

# How to Use
1. connect iPhone to Android via Bluetooth<br/>
2. open this app on Android, choose iPhone on paired device<br/>
3. iPhone click an area where can input, then press any key on this app

# Requirement
Android Pie 9, API 28<br/>
Bluetooth HID Profile Enabled

# Test Device 
Redmi Note 7, LineageOS 18.1, Android 11, lineage-18.1-20210525-nightly-lavender-signed.zip<br/>
iPhone 11, iOS 14.2<br/>
Redmi 2, LineageOS 17.1, Android 10, lineage-17.1-20200225-UNOFFICIAL-wt88047.zip<br/>

# Issue
switch this app to background, bluetooth will disconnect, you need restart this app. fixed!<br/>
different layout for different screen size? fixed!<br/>
Specific target name? fixed!<br/>
Tab-A? for iOS<br/>
C-S? for Windows<br/>

# Release
see --> github release

# Feature
add OTG Keyboard feature, so if you use otg connect wired keyboard to your android phone<br/>
then connect your android phone to iPhone via bluetooth, this app can turn you wired keyboard<br/>
into a bluetooth keyboard, use android phone as a bridge.

# Note
there is a bug from https://github.com/domi1294/BluetoothHidDemo,<br/>
it can not work with Windows/Linux/Mac OSX, only work with Android/iPhone,<br/>
Shift Del just send Shift, and Win Del just send Win. iOS use Ctrl-Space to switch English/Chinese in default input method, Android use Shift-Space to switch English/Chinese in Google Pinyin.<br/>
it uses scan code, not key code,<br/>
for example, Delete 76, Backspace 42, Ctrl 224<br/>
check it on https://www.usb.org/sites/default/files/documents/hut1_12v2.pdf<br/>
android keycode https://elementalx.org/button-mapper/android-key-codes/ <br/>

there's a bluetooth connect issue in latest LineageOS ROM for Redmi 2, but the old one is fine.<br/>
if bluetooth keep disconnect, reboot the Android device, or forget paired device from both side, then pair again.

# Another Project
there is another project is very likely, this project can make android as bluetooth keyboard work with Windows/Linux<br/>
https://github.com/AchimStuy/Kontroller <br/>
follow the step to use it strictly:<br/>
 Remove previous pairings with the host device in bluetooth settings(This has to be done once) <br/>
 Open the app<br/>
 Send a pairing request from the host device to the controlling device<br/>
 Accept the pairing on the device running Kontroller<br/>
 Use as Mouse/keyboard for your host device<br/>


