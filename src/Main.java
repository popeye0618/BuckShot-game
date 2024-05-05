import gamewindow.GameWindow;

public class Main {
    public static void main(String[] args) {
        // 안전하게 UI를 생성하기 위해 Event Dispatch Thread를 사용
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GameWindow(); // GameWindow 인스턴스 생성
            }
        });
    }
}
