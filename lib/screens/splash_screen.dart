import 'dart:async';
import 'package:flutter/material.dart';
import '../components/quizora_components.dart';

class SplashScreen extends StatefulWidget {
  final VoidCallback onSplashFinished;

  const SplashScreen({Key? key, required this.onSplashFinished}) : super(key: key);

  @override
  _SplashScreenState createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _scaleAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1500),
    )..repeat(reverse: true);

    _scaleAnimation = Tween<double>(begin: 0.9, end: 1.1).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOutSine),
    );

    Timer(const Duration(milliseconds: 2500), widget.onSplashFinished);
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GlowBackground(
        child: Center(
          child: Padding(
            padding: const EdgeInsets.all(24.0),
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                const Spacer(),
                AnimatedBuilder(
                  animation: _scaleAnimation,
                  builder: (context, child) {
                    return SizedBox(
                      width: 160,
                      height: 160,
                      child: Stack(
                        alignment: Alignment.center,
                        children: [
                          // Pulse exterior border ring
                          Container(
                            width: 160 * _scaleAnimation.value,
                            height: 160 * _scaleAnimation.value,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              border: Border.all(
                                color: neonBlue.withOpacity(0.5),
                                width: 2.0,
                              ),
                            ),
                          ),
                          // Core logo shield container
                          Container(
                            width: 110,
                            height: 110,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              color: const Color(0x7A1E1546),
                              border: Border.all(
                                color: neonBlue,
                                width: 1.5,
                              ),
                            ),
                            child: const Icon(
                              Icons.electric_bolt,
                              color: neonBlue,
                              size: 54,
                            ),
                          ),
                        ],
                      ),
                    );
                  },
                ),
                const SizedBox(height: 30),
                const Text(
                  'QUIZORA',
                  style: TextStyle(
                    color: Colors.white,
                    fontSize: 38.0,
                    fontWeight: FontWeight.black,
                    letterSpacing: 4.0,
                  ),
                ),
                const SizedBox(height: 8),
                const Text(
                  'CBSE SMART LEARNING PORTAL',
                  style: TextStyle(
                    color: neonBlue,
                    fontSize: 12.0,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 2.0,
                  ),
                ),
                const Spacer(),
                const Text(
                  'ENTERING GUEST MODE...',
                  style: TextStyle(
                    color: textMuted,
                    fontSize: 11.0,
                    fontWeight: FontWeight.w500,
                    letterSpacing: 1.0,
                  ),
                ),
                const SizedBox(height: 30),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
