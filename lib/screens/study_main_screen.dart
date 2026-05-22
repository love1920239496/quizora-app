import 'dart:async';
import 'package:flutter/material.dart';
import '../components/quizora_components.dart';
import '../data/database_helper.dart';
import '../models/user_progress.dart';
import '../models/cbse_question.dart';
import '../models/cbse_notes.dart';
import 'result_screen.dart';

class StudyMainScreen extends StatefulWidget {
  final String subject;
  final String chapter;
  final bool isDailyChallenge;
  final int initialTabIndex; // 0 = Notes, 1 = Quiz

  const StudyMainScreen({
    Key? key,
    required this.subject,
    required this.chapter,
    required this.isDailyChallenge,
    this.initialTabIndex = 0,
  }) : super(key: key);

  @override
  _StudyMainScreenState createState() => _StudyMainScreenState();
}

class _StudyMainScreenState extends State<StudyMainScreen> {
  late int _selectedTab;
  UserProgress _progress = UserProgress();
  CBSENotes? _notes;
  List<CBSEQuestion> _questions = [];

  bool _isLoadingContent = true;
  int _currentQuestionIndex = 0;
  int? _selectedAnswerIndex;
  bool _isAnswered = false;
  bool _isAnswerCorrect = false;
  int _sessionQuizScore = 0;
  int _correctCount = 0;

  Timer? _autoNextTimer;

  @override
  void initState() {
    super.initState();
    _selectedTab = widget.initialTabIndex;
    _loadAllContent();
  }

  @override
  void dispose() {
    _autoNextTimer?.cancel();
    super.dispose();
  }

  Future<void> _loadAllContent() async {
    final progress = await DatabaseHelper.instance.getProgress();
    final language = progress.selectedLanguage.isEmpty ? 'English' : progress.selectedLanguage;

    final notes = await DatabaseHelper.instance.getNotes(
      progress.selectedClass,
      language,
      widget.subject,
      widget.chapter,
    );

    var questions = await DatabaseHelper.instance.getQuestions(
      progress.selectedClass,
      language,
      widget.subject,
      widget.chapter,
    );

    if (widget.isDailyChallenge) {
      // Sub-select 10 random questions for the daily battle
      questions = List<CBSEQuestion>.from(questions)..shuffle();
      if (questions.length > 10) {
        questions = questions.sublist(0, 10);
      }
    }

    setState(() {
      _progress = progress;
      _notes = notes;
      _questions = questions;
      _isLoadingContent = false;
    });
  }

  void _submitAnswer(int index) {
    if (_isAnswered || _questions.isEmpty) return;

    final currentQuestion = _questions[_currentQuestionIndex];
    final isCorrect = currentQuestion.correctIndex == index;

    setState(() {
      _selectedAnswerIndex = index;
      _isAnswered = true;
      _isAnswerCorrect = isCorrect;
    });

    if (isCorrect) {
      _sessionQuizScore += 10;
      _correctCount += 1;
      _updateUserXp(5); // bonus student XP
      _autoNextTimer = Timer(const Duration(milliseconds: 1500), () {
        _nextQuestion();
      });
    } else {
      _sessionQuizScore -= 5;
      _updateUserXp(-2); // penalty
    }
  }

  Future<void> _updateUserXp(int delta) async {
    final newXp = (_progress.xp + delta).clamp(0, 999999);
    final updated = _progress.copyWith(xp: newXp);
    await DatabaseHelper.instance.saveProgress(updated);
    setState(() {
      _progress = updated;
    });
  }

  void _nextQuestion() {
    _autoNextTimer?.cancel();
    if (_currentQuestionIndex + 1 < _questions.sizeOrLength) {
      setState(() {
        _currentQuestionIndex += 1;
        _selectedAnswerIndex = null;
        _isAnswered = false;
        _isAnswerCorrect = false;
      });
    } else {
      _completeQuizFlow();
    }
  }

