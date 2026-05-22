class CBSEQuestion {
  final int id;
  final String clazz;
  final String language;
  final String subject;
  final String chapter;
  final String question;
  final String option1;
  final String option2;
  final String option3;
  final String option4;
  final int correctIndex;
  final String explanation;

  CBSEQuestion({
    required this.id,
    required this.clazz,
    required this.language,
    required this.subject,
    required this.chapter,
    required this.question,
    required this.option1,
    required this.option2,
    required this.option3,
    required this.option4,
    required this.correctIndex,
    required this.explanation,
  });

  List<String> getOptions() => [option1, option2, option3, option4];

  Map<String, dynamic> toMap() {
    return {
      'clazz': clazz,
      'language': language,
      'subject': subject,
      'chapter': chapter,
      'question': question,
      'option1': option1,
      'option2': option2,
      'option3': option3,
      'option4': option4,
      'correctIndex': correctIndex,
      'explanation': explanation,
    };
  }

  factory CBSEQuestion.fromMap(Map<String, dynamic> map) {
    return CBSEQuestion(
      id: map['id'] ?? 0,
      clazz: map['clazz'] ?? "",
      language: map['language'] ?? "",
      subject: map['subject'] ?? "",
      chapter: map['chapter'] ?? "",
      question: map['question'] ?? "",
      option1: map['option1'] ?? "",
      option2: map['option2'] ?? "",
      option3: map['option3'] ?? "",
      option4: map['option4'] ?? "",
      correctIndex: map['correctIndex'] ?? 0,
      explanation: map['explanation'] ?? "",
    );
  }
}
