# Imports the monkeyrunner modules used by this program
from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice
from com.android.monkeyrunner.easy import EasyMonkeyDevice, By

import commands
import sys
import os

print "start"

# get all the connected devices
devices = os.popen('adb devices').read().strip().split('\n')[1:]

print "the number of connected devices=%s" % len(devices)

for i in xrange(0, len(devices)):

	print "connecting with %s" % devices[i].split('\t')[0]

	# Connects to the current device, returning a MonkeyDevice object
	device = MonkeyRunner.waitForConnection('', devices[i].split('\t')[0])

	# Installs the Android package. Notice that this method returns a boolean, so you can test
	# to see if the installation worked.
	# device.installPackage('listviewlearning/app/build/outputs/apk/app-debug.apk')

	# sets a variable with the package's internal name
	package = 'com.example.hisashi.listviewlearning'

	# sets a variable with the name of an Activity in the package
	activity = 'com.example.hisashi.listviewlearning.ShowListActivity'

	# sets the name of the component to start
	runComponent = package + '/' + activity

	print "\tlaunching app"
	# Runs the component
	device.startActivity(component=runComponent)
	print "\tpressing Menu button"
	# Presses the Menu button
	device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
	device.type("C")
	MonkeyRunner.sleep(5)


	print "\tpressing Home button"
	# Presses the Home button
	device.startActivity(component=runComponent)
	device.press('KEYCODE_HOME', 'DOWN_AND_UP')
	MonkeyRunner.sleep(3)


	print "\tpressing Back button"
	# Presses the Back button
	device.startActivity(component=runComponent)
	device.press('KEYCODE_BACK', MonkeyDevice.DOWN_AND_UP)

	# press add option
	device.startActivity(component=runComponent)
	device.press('KEYCODE_MENU', MonkeyDevice.DOWN_AND_UP)
	# easy_device = EasyMonkeyDevice(device)
	# print "touching add option"
	# easy_device.touch(By.id("add_time_menu_item"), MonkeyDevice.DOWN_AND_UP)

	# Takes a screenshot
	result = device.takeSnapshot()

	# Writes the screenshot to a file
	result.writeToFile('screenshot.png','png')