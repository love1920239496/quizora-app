import 'dart:math' as math;
import 'package:flutter/material.dart';

// Universal Theme Colors for space cybernetic UI
const Color spaceBgStart = Color(0xFF0C0720);
const Color spaceBgEnd = Color(0xFF080315);
const Color neonBlue = Color(0xFF06CAFD);
const Color neonPurple = Color(0xFF9E0DFD);
const Color textSecondary = Color(0xB3FFFFFF);
const Color textMuted = Color(0x66FFFFFF);
const Color glassBg = Color(0x1F2C1D54);

class GlowBackground extends StatefulWidget {
  final Widget child;
  const GlowBackground({Key? key, required this.child}) : super(key: key);

  @override
  _GlowBackgroundState createState() => _GlowBackgroundState();
}

class _GlowBackgroundState extends State<GlowBackground> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _pulse;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 4),
    )..repeat(reverse: true);
    _pulse = Tween<double>(begin: 0.3, end: 0.6).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: _pulse,
      builder: (context, child) {
        return Container(
          decoration: const BoxDecoration(
            gradient: LinearGradient(
              begin: Alignment.topCenter,
              end: Alignment.bottomCenter,
              colors: [spaceBgStart, spaceBgEnd],
            ),
          ),
          child: Stack(
            children: [
              // Cyan top-left ambient glow
              Positioned(
                left: -150,
                top: -150,
                child: Container(
                  width: 400,
                  height: 400,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: neonBlue.withOpacity(0.08 * _pulse.value),
                  ),
                ),
              ),
              // Purple bottom-right ambient glow
              Positioned(
                right: -150,
                bottom: -150,
                child: Container(
                  width: 500,
                  height: 500,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: neonPurple.withOpacity(0.12 * _pulse.value),
                  ),
                ),
              ),
              SafeArea(child: widget.child),
            ],
          ),
        );
      },
    );
  }
}

class GlassCard extends StatelessWidget {
  final Widget child;
  final Color borderGlowColor;
  final double borderWidth;
  final EdgeInsetsGeometry? padding;

  const GlassCard({
    Key? key,
    required this.child,
    this.borderGlowColor = neonBlue,
    this.borderWidth = 1.0,
    this.padding,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.symmetric(vertical: 8.0),
      decoration: BoxDecoration(
        color: glassBg,
        borderRadius: BorderRadius.circular(24.0),
        border: Border.all(
          width: borderWidth,
          color: borderGlowColor.withOpacity(0.3),
        ),
      ),
      padding: padding ?? const EdgeInsets.all(20.0),
      child: child,
    );
  }
}

class NeonButton extends StatefulWidget {
  final String text;
  final VoidKey? key;
  final VoidCallback onClick;
  final bool isPrimary;
  final bool enabled;
  final Color glowColor;

  const NeonButton({
    Key? key,
    required this.text,
    required this.onClick,
    this.isPrimary = true,
    this.enabled = true,
    this.glowColor = neonBlue,
  }) : super(key: key);

  @override
  _NeonButtonState createState() => _NeonButtonState();
}

