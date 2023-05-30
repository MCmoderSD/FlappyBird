import javax.swing.*;

public class UI extends JFrame{
    public static UI instance;
    private JButton bStart;
    private JPanel UI;
    private JCheckBox soundCheckBox;
    private JTable leaderBoard;
    private JTextField playerName;
    private JLabel score;
    private int scoredPoints = -10;
    private boolean newGame = true, isUploaded = true;

    public UI() {
        instance = this;
        initFrame(scoredPoints);
        score.setVisible(false);
        playerName.setVisible(false);
        leaderBoard.setVisible(false);
        soundCheckBox.setSelected(true);
        bStart.addActionListener(e -> {
            if (newGame) {
                play();
            } else if (scoredPoints >= 0 && !isUploaded && !newGame) {
                upload();
            } else if (scoredPoints >= 0 && isUploaded && newGame) play();
        });
    }

    public static void main(String[] args) {
        new UI();
    }

    private void play() {
        new Main().run(getSound());
        dispose();
    }

    public void initFrame(int points) {
        scoredPoints = points;
        add(UI);
        setTitle("Flappy Bird");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
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

    private void LeaderBoard() {
        setVisible(true);
        Timer refreshLeaderBoard = new Timer(1000, e -> {

        });
        refreshLeaderBoard.start();
    }
}
