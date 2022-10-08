import 'package:flutter/services.dart';

class FolderPermission {
  final methodChannel = const MethodChannel('folder_permission');

  Future getFolderPermission() async {
    final permission = await methodChannel.invokeMethod<String>('getFolderPermission');
    return permission;
  }
}
