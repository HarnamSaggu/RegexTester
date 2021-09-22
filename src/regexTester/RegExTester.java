package regexTester;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTester implements ActionListener {
   private static final Color COLOR = new Color(246, 246, 246);
   private static final Font FONT = new Font("Fira Code", Font.PLAIN, 12);

//   public static void main(String[] args) {
//      new RegExTester();
//   }

   public RegExTester() {
      init_components();
   }

   private HintTextField regex;
   private HintEditorPane matchText;
   private HintEditorPane replaceText;
   private JButton testMatch;
   private JButton replace;
   private JLabel lookingAt;
   private JLabel matches;
   private HintEditorPane output;

   private void init_components() {
      JFrame jFrame = new JFrame("RegEx Tester");
      jFrame.setIconImage(new ImageIcon("assets/icon.png").getImage());
      jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      jFrame.setLayout(new BorderLayout(5, 5));

      JPanel mainPanel = new JPanel();
      mainPanel.setLayout(null);
      jFrame.add(mainPanel, BorderLayout.CENTER);

      regex = new HintTextField("RegEx");
      regex.setFont(FONT);
      regex.setBounds(10, 10, 780, 20);
      mainPanel.add(regex);

      matchText = new HintEditorPane("Match text");
      matchText.setFont(FONT);
      JScrollPane s0 = new JScrollPane(matchText);
      s0.setBounds(10, 40, 780, 200);
      mainPanel.add(s0);

      replaceText = new HintEditorPane("Replace text (Optional)");
      replaceText.setFont(FONT);
      JScrollPane s1 = new JScrollPane(replaceText);
      s1.setBounds(10, 250, 780, 80);
      mainPanel.add(s1);

      testMatch = new JButton("Test match");
      testMatch.addActionListener(this);
      testMatch.setFont(FONT);
      testMatch.setBounds(10, 340, 240, 30);
      testMatch.setBackground(COLOR);
      mainPanel.add(testMatch);

      replace = new JButton("Replace first");
      replace.addActionListener(this);
      replace.setFont(FONT);
      replace.setBounds(260, 340, 280, 30);
      replace.setBackground(COLOR);
      mainPanel.add(replace);

      JButton replaceAll = new JButton("Replace all");
      replaceAll.addActionListener(this);
      replaceAll.setFont(FONT);
      replaceAll.setBounds(550, 340, 240, 30);
      replaceAll.setBackground(COLOR);
      mainPanel.add(replaceAll);

      lookingAt = new JLabel("Looking at: ");
      lookingAt.setFont(FONT);
      lookingAt.setBounds(10, 380, 780, 15);
      mainPanel.add(lookingAt);

      matches = new JLabel("Matches: ");
      matches.setFont(FONT);
      matches.setBounds(10, 400, 780, 15);
      mainPanel.add(matches);

      output = new HintEditorPane("Output");
      output.setFont(FONT);
      JScrollPane s2 = new JScrollPane(output);
      s2.setBounds(10, 420, 780, 200);
      mainPanel.add(s2);

      jFrame.setResizable(false);
      jFrame.setSize(new Dimension(815, 670));
      jFrame.setLocationRelativeTo(null);
      jFrame.setVisible(true);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      Pattern pattern = Pattern.compile(regex.getText());
      Matcher matcher = pattern.matcher(matchText.getText());
      lookingAt.setText("Looking at: " + matcher.lookingAt());
      matches.setText("Matches: " + matcher.matches());
      output.setText(matchText.getText());

      if (e.getSource() == testMatch) {
         for (MatchResult match : allMatches(pattern, matchText.getText())) {
            DefaultHighlighter.DefaultHighlightPainter highlightPainter =
                    new DefaultHighlighter.DefaultHighlightPainter(new Color(206, 227, 253));
            try {
               output.getHighlighter().addHighlight(match.start(), match.start() + match.group().length(),
                       highlightPainter);
            } catch (BadLocationException ex) {
               ex.printStackTrace();
            }
         }
      } else if (e.getSource() == replace) {
         output.setText(matchText.getText().replaceFirst(regex.getText(), replaceText.getText()));
      } else {
         output.setText(matchText.getText().replaceAll(regex.getText(), replaceText.getText()));
      }
   }

   private static Iterable<MatchResult> allMatches(final Pattern p, final CharSequence input) {
      return () -> new Iterator<>() {
         final Matcher matcher = p.matcher(input);
         MatchResult pending;

         public boolean hasNext() {
            if (pending == null && matcher.find()) {
               pending = matcher.toMatchResult();
            }
            return pending != null;
         }

         public MatchResult next() {
            if (!hasNext()) {
               throw new NoSuchElementException();
            }
            MatchResult next = pending;
            pending = null;
            return next;
         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   private static class HintTextField extends JTextField {
      private final String HINT;

      public HintTextField(String hint) {
         this.HINT = hint;
      }

      @Override
      public void paint(Graphics g) {
         super.paint(g);
         if (getText().length() == 0) {
            int h = getHeight();
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(HINT, ins.left, h / 2 + fm.getAscent() / 2 - 2);
         }
      }
   }

   private static class HintEditorPane extends JEditorPane {
      private final String HINT;

      public HintEditorPane(String hint) {
         this.HINT = hint;
      }

      @Override
      public void paint(Graphics g) {
         super.paint(g);
         if (getText().length() == 0) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            Insets ins = getInsets();
            FontMetrics fm = g.getFontMetrics();
            int c0 = getBackground().getRGB();
            int c1 = getForeground().getRGB();
            int m = 0xfefefefe;
            int c2 = ((c0 & m) >>> 1) + ((c1 & m) >>> 1);
            g.setColor(new Color(c2, true));
            g.drawString(HINT, ins.left, fm.getHeight());
         }
      }
   }
}
