import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';
import '../models/user_progress.dart';

class BadgeDetail {
  final String title;
  final String description;
  final IconData icon;
  final bool isUnlocked;

  BadgeDetail(this.title, this.description, this.icon, this.isUnlocked);
}

class ProfileScreen extends StatefulWidget {
  final UserProgress progress;
  final VoidCallback onSwitchLanguage;
  final VoidCallback onRefreshProgress;

  const ProfileScreen({
    Key? key,
    required this.progress,
    required this.onSwitchLanguage,
    required this.onRefreshProgress,
  }) : super(key: key);

  @override
  _ProfileScreenState createState() => _ProfileScreenState();
}

class _ProfileScreenState extends State<ProfileScreen> {
  @override
  Widget build(BuildContext context) {
    final unlockedBadges = widget.progress.badgesUnlocked
        .split(',')
        .map((e) => e.trim())
        .where((e) => e.isNotEmpty)
        .toSet();

    final List<BadgeDetail> badgesList = [
      BadgeDetail('Quiz Rookie', 'Awarded upon entering Quizora in Guest Mode.', Icons.workspace_premium, unlockedBadges.contains('Quiz Rookie')),
      BadgeDetail('Quiz King', 'Reach 200 XP to master speed challenges.', Icons.emoji_events, widget.progress.xp >= 200),
      BadgeDetail('Science Master', 'Unlock 350 XP to claim the science scepter.', Icons.science, widget.progress.xp >= 350),
      BadgeDetail('Math Genius', 'Unlock 3 or more total badges successfully.', Icons.functions, unlockedBadges.length >= 3),
      BadgeDetail('CBSE Champion', 'Earn 500 XP to achieve the board ranking.', Icons.local_activity, widget.progress.xp >= 500),
    ];

    return ListView(
      padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 24.0),
      children: [
        // Upper Gamer Avatar Badge Card
        Container(
          width: double.infinity,
          decoration: BoxDecoration(
            color: glassBg,
            borderRadius: BorderRadius.circular(24.0),
            border: Border.all(color: neonBlue.withOpacity(0.5), width: 1.0),
          ),
          padding: const EdgeInsets.all(24.0),
          child: Column(
            children: [
              Container(
                width: 76,
                height: 76,
                decoration: BoxDecoration(
                  shape: BoxShape.circle,
                  color: neonPurple.withOpacity(0.2),
                  border: Border.all(color: neonPurple, width: 1.5),
                ),
                child: const Icon(
                  Icons.account_circle,
                  color: neonPurple,
                  size: 52,
                ),
              ),
              const SizedBox(height: 14),
              const Text(
                'QUIZORA GUEST',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.black,
                  letterSpacing: 1.0,
                ),
              ),
              const SizedBox(height: 2),
              Text(
                'CBSE ${widget.progress.selectedClass.toUpperCase()} STUDENT',
                style: const TextStyle(
                  color: neonBlue,
                  fontSize: 11,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.0,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),

        // Stats Display Block
        Row(
          children: [
            Expanded(
              child: _GamerStatBox(
                title: 'TOTAL XP',
                value: '${widget.progress.xp} XP',
                color: neonBlue,
                icon: Icons.star,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _GamerStatBox(
                title: 'STREAK',
                value: '${widget.progress.streak} Days',
                color: Colors.orange,
                icon: Icons.local_fire_department,
              ),
            ),
          ],
        ),
        const SizedBox(height: 16),

        // Language / Class Academic Settings
        GlassCard(
          borderGlowColor: neonPurple,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'ACADEMICS SETTINGS',
                style: TextStyle(
                  color: neonPurple,
                  fontSize: 11,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.0,
                ),
              ),
              const SizedBox(height: 14),
              _SettingsInteractiveRow(
                title: 'Instruction Medium',
                subtitle: widget.progress.selectedLanguage.isEmpty ? 'English' : widget.progress.selectedLanguage,
                icon: Icons.translate,
                onClick: widget.onSwitchLanguage,
              ),
              const Divider(color: textMuted, height: 24),
              _SettingsInteractiveRow(
                title: 'Target Syllabus Class',
                subtitle: widget.progress.selectedClass,
                icon: Icons.play_for_work,
                onClick: () async {
                  // Switch class cycled
                  final currentClass = widget.progress.selectedClass;
                  final List<String> classes = ['Class 6', 'Class 7', 'Class 8', 'Class 9', 'Class 10', 'Class 11', 'Class 12'];
                  final index = classes.indexOf(currentClass);
                  final nextClass = classes[(index + 1) % classes.length];

                  final updated = widget.progress.copyWith(selectedClass: nextClass);
                  await DatabaseHelper.instance.saveProgress(updated);
                  widget.onRefreshProgress();
                },
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),

        // Badges Progress
        const Text(
          'BADGES INVENTORY',
          style: TextStyle(
            color: textSecondary,
            fontSize: 13,
            fontWeight: FontWeight.bold,
            letterSpacing: 0.5,
          ),
        ),
        const SizedBox(height: 10),
        ...badgesList.map((badge) {
          return Container(
            margin: const EdgeInsets.only(bottom: 10.0),
            padding: const EdgeInsets.all(12.0),
            decoration: BoxDecoration(
              color: glassBg,
              borderRadius: BorderRadius.circular(16),
              border: Border.all(
                color: badge.isUnlocked ? neonPurple.withOpacity(0.5) : textMuted.withOpacity(0.15),
                width: 1.0,
              ),
            ),
            child: Row(
              children: [
                Opacity(
                  opacity: badge.isUnlocked ? 1.0 : 0.4,
                  child: Container(
                    width: 44,
                    height: 44,
                    decoration: BoxDecoration(
                      color: neonPurple.withOpacity(0.15),
                      shape: BoxShape.circle,
                    ),
                    child: Icon(badge.icon, color: badge.isUnlocked ? Colors.orange : textMuted, size: 22),
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        badge.title,
                        style: TextStyle(
                          color: badge.isUnlocked ? Colors.white : textSecondary,
                          fontWeight: FontWeight.bold,
                          fontSize: 14,
                        ),
                      ),
                      Text(
                        badge.description,
                        style: const TextStyle(color: textSecondary, fontSize: 11, height: 1.2),
                      ),
                    ],
                  ),
                ),
                if (!badge.isUnlocked)
                  const Icon(Icons.lock, color: textMuted, size: 16)
                else
                  const Icon(Icons.check_circle, color: Colors.greenAccent, size: 18),
              ],
            ),
          );
        }),
        const SizedBox(height: 30),
      ],
    );
  }
}

class _GamerStatBox extends StatelessWidget {
  final String title;
  final String value;
  final Color color;
  final IconData icon;

