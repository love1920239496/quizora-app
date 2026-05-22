import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';
import '../models/user_progress.dart';
import 'chapter_progress_screen.dart';
import 'profile_screen.dart';
import 'quiz_flow_selection_screen.dart';
import 'study_main_screen.dart';

enum HomeTab { home, notes, quiz, profile }

class GuestHomeScreen extends StatefulWidget {
  final VoidCallback onSwitchLanguageRequested;

  const GuestHomeScreen({Key? key, required this.onSwitchLanguageRequested}) : super(key: key);

  @override
  _GuestHomeScreenState createState() => _GuestHomeScreenState();
}

class _GuestHomeScreenState extends State<GuestHomeScreen> {
  HomeTab _activeTab = HomeTab.home;
  UserProgress _progress = UserProgress();
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadProgress();
  }

  Future<void> _loadProgress() async {
    final progress = await DatabaseHelper.instance.getProgress();
    setState(() {
      _progress = progress;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Scaffold(
        backgroundColor: spaceBgStart,
        body: Center(child: CircularProgressIndicator(color: neonBlue)),
      );
    }

    return Scaffold(
      backgroundColor: Colors.transparent,
      body: GlowBackground(
        child: Column(
          children: [
            Expanded(
              child: AnimatedSwitcher(
                duration: const Duration(milliseconds: 220),
                child: _buildTabContent(),
              ),
            ),
          ],
        ),
      ),
      bottomNavigationBar: BottomNavigationBarWidget(
        activeTab: _activeTab,
        onTabSelected: (tab) {
          setState(() {
            _activeTab = tab;
          });
          _loadProgress();
        },
      ),
    );
  }

  Widget _buildTabContent() {
    switch (_activeTab) {
      case HomeTab.home:
        return HomeContent(
          progress: _progress,
          onSwitchLanguage: widget.onSwitchLanguageRequested,
          onTabSelected: (tab) {
            setState(() {
              _activeTab = tab;
            });
          },
          onNavigateToDailyChallenge: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) => const StudyMainScreen(
                  subject: 'Science',
                  chapter: 'Daily Battle Mix',
                  isDailyChallenge: true,
                ),
              ),
            ).then((_) => _loadProgress());
          },
          onNavigateToChapterProgress: () {
            Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const ChapterProgressScreen()),
            ).then((_) => _loadProgress());
          },
        );
      case HomeTab.notes:
        return const QuizFlowSelectionScreen(initialIsNotesFlow: true);
      case HomeTab.quiz:
        return const QuizFlowSelectionScreen(initialIsNotesFlow: false);
      case HomeTab.profile:
        return ProfileScreen(
          progress: _progress,
          onSwitchLanguage: widget.onSwitchLanguageRequested,
          onRefreshProgress: _loadProgress,
        );
    }
  }
}

class HomeContent extends StatelessWidget {
  final UserProgress progress;
  final VoidCallback onSwitchLanguage;
  final Function(HomeTab) onTabSelected;
  final VoidCallback onNavigateToDailyChallenge;
  final VoidCallback onNavigateToChapterProgress;

