import 'package:flutter/material.dart';

class AppTheme {
  // Islamic green color scheme
  static const Color primaryGreen = Color(0xFF2D5A27);
  static const Color lightGreen = Color(0xFF4A7C59);
  static const Color accentGreen = Color(0xFF4CAF50);
  
  static ThemeData get lightTheme {
    return ThemeData(
      primarySwatch: MaterialColor(0xFF2D5A27, {
        50: Color(0xFFE8F5E8),
        100: Color(0xFFC8E6C9),
        200: Color(0xFFA5D6A7),
        300: Color(0xFF81C784),
        400: Color(0xFF66BB6A),
        500: primaryGreen,
        600: Color(0xFF43A047),
        700: Color(0xFF388E3C),
        800: Color(0xFF2E7D32),
        900: Color(0xFF1B5E20),
      }),
      scaffoldBackgroundColor: Color(0xFFF8FFFE),
      appBarTheme: AppBarTheme(
        backgroundColor: primaryGreen,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      cardTheme: CardTheme(
        elevation: 4,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(15),
        ),
      ),
      elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: primaryGreen,
          foregroundColor: Colors.white,
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
          padding: EdgeInsets.symmetric(horizontal: 24, vertical: 12),
        ),
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData.dark().copyWith(
      primaryColor: primaryGreen,
      scaffoldBackgroundColor: Color(0xFF121212),
      appBarTheme: AppBarTheme(
        backgroundColor: primaryGreen,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      cardTheme: CardTheme(
        color: Color(0xFF1E1E1E),
        elevation: 4,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(15),
        ),
      ),
    );
  }

  // Gradient for glass cards
  static LinearGradient get glassGradient {
    return LinearGradient(
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
      colors: [primaryGreen, lightGreen],
    );
  }

  // Text styles
  static TextStyle get clockTextStyle {
    return TextStyle(
      fontSize: 48,
      fontWeight: FontWeight.w100,
      color: Colors.white,
    );
  }

  static TextStyle get prayerNameStyle {
    return TextStyle(
      fontSize: 16,
      fontWeight: FontWeight.w600,
    );
  }

  static TextStyle get prayerTimeStyle {
    return TextStyle(
      fontSize: 18,
      fontWeight: FontWeight.bold,
    );
  }
}