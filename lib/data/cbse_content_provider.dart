class CBSEContentProvider {
  static List<String> getChapters(String clazz, String subject, String language) {
    final bool isHindi = language.toLowerCase() == 'hindi';

    switch (clazz) {
      case 'Class 6':
        switch (subject) {
          case 'Science':
            return isHindi
                ? [
                    'भोजन: यह कहाँ से आता है?',
                    'भोजन के घटक',
                    'तंतु से वस्त्र तक',
                    'वस्तुओं के समूह बनाना',
                    'पदार्थों का पृथक्करण',
                    'हमारे चारों ओर के परिवर्तन',
                    'पौधों को जानिए',
                    'शरीर में गति'
                  ]
                : [
                    'Food: Where does it come from?',
                    'Components of Food',
                    'Fibre to Fabric',
                    'Sorting Materials into Groups',
                    'Separation of Substances',
                    'Changes Around Us',
                    'Getting to Know Plants',
                    'Body Movements'
                  ];
          case 'Mathematics':
            return isHindi
                ? [
                    'अपनी संख्याओं की जानकारी',
                    'पूर्ण संख्याएँ',
                    'संख्याओं के साथ खेलना',
                    'आधारभूत ज्यामितीय अवधारणाएँ',
                    'प्रारंभिक आकारों को समझना',
                    'पूर्णांक',
                    'भिन्न',
                    'दशमलव'
                  ]
                : [
                    'Knowing Our Numbers',
                    'Whole Numbers',
                    'Playing with Numbers',
                    'Basic Geometrical Ideas',
                    'Understanding Elementary Shapes',
                    'Integers',
                    'Fractions',
                    'Decimals'
                  ];
          case 'English':
            return [
              "Who Did Patrick's Homework?",
              "How the Dog Found Himself a New Master!",
              "Taro's Reward",
              "An Indian-American Woman in Space",
              "A Different Kind of School",
              "Who I Am"
            ];
          case 'Social Science':
            return isHindi
                ? [
                    'क्या, कब, कहाँ और कैसे?',
                    'आखेट-खाद्य संग्रह से भोजन उत्पादन तक',
                    'आरंभिक नगर',
                    'क्या बताती हैं हमें किताबें और कब्रें',
                    'विविधता की समझ और भेदभाव'
                  ]
                : [
                    'What, Where, How and When?',
                    'From Hunting-Gathering to Growing Food',
                    'In the Earliest Cities',
                    'What Books and Burials Tell Us',
                    'Understanding Diversity and Discrimination'
                  ];
          case 'Hindi':
            return [
              'वह चिड़िया जो',
              'बचपन',
              'नादान दोस्त',
              'चाँद से थोड़ी सी गप्पें',
              'अक्षरों का महत्व'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 7':
        switch (subject) {
          case 'Science':
            return isHindi
                ? [
                    'पादपों में पोषण',
                    'जंतुओं में पोषण',
                    'रेशों से वस्त्र तक',
                    'ऊष्मा',
                    'अम्ल, क्षारक और लवण',
                    'भौतिक एवं रासायनिक परिवर्तन',
                    'मौसम, जलवायु तथा अनुकूलन'
                  ]
                : [
                    'Nutrition in Plants',
                    'Nutrition in Animals',
                    'Fibre to Fabric',
                    'Heat',
                    'Acids, Bases and Salts',
                    'Physical and Chemical Changes',
                    'Weather, Climate and Adaptations'
                  ];
          case 'Mathematics':
            return isHindi
                ? [
                    'पूर्णांक कुल अध्ययन',
                    'भिन्न एवं दशमलव',
                    'आंकड़ों का प्रबंधन',
                    'सरल समीकरण',
                    'रेखा और कोण',
                    'त्रिभुज और उसके गुण',
                    'त्रिभुजों की सर्वांगसमता'
                  ]
                : [
                    'Integers Deep Study',
                    'Fractions and Decimals',
                    'Data Handling',
                    'Simple Equations',
                    'Lines and Angles',
                    'The Triangle and its Properties',
                    'Congruence of Triangles'
                  ];
          case 'English':
            return [
              'Three Questions',
              'A Gift of Chappals',
              'Gopal and the Hilsa Fish',
              'The Ashes That Made Trees Bloom'
            ];
          case 'Social Science':
            return isHindi
                ? [
                    'हज़ार वर्षों के दौरान हुए परिवर्तनों की पड़ताल',
                    'नए राजा और उनके राज्य',
                    'दिल्ली के सुल्तान',
                    'मुग़ल साम्राज्य',
                    'हमारा पर्यावरण',
                    'हमारी पृथ्वी के अंदर'
                  ]
                : [
                    'Tracing Changes Through a Thousand Years',
                    'New Kings and Kingdoms',
                    'The Delhi Sultans',
                    'The Mughal Empire',
                    'Environment',
                    'Inside Our Earth'
                  ];
          case 'Hindi':
            return [
              'हम पंछी उन्मुक्त गगन के',
              'दादी माँ',
              'हिमालय की बेटियाँ',
              'कठपुतली',
              'मिठाईवाला'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 8':
        switch (subject) {
          case 'Science':
            return isHindi
                ? [
                    'फसल उत्पादन एवं प्रबंध',
                    'सूक्ष्मजीव: मित्र एवं शत्रु',
                    'संश्लेषित रेशे और प्लास्टिक',
                    'धातु और अधातु',
                    'कोयला और पेट्रोलियम',
                    'दहन और ज्वाला'
                  ]
                : [
                    'Crop Production and Management',
                    'Microorganisms: Friend and Foe',
                    'Synthetic Fibres and Plastics',
                    'Materials: Metals and Non-Metals',
                    'Coal and Petroleum',
                    'Combustion and Flame'
                  ];
          case 'Mathematics':
            return isHindi
                ? [
                    'परिमेय संख्याएँ',
                    'एक चर वाले रैखिक समीकरण',
                    'चतुर्भुजों को समझना',
                    'प्रायोगिक ज्यामिति',
                    'वर्ग और वर्गमूल'
                  ]
                : [
                    'Rational Numbers',
                    'Linear Equations in One Variable',
                    'Understanding Quadrilaterals',
                    'Practical Geometry',
                    'Squares and Square Roots'
                  ];
          case 'English':
            return [
              'The Best Christmas Present in the World',
              'The Tsunami',
              'Glimpses of the Past',
              "Bepin Choudhury's Lapse of Memory"
            ];
          case 'Social Science':
            return isHindi
                ? [
                    'कब, कहाँ और कैसे',
                    'व्यापार से साम्राज्य तक',
                    'ग्रामीण क्षेत्र पर शासन चलाना',
                    'जब जनता बगावत करती है'
                  ]
                : [
                    'How, When and Where',
                    'From Trade to Territory',
                    'Ruling the Countryside',
                    'When People Rebel'
                  ];
          case 'Hindi':
            return [
              'ध्वनि',
              'लाख की चूड़ियाँ',
              'बस की यात्रा',
              'दीवानों की hasti'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 9':
        switch (subject) {
          case 'Science':
            return isHindi
                ? [
                    'हमारे आस-पास के पदार्थ',
                    'क्या हमारे आस-पास के पदार्थ शुद्ध हैं?',
                    'परमाणु एवं अणु',
                    'परमाणु की संरचना',
                    'जीवन की मौलिक इकाई',
                    'ऊतक',
                    'गति नियम',
                    'गुरुत्वाकर्षण'
                  ]
                : [
                    'Matter in Our Surroundings',
                    'Is Matter Around Us Pure?',
                    'Atoms and Molecules',
                    'Structure of the Atom',
                    'The Fundamental Unit of Life',
                    'Tissues',
                    'Motion & Forces',
                    'Gravitation'
                  ];
          case 'Mathematics':
            return isHindi
                ? [
                    'संख्या पद्धति',
                    'बहुपद',
                    'निर्देशांक ज्यामिति',
                    'दो चरों वाले रैखिक समीकरण',
                    'त्रिभुज और समानांतर कोण'
                  ]
                : [
                    'Number Systems',
                    'Polynomials',
                    'Coordinate Geometry',
                    'Linear Equations in Two Variables',
                    'Triangles & Parallel Angles'
                  ];
          case 'English':
            return [
              'The Fun They Had',
              'The Sound of Music',
              'The Little Girl',
              'A Truly Beautiful Mind',
              'The Snake and the Mirror'
            ];
          case 'Social Science':
            return isHindi
                ? [
                    'फ्रांसीसी क्रांति',
                    'यूरोप में समाजवाद एवं रूसी क्रांति',
                    'नात्सीवाद और हिटलर का उदय',
                    'भारत - आकार और स्थिति'
                  ]
                : [
                    'The French Revolution',
                    'Socialism in Europe and the Russian Revolution',
                    'Nazism and the Rise of Hitler',
                    'India - Size and Location'
                  ];
          case 'Hindi':
            return [
              'दो बैलों की कथा',
              'ल्हासा की ओर',
              'उपभोक्तावाद की संस्कृति',
              'साँवले सपनों की याद'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 10':
        switch (subject) {
          case 'Science':
            return isHindi
                ? [
                    'जैव प्रक्रम',
                    'रासायनिक अभिक्रियाएं और समीकरण',
                    'अम्ल, क्षारक और लवण',
                    'धातु और अधातु',
                    'कार्बन और उसके यौगिक',
                    'नियंत्रण एवं समन्वय',
                    'प्रकाश - परावर्तन तथा अपवर्तन',
                    'विद्युत',
                    'विद्युत धारा के चुंबकीय प्रभाव',
                    'हमारा पर्यावरण'
                  ]
                : [
                    'Life Processes',
                    'Chemical Reactions & Equations',
                    'Acids, Bases & Salts',
                    'Metals & Non-Metals',
                    'Carbon & its Compounds',
                    'Control & Coordination',
                    'Light - Reflection & Refraction',
                    'Electricity',
                    'Magnetic Effects of Electric Current',
                    'Our Environment'
                  ];
          case 'Mathematics':
            return isHindi
                ? [
                    'वास्तविक संख्याएँ',
                    'बहुपद',
                    'दो चर वाले रैखिक समीकरण',
                    'द्विघात समीकरण',
                    'समांतर श्रेढ़ियाँ',
                    'त्रिभुज',
                    'निर्देशांक ज्यामिति',
                    'त्रिकोणमिति का परिचय',
                    'सांख्यिकी',
                    'प्रायिकता'
                  ]
                : [
                    'Real Numbers',
                    'Polynomials',
                    'Pair of Linear Equations',
                    'Quadratic Equations',
                    'Arithmetic Progressions',
                    'Triangles',
                    'Coordinate Geometry',
                    'Trigonometry',
                    'Statistics',
                    'Probability'
                  ];
          case 'English':
            return [
              'A Letter to God',
              'Nelson Mandela: Long Walk to Freedom',
              'Two Stories about Flying',
              'From the Diary of Anne Frank',
              'Glimpses of India',
              'Madam Rides the Bus',
              'The Sermon at Benares'
            ];
          case 'Social Science':
            return isHindi
                ? [
                    'यूरोप में राष्ट्रवाद का उदय',
                    'भारत में राष्ट्रवाद',
                    'संसाधन और विकास',
                    'जल संसाधन',
                    'सत्ता की साझेदारी',
                    'संघवाद',
                    'राजनीतिक दल'
                  ]
                : [
                    'The Rise of Nationalism in Europe',
                    'Nationalism in India',
                    'Resources and Development',
                    'Water Resources',
                    'Power Sharing',
                    'Federalism',
                    'Political Parties'
                  ];
          case 'Hindi':
            return [
              'नेताजी का चश्मा',
              'बालगोबिन भगत',
              'लखनवी अंदाज',
              'सूरदास के पद',
              'राम-लक्ष्मण-परशुराम संवाद'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 11':
        switch (subject) {
          case 'Physics':
            return [
              'Units and Measurements',
              'Motion in a Straight Line',
              'Motion in a Plane',
              'Laws of Motion',
              'Work, Energy and Power',
              'Rotational Motion',
              'Gravitation'
            ];
          case 'Chemistry':
            return [
              'Some Basic Concepts of Chemistry',
              'Structure of Atom',
              'Classification of Elements',
              'Chemical Bonding',
              'Thermodynamics',
              'Equilibrium'
            ];
          case 'Biology':
            return [
              'The Living World',
              'Biological Classification',
              'Plant Kingdom',
              'Animal Kingdom',
              'Morphology of Flowering Plants'
            ];
          case 'Mathematics':
            return [
              'Sets',
              'Relations & Functions',
              'Trigonometric Functions',
              'Complex Numbers',
              'Permutations & Combinations',
              'Binomial Theorem',
              'Sequences & Series'
            ];
          case 'English':
            return [
              'The Portrait of a Lady',
              "We're Not Afraid to Die...",
              'Discovering Tut',
              'The Laburnum Top',
              'Landscape of the Soul'
            ];
          case 'Hindi':
            return [
              'कबीर के पद',
              'मीरा के पद',
              'घर की याद',
              'चंपा काले काले अक्षर',
              'ग़ज़ल'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      case 'Class 12':
        switch (subject) {
          case 'Physics':
            return [
              'Electric Charges & Fields',
              'Electrostatic Potential & Capacitance',
              'Current Electricity',
              'Moving Charges & Magnetism',
              'Magnetism & Matter',
              'Electromagnetic Induction',
              'Alternating Current',
              'Ray Optics & Optical Instruments'
            ];
          case 'Chemistry':
            return [
              'Solutions',
              'Electrochemistry',
              'Chemical Kinetics',
              'The d- and f- Block Elements',
              'Coordination Compounds',
              'Haloalkanes and Haloarenes',
              'Alcohols, Phenols and Ethers'
            ];
          case 'Biology':
            return [
              'Sexual Reproduction in Flowering Plants',
              'Human Reproduction',
              'Reproductive Health',
              'Principles of Inheritance & Variation',
              'Molecular Basis of Inheritance'
            ];
          case 'Mathematics':
            return [
              'Relations & Functions',
              'Inverse Trigonometric Functions',
              'Matrices',
              'Determinants',
              'Continuity & Differentiability',
              'Application of Derivatives',
              'Integrals'
            ];
          case 'English':
            return [
              'The Last Lesson',
              'Lost Spring',
              'Deep Water',
              'The Rattrap',
              'Indigo'
            ];
          case 'Hindi':
            return [
              'आत्मपरिचय / एक गीत',
              'पतंग',
              'कविता के बहाने',
              'भक्तिन',
              'बाजार दर्शन'
            ];
          default:
            return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
        }

      default:
        return isHindi ? ['$subject परिचय अध्याय'] : ['$subject Introduction Chapter'];
    }
  }

  static List<String> getSubjects(String clazz) {
    switch (clazz) {
      case 'Class 6':
      case 'Class 7':
      case 'Class 8':
      case 'Class 9':
        return ['Science', 'Mathematics', 'English', 'Social Science', 'Hindi'];
      case 'Class 10':
        return ['Science', 'Mathematics', 'English', 'Social Science', 'Hindi'];
      case 'Class 11':
      case 'Class 12':
        return ['Physics', 'Chemistry', 'Biology', 'Mathematics', 'English', 'Hindi'];
      default:
        return ['Science', 'Mathematics', 'English'];
    }
  }

  static List<String> getClasses() {
    return [
      'Class 6',
      'Class 7',
      'Class 8',
      'Class 9',
      'Class 10',
      'Class 11',
      'Class 12',
    ];
  }
}
