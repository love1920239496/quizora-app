import '../models/cbse_question.dart';
import '../models/cbse_notes.dart';

class CBSEQuestionGenerator {
  static CBSENotes generateNotes({
    required String clazz,
    required String subject,
    required String chapter,
    required String language,
  }) {
    final bool isHindi = language.toLowerCase() == 'hindi';

    final summary = isHindi
        ? "अध्याय '$chapter' $clazz के नवीनतम CBSE पाठ्यक्रम के अंतर्गत $subject का एक अत्यंत महत्वपूर्ण हिस्सा है। इस अंश में मुख्य रूप से महत्वपूर्ण सिद्धांतों और मुख्य वैचारिक अवयवों का संकलन दिया गया है।"
        : "The chapter '$chapter' is a crucial unit of the $clazz CBSE aligned syllabus of $subject. This study segment covers central tenets, foundational axioms, and key conceptual milestones.";

    final keyPoints = isHindi
        ? "• बोर्ड परीक्षा दिशानिर्देशों के तहत '$chapter' के मूल सिद्धांतों को ध्यानपूर्वक कंठस्थ करें।\n• वैचारिक स्पष्टता के लिए पाठ्यपुस्तक के सभी हल किए गए उदाहरणों को हल करें।\n• परीक्षा में श्रेष्ठ परिणाम प्राप्त करने हेतु पिछले वर्षों के बोर्ड प्रश्नों (PYQs) का अभ्यास करें।"
        : "• Focus on learning the core foundational definitions and steps outlined in '$chapter'.\n• Practice standard back-exercises and examples to test your cognitive progress.\n• Review past school and Board Examination question trends to maximize marks.";

    final definitions = isHindi
        ? "• मुख्य शब्दावली 1: अध्याय '$chapter' के अंतर्गत प्रमुख संरचनाओं के सटीक गुणों को प्रभाषित करती है।\n• मुख्य शब्दावली 2: वैचारिक परीक्षा विश्लेषिकी के लिए वैज्ञानिक रूप से महत्वपूर्ण परिभाषाएँ।"
        : "• Main Definition 1: Core concepts illustrating the primary variables under '$chapter'.\n• Main Definition 2: High-yield terminology frequently asked in short-answer CBSE assessments.";

    final formulas = isHindi
        ? "• $subject के लिए विशेष सूत्र / मुख्य समीकरण: वैचारिक सिद्धांतों के अनुसार लागू होते हैं।"
        : "• Key Mathematical / Physics Formulas applicable for '$chapter' study parameters.";

    final importantLines = isHindi
        ? "CBSE टॉपर टिप: 'उत्तर पत्रक में मुख्य परिभाषाओं और निष्कर्षों को अवश्य रेखांकित करें।'"
        : "CBSE TOPPER HIGH-YIELD ALERT: 'Underline key terminology in your board examinations to ensure complete step-wise credit.";

    final revisionTricks = isHindi
        ? "स्मरण रखने की युक्ति (Mnemonics): अवधारणाओं को दैनिक जीवन से जोड़कर याद रखें!"
        : "Revision Mnemonic: Create structural lists and relate the formulas to real-world objects!";

    return CBSENotes(
      id: 0,
      clazz: clazz,
      language: language,
      subject: subject,
      chapter: chapter,
      summary: summary,
      keyPoints: keyPoints,
      definitions: definitions,
      formulas: formulas,
      importantLines: importantLines,
      revisionTricks: revisionTricks,
    );
  }

  static List<CBSEQuestion> generateQuestions({
    required String clazz,
    required String subject,
    required String chapter,
    required String language,
  }) {
    final bool isHindi = language.toLowerCase() == 'hindi';
    final List<CBSEQuestion> questions = [];

    for (int i = 1; i <= 50; i++) {
      final int correctIdx = i % 4;
      final String questionText = isHindi
          ? "अध्याय '$chapter' के संदर्भ में, निम्नलिखित में से कौन सा कथन पूरी तरह से सही है? (प्रश्न $i)"
          : "With reference to '$chapter' in $subject, which of the following statements is mathematically/scientifically valid? (Q $i)";

      final List<String> optTemplates = isHindi
          ? [
              "यह $subject की एक बुनियादी घटक सिद्धांत है",
              "यह बोर्ड परीक्षा के दृष्टिकोण से माध्यमिक महत्त्व रखता है",
              "यह सूत्र केवल विशिष्ट प्रयोगशाला स्थितियों में कार्य करता है",
              "दिए गए सभी विकल्प सही हैं"
            ]
          : [
              "This represents a primary foundational theorem of $subject",
              "This variable functions as a direct constant parameter under standard parameters",
              "The concept is mostly tested in theoretical subjective questions",
              "All of the mentioned options are correct"
            ];

      final explanation = isHindi
          ? "चयनित विकल्प अध्याय '$chapter' के वैचारिक नियमों का पूरी तरह से अनुसरण करता है।"
          : "The selected option represents a verified theoretical property described under the CBSE criteria for $chapter.";

      questions.add(
        CBSEQuestion(
          id: i,
          clazz: clazz,
          language: language,
          subject: subject,
          chapter: chapter,
          question: questionText,
          option1: optTemplates[0],
          option2: optTemplates[1],
          option3: optTemplates[2],
          option4: optTemplates[3],
          correctIndex: correctIdx,
          explanation: explanation,
        ),
      );
    }

    return questions;
  }
}
