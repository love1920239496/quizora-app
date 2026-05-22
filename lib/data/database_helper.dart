import 'dart:convert';
import 'package:flutter/services.dart' show rootBundle;
import 'package:sqflite/sqflite.dart';
import 'package:path/path.dart' as p;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_progress.dart';
import '../models/cbse_question.dart';
import '../models/cbse_notes.dart';
import 'cbse_question_generator.dart';

class DatabaseHelper {
  static final DatabaseHelper instance = DatabaseHelper._init();
  static Database? _database;

  DatabaseHelper._init();

  Future<Database> get database async {
    if (_database != null) return _database!;
    _database = await _initDB('quizora.db');
    return _database!;
  }

  Future<Database> _initDB(String filePath) async {
    final dbPath = await getDatabasesPath();
    final path = p.join(dbPath, filePath);

    return await openDatabase(
      path,
      version: 1,
      onCreate: _createDB,
    );
  }

  Future _createDB(Database db, int version) async {
    // User progress table
    await db.execute('''
      CREATE TABLE user_progress (
        id INTEGER PRIMARY KEY,
        xp INTEGER,
        streak INTEGER,
        selectedLanguage TEXT,
        selectedClass TEXT,
        badgesUnlocked TEXT,
        completedChapters TEXT,
        revisionChapters TEXT,
        dailyChallengeCompletedToday INTEGER,
        lastChallengeDate TEXT
      )
    ''');

    // Create default progress entry
    await db.insert('user_progress', {
      'id': 1,
      'xp': 150,
      'streak': 3,
      'selectedLanguage': '',
      'selectedClass': 'Class 10',
      'badgesUnlocked': 'Quiz Rookie',
      'completedChapters': 'Chemical Reactions',
      'revisionChapters': '',
      'dailyChallengeCompletedToday': 0,
      'lastChallengeDate': ''
    });

    // CBSE questions table
    await db.execute('''
      CREATE TABLE cbse_questions (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        clazz TEXT,
        language TEXT,
        subject TEXT,
        chapter TEXT,
        question TEXT,
        option1 TEXT,
        option2 TEXT,
        option3 TEXT,
        option4 TEXT,
        correctIndex INTEGER,
        explanation TEXT
      )
    ''');

    // CBSE notes table
    await db.execute('''
      CREATE TABLE cbse_notes (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        clazz TEXT,
        language TEXT,
        subject TEXT,
        chapter TEXT,
        summary TEXT,
        keyPoints TEXT,
        definitions TEXT,
        formulas TEXT,
        importantLines TEXT,
        revisionTricks TEXT
      )
    ''');
  }

  // User Progress DAO replacement
  Future<UserProgress> getProgress() async {
    final db = await instance.database;
    final List<Map<String, dynamic>> maps = await db.query(
      'user_progress',
      where: 'id = ?',
      whereArgs: [1],
    );

    if (maps.isNotEmpty) {
      return UserProgress.fromMap(maps.first);
    }
    return UserProgress();
  }

  Future<int> saveProgress(UserProgress progress) async {
    final db = await instance.database;
    return await db.update(
      'user_progress',
      progress.toMap(),
      where: 'id = ?',
      whereArgs: [1],
    );
  }

  // Questions / Notes loading with lazy asset seeding
  Future<List<CBSEQuestion>> getQuestions(
    String clazz,
    String language,
    String subject,
    String chapter,
  ) async {
    final db = await instance.database;

    // Check sqlite first
    final List<Map<String, dynamic>> maps = await db.query(
      'cbse_questions',
      where: 'clazz = ? AND language = ? AND subject = ? AND chapter = ?',
      whereArgs: [clazz, language, subject, chapter],
    );

    if (maps.isNotEmpty) {
      return maps.map((map) => CBSEQuestion.fromMap(map)).toList();
    }

    // Try loading pre-bundled JSON assets
    final assetPath = _getAssetPath(clazz, subject, chapter);
    if (assetPath != null) {
      final parsed = await _loadFromAssetAndCache(assetPath, clazz, language, subject, chapter);
      if (parsed != null) {
        return parsed;
      }
    }

    // Dynamic generation fallback
    final generatedQuestions = CBSEQuestionGenerator.generateQuestions(
      clazz: clazz,
      subject: subject,
      chapter: chapter,
      language: language,
    );

    final generatedNotes = CBSEQuestionGenerator.generateNotes(
      clazz: clazz,
      subject: subject,
      chapter: chapter,
      language: language,
    );

    // Save to cache
    for (var q in generatedQuestions) {
      await db.insert('cbse_questions', q.toMap());
    }
    await db.insert('cbse_notes', generatedNotes.toMap());

    return generatedQuestions;
  }

