import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';
import '../models/user_progress.dart';

class SyllabusChapter {
  final String subject;
  final String name;

  SyllabusChapter(this.subject, this.name);
}

class ChapterProgressScreen extends StatefulWidget {
  const ChapterProgressScreen({Key? key}) : super(key: key);

  @override
  _ChapterProgressScreenState createState() => _ChapterProgressScreenState();
}

class _ChapterProgressScreenState extends State<ChapterProgressScreen> {
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

    final activeClass = _progress.selectedClass;
    final lang = _progress.selectedLanguage.isEmpty ? 'English' : _progress.selectedLanguage;

    final standardSyllabus = activeClass == 'Class 10'
        ? [
            SyllabusChapter('Science', lang == 'English' ? 'Light - Reflection & Refraction' : 'प्रकाश - परावर्तन तथा अपवर्तन'),
            SyllabusChapter('Science', 'Chemical Reactions & Equations'),
            SyllabusChapter('Science', 'Acids, Bases & Salts'),
            SyllabusChapter('Mathematics', lang == 'English' ? 'Quadratic Equations' : 'द्विघात समीकरण'),
            SyllabusChapter('Mathematics', 'Real Numbers'),
            SyllabusChapter('Social Science', 'The Rise of Nationalism in Europe')
          ]
        : (activeClass == 'Class 12'
            ? [
                SyllabusChapter('Physics', 'Electric Charges & Fields'),
                SyllabusChapter('Physics', 'Electrostatic Potential & Capacitance'),
                SyllabusChapter('Biology', 'Sexual Reproduction in Flowering Plants')
              ]
            : [
                SyllabusChapter('Science', lang == 'English' ? 'Food: Where does it come from?' : 'भोजन: यह कहाँ से आता है?')
              ]);

    final completedSet = _progress.completedChapters
        .split(',')
        .map((e) => e.trim())
        .where((e) => e.isNotEmpty)
        .toSet();

    final revisionSet = _progress.revisionChapters
        .split(',')
        .map((e) => e.trim())
        .where((e) => e.isNotEmpty)
        .toSet();

    // Categorize
    final completedChapters = standardSyllabus.where((ch) =>
        completedSet.contains('${ch.name} - ${ch.subject}') ||
        completedSet.contains(ch.name));

    final revisionChapters = standardSyllabus.where((ch) =>
        revisionSet.contains(ch.name));

    final pendingChapters = standardSyllabus.where((ch) =>
        !completedSet.contains('${ch.name} - ${ch.subject}') &&
        !completedSet.contains(ch.name) &&
        !revisionSet.contains(ch.name));

    final totalCount = standardSyllabus.length;
    final completedCount = completedChapters.length;
    final double percentage = totalCount > 0 ? completedCount / totalCount : 0.0;

    return Scaffold(
      body: GlowBackground(
        child: Column(
          children: [
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Row(
                    children: [
                      IconButton(
                        icon: const Icon(Icons.arrow_back, color: neonBlue),
                        onPressed: () => Navigator.pop(context),
                      ),
                      const Text(
                        'SYLLABUS REPORT',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 18,
                          fontWeight: FontWeight.black,
                        ),
                      ),
                    ],
                  ),
                  StatusShield(
                    icon: Icons.play_for_work,
                    label: 'GRADE',
                    value: activeClass,
                    glowColor: neonPurple,
                  ),
                ],
              ),
            ),
            Expanded(
              child: ListView(
                padding: const EdgeInsets.symmetric(horizontal: 20.0),
                children: [
                  GlassCard(
                    borderGlowColor: neonBlue,
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        const Text(
                          'TOTAL SYLLABUS PROGRESS',
                          style: TextStyle(
                            color: textSecondary,
                            fontSize: 11,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 12),
                        Row(
                          mainAxisAlignment: MainAxisAlignment.spaceBetween,
                          children: [
                            Text(
                              '$completedCount / $totalCount Chapters Done',
                              style: const TextStyle(
                                color: Colors.white,
                                fontSize: 18,
                                fontWeight: FontWeight.black,
                              ),
                            ),
                            Text(
                              '${(percentage * 100).toInt()}%',
                              style: const TextStyle(
                                color: neonBlue,
                                fontSize: 18,
                                fontWeight: FontWeight.black,
                              ),
                            ),
                          ],
                        ),
                        const SizedBox(height: 10),
                        ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: LinearProgressIndicator(
                            value: percentage,
                            color: neonBlue,
                            backgroundColor: const Color(0x306C688D),
                            minHeight: 8,
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 16),
                  _buildSectionHeader('COMPLETED CHAPTERS', completedChapters.length, Colors.greenAccent),
                  if (completedChapters.isEmpty)
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 8.0),
                      child: Text('No completed chapters yet. Go take some quizzes!', style: TextStyle(color: textMuted, fontSize: 12)),
                    )
                  else
                    ...completedChapters.map((ch) => _buildChapterProgressRow(ch, 'Completed', Colors.greenAccent)),
                  const SizedBox(height: 16),
                  _buildSectionHeader('MARKED FOR REVISION', revisionChapters.length, neonPurple),
                  if (revisionChapters.isEmpty)
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 8.0),
                      child: Text('Bookmark chapters via notes to access revision tricks here.', style: TextStyle(color: textMuted, fontSize: 12)),
                    )
                  else
                    ...revisionChapters.map((ch) => _buildChapterProgressRow(ch, 'Revision Pending', neonPurple)),
                  const SizedBox(height: 16),
                  _buildSectionHeader('INCOMPLETE SYLLABUS', pendingChapters.length, Colors.orange),
                  if (pendingChapters.isEmpty)
                    const Padding(
                      padding: EdgeInsets.symmetric(vertical: 8.0),
                      child: Text('All syllabus chapters completed! Excellent job!', style: TextStyle(color: textMuted, fontSize: 12)),
                    )
                  else
                    ...pendingChapters.map((ch) => _buildChapterProgressRow(ch, 'Not Started', Colors.orange)),
                  const SizedBox(height: 30),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSectionHeader(String title, int count, Color color) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 8.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            title,
            style: const TextStyle(
              color: textSecondary,
              fontSize: 12,
              fontWeight: FontWeight.bold,
              letterSpacing: 0.5,
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 8.0, vertical: 2.0),
            decoration: BoxDecoration(
              color: color.withOpacity(0.15),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Text(
              '$count',
              style: TextStyle(
                color: color,
                fontSize: 11,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChapterProgressRow(SyllabusChapter chapter, String status, Color color) {
    return Container(
      margin: const EdgeInsets.only(bottom: 10.0),
      padding: const EdgeInsets.all(16.0),
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: color.withOpacity(0.25), width: 0.8),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  chapter.name,
                  style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                ),
                Text(
                  chapter.subject.toUpperCase(),
                  style: const TextStyle(color: textSecondary, fontSize: 10, fontWeight: FontWeight.bold),
                ),
              ],
            ),
          ),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 4.0),
            decoration: BoxDecoration(
              color: color.withOpacity(0.15),
              borderRadius: BorderRadius.circular(12),
            ),
            child: Text(
              status,
              style: TextStyle(color: color, fontSize: 10, fontWeight: FontWeight.bold),
            ),
          ),
        ],
      ),
    );
  }
}
