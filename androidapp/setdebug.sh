#!/bin/sh
adb shell setprop log.tag.org.apache.http VERBOSE
adb shell setprop log.tag.org.apache.http.wire VERBOSE
adb shell setprop log.tag.org.apache.http.headers VERBOSE
adb shell setprop log.tag.httpclient.wire.header VERBOSE
adb shell setprop log.tag.httpclient.wire.content VERBOSE