  Future<CBSENotes?> getNotes(
    String clazz,
    String language,
    String subject,
    String chapter,
  ) async {
    final db = await instance.database;

    // Check cache
    final List<Map<String, dynamic>> maps = await db.query(
      'cbse_notes',
      where: 'clazz = ? AND language = ? AND subject = ? AND chapter = ?',
      whereArgs: [clazz, language, subject, chapter],
    );

    if (maps.isNotEmpty) {
      return CBSENotes.fromMap(maps.first);
    }

    // Trigger questions retrieval to perform lazy-seeding
    await getQuestions(clazz, language, subject, chapter);

    // Try reading again
    final List<Map<String, dynamic>> reMaps = await db.query(
      'cbse_notes',
      where: 'clazz = ? AND language = ? AND subject = ? AND chapter = ?',
      whereArgs: [clazz, language, subject, chapter],
    );

    if (reMaps.isNotEmpty) {
      return CBSENotes.fromMap(reMaps.first);
    }

    return null;
  }

  String? _getAssetPath(String clazz, String subject, String chapter) {
    chapter = chapter.toLowerCase();
    if (clazz == 'Class 10') {
      if (subject == 'Science' && (chapter.contains('light') || chapter.contains('प्रकाश'))) {
        return 'assets/data/class_10/science_light.json';
      }
      if (subject == 'Mathematics' && (chapter.contains('quadratic') || chapter.contains('द्विघात'))) {
        return 'assets/data/class_10/mathematics_quadratic.json';
      }
    } else if (clazz == 'Class 12') {
      if (subject == 'Physics' && chapter.contains('charges')) {
        return 'assets/data/class_12/physics_field.json';
      }
    }
    return null;
  }

  Future<List<CBSEQuestion>?> _loadFromAssetAndCache(
    String path,
    String clazz,
    String language,
    String subject,
    String chapter,
  ) async {
    try {
      final db = await instance.database;
      final jsonString = await rootBundle.loadString(path);
      final Map<String, dynamic> root = jsonDecode(jsonString);

      // Parse notes
      final notesJson = root['notes'] as Map<String, dynamic>;
      final notes = CBSENotes(
        id: 0,
        clazz: clazz,
        language: language,
        subject: subject,
        chapter: chapter,
        summary: notesJson['summary'] ?? '',
        keyPoints: (notesJson['keyPoints'] as List?)?.join('\n') ?? '',
        definitions: (notesJson['definitions'] as List?)?.join('\n') ?? '',
        formulas: (notesJson['formulas'] as List?)?.join('\n') ?? '',
        importantLines: (notesJson['importantLines'] as List?)?.join('\n') ?? '',
        revisionTricks: (notesJson['revisionTricks'] as List?)?.join('\n') ?? '',
      );

      // Parse questions
      final questionsListJson = root['questions'] as List;
      final List<CBSEQuestion> parsedQuestions = [];

      for (var qItem in questionsListJson) {
        final options = qItem['options'] as List;
        parsedQuestions.add(CBSEQuestion(
          id: 0,
          clazz: clazz,
          language: language,
          subject: subject,
          chapter: chapter,
          question: qItem['question'] ?? '',
          option1: options.isNotEmpty ? options[0] : 'Option 1',
          option2: options.length > 1 ? options[1] : 'Option 2',
          option3: options.length > 2 ? options[2] : 'Option 3',
          option4: options.length > 3 ? options[3] : 'Option 4',
          correctIndex: qItem['correctIndex'] ?? 0,
          explanation: qItem['explanation'] ?? '',
        ));
      }

      // Save to SQLite
      await db.insert('cbse_notes', notes.toMap());
      for (var q in parsedQuestions) {
        await db.insert('cbse_questions', q.toMap());
      }

      return parsedQuestions;
    } catch (e) {
      return null;
    }
  }

  // Revision helpers
  Future addChapterForRevision(String chapterName) async {
    final current = await getProgress();
    final list = current.revisionChapters.split(',').map((e) => e.trim()).where((e) => e.isNotEmpty).toSet();
    list.add(chapterName);
    await saveProgress(current.copyWith(revisionChapters: list.join(',')));
  }

  Future removeChapterFromRevision(String chapterName) async {
    final current = await getProgress();
    final list = current.revisionChapters.split(',').map((e) => e.trim()).where((e) => e.isNotEmpty).toSet();
    list.remove(chapterName);
    await saveProgress(current.copyWith(revisionChapters: list.join(',')));
  }

  // Completion helpers
  Future completeChapter(String chapterAndSubject) async {
    final current = await getProgress();
    final list = current.completedChapters.split(',').map((e) => e.trim()).where((e) => e.isNotEmpty).toSet();
    list.add(chapterAndSubject);
    await saveProgress(current.copyWith(completedChapters: list.join(',')));
  }
}
