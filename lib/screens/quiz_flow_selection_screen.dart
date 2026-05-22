import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';
import '../data/cbse_content_provider.dart';
import 'study_main_screen.dart';

enum SelectionStep { clazz, subject, chapter }

class QuizFlowSelectionScreen extends StatefulWidget {
  final bool initialIsNotesFlow;

  const QuizFlowSelectionScreen({Key? key, required this.initialIsNotesFlow}) : super(key: key);

  @override
  _QuizFlowSelectionScreenState createState() => _QuizFlowSelectionScreenState();
}

class _QuizFlowSelectionScreenState extends State<QuizFlowSelectionScreen> {
  SelectionStep _currentStep = SelectionStep.clazz;
  String _selectedClass = 'Class 10';
  String _selectedSubject = 'Science';
  String _selectedLanguage = 'English';
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadUserPreferences();
  }

  Future<void> _loadUserPreferences() async {
    final progress = await DatabaseHelper.instance.getProgress();
    setState(() {
      _selectedClass = progress.selectedClass;
      _selectedLanguage = progress.selectedLanguage.isEmpty ? 'English' : progress.selectedLanguage;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator(color: neonBlue));
    }

    // Dynamic subjects
    final isSeniorClass = _selectedClass == 'Class 11' || _selectedClass == 'Class 12';
    final List<String> subjectsList = isSeniorClass
        ? ['Physics', 'Chemistry', 'Biology', 'Mathematics', 'English', 'Hindi']
        : ['Science', 'Mathematics', 'English', 'Social Science', 'Hindi'];

    // Dynamic chapters
    final List<String> chaptersList = CBSEContentProvider.getChapters(_selectedClass, _selectedSubject, _selectedLanguage);

    return Scaffold(
      backgroundColor: Colors.transparent,
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 20.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const SizedBox(height: 16),
            if (widget.initialIsNotesFlow && _currentStep == SelectionStep.clazz) ...[
              const Text(
                'SELECT BOOK NOTES',
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.black,
                  letterSpacing: 0.5,
                ),
              ),
              const SizedBox(height: 4),
              const Text(
                'Notes are paired along with quizzes. Select any chapter code below to access instant notes summary.',
                style: TextStyle(
                  color: textSecondary,
                  fontSize: 12,
                ),
              ),
              const SizedBox(height: 16),
            ],

            // Step Header and back navigation
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Row(
                  children: [
                    if (_currentStep != SelectionStep.clazz)
                      IconButton(
                        icon: const Icon(Icons.arrow_back, color: neonBlue),
                        onPressed: () {
                          setState(() {
                            _currentStep = _currentStep == SelectionStep.chapter
                                ? SelectionStep.subject
                                : SelectionStep.clazz;
                          });
                        },
                      ),
                    Text(
                      _getStepTitle(),
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 18,
                        fontWeight: FontWeight.black,
                      ),
                    ),
                  ],
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 4.0),
                  decoration: BoxDecoration(
                    color: neonPurple.withOpacity(0.15),
                    borderRadius: BorderRadius.circular(8.0),
                  ),
                  child: Text(
                    '${_getStepIndex()}/3',
                    style: const TextStyle(
                      color: neonPurple,
                      fontSize: 13,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),

            // Pills summary of selection
            if (_currentStep != SelectionStep.clazz)
              Row(
                children: [
                  _SelectionProgressPill(label: 'Class', value: _selectedClass),
                  const SizedBox(width: 8),
                  if (_currentStep == SelectionStep.chapter)
                    _SelectionProgressPill(label: 'Subject', value: _selectedSubject),
                ],
              ),
            const SizedBox(height: 16),

            // Step wizard list or grid
            Expanded(
              child: AnimatedSwitcher(
                duration: const Duration(milliseconds: 180),
                child: _buildStepContent(subjectsList, chaptersList),
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _getStepTitle() {
    switch (_currentStep) {
      case SelectionStep.clazz:
        return 'STEP 1: SELECT CLASS';
      case SelectionStep.subject:
        return 'STEP 2: SELECT SUBJECT';
      case SelectionStep.chapter:
        return 'STEP 3: SELECT CHAPTER';
    }
  }

  int _getStepIndex() {
    switch (_currentStep) {
      case SelectionStep.clazz:
        return 1;
      case SelectionStep.subject:
        return 2;
      case SelectionStep.chapter:
        return 3;
    }
  }

  Widget _buildStepContent(List<String> subjectsList, List<String> chaptersList) {
    switch (_currentStep) {
      case SelectionStep.clazz:
        final classes = CBSEContentProvider.getClasses();
        return GridView.builder(
          key: const ValueKey('class_grid'),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 14,
            mainAxisSpacing: 14,
            childAspectRatio: 1.8,
          ),
          itemCount: classes.length,
          itemBuilder: (context, index) {
            final className = classes[index];
            final isSelected = _selectedClass == className;
            return _ClassSelectionCard(
              className: className,
              isSelected: isSelected,
              onClick: () async {
                final progress = await DatabaseHelper.instance.getProgress();
                await DatabaseHelper.instance.saveProgress(
                  progress.copyWith(selectedClass: className),
                );
                setState(() {
                  _selectedClass = className;
                  _currentStep = SelectionStep.subject;
                });
              },
            );
          },
        );

      case SelectionStep.subject:
        return GridView.builder(
          key: const ValueKey('subject_grid'),
          gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: 2,
            crossAxisSpacing: 14,
            mainAxisSpacing: 14,
            childAspectRatio: 1.8,
          ),
          itemCount: subjectsList.length,
          itemBuilder: (context, index) {
            final subjectName = subjectsList[index];
            final isSelected = _selectedSubject == subjectName;
            return _SubjectSelectionCard(
              subjectName: subjectName,
              isSelected: isSelected,
              onClick: () {
                setState(() {
                  _selectedSubject = subjectName;
                  _currentStep = SelectionStep.chapter;
                });
              },
            );
          },
        );

      case SelectionStep.chapter:
        if (chaptersList.isEmpty) {
          return const Center(
            child: Text(
              'No chapters available for this configuration.',
              style: TextStyle(color: textSecondary),
            ),
          );
        }
        return ListView.builder(
          key: const ValueKey('chapter_list'),
          itemCount: chaptersList.length,
          itemBuilder: (context, index) {
            final chapterName = chaptersList[index];
            return _ChapterSelectionCard(
              chapterName: chapterName,
              onClick: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (context) => StudyMainScreen(
                      subject: _selectedSubject,
                      chapter: chapterName,
                      isDailyChallenge: false,
                      initialTabIndex: widget.initialIsNotesFlow ? 0 : 1,
                    ),
                  ),
                );
              },
            );
          },
        );
    }
  }
}

