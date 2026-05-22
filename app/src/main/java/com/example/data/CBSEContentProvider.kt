package com.example.data

object CBSEContentProvider {

    fun getChapters(clazz: String, subject: String, language: String): List<String> {
        val isHindi = language.equals("Hindi", ignoreCase = true)
        
        return when (clazz) {
            "Class 6" -> when (subject) {
                "Science" -> {
                    if (isHindi) listOf(
                        "भोजन: यह कहाँ से आता है?",
                        "भोजन के घटक",
                        "तंतु से वस्त्र तक",
                        "वस्तुओं के समूह बनाना",
                        "पदार्थों का पृथक्करण",
                        "हमारे चारों ओर के परिवर्तन",
                        "पौधों को जानिए",
                        "शरीर में गति"
                    ) else listOf(
                        "Food: Where does it come from?",
                        "Components of Food",
                        "Fibre to Fabric",
                        "Sorting Materials into Groups",
                        "Separation of Substances",
                        "Changes Around Us",
                        "Getting to Know Plants",
                        "Body Movements"
                    )
                }
                "Mathematics" -> {
                    if (isHindi) listOf(
                        "अपनी संख्याओं की जानकारी",
                        "पूर्ण संख्याएँ",
                        "संख्याओं के साथ खेलना",
                        "आधारभूत ज्यामितीय अवधारणाएँ",
                        "प्रारंभिक आकारों को समझना",
                        "पूर्णांक",
                        "भिन्न",
                        "दशमलव"
                    ) else listOf(
                        "Knowing Our Numbers",
                        "Whole Numbers",
                        "Playing with Numbers",
                        "Basic Geometrical Ideas",
                        "Understanding Elementary Shapes",
                        "Integers",
                        "Fractions",
                        "Decimals"
                    )
                }
                "English" -> {
                    listOf(
                        "Who Did Patrick's Homework?",
                        "How the Dog Found Himself a New Master!",
                        "Taro's Reward",
                        "An Indian-American Woman in Space",
                        "A Different Kind of School",
                        "Who I Am"
                    )
                }
                "Social Science" -> {
                    if (isHindi) listOf(
                        "क्या, कब, कहाँ और कैसे?",
                        "आखेट-खाद्य संग्रह से भोजन उत्पादन तक",
                        "आरंभिक नगर",
                        "क्या बताती हैं हमें किताबें और कब्रें",
                        "विविधता की समझ और भेदभाव"
                    ) else listOf(
                        "What, Where, How and When?",
                        "From Hunting-Gathering to Growing Food",
                        "In the Earliest Cities",
                        "What Books and Burials Tell Us",
                        "Understanding Diversity and Discrimination"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "वह चिड़िया जो",
                        "बचपन",
                        "नादान दोस्त",
                        "चाँद से थोड़ी सी गप्पें",
                        "अक्षरों का महत्व"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 7" -> when (subject) {
                "Science" -> {
                    if (isHindi) listOf(
                        "पादपों में पोषण",
                        "जंतुओं में पोषण",
                        "रेशों से वस्त्र तक",
                        "ऊष्मा",
                        "अम्ल, क्षारक और लवण",
                        "भौतिक एवं रासायनिक परिवर्तन",
                        "मौसम, जलवायु तथा अनुकूलन"
                    ) else listOf(
                        "Nutrition in Plants",
                        "Nutrition in Animals",
                        "Fibre to Fabric",
                        "Heat",
                        "Acids, Bases and Salts",
                        "Physical and Chemical Changes",
                        "Weather, Climate and Adaptations"
                    )
                }
                "Mathematics" -> {
                    if (isHindi) listOf(
                        "पूर्णांक कुल अध्ययन",
                        "भिन्न एवं दशमलव",
                        "आंकड़ों का प्रबंधन",
                        "सरल समीकरण",
                        "रेखा और कोण",
                        "त्रिभुज और उसके गुण",
                        "त्रिभुजों की सर्वांगसमता"
                    ) else listOf(
                        "Integers Deep Study",
                        "Fractions and Decimals",
                        "Data Handling",
                        "Simple Equations",
                        "Lines and Angles",
                        "The Triangle and its Properties",
                        "Congruence of Triangles"
                    )
                }
                "English" -> {
                    listOf(
                        "Three Questions",
                        "A Gift of Chappals",
                        "Gopal and the Hilsa Fish",
                        "The Ashes That Made Trees Bloom"
                    )
                }
                "Social Science" -> {
                    if (isHindi) listOf(
                        "हज़ार वर्षों के दौरान हुए परिवर्तनों की पड़ताल",
                        "नए राजा और उनके राज्य",
                        "दिल्ली के सुल्तान",
                        "मुग़ल साम्राज्य",
                        "हमारा पर्यावरण",
                        "हमारी पृथ्वी के अंदर"
                    ) else listOf(
                        "Tracing Changes Through a Thousand Years",
                        "New Kings and Kingdoms",
                        "The Delhi Sultans",
                        "The Mughal Empire",
                        "Environment",
                        "Inside Our Earth"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "हम पंछी उन्मुक्त गगन के",
                        "दादी माँ",
                        "हिमालय की बेटियाँ",
                        "कठपुतली",
                        "मिठाईवाला"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 8" -> when (subject) {
                "Science" -> {
                    if (isHindi) listOf(
                        "फसल उत्पादन एवं प्रबंध",
                        "सूक्ष्मजीव: मित्र एवं शत्रु",
                        "संश्लेषित रेशे और प्लास्टिक",
                        "धातु और अधातु",
                        "कोयला और पेट्रोलियम",
                        "दहन और ज्वाला"
                    ) else listOf(
                        "Crop Production and Management",
                        "Microorganisms: Friend and Foe",
                        "Synthetic Fibres and Plastics",
                        "Materials: Metals and Non-Metals",
                        "Coal and Petroleum",
                        "Combustion and Flame"
                    )
                }
                "Mathematics" -> {
                    if (isHindi) listOf(
                        "परिमेय संख्याएँ",
                        "एक चर वाले रैखिक समीकरण",
                        "चतुर्भुजों को समझना",
                        "प्रायोगिक ज्यामिति",
                        "वर्ग और वर्गमूल"
                    ) else listOf(
                        "Rational Numbers",
                        "Linear Equations in One Variable",
                        "Understanding Quadrilaterals",
                        "Practical Geometry",
                        "Squares and Square Roots"
                    )
                }
                "English" -> {
                    listOf(
                        "The Best Christmas Present in the World",
                        "The Tsunami",
                        "Glimpses of the Past",
                        "Bepin Choudhury's Lapse of Memory"
                    )
                }
                "Social Science" -> {
                    if (isHindi) listOf(
                        "कब, कहाँ और कैसे",
                        "व्यापार से साम्राज्य तक",
                        "ग्रामीण क्षेत्र पर शासन चलाना",
                        "जब जनता बगावत करती है"
                    ) else listOf(
                        "How, When and Where",
                        "From Trade to Territory",
                        "Ruling the Countryside",
                        "When People Rebel"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "ध्वनि",
                        "लाख की चूड़ियाँ",
                        "बस की यात्रा",
                        "दीवानों की hasti"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 9" -> when (subject) {
                "Science" -> {
                    if (isHindi) listOf(
                        "हमारे आस-पास के पदार्थ",
                        "क्या हमारे आस-पास के पदार्थ शुद्ध हैं?",
                        "परमाणु एवं अणु",
                        "परमाणु की संरचना",
                        "जीवन की मौलिक इकाई",
                        "ऊतक",
                        "गति नियम",
                        "गुरुत्वाकर्षण"
                    ) else listOf(
                        "Matter in Our Surroundings",
                        "Is Matter Around Us Pure?",
                        "Atoms and Molecules",
                        "Structure of the Atom",
                        "The Fundamental Unit of Life",
                        "Tissues",
                        "Motion & Forces",
                        "Gravitation"
                    )
                }
                "Mathematics" -> {
                    if (isHindi) listOf(
                        "संख्या पद्धति",
                        "बहुपद",
                        "निर्देशांक ज्यामिति",
                        "दो चरों वाले रैखिक समीकरण",
                        "त्रिभुज और समानांतर कोण"
                    ) else listOf(
                        "Number Systems",
                        "Polynomials",
                        "Coordinate Geometry",
                        "Linear Equations in Two Variables",
                        "Triangles & Parallel Angles"
                    )
                }
                "English" -> {
                    listOf(
                        "The Fun They Had",
                        "The Sound of Music",
                        "The Little Girl",
                        "A Truly Beautiful Mind",
                        "The Snake and the Mirror"
                    )
                }
                "Social Science" -> {
                    if (isHindi) listOf(
                        "फ्रांसीसी क्रांति",
                        "यूरोप में समाजवाद एवं रूसी क्रांति",
                        "नात्सीवाद और हिटलर का उदय",
                        "भारत - आकार और स्थिति"
                    ) else listOf(
                        "The French Revolution",
                        "Socialism in Europe and the Russian Revolution",
                        "Nazism and the Rise of Hitler",
                        "India - Size and Location"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "दो बैलों की कथा",
                        "ल्हासा की ओर",
                        "उपभोक्तावाद की संस्कृति",
                        "साँवले सपनों की याद"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 10" -> when (subject) {
                "Science" -> {
                    if (isHindi) listOf(
                        "जैव प्रक्रम",
                        "रासायनिक अभिक्रियाएं और समीकरण",
                        "अम्ल, क्षारक और लवण",
                        "धातु और अधातु",
                        "कार्बन और उसके यौगिक",
                        "नियंत्रण एवं समन्वय",
                        "प्रकाश - परावर्तन तथा अपवर्तन",
                        "विद्युत",
                        "विद्युत धारा के चुंबकीय प्रभाव",
                        "हमारा पर्यावरण"
                    ) else listOf(
                        "Life Processes",
                        "Chemical Reactions & Equations",
                        "Acids, Bases & Salts",
                        "Metals & Non-Metals",
                        "Carbon & its Compounds",
                        "Control & Coordination",
                        "Light - Reflection & Refraction",
                        "Electricity",
                        "Magnetic Effects of Electric Current",
                        "Our Environment"
                    )
                }
                "Mathematics" -> {
                    if (isHindi) listOf(
                        "वास्तविक संख्याएँ",
                        "बहुपद",
                        "दो चर वाले रैखिक समीकरण",
                        "द्विघात समीकरण",
                        "समांतर श्रेढ़ियाँ",
                        "त्रिभुज",
                        "निर्देशांक ज्यामिति",
                        "त्रिकोणमिति का परिचय",
                        "सांख्यिकी",
                        "प्रायिकता"
                    ) else listOf(
                        "Real Numbers",
                        "Polynomials",
                        "Pair of Linear Equations",
                        "Quadratic Equations",
                        "Arithmetic Progressions",
                        "Triangles",
                        "Coordinate Geometry",
                        "Trigonometry",
                        "Statistics",
                        "Probability"
                    )
                }
                "English" -> {
                    listOf(
                        "A Letter to God",
                        "Nelson Mandela: Long Walk to Freedom",
                        "Two Stories about Flying",
                        "From the Diary of Anne Frank",
                        "Glimpses of India",
                        "Madam Rides the Bus",
                        "The Sermon at Benares"
                    )
                }
                "Social Science" -> {
                    if (isHindi) listOf(
                        "यूरोप में राष्ट्रवाद का उदय",
                        "भारत में राष्ट्रवाद",
                        "संसाधन और विकास",
                        "जल संसाधन",
                        "सत्ता की साझेदारी",
                        "संघवाद",
                        "राजनीतिक दल"
                    ) else listOf(
                        "The Rise of Nationalism in Europe",
                        "Nationalism in India",
                        "Resources and Development",
                        "Water Resources",
                        "Power Sharing",
                        "Federalism",
                        "Political Parties"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "नेताजी का चश्मा",
                        "बालगोबिन भगत",
                        "लखनवी अंदाज",
                        "सूरदास के पद",
                        "राम-लक्ष्मण-परशुराम संवाद"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 11" -> when (subject) {
                "Physics" -> {
                    listOf(
                        "Units and Measurements",
                        "Motion in a Straight Line",
                        "Motion in a Plane",
                        "Laws of Motion",
                        "Work, Energy and Power",
                        "Rotational Motion",
                        "Gravitation"
                    )
                }
                "Chemistry" -> {
                    listOf(
                        "Some Basic Concepts of Chemistry",
                        "Structure of Atom",
                        "Classification of Elements",
                        "Chemical Bonding",
                        "Thermodynamics",
                        "Equilibrium"
                    )
                }
                "Biology" -> {
                    listOf(
                        "The Living World",
                        "Biological Classification",
                        "Plant Kingdom",
                        "Animal Kingdom",
                        "Morphology of Flowering Plants"
                    )
                }
                "Mathematics" -> {
                    listOf(
                        "Sets",
                        "Relations & Functions",
                        "Trigonometric Functions",
                        "Complex Numbers",
                        "Permutations & Combinations",
                        "Binomial Theorem",
                        "Sequences & Series"
                    )
                }
                "English" -> {
                    listOf(
                        "The Portrait of a Lady",
                        "We're Not Afraid to Die...",
                        "Discovering Tut",
                        "The Laburnum Top",
                        "Landscape of the Soul"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "कबीर के पद",
                        "मीरा के पद",
                        "घर की याद",
                        "चंपा काले काले अक्षर",
                        "ग़ज़ल"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            "Class 12" -> when (subject) {
                "Physics" -> {
                    listOf(
                        "Electric Charges & Fields",
                        "Electrostatic Potential & Capacitance",
                        "Current Electricity",
                        "Moving Charges & Magnetism",
                        "Magnetism & Matter",
                        "Electromagnetic Induction",
                        "Alternating Current",
                        "Ray Optics & Optical Instruments"
                    )
                }
                "Chemistry" -> {
                    listOf(
                        "Solutions",
                        "Electrochemistry",
                        "Chemical Kinetics",
                        "The d- and f- Block Elements",
                        "Coordination Compounds",
                        "Haloalkanes and Haloarenes",
                        "Alcohols, Phenols and Ethers"
                    )
                }
                "Biology" -> {
                    listOf(
                        "Sexual Reproduction in Flowering Plants",
                        "Human Reproduction",
                        "Reproductive Health",
                        "Principles of Inheritance & Variation",
                        "Molecular Basis of Inheritance"
                    )
                }
                "Mathematics" -> {
                    listOf(
                        "Relations & Functions",
                        "Inverse Trigonometric Functions",
                        "Matrices",
                        "Determinants",
                        "Continuity & Differentiability",
                        "Application of Derivatives",
                        "Integrals"
                    )
                }
                "English" -> {
                    listOf(
                        "The Last Lesson",
                        "Lost Spring",
                        "Deep Water",
                        "The Rattrap",
                        "Indigo"
                    )
                }
                "Hindi" -> {
                    listOf(
                        "आत्मपरिचय / एक गीत",
                        "पतंग",
                        "कविता के बहाने",
                        "भक्तिन",
                        "बाजार दर्शन"
                    )
                }
                else -> {
                    if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
                }
            }

            else -> {
                if (isHindi) listOf("$subject परिचय अध्याय") else listOf("$subject Introduction Chapter")
            }
        }
    }
}
