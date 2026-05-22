import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';

class LanguageSelectScreen extends StatelessWidget {
  final VoidCallback onLanguageSelected;

  const LanguageSelectScreen({Key? key, required this.onLanguageSelected}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: GlowBackground(
        child: Padding(
          padding: const EdgeInsets.all(24.0),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const SizedBox(height: 30),
              // Header description block
              Column(
                children: const [
                  Icon(
                    Icons.translate,
                    color: neonBlue,
                    size: 52,
                  ),
                  SizedBox(height: 16),
                  Text(
                    'SELECT MEDIUM',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 28,
                      fontWeight: FontWeight.black,
                      letterSpacing: 1.5,
                    ),
                    textAlign: TextAlign.center,
                  ),
                  SizedBox(height: 8),
                  Text(
                    'अपना शिक्षण माध्यम चुनें',
                    style: TextStyle(
                      color: neonPurple,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                    textAlign: TextAlign.center,
                  ),
                ],
              ),
              // Option selection glass card container
              GlassCard(
                borderGlowColor: neonPurple,
                child: Column(
                  children: [
                    const Text(
                      'Please select your preferred CBSE instruction medium to customize notes & challenges.',
                      style: TextStyle(
                        color: textSecondary,
                        fontSize: 14,
                        height: 1.4,
                      ),
                      textAlign: TextAlign.center,
                    ),
                    const SizedBox(height: 24),
                    NeonButton(
                      text: 'ENGLISH MEDIUM',
                      glowColor: neonBlue,
                      onClick: () async {
                        final progress = await DatabaseHelper.instance.getProgress();
                        await DatabaseHelper.instance.saveProgress(
                          progress.copyWith(selectedLanguage: 'English'),
                        );
                        onLanguageSelected();
                      },
                    ),
                    const SizedBox(height: 16),
                    NeonButton(
                      text: 'हिंदी माध्यम (HINDI MEDIUM)',
                      glowColor: neonPurple,
                      onClick: () async {
                        final progress = await DatabaseHelper.instance.getProgress();
                        await DatabaseHelper.instance.saveProgress(
                          progress.copyWith(selectedLanguage: 'Hindi'),
                        );
                        onLanguageSelected();
                      },
                    ),
                  ],
                ),
              ),
              // Subtitle footnote Board instructions text
              const Padding(
                padding: EdgeInsets.only(bottom: 12.0),
                child: Text(
                  'CBSE Class 6 - 12 (Revised 2026 Board Guidelines)',
                  style: TextStyle(
                    color: textSecondary,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 0.5,
                  ),
                  textAlign: TextAlign.center,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
