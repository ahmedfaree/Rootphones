package com.example.rootphones // استبدل هذا باسم الحزمة (Package Name) الخاص بمشروعك إن كان مختلفاً

import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import java.io.DataOutputStream
import java.io.IOException

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.rootphones/reboot"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // إعداد قنوات الاتصال بين Flutter و الأندرويد (Kotlin)
        flutterEngine?.dartExecutor?.binaryMessenger?.let { messenger ->
            MethodChannel(messenger, CHANNEL).setMethodCallHandler { call, result ->
                if (call.method == "rebootDevice") {
                    val status = triggerRootReboot()
                    if (status) {
                        result.success("جاري إعادة التشغيل...")
                    } else {
                        result.error("ROOT_FAILED", "فشل الحصول على صلاحيات الروت أو تنفيذ الأمر", null)
                    }
                } else {
                    result.notImplemented()
                }
            }
        }
    }

    // دالة أندرويد لتنفيذ أمر الروت وإعادة التشغيل بأمان من جانب الـ Native
    private fun triggerRootReboot(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("su")
            val os = DataOutputStream(process.outputStream)
            
            // كتابة أمر إعادة التشغيل في مخرجات النظام (Shell)
            os.writeBytes("reboot\n")
            os.flush()
            os.writeBytes("exit\n")
            os.flush()
            
            process.waitFor()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}