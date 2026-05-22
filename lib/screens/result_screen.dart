import 'package:flutter/material.dart';
import '../components/quizora_components.dart';

class ResultScreen extends StatelessWidget {
  final int score;
  final int correctClicks;
  final int totalCount;
  final bool isDaily;
  final String chapter;

  const ResultScreen({
    Key? key,
    required this.score,
    required this.correctClicks,
    required this.totalCount,
    required this.isDaily,
    required this.chapter,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final double percentage = totalCount > 0 ? correctClicks / totalCount : 0.0;
    
    return Scaffold(
      body: GlowBackground(
        child: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              const Text(
                'QUIZ COMPLETED',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 28,
                  fontWeight: FontWeight.black,
                  letterSpacing: 2.0,
                ),
              ),
              const SizedBox(height: 6),
              Text(
                isDaily ? 'DAILY BATTLE CHALLENGE' : chapter.toUpperCase(),
                style: const TextStyle(
                  color: neonBlue,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.0,
                ),
                textAlign: TextAlign.center,
              ),
              const SizedBox(height: 36),
              
              // Custom Circular Glow Progress Indicator
              CircularGlowProgress(
                percentage: percentage,
                title: 'ACCURACY METRIC',
                scoreText: '$correctClicks/$totalCount',
                glowColor: percentage >= 0.7 ? Colors.greenAccent : (percentage >= 0.4 ? neonBlue : neonPurple),
              ),
              const SizedBox(height: 30),
              
              GlassCard(
                borderGlowColor: neonPurple,
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text(
                          'TOTAL XP EARNED',
                          style: TextStyle(
                            color: textSecondary,
                            fontSize: 13,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        Text(
                          '+${(correctClicks * 10) + (isDaily ? 50 : 0)} XP',
                          style: const TextStyle(
                            color: Colors.greenAccent,
                            fontSize: 16,
                            fontWeight: FontWeight.black,
                          ),
                        ),
                      ],
                    ),
                    const Divider(color: textMuted, height: 24),
                    const Text(
                      'Keep up the great study routine! Solved chapters are updated on your dashboard progress logs.',
                      style: TextStyle(
                        color: textSecondary,
                        fontSize: 12,
                        height: 1.4,
                      ),
                      textAlign: TextAlign.center,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 40),
              
              NeonButton(
                text: 'RETURN TO HOME',
                onClick: () {
                  Navigator.pop(context);
                },
                glowColor: neonBlue,
              ),
            ],
          ),
        ),
      ),
    );
  }
}
