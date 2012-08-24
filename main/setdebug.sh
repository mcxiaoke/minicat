#!/bin/sh
adb shell setprop log.tag.org.apache.http VERBOSE
adb shell setprop log.tag.org.apache.http.wire VERBOSE
adb shell setprop log.tag.org.apache.http.headers VERBOSE