  const HomeContent({
    Key? key,
    required this.progress,
    required this.onSwitchLanguage,
    required this.onTabSelected,
    required this.onNavigateToDailyChallenge,
    required this.onNavigateToChapterProgress,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final List<String> badges = progress.badgesUnlocked
        .split(',')
        .map((e) => e.trim())
        .where((e) => e.isNotEmpty)
        .toList();

    return ListView(
      padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 24.0),
      children: [
        // --- 1. Header Segment ---
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: const [
                    Icon(Icons.electric_bolt, color: neonBlue, size: 24),
                    SizedBox(width: 6),
                    Text(
                      'QUIZORA',
                      style: TextStyle(
                        color: Colors.white,
                        fontSize: 24,
                        fontWeight: FontWeight.black,
                        letterSpacing: 2,
                      ),
                    ),
                  ],
                ),
                const Text(
                  'CBSE SMART LEARNING',
                  style: TextStyle(
                    color: neonPurple,
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    letterSpacing: 1.0,
                  ),
                ),
              ],
            ),
            StatusShield(
              icon: Icons.translate,
              label: 'MEDIUM',
              value: progress.selectedLanguage.isEmpty ? 'English' : progress.selectedLanguage,
              glowColor: neonPurple,
              onClick: onSwitchLanguage,
            ),
          ],
        ),
        const SizedBox(height: 20),

        // --- 2. Xp and Streak cards Row ---
        Row(
          children: [
            Expanded(
              child: StatusShield(
                icon: Icons.star,
                label: 'STUDENT XP',
                value: '${progress.xp} XP',
                glowColor: neonBlue,
              ),
            ),
            const SizedBox(width: 10),
            Expanded(
              child: StatusShield(
                icon: Icons.local_fire_department,
                label: 'STREAK',
                value: '${progress.streak} Days',
                glowColor: Colors.orange,
              ),
            ),
          ],
        ),
        const SizedBox(height: 20),

        // --- 3. Prominent Daily challenge ---
        GlassCard(
          borderGlowColor: Colors.greenAccent,
          borderWidth: 1.5,
          child: Row(
            children: [
              Expanded(
                flex: 3,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: const [
                  Text(
                    'DAILY CHALLENGE',
                    style: TextStyle(
                      color: Colors.greenAccent,
                      fontSize: 16,
                      fontWeight: FontWeight.black,
                      letterSpacing: 0.5,
                    ),
                  ),
                  SizedBox(height: 4),
                  Text(
                    'Test your skills with a 10-Question mix for a bonus 50 XP!',
                    style: TextStyle(
                      color: textSecondary,
                      fontSize: 12,
                      height: 1.3,
                    ),
                  ),
                ],
              ),
              const SizedBox(width: 10),
              Expanded(
                flex: 2,
                child: NeonButton(
                  text: 'PLAY NOW',
                  onClick: onNavigateToDailyChallenge,
                  glowColor: Colors.greenAccent,
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 20),

        // --- 4. Modules grid ---
        const Text(
          'LEARNING MODULES',
          style: TextStyle(
            color: textSecondary,
            fontSize: 14,
            fontWeight: FontWeight.bold,
            letterSpacing: 1.0,
          ),
        ),
        const SizedBox(height: 12),
        Column(
          children: [
            Row(
              children: [
                Expanded(
                  child: ModuleMenuCard(
                    title: 'Quiz / Tests',
                    description: 'CBSE Chapter MCQs with feedback and animations.',
                    icon: Icons.gamepad,
                    glowColor: neonBlue,
                    onClick: () => onTabSelected(HomeTab.quiz),
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: ModuleMenuCard(
                    title: 'Short Notes',
                    description: 'Definitions, formulas, textbook key highlights.',
                    icon: Icons.book,
                    glowColor: neonPurple,
                    onClick: () => onTabSelected(HomeTab.notes),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 14),
            Row(
              children: [
                Expanded(
                  child: ModuleMenuCard(
                    title: 'Daily Battle',
                    description: 'Randomized syllabus challenges and streak protectors.',
                    icon: Icons.bolt,
                    glowColor: Colors.orange,
                    onClick: onNavigateToDailyChallenge,
                  ),
                ),
                const SizedBox(width: 14),
                Expanded(
                  child: ModuleMenuCard(
                    title: 'My Progress',
                    description: 'Completed courses, revision trackers, metrics.',
                    icon: Icons.show_chart,
                    glowColor: Colors.greenAccent,
                    onClick: onNavigateToChapterProgress,
                  ),
                ),
              ],
            ),
          ],
        ),
        const SizedBox(height: 20),

        // --- 5. Active Badges ---
        GlassCard(
          borderGlowColor: textMuted,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                'YOUR ACTIVE BADGES',
                style: TextStyle(
                  color: textSecondary,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                  letterSpacing: 1.0,
                ),
              ),
              const SizedBox(height: 10),
              if (badges.isEmpty)
                const Text(
                  'Unlock badges by solving quizzes & scoring perfect points!',
                  style: TextStyle(color: textMuted, fontSize: 12),
                )
              else
                Wrap(
                  spacing: 8.0,
                  runSpacing: 8.0,
                  children: badges.map((badge) {
                    return Container(
                      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 6.0),
                      decoration: BoxDecoration(
                        color: neonPurple.withOpacity(0.15),
                        borderRadius: BorderRadius.circular(8.0),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(Icons.workspace_premium, color: Colors.orange, size: 14),
                          const SizedBox(width: 4),
                          Text(
                            badge,
                            style: const TextStyle(
                              color: Colors.white,
                              fontSize: 11,
                              fontWeight: FontWeight.bold,
                            ),
                          ),
                        ],
                      ),
                    );
                  }).toList(),
                ),
            ],
          ),
        ),
      ],
    );
  }
}

class ModuleMenuCard extends StatelessWidget {
  final String title;
  final String description;
  final IconData icon;
  final Color glowColor;
  final VoidCallback onClick;

  const ModuleMenuCard({
    Key? key,
    required this.title,
    required this.description,
    required this.icon,
    required this.glowColor,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 140,
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(20.0),
        border: Border.all(
          color: glowColor.withOpacity(0.35),
          width: 1.0,
        ),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(20.0),
          onTap: onClick,
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Icon(icon, color: glowColor, size: 28),
                Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      title,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 15,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 2),
                    Text(
                      description,
                      style: const TextStyle(
                        color: textSecondary,
                        fontSize: 11,
                        height: 1.2,
                      ),
                      maxLines: 2,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class BottomNavigationBarWidget extends StatelessWidget {
  final HomeTab activeTab;
  final Function(HomeTab) onTabSelected;

  const BottomNavigationBarWidget({
    Key? key,
    required this.activeTab,
    required this.onTabSelected,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Theme(
      data: Theme.of(context).copyWith(
        canvasColor: const Color(0xDC060410),
      ),
      child: BottomNavigationBar(
        currentIndex: _getTabIndex(activeTab),
        onTap: (index) {
          onTabSelected(_getTabFromIndex(index));
        },
        selectedItemColor: activeTab == HomeTab.notes || activeTab == HomeTab.profile ? neonPurple : neonBlue,
        unselectedItemColor: textSecondary,
        backgroundColor: const Color(0xDC060410),
        elevation: 8,
        type: BottomNavigationBarType.fixed,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home),
            label: 'Home',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.book),
            label: 'Notes',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.gamepad),
            label: 'Quiz',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person),
            label: 'Profile',
          ),
        ],
      ),
    );
  }

  int _getTabIndex(HomeTab tab) {
    switch (tab) {
      case HomeTab.home:
        return 0;
      case HomeTab.notes:
        return 1;
      case HomeTab.quiz:
        return 2;
      case HomeTab.profile:
        return 3;
    }
  }

  HomeTab _getTabFromIndex(int index) {
    switch (index) {
      case 0:
        return HomeTab.home;
      case 1:
        return HomeTab.notes;
      case 2:
        return HomeTab.quiz;
      case 3:
        return HomeTab.profile;
      default:
        return HomeTab.home;
    }
  }
}
