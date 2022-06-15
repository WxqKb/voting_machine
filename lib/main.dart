import 'dart:io';

import 'package:flutter/material.dart';

import 'package:android_window/android_window.dart';
import 'package:window_manager/window_manager.dart';

@pragma('vm:entry-point')
void androidWindow() {
  runApp(const VotingMachine());
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  if (Platform.isMacOS || Platform.isWindows) {
    await windowManager.ensureInitialized();
    WindowOptions windowOptions = const WindowOptions(
      size: Size(600, 800),
      center: true,
      titleBarStyle: TitleBarStyle.hidden,
      skipTaskbar: true,
    );
    windowManager.waitUntilReadyToShow(windowOptions, () async {
      await windowManager.show();
      await windowManager.focus();
      await windowManager.setBackgroundColor(Colors.transparent);
      await windowManager.setAsFrameless();
    });
    runApp(
      Platform.isWindows
          ? const DragToMoveArea(child: VotingMachine())
          : const VotingMachine(),
    );
  }
}

class VotingMachine extends StatelessWidget {
  const VotingMachine({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Voting Machine',
      home: AndroidWindow(
        child: Material(
          color: Colors.white,
          clipBehavior: Clip.antiAliasWithSaveLayer,
          borderRadius: const BorderRadius.all(Radius.circular(8)),
          child: Padding(
            padding: const EdgeInsets.all(12),
            child: Column(
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      '投票器',
                      style: TextStyle(fontSize: 16, color: Colors.black),
                    ),
                    GestureDetector(
                      onTap: () {
                        if (Platform.isAndroid) {
                          AndroidWindow.close();
                        }
                        exit(0);
                      },
                      child:
                          const Icon(Icons.close, size: 20, color: Colors.grey),
                    ),
                  ],
                ),
                const Spacer(),
                ElevatedButton(
                  onPressed: () {
                    debugPrint('>>>>');
                  },
                  child: const Text(
                    '投票',
                    style: TextStyle(fontSize: 16, color: Colors.white),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
      debugShowCheckedModeBanner: false,
    );
  }
}
