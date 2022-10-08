package com.miracle.folder_permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import android.provider.DocumentsContract
import android.util.Log
import androidx.annotation.NonNull
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.startActivityForResult

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** FolderPermissionPlugin */
class FolderPermissionPlugin: FlutterPlugin, MethodCallHandler, ActivityAware {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var activity: Activity


    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "folder_permission")
    channel.setMethodCallHandler(this)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
      if (call.method == "getFolderPermission") {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
              getFolderPermission()
          }
          result.success("Android ${android.os.Build.VERSION.RELEASE}")
      } else {
          result.notImplemented()
      }
  }

  private fun getFolderPermission(){
      Log.d("getFolderPermission","getFolderPermission called")
      val storageManager = activity.getSystemService(Context.STORAGE_SERVICE) as StorageManager
      val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
          storageManager.primaryStorageVolume.createOpenDocumentTreeIntent()
      } else {
         return
      }
      val targetDirectory = "Android/media"
      var uri = intent.getParcelableExtra<Uri>("android.provider.extra.INITIAL_URI") as Uri
      var scheme = uri.toString()
      scheme = scheme.replace("/root/","/tree/")
      scheme += "%3A$targetDirectory"

      uri = Uri.parse(scheme)

      Log.d("saiojf",activity.checkUriPermission(uri,1234,1234,Intent.FLAG_GRANT_READ_URI_PERMISSION).toString())

      intent.putExtra("android.provider.extra.INITIAL_URI",uri)
      intent.putExtra("android.provider.extra.SHOW_ADVANCED",true)

      activity.startActivityForResult(intent,1234)

  }


  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }


    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activity = binding.activity

    }

    override fun onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        onAttachedToActivity(binding)
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }

}
