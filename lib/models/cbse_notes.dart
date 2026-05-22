class CBSENotes {
  final int id;
  final String clazz;
  final String language;
  final String subject;
  final String chapter;
  final String summary;
  final String keyPoints;
  final String definitions;
  final String formulas;
  final String importantLines;
  final String revisionTricks;

  CBSENotes({
    required this.id,
    required this.clazz,
    required this.language,
    required this.subject,
    required this.chapter,
    required this.summary,
    required this.keyPoints,
    required this.definitions,
    required this.formulas,
    required this.importantLines,
    required this.revisionTricks,
  });

  Map<String, dynamic> toMap() {
    return {
      'clazz': clazz,
      'language': language,
      'subject': subject,
      'chapter': chapter,
      'summary': summary,
      'keyPoints': keyPoints,
      'definitions': definitions,
      'formulas': formulas,
      'importantLines': importantLines,
      'revisionTricks': revisionTricks,
    };
  }

  factory CBSENotes.fromMap(Map<String, dynamic> map) {
    return CBSENotes(
      id: map['id'] ?? 0,
      clazz: map['clazz'] ?? "",
      language: map['language'] ?? "",
      subject: map['subject'] ?? "",
      chapter: map['chapter'] ?? "",
      summary: map['summary'] ?? "",
      keyPoints: map['keyPoints'] ?? "",
      definitions: map['definitions'] ?? "",
      formulas: map['formulas'] ?? "",
      importantLines: map['importantLines'] ?? "",
      revisionTricks: map['revisionTricks'] ?? "",
    );
  }
}