  const _GamerStatBox({
    Key? key,
    required this.title,
    required this.value,
    required this.color,
    required this.icon,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(16.0),
        border: Border.all(color: color.withOpacity(0.35), width: 1.0),
      ),
      padding: const EdgeInsets.all(16.0),
      child: Row(
        children: [
          Icon(icon, color: color, size: 24),
          const SizedBox(width: 12),
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                title,
                style: const TextStyle(color: textSecondary, fontSize: 10, fontWeight: FontWeight.bold),
              ),
              Text(
                value,
                style: const TextStyle(color: Colors.white, fontSize: 15, fontWeight: FontWeight.bold),
              ),
            ],
          ),
        ],
      ),
    );
  }
}

class _SettingsInteractiveRow extends StatelessWidget {
  final String title;
  final String subtitle;
  final IconData icon;
  final VoidCallback onClick;

  const _SettingsInteractiveRow({
    Key? key,
    required this.title,
    required this.subtitle,
    required this.icon,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onClick,
      borderRadius: BorderRadius.circular(8.0),
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 4.0),
        child: Row(
          children: [
            Icon(icon, color: neonBlue, size: 20),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    title,
                    style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
                  ),
                  Text(
                    subtitle,
                    style: const TextStyle(color: neonPurple, fontSize: 12, fontWeight: FontWeight.bold),
                  ),
                ],
              ),
            ),
            const Icon(Icons.edit, color: textSecondary, size: 16),
          ],
        ),
      ),
    );
  }
}
