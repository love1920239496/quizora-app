class UserProgress {
  final int id;
  final int xp;
  final int streak;
  final String selectedLanguage;
  final String selectedClass;
  final String badgesUnlocked;
  final String completedChapters;
  final String revisionChapters;
  final bool dailyChallengeCompletedToday;
  final String lastChallengeDate;

  UserProgress({
    this.id = 1,
    this.xp = 150,
    this.streak = 3,
    this.selectedLanguage = "",
    this.selectedClass = "Class 10",
    this.badgesUnlocked = "Quiz Rookie",
    this.completedChapters = "Chemical Reactions",
    this.revisionChapters = "",
    this.dailyChallengeCompletedToday = false,
    this.lastChallengeDate = "",
  });

  UserProgress copyWith({
    int? id,
    int? xp,
    int? streak,
    String? selectedLanguage,
    String? selectedClass,
    String? badgesUnlocked,
    String? completedChapters,
    String? revisionChapters,
    bool? dailyChallengeCompletedToday,
    String? lastChallengeDate,
  }) {
    return UserProgress(
      id: id ?? this.id,
      xp: xp ?? this.xp,
      streak: streak ?? this.streak,
      selectedLanguage: selectedLanguage ?? this.selectedLanguage,
      selectedClass: selectedClass ?? this.selectedClass,
      badgesUnlocked: badgesUnlocked ?? this.badgesUnlocked,
      completedChapters: completedChapters ?? this.completedChapters,
      revisionChapters: revisionChapters ?? this.revisionChapters,
      dailyChallengeCompletedToday: dailyChallengeCompletedToday ?? this.dailyChallengeCompletedToday,
      lastChallengeDate: lastChallengeDate ?? this.lastChallengeDate,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      'id': id,
      'xp': xp,
      'streak': streak,
      'selectedLanguage': selectedLanguage,
      'selectedClass': selectedClass,
      'badgesUnlocked': badgesUnlocked,
      'completedChapters': completedChapters,
      'revisionChapters': revisionChapters,
      'dailyChallengeCompletedToday': dailyChallengeCompletedToday ? 1 : 0,
      'lastChallengeDate': lastChallengeDate,
    };
  }

  factory UserProgress.fromMap(Map<String, dynamic> map) {
    return UserProgress(
      id: map['id'] ?? 1,
      xp: map['xp'] ?? 150,
      streak: map['streak'] ?? 3,
      selectedLanguage: map['selectedLanguage'] ?? "",
      selectedClass: map['selectedClass'] ?? "Class 10",
      badgesUnlocked: map['badgesUnlocked'] ?? "Quiz Rookie",
      completedChapters: map['completedChapters'] ?? "Chemical Reactions",
      revisionChapters: map['revisionChapters'] ?? "",
      dailyChallengeCompletedToday: (map['dailyChallengeCompletedToday'] ?? 0) == 1,
      lastChallengeDate: map['lastChallengeDate'] ?? "",
    );
  }
}
