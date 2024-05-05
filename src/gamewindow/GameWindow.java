package gamewindow;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import game.Game;

public class GameWindow extends JFrame implements Game.IGameListener {
    private JLabel dealerLivesLabel;
    private JLabel playerLivesLabel;
    private JButton fireAtSelfButton;
    private JButton fireAtDealerButton;
    private JButton reloadBulletsButton;
    private JTextArea actionLog;
    private JLabel ammoCountLabel;
    private JLabel refillPromptLabel;
    private JScrollPane scrollPane;

    private Game game;

    public GameWindow() {
        game = new Game(this);
        createUI();
    }

    private void createUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("벅샷 룰렛 게임");
        setSize(1000, 700);
        setLayout(new BorderLayout(5, 5));  // 간격 추가

        // 상단 패널: 딜러의 목숨과 아이템 표시
        JPanel topPanel = new JPanel(new BorderLayout());
        dealerLivesLabel = new JLabel("딜러 목숨: " + game.getComLife(), SwingConstants.CENTER);
        JLabel dealerItems = new JLabel("아이템: " + Arrays.toString(game.getComItems()), SwingConstants.CENTER);
        topPanel.add(dealerLivesLabel, BorderLayout.NORTH);
        topPanel.add(dealerItems, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // 중간 패널: 액션 로그와 탄약 수, 버튼
        JPanel middlePanel = new JPanel(new GridLayout(1, 2)); // 2개의 컬럼을 가진 그리드 레이아웃

        Font font = new Font("monaco", Font.PLAIN, 12);
        actionLog = new JTextArea("벅샷 룰렛에 오신걸 환영합니다!!\n당신의 선공으로 게임을 시작합니다!\n");
        actionLog.setEditable(false);  // 텍스트 에디트 불가능 설정
        actionLog.setFont(font);
        scrollPane = new JScrollPane(actionLog);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        middlePanel.add(scrollPane); // 왼쪽 패널: 로그창 추가

        // 오른쪽 패널: 총알 정보, 안내 문구, 버튼
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS)); // 수직 박스 레이아웃

        Font ammoCountFont = new Font("monaco", Font.PLAIN, 16);
        ammoCountLabel = new JLabel("실탄: " + game.getRealBullet() + ", 공포탄: " + game.getBlankBullet(), SwingConstants.CENTER);
        ammoCountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        ammoCountLabel.setFont(ammoCountFont);
        rightPanel.add(ammoCountLabel);

        refillPromptLabel = new JLabel("남은 실탄이 0이면 누르세요.", SwingConstants.CENTER);
        refillPromptLabel.setForeground(Color.BLUE);
        refillPromptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        rightPanel.add(refillPromptLabel);

        reloadBulletsButton = new JButton("총알 리필하기");
        reloadBulletsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        reloadBulletsButton.addActionListener(e -> game.reloadBullets());
        rightPanel.add(reloadBulletsButton);

        middlePanel.add(rightPanel); // 오른쪽 패널 추가

        add(middlePanel, BorderLayout.CENTER);

        // 하단 패널: 플레이어의 목숨과 아이템, 발사 버튼
        JPanel bottomPanel = new JPanel(new BorderLayout());
        playerLivesLabel = new JLabel("내 체력: " + game.getPlayerLife(), SwingConstants.CENTER);
        JLabel playerItems = new JLabel("아이템 목록: " + Arrays.toString(game.getPlayerItems()), SwingConstants.CENTER);
        JPanel buttonPanel = new JPanel();
        fireAtSelfButton = new JButton("나에게 쏘기");
        fireAtDealerButton = new JButton("딜러에게 쏘기");
        fireAtSelfButton.addActionListener(e -> {
            game.fireToUser();
        });
        fireAtDealerButton.addActionListener(e -> {
            game.fireToCom();
        });
        buttonPanel.add(fireAtSelfButton);
        buttonPanel.add(fireAtDealerButton);
        bottomPanel.add(playerLivesLabel, BorderLayout.NORTH);
        bottomPanel.add(playerItems, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void appendActionLog(String message) {
        actionLog.append("[진행]: " + message + "\n");
        actionLog.setCaretPosition(actionLog.getDocument().getLength()); // 스크롤을 로그의 끝으로 이동
    }

    @Override
    public void onPlayerLivesUpdated(int lives, String message) {
        playerLivesLabel.setText("내 체력: " + lives);
        appendActionLog(message);
    }

    @Override
    public void bulletsUpdated(int realBullet, int blankBullet) {
        ammoCountLabel.setText("실탄: " + game.getRealBullet() + ", 공포탄: " + game.getBlankBullet());
    }

    @Override
    public void zeroBullet(String message, int realBullet, int blankBullet) {
        ammoCountLabel.setText("실탄: " + game.getRealBullet() + ", 공포탄: " + game.getBlankBullet());
        appendActionLog(message);
    }

    @Override
    public void showReloadWarning(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    @Override
    public void updatePlayerTurn(boolean isMyTurn) {
        fireAtSelfButton.setEnabled(isMyTurn);
        fireAtDealerButton.setEnabled(isMyTurn);
        if (!isMyTurn) {
            // 컴퓨터의 턴이 진행되는 로직을 여기에 구현
        }
    }

    @Override
    public void onEnemyLivesUpdated(int lives, String message) {
        dealerLivesLabel.setText("딜러 체력: " + lives);
        appendActionLog(message);
    }
}