class _NeonButtonState extends State<NeonButton> with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _glow;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 2),
    )..repeat(reverse: true);
    _glow = Tween<double>(begin: 0.4, end: 0.8).animate(
      CurvedAnimation(parent: _controller, curve: Curves.easeInOut),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final shape = RoundedRectangleBorder(borderRadius: BorderRadius.circular(16.0));

    return AnimatedBuilder(
      animation: _glow,
      builder: (context, child) {
        return Opacity(
          opacity: widget.enabled ? 1.0 : 0.5,
          child: Container(
            height: 52,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(16.0),
              boxShadow: widget.isPrimary && widget.enabled
                  ? [
                      BoxShadow(
                        color: widget.glowColor.withOpacity(0.15 * _glow.value),
                        blurRadius: 10,
                        spreadRadius: 2,
                      )
                    ]
                  : [],
            ),
            child: Material(
              color: widget.isPrimary ? widget.glowColor.withOpacity(0.18) : Colors.transparent,
              shape: shape,
              child: InkWell(
                borderRadius: BorderRadius.circular(16.0),
                onTap: widget.enabled ? widget.onClick : null,
                child: Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  decoration: BoxDecoration(
                    borderRadius: BorderRadius.circular(16.0),
                    border: Border.all(
                      width: widget.isPrimary ? 1.5 : 1.0,
                      color: widget.isPrimary
                          ? widget.glowColor.withOpacity(_glow.value)
                          : textMuted.withOpacity(0.2),
                    ),
                  ),
                  alignment: Alignment.center,
                  child: Text(
                    widget.text,
                    style: TextStyle(
                      color: widget.isPrimary ? Colors.white : textSecondary,
                      fontSize: 16.0,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 0.5,
                    ),
                  ),
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}

class StatusShield extends StatelessWidget {
  final IconData icon;
  final String label;
  final String value;
  final Color glowColor;
  final VoidCallback? onClick;

  const StatusShield({
    Key? key,
    required this.icon,
    required this.label,
    required this.value,
    this.glowColor = neonBlue,
    this.onClick,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onClick,
      borderRadius: BorderRadius.circular(14.0),
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12.0, vertical: 8.0),
        decoration: BoxDecoration(
          color: glassBg,
          borderRadius: BorderRadius.circular(14.0),
          border: Border.all(
            color: glowColor.withOpacity(0.25),
            width: 0.8,
          ),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, color: glowColor, size: 18.0),
            const SizedBox(width: 8.0),
            Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  label,
                  style: const TextStyle(
                    color: textSecondary,
                    fontSize: 9.0,
                    fontWeight: FontWeight.w500,
                  ),
                ),
                Text(
                  value,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 12.0,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class CircularGlowProgress extends StatelessWidget {
  final double percentage;
  final String title;
  final String scoreText;
  final Color glowColor;

  const CircularGlowProgress({
    Key? key,
    required this.percentage,
    required this.title,
    required this.scoreText,
    this.glowColor = neonBlue,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        SizedBox(
          width: 120,
          height: 120,
          child: Stack(
            alignment: Alignment.center,
            children: [
              CustomPaint(
                size: const Size(120, 120),
                painter: _CircularProgressPainter(
                  percentage: percentage,
                  color: glowColor,
                ),
              ),
              Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(
                    scoreText,
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 22.0,
                      fontWeight: FontWeight.black,
                    ),
                  ),
                  Text(
                    "${(percentage * 100).toInt()}%",
                    style: const TextStyle(
                      color: textSecondary,
                      fontSize: 11.0,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                ],
              ),
            ],
          ),
        ),
        const SizedBox(height: 12.0),
        Text(
          title,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 14.0,
            fontWeight: FontWeight.bold,
          ),
        ),
      ],
    );
  }
}

class _CircularProgressPainter extends CustomPainter {
  final double percentage;
  final Color color;

  _CircularProgressPainter({required this.percentage, required this.color});

  @override
  void paint(Canvas canvas, Size size) {
    final center = Offset(size.width / 2, size.height / 2);
    final radius = size.width / 2 - 6;

    final bgPaint = Paint()
      ..color = const Color(0x306C688D)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 10
      ..strokeCap = StrokeCap.round;

    final shadowPaint = Paint()
      ..color = color.withOpacity(0.25)
      ..style = PaintingStyle.stroke
      ..strokeWidth = 12
      ..strokeCap = StrokeCap.round;

    final strokePaint = Paint()
      ..color = color
      ..style = PaintingStyle.stroke
      ..strokeWidth = 10
      ..strokeCap = StrokeCap.round;

    // Draw background track
    canvas.drawCircle(center, radius, bgPaint);

    // Draw outer neon blur arc
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -math.pi / 2,
      2 * math.pi * percentage,
      false,
      shadowPaint,
    );

    // Draw active colored arc
    canvas.drawArc(
      Rect.fromCircle(center: center, radius: radius),
      -math.pi / 2,
      2 * math.pi * percentage,
      false,
      strokePaint,
    );
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}
