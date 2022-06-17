import 'dart:io';

import 'package:android_window/android_window.dart';
import 'package:flutter/material.dart';
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
      size: Size(300, 400),
      center: true,
      titleBarStyle: TitleBarStyle.hidden,
      skipTaskbar: true,
    );
    windowManager.waitUntilReadyToShow(windowOptions, () async {
      await windowManager.show();
      await windowManager.focus();
      await windowManager.setBackgroundColor(Colors.transparent);
      await windowManager.setAlwaysOnTop(true);
      await windowManager.setAsFrameless();
    });
    runApp(
      Platform.isWindows
          ? const DragToMoveArea(child: VotingMachine())
          : const VotingMachine(),
    );
  }
}

class VotingMachine extends StatefulWidget {
  const VotingMachine({Key? key}) : super(key: key);

  @override
  State<VotingMachine> createState() => _VotingMachineState();
}

class _VotingMachineState extends State<VotingMachine>
    with SingleTickerProviderStateMixin {
  List<Tab> tabs = const [Tab(text: ' Quick '), Tab(text: ' Advanced ')];
  late TabController controller;

  @override
  void initState() {
    super.initState();
    controller = TabController(length: tabs.length, vsync: this);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Voting Machine',
      home: Platform.isAndroid
          ? AndroidWindow(
              child: buildConnect(),
            )
          : buildConnect(),
      debugShowCheckedModeBanner: false,
    );
  }

  Widget buildConnect() {
    return Material(
      color: Colors.white,
      clipBehavior: Clip.antiAliasWithSaveLayer,
      borderRadius: const BorderRadius.all(Radius.circular(8)),
      child: Padding(
        padding: const EdgeInsets.fromLTRB(32, 24, 32, 16),
        child: Column(
          children: [
            SizedBox(
              width: double.infinity,
              child: Stack(
                alignment: Alignment.center,
                children: [
                  const Text(
                    'Voter',
                    style: TextStyle(fontSize: 24),
                  ),
                  Positioned(
                    right: 0,
                    child: GestureDetector(
                      onTap: () {
                        if (Platform.isAndroid) {
                          AndroidWindow.close();
                        }
                        exit(0);
                      },
                      child:
                          const Icon(Icons.close, size: 22, color: Colors.grey),
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            Expanded(
              child: Column(
                children: [
                  TabBar(
                    tabs: tabs,
                    controller: controller,
                    unselectedLabelColor: Colors.grey,
                    labelColor: Colors.blue,
                    labelStyle: const TextStyle(fontSize: 18),
                    indicatorWeight: 3,
                    indicatorColor: Colors.blue,
                    indicatorSize: TabBarIndicatorSize.label,
                  ),
                  const SizedBox(height: 12),
                  Expanded(
                    child: TabBarView(
                      controller: controller,
                      children: [
                        Container(color: Colors.red),
                        Container(color: Colors.yellow),
                      ],
                    ),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            ElevatedButton(
              onPressed: () {
                debugPrint('>>>>');
              },
              child: const SizedBox(
                width: double.infinity,
                height: 48,
                child: Center(
                  child: Text(
                    '投票',
                    style: TextStyle(fontSize: 16, color: Colors.white),
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