class _SelectionProgressPill extends StatelessWidget {
  final String label;
  final String value;

  const _SelectionProgressPill({
    Key? key,
    required this.label,
    required this.value,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10.0, vertical: 6.0),
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(8.0),
      ),
      child: Text(
        '$label: $value',
        style: const TextStyle(
          color: textSecondary,
          fontSize: 11,
          fontWeight: FontWeight.bold,
        ),
      ),
    );
  }
}

class _ClassSelectionCard extends StatelessWidget {
  final String className;
  final bool isSelected;
  final VoidCallback onClick;

  const _ClassSelectionCard({
    Key? key,
    required this.className,
    required this.isSelected,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final glowColor = isSelected ? neonBlue : textMuted;
    return Container(
      decoration: BoxDecoration(
        color: isSelected ? glowColor.withOpacity(0.2) : glassBg,
        borderRadius: BorderRadius.circular(16.0),
        border: Border.all(
          color: isSelected ? glowColor : glowColor.withOpacity(0.2),
          width: 1.0,
        ),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(16.0),
          onTap: onClick,
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Text(
                className,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.black,
                ),
              ),
              Text(
                'CBSE',
                style: TextStyle(
                  color: isSelected ? neonBlue : textSecondary,
                  fontSize: 10,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

class _SubjectSelectionCard extends StatelessWidget {
  final String subjectName;
  final bool isSelected;
  final VoidCallback onClick;

  const _SubjectSelectionCard({
    Key? key,
    required this.subjectName,
    required this.isSelected,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final glowColor = isSelected ? neonPurple : textMuted;
    return Container(
      decoration: BoxDecoration(
        color: isSelected ? glowColor.withOpacity(0.2) : glassBg,
        borderRadius: BorderRadius.circular(16.0),
        border: Border.all(
          color: isSelected ? glowColor : glowColor.withOpacity(0.2),
          width: 1.0,
        ),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(16.0),
          onTap: onClick,
          child: Container(
            alignment: Alignment.center,
            padding: const EdgeInsets.all(8.0),
            child: Text(
              subjectName,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 16,
                fontWeight: FontWeight.black,
              ),
              textAlign: TextAlign.center,
            ),
          ),
        ),
      ),
    );
  }
}

class _ChapterSelectionCard extends StatelessWidget {
  final String chapterName;
  final VoidCallback onClick;

  const _ChapterSelectionCard({
    Key? key,
    required this.chapterName,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 12.0),
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(16.0),
        border: Border.all(
          color: neonBlue.withOpacity(0.4),
          width: 1.0,
        ),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(16.0),
          onTap: onClick,
          child: Padding(
            padding: const EdgeInsets.all(20.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        chapterName,
                        style: const TextStyle(
                          color: Colors.white,
                          fontSize: 15,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      const Text(
                        'Syllabus Aligned MCQ Quiz & Notes',
                        style: TextStyle(
                          color: textSecondary,
                          fontSize: 11,
                        ),
                      ),
                    ],
                  ),
                ),
                const Icon(
                  Icons.chevron_right,
                  color: neonBlue,
                  size: 24,
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
