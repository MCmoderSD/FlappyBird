import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;

public class UI extends JFrame {
    public static UI instance;
    private final String Background;
    private final int scoredPoints;
    private JButton bStart;
    private JPanel UI;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private JSpinner spinnerTPS;
    private int TPS = 100;
    private boolean newGame = true, isUploaded = true;

    public UI(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int Tickrate, String[] args, int points) {
        scoredPoints = points;
        Background = backgroundImage;
        instance = this;

        initFrame(width, height, title, icon, resizable, scoredPoints, Tickrate);

        score.setVisible(false);
        playerName.setVisible(false);
        leaderBoard.setVisible(false);
        soundCheckBox.setSelected(true);

        bStart.addActionListener(e -> {
            if (newGame) {
                TPS = (int) spinnerTPS.getValue();
                play(TPS, args);
            } else if (scoredPoints >= 0 && !isUploaded) {
                upload();
            }
        });
    }

    private void play(int Tickrate, String[] args) {
        new Main().run(Tickrate, getSound(), args);
        dispose();
    }

    public void initFrame(int width, int height, String title, String icon, boolean resizable, int points, int Tickrate) {
        if (Tickrate <= TPS)
            TPS = Tickrate;

        add(UI);
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(resizable);
        setIconImage((Methods.instance.reader(icon)));
        Movement.instance.backgroundResetX = 0;
        UI.repaint();

        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            score.setVisible(true);
            playerName.setVisible(true);
            score.setText("Dein Score: " + points);
            bStart.setText("Score Bestätigen");
            bStart.setToolTipText("Lade deinen Score hoch");
        }
    }

    private void upload() {
        bStart.setText("Nochmal Spielen");
        bStart.setToolTipText("Nochmal Spielen");
        isUploaded = true;
        newGame = true;
    }

    private boolean getSound() {
        return soundCheckBox.isSelected();
    }

    private void createUIComponents() {
        UI = new JPanel() {
            @Override
            protected void paintComponent(Graphics gUI) {
                super.paintComponent(gUI);
                gUI.drawImage(Methods.instance.reader(Background), Movement.instance.backgroundResetX, 0, Methods.instance.getBackgroundWidth(), getHeight(), this);
                repaint();
            }
        };

        bStart = new JButton();
        bStart.setOpaque(false);
        bStart.setToolTipText("Starte das Spiel");

        spinnerTPS = new JSpinner();
        spinnerTPS.setValue(TPS);
        spinnerTPS.addChangeListener(e -> {
            if (spinnerTPS.getValue() != null) {
                if ((int) spinnerTPS.getValue() >= 100)
                    spinnerTPS.setValue(100);
                if ((int) spinnerTPS.getValue() <= 1)
                    spinnerTPS.setValue(1);
            }
        });
        JFormattedTextField txt = ((JSpinner.DefaultEditor) spinnerTPS.getEditor()).getTextField();
        ((NumberFormatter) txt.getFormatter()).setAllowsInvalid(false); // Verhindert ungültige Eingaben
    }

// --Commented out by Inspection START (31.05.2023 04:08):
//    private void initLeaderBoard() {
//        setVisible(true);
//        Timer refreshLeaderBoard = new Timer(1000, e -> {
//
//        });
//        refreshLeaderBoard.start();
//    }
// --Commented out by Inspection STOP (31.05.2023 04:08)
}