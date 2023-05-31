import javax.swing.*;

public class UI extends JFrame {
    public static UI instance;
    private JButton bStart;
    private JPanel UI;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private int scoredPoints = -10;
    private boolean newGame = true, isUploaded = true;

    public UI(int width, int height, String title, String icon, boolean resizable, String backgroundImage) {
        instance = this;
        initFrame(width, height, title, icon, resizable, backgroundImage,scoredPoints);
        score.setVisible(false);
        playerName.setVisible(false);
        leaderBoard.setVisible(false);
        soundCheckBox.setSelected(true);
        bStart.addActionListener(e -> {
            if (newGame) {
                play();
            } else if (scoredPoints >= 0 && !isUploaded) {
                upload();
            }
        });
    }

    private void play() {
        new Main().run(getSound());
        dispose();
    }

    public void initFrame(int width, int height, String title, String icon, boolean resizable, String backgroundImage, int points) {
        scoredPoints = points;
        add(UI);
        setTitle(title);
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setResizable(resizable);
        setIconImage((Methods.instance.reader(icon)));
        if (points >= 0) {
            isUploaded = false;
            newGame = false;
            score.setVisible(true);
            playerName.setVisible(true);
            score.setText("Dein Score: " + points);
            bStart.setText("Score Bestätigen");
        }
    }

    private void upload() {
        bStart.setText("Nochmal Spielen");
        isUploaded = true;
        newGame = true;
    }

    private boolean getSound() {
        return soundCheckBox.isSelected();
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