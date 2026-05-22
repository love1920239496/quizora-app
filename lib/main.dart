import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:google_fonts/google_fonts.dart';
import 'components/quizora_components.dart';
import 'data/database_helper.dart';
import 'screens/splash_screen.dart';
import 'screens/language_select_screen.dart';
import 'screens/guest_home_screen.dart';

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  
  // Lock orientation to portrait
  await SystemChrome.setPreferredOrientations([
    DeviceOrientation.portraitUp,
    DeviceOrientation.portraitDown,
  ]);

  // Set system navigation bar & status bar transparent
  SystemChrome.setSystemUIOverlayStyle(const SystemUiOverlayStyle(
    statusBarColor: Colors.transparent,
    statusBarIconBrightness: Brightness.light,
    systemNavigationBarColor: Colors.transparent,
    systemNavigationBarIconBrightness: Brightness.light,
  ));

  runApp(const QuizoraApp());
}

class QuizoraApp extends StatelessWidget {
  const QuizoraApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Quizora',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        brightness: Brightness.dark,
        primaryColor: neonBlue,
        scaffoldBackgroundColor: spaceBgStart,
        textTheme: GoogleFonts.spaceGroteskTextTheme(
          ThemeData.dark().textTheme,
        ),
      ),
      home: const MainRouter(),
    );
  }
}

class MainRouter extends StatefulWidget {
  const MainRouter({Key? key}) : super(key: key);

  @override
  _MainRouterState createState() => _MainRouterState();
}

class _MainRouterState extends State<MainRouter> {
  bool _isSplashVisible = true;
  bool _isLanguageLoaded = false;
  bool _needsLanguageSelect = true;

  @override
  void initState() {
    super.initState();
    _checkLanguageSetting();
  }

  Future<void> _checkLanguageSetting() async {
    final progress = await DatabaseHelper.instance.getProgress();
    setState(() {
      _needsLanguageSelect = progress.selectedLanguage.isEmpty;
      _isLanguageLoaded = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    if (_isSplashVisible) {
      return SplashScreen(
        onSplashFinished: () {
          setState(() {
            _isSplashVisible = false;
          });
        },
      );
    }

    if (!_isLanguageLoaded) {
      return const Scaffold(
        backgroundColor: spaceBgStart,
        body: Center(
          child: CircularProgressIndicator(color: neonBlue),
        ),
      );
    }

    if (_needsLanguageSelect) {
      return LanguageSelectScreen(
        onLanguageSelected: () {
          setState(() {
            _needsLanguageSelect = false;
          });
        },
      );
    }

    return GuestHomeScreen(
      onSwitchLanguageRequested: () {
        setState(() {
          _needsLanguageSelect = true;
        });
      },
    );
  }
}