  Future<void> _completeQuizFlow() async {
    // Save completed state
    if (!widget.isDailyChallenge) {
      await DatabaseHelper.instance.completeChapter('${widget.chapter} - ${widget.subject}');
    } else {
      // Bonus daily reward check
      final isCompletedBefore = _progress.dailyChallengeCompletedToday;
      if (!isCompletedBefore) {
        final withDailyBonus = _progress.copyWith(
          xp: _progress.xp + 50,
          streak: _progress.streak + 1,
          dailyChallengeCompletedToday: true,
        );
        await DatabaseHelper.instance.saveProgress(withDailyBonus);
        if (mounted) {
          setState(() {
            _progress = withDailyBonus;
          });
        }
      }
    }

    if (mounted) {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => ResultScreen(
            score: _sessionQuizScore,
            correctClicks: _correctCount,
            totalCount: _questions.length,
            isDaily: widget.isDailyChallenge,
            chapter: widget.chapter,
          ),
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.transparent,
      body: GlowBackground(
        child: Column(
          children: [
            _buildHeader(),
            _buildTabPicker(),
            const SizedBox(height: 10),
            Expanded(
              child: _isLoadingContent
                  ? const Center(child: CircularProgressIndicator(color: neonBlue))
                  : _selectedTab == 0
                      ? _buildNotesTab()
                      : _buildQuizTab(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16.0, vertical: 12.0),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Row(
            children: [
              IconButton(
                icon: const Icon(Icons.arrow_back, color: neonBlue),
                onPressed: () {
                  Navigator.pop(context);
                },
              ),
              const SizedBox(width: 4),
              Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.subject.toUpperCase(),
                    style: const TextStyle(
                      color: neonBlue,
                      fontSize: 11,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.0,
                    ),
                  ),
                  SizedBox(
                    width: MediaQuery.of(context).size.width * 0.45,
                    child: Text(
                      widget.chapter,
                      style: const TextStyle(
                        color: Colors.white,
                        fontSize: 15,
                        fontWeight: FontWeight.black,
                      ),
                      maxLines: 1,
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ),
            ],
          ),
          StatusShield(
            icon: Icons.star,
            label: 'XP STAT',
            value: '${_progress.xp} XP',
            glowColor: neonPurple,
          ),
        ],
      ),
    );
  }

  Widget _buildTabPicker() {
    return Container(
      margin: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 8.0),
      height: 48,
      decoration: BoxDecoration(
        color: const Color(0x33000000),
        borderRadius: BorderRadius.circular(12.0),
        border: Border.all(color: textMuted.withOpacity(0.15), width: 0.8),
      ),
      padding: const EdgeInsets.all(4.0),
      child: Row(
        children: [
          Expanded(
            child: InkWell(
              onTap: () => setState(() => _selectedTab = 0),
              borderRadius: BorderRadius.circular(8.0),
              child: Container(
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: _selectedTab == 0 ? neonPurple.withOpacity(0.25) : Colors.transparent,
                  borderRadius: BorderRadius.circular(8.0),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.book,
                      color: _selectedTab == 0 ? neonPurple : textSecondary,
                      size: 16,
                    ),
                    const SizedBox(width: 6),
                    Text(
                      'Short Notes',
                      style: TextStyle(
                        color: _selectedTab == 0 ? Colors.white : textSecondary,
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
          Expanded(
            child: InkWell(
              onTap: () => setState(() => _selectedTab = 1),
              borderRadius: BorderRadius.circular(8.0),
              child: Container(
                alignment: Alignment.center,
                decoration: BoxDecoration(
                  color: _selectedTab == 1 ? neonBlue.withOpacity(0.25) : Colors.transparent,
                  borderRadius: BorderRadius.circular(8.0),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(
                      Icons.gamepad,
                      color: _selectedTab == 1 ? neonBlue : textSecondary,
                      size: 16,
                    ),
                    const SizedBox(width: 6),
                    Text(
                      'Quiz Game',
                      style: TextStyle(
                        color: _selectedTab == 1 ? Colors.white : textSecondary,
                        fontSize: 14,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ],
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildNotesTab() {
    if (_notes == null) {
      return const Center(child: Text('No notes available.', style: TextStyle(color: Colors.white)));
    }
    final isInsideRevision = _progress.revisionChapters
        .split(',')
        .map((e) => e.trim())
        .contains(widget.chapter);

    return ListView(
      padding: const EdgeInsets.symmetric(horizontal: 20.0, vertical: 8.0),
      children: [
        // Plus revision shortcut button
        ListTile(
          contentPadding: EdgeInsets.zero,
          title: Text(
            isInsideRevision ? 'Chapter Marked for Revision' : 'Add to Revision Tracker',
            style: const TextStyle(color: Colors.white, fontSize: 13, fontWeight: FontWeight.bold),
          ),
          subtitle: const Text('Add to revision shelf for future quick board exams references.', style: TextStyle(color: textSecondary, fontSize: 11)),
          trailing: IconButton(
            icon: Icon(
              isInsideRevision ? Icons.bookmark : Icons.bookmark_outline,
              color: neonPurple,
              size: 28,
            ),
            onPressed: () async {
              if (isInsideRevision) {
                await DatabaseHelper.instance.removeChapterFromRevision(widget.chapter);
              } else {
                await DatabaseHelper.instance.addChapterForRevision(widget.chapter);
              }
              _loadAllContent();
            },
          ),
        ),
        const SizedBox(height: 10),

        GlassCard(
          borderGlowColor: neonPurple,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.info, color: neonPurple, size: 20),
                SizedBox(width: 8),
                Text('SUMMARY', style: TextStyle(color: neonPurple, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.summary, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        GlassCard(
          borderGlowColor: neonBlue,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.format_list_bulleted, color: neonBlue, size: 20),
                SizedBox(width: 8),
                Text('KEY POINTS', style: TextStyle(color: neonBlue, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.keyPoints, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        GlassCard(
          borderGlowColor: Colors.greenAccent,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.spellcheck, color: Colors.greenAccent, size: 20),
                SizedBox(width: 8),
                Text('DEFINITIONS', style: TextStyle(color: Colors.greenAccent, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.definitions, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        GlassCard(
          borderGlowColor: Colors.orange,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.functions, color: Colors.orange, size: 20),
                SizedBox(width: 8),
                Text('FORMULAS', style: TextStyle(color: Colors.orange, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.formulas, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        GlassCard(
          borderGlowColor: neonPurple,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.menu_book, color: neonPurple, size: 20),
                SizedBox(width: 8),
                Text('CBSE IMPORTANT HIGHLIGHTS', style: TextStyle(color: neonPurple, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.importantLines, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        GlassCard(
          borderGlowColor: Colors.greenAccent,
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(children: const [
                Icon(Icons.psychology, color: Colors.greenAccent, size: 20),
                SizedBox(width: 8),
                Text('REVISION TRICKS (MNEMONICS)', style: TextStyle(color: Colors.greenAccent, fontWeight: FontWeight.black, fontSize: 14)),
              ]),
              const SizedBox(height: 10),
              Text(_notes!.revisionTricks, style: const TextStyle(color: Colors.white, fontSize: 13, height: 1.4)),
            ],
          ),
        ),
        const SizedBox(height: 30),
      ],
    );
  }

  Widget _buildQuizTab() {
    if (_questions.isEmpty) {
      return const Center(child: Text('No quiz questions found.', style: TextStyle(color: Colors.white)));
    }
    final activeQuestion = _questions[_currentQuestionIndex];
    final options = activeQuestion.getOptions();

    return ListView(
      padding: const EdgeInsets.symmetric(horizontal: 20.0),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              'QUESTION ${_currentQuestionIndex + 1} OF ${_questions.length}',
              style: const TextStyle(color: textSecondary, fontWeight: FontWeight.bold, fontSize: 12),
            ),
            Text(
              'SCORE: $_sessionQuizScore PTS',
              style: TextStyle(
                color: _sessionQuizScore >= 0 ? Colors.greenAccent : Colors.redAccent,
                fontWeight: FontWeight.bold,
                fontSize: 12,
              ),
            ),
          ],
        ),
        const SizedBox(height: 12),
        ClipRRect(
          borderRadius: BorderRadius.circular(10),
          child: LinearProgressIndicator(
            value: (_currentQuestionIndex) / _questions.length,
            color: neonBlue,
            backgroundColor: const Color(0x306C688D),
            minHeight: 6,
          ),
        ),
        const SizedBox(height: 16),
        GlassCard(
          borderGlowColor: neonBlue,
          child: Text(
            activeQuestion.question,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.bold,
              height: 1.4,
            ),
          ),
        ),
        const SizedBox(height: 12),
        ...List.generate(options.length, (index) {
          final optionVal = options[index];
          OptionVisualState state = OptionVisualState.normal;
          if (_isAnswered) {
            if (activeQuestion.correctIndex == index) {
              state = OptionVisualState.correct;
            } else if (_selectedAnswerIndex == index) {
              state = OptionVisualState.wrong;
            }
          }
          return Padding(
            padding: const EdgeInsets.only(bottom: 12.0),
            child: _QuizOptionRowWidget(
              index: index,
              text: optionVal,
              state: state,
              onClick: () => _submitAnswer(index),
            ),
          );
        }),
        const SizedBox(height: 10),
        if (_isAnswered) ...[
          if (_isAnswerCorrect) ...[
            Container(
              padding: const EdgeInsets.all(16.0),
              decoration: BoxDecoration(
                color: Colors.greenAccent.withOpacity(0.15),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: Colors.greenAccent, width: 1.0),
              ),
              alignment: Alignment.center,
              child: Row(
                mainAxisAlignment: MainAxisAlignment.center,
                children: const [
                  Icon(Icons.check_circle, color: Colors.greenAccent),
                  SizedBox(width: 8),
                  Text(
                    'EXCELLENT! CORRECT (+10 PTS)',
                    style: TextStyle(color: Colors.greenAccent, fontWeight: FontWeight.bold, fontSize: 14),
                  ),
                ],
              ),
            )
          ] else ...[
            GlassCard(
              borderGlowColor: Colors.redAccent,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: const [
                      Icon(Icons.info, color: Colors.redAccent),
                      SizedBox(width: 6),
                      Text(
                        'INCORRECT (-5 PTS)',
                        style: TextStyle(color: Colors.redAccent, fontWeight: FontWeight.bold, fontSize: 14),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Text(
                    'Correct answer is: Option ${activeQuestion.correctIndex + 1}: ${options[activeQuestion.correctIndex]}',
                    style: const TextStyle(color: Colors.greenAccent, fontWeight: FontWeight.bold, fontSize: 13),
                  ),
                  const SizedBox(height: 4),
                  Text(
                    activeQuestion.explanation,
                    style: const TextStyle(color: textSecondary, fontSize: 12, height: 1.3),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 12),
            NeonButton(
              text: 'PROCEED TO NEXT',
              glowColor: neonPurple,
              onClick: _nextQuestion,
            ),
          ]
        ],
        const SizedBox(height: 40),
      ],
    );
  }
}

enum OptionVisualState { normal, correct, wrong }

class _QuizOptionRowWidget extends StatelessWidget {
  final int index;
  final String text;
  final OptionVisualState state;
  final VoidCallback onClick;

  const _QuizOptionRowWidget({
    Key? key,
    required this.index,
    required this.text,
    required this.state,
    required this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    Color borderColor = textMuted.withOpacity(0.4);
    Color glowColor = Colors.transparent;

    if (state == OptionVisualState.correct) {
      borderColor = Colors.greenAccent;
      glowColor = Colors.greenAccent.withOpacity(0.15);
    } else if (state == OptionVisualState.wrong) {
      borderColor = Colors.redAccent;
      glowColor = Colors.redAccent.withOpacity(0.15);
    }

    return Container(
      decoration: BoxDecoration(
        color: state == OptionVisualState.normal ? glassBg : glowColor,
        borderRadius: BorderRadius.circular(16.0),
        border: Border.all(color: borderColor, width: 1.2),
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          borderRadius: BorderRadius.circular(16.0),
          onTap: onClick,
          child: Padding(
            padding: const EdgeInsets.all(16.0),
            child: Row(
              children: [
                Container(
                  width: 28,
                  height: 28,
                  decoration: BoxDecoration(
                    color: state == OptionVisualState.normal
                        ? const Color(0x306C688D)
                        : (state == OptionVisualState.correct ? Colors.greenAccent : Colors.redAccent),
                    shape: BoxShape.circle,
                  ),
                  alignment: Alignment.center,
                  child: state == OptionVisualState.correct
                      ? const Icon(Icons.check, color: Colors.black, size: 16)
                      : (state == OptionVisualState.wrong
                          ? const Icon(Icons.close, color: Colors.white, size: 16)
                          : Text(
                              String.fromCharCode(65 + index),
                              style: const TextStyle(color: Colors.white, fontSize: 11, fontWeight: FontWeight.bold),
                            )),
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Text(
                    text,
                    style: const TextStyle(color: Colors.white, fontSize: 14, fontWeight: FontWeight.bold),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

extension on List {
  int get sizeOrLength => length;
}
