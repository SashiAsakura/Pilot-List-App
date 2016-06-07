#!/bin/bash
# utility script to run monkey on Android app
# see http://developer.android.com/guide/developing/tools/monkey.html

# usage: adb shell monley -p package.name.of.app -v numberOfTimesRunningInstance
adb shell monkey -p com.example.hisashi.listviewlearning -v 5000 
