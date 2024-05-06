package game;
import entity.*;

import javax.swing.*;
import java.util.Random;

//Todo: 아이템 구현
public class Game {
    private Player player;
    private Player dealer;
    private Shotgun shotgun;
    private int now = 0;
    private IGameListener listener; // UI 업데이트를 위한 리스너 인터페이스
    private Timer actionTimer;
    private int actionStep = 0;

    public Game(IGameListener listener) {
        Random random = new Random();
        int startLife = random.nextInt(7) + 2;
        this.player = new Player(startLife, new String[]{"내 아이템 리스트"}, true);
        this.dealer = new Player(startLife, new String[]{"딜러 아이템 리스트"}, false);
        this.shotgun = new Shotgun();
        this.listener = listener;
    }

    private boolean checkBullets() {
        if (shotgun.getRealBullets() == 0) {
            return false;
        }
        return true;
    }

    public void reloadBullets() {
        if (checkBullets()) {
            if (listener != null) {
                listener.showReloadWarning("실탄이 남아있습니다. 모두 소진하고 재장전해주세요.");
            }
        } else {
            now = 0;
            shotgun.resetBullets();
            String message = "[재장전] 총알 소진!! 총알을 다시 채우겠습니다...";
            if (listener != null) {
                listener.zeroBullet(message, shotgun.getRealBullets(), shotgun.getBlankBullets());
            }
        }
    }

    public void fireToUser() {
        if (!checkBullets()) { // 총알을 확인하고 총알이 없으면 리로드 요청
            if (listener != null) {
                listener.showReloadWarning("실탄이 부족합니다. 재장전 해야 합니다!");
            }
            return; // 메소드 종료
        }
        int[] bullets = shotgun.getBullets();
        boolean getShot = player.fireToMe(bullets[now]);
        now ++;

        if (listener != null) {
            String message;
            if (getShot) {
                shotgun.setRealBullets(shotgun.getRealBullets() - 1);
                message = "[진행]: 탕!! 당신의 체력이 깎였습니다. => 내 남은 체력: " + player.getLife();
            } else {
                shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
                message = "[진행]: 틱! 휴~~ 공포탄이었다... => 내 남은 체력: " + player.getLife();
            }
            listener.onPlayerLivesUpdated(player.getLife(), message);
            listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());

            checkGameEnd();
        }
    }

    public void fireToDealer() {
        if (!checkBullets()) { // 총알을 확인하고 총알이 없으면 리로드 요청
            if (listener != null) {
                listener.showReloadWarning("실탄이 부족합니다. 재장전 해야 합니다!");
            }
            return; // 메소드 종료
        }

        if (player.isMyTurn()) {
            boolean getShot = player.fireToEnemy(shotgun.getBullets()[now], dealer);
            now++;
            if (listener != null) {
                String message;
                if (getShot) {
                    shotgun.setRealBullets(shotgun.getRealBullets() - 1);
                    message = "[진행]: 탕!! 딜러의 체력이 깎였습니다. => 남은 딜러 체력: " + dealer.getLife();
                } else {
                    shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
                    message = "[진행]: 틱! 오마깟~~ 공포탄이었다... => 남은 딜러 체력: " + dealer.getLife();
                }
                player.changeTurn();
                listener.onDealerLivesUpdated(dealer.getLife(), message);
                listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
                listener.updatePlayerTurn(false);
            }
            checkGameEnd();
            startDealerAction();
        }
    }

    private void startDealerAction() {
        actionStep = 0; // 로그 인덱스 초기화
        actionTimer = new Timer(1000, e -> {
            switch (actionStep) {
                case 0:
                    listener.addLog("\t\t\t[딜러의 턴]");
                    actionStep++;
                    break;
                case 1:
                    dealerCheckBullet();
                    actionStep++;
                    break;
                case 2:
                    if (shotgun.getRealBullets() >= shotgun.getBlankBullets()) {
                        performActionAgainstPlayer();
                        actionStep = 3; // 플레이어에게 총을 쏘고 턴 종료
                    } else {
                        if (shotgun.getRealBullets() == 0) {
                            dealerCheckBullet();
                            actionStep = 2;
                        }
                        performActionAgainstSelf();
                        // 스스로에게 총을 쏘면 딜러의 턴이 한 번 더 진행됨
                        actionStep = 2; // 다시 동일한 스텝을 수행하도록 설정
                    }
                    break;
                case 3:
                    actionTimer.stop(); // 모든 행동이 끝나면 타이머 중지
                    listener.updatePlayerTurn(true); // 플레이어의 턴으로 전환
                    listener.addLog("\t\t\t[나의 턴]");
                    break;
            }
        });
        actionTimer.start();
    }

    private void performActionAgainstPlayer() {
        int[] bullets = shotgun.getBullets();
        int bullet = bullets[now];
        now++;

        boolean getShot = dealer.fireToEnemy(bullet, player);
        String message;
        if (getShot) {
            shotgun.setRealBullets(shotgun.getRealBullets() - 1);
            message = "[진행]: 딜러가 당신에게 총을 쏩니다!\n[진행]: 탕!! 당신의 체력이 깎였습니다. => 남은 당신의 체력: " + player.getLife();
        } else {
            shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
            message = "[진행]: 딜러가 당신에게 총을 쏩니다!\n[진행]: 틱! 공포탄이었다... => 남은 당신의 체력: " + player.getLife();
        }
        player.changeTurn();
        if (listener != null) {
            listener.onPlayerLivesUpdated(player.getLife(), message);
            listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
            listener.updatePlayerTurn(player.isMyTurn());
        }
        checkGameEnd();
    }

    private void performActionAgainstSelf() {
        int[] bullets = shotgun.getBullets();
        int bullet = bullets[now];
        now++;

        boolean getShot = dealer.fireToMe(bullet);
        String message;
        if (getShot) {
            shotgun.setRealBullets(shotgun.getRealBullets() - 1);
            message = "[진행]: 딜러가 스스로에게 총을 쏩니다!\n[진행]: 탕!! 딜러의 체력이 깎였습니다. => 남은 딜러의 체력: " + dealer.getLife();
        } else {
            shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
            message = "[진행]: 딜러가 스스로에게 총을 쏩니다!\n[진행]: 틱! 공포탄이었다... => 남은 딜러의 체력: " + dealer.getLife();
        }
        if (listener != null) {
            listener.onDealerLivesUpdated(dealer.getLife(), message);
            listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
            checkGameEnd();
            listener.addLog("\t\t[딜러의 턴이 다시 진행됩니다]");
        }
    }

    private void dealerCheckBullet() {
        if (!checkBullets()) { // 총알을 확인하고 총알이 없으면 리로드 요청
            shotgun.resetBullets();
            now = 0;
            if (listener != null) {
                listener.addLog("[딜러]: 실탄을 다 썼으니 내가 재장전 하도록 하지...");
                listener.addLog("[진행]: 실탄 " + shotgun.getRealBullets() + "발, 공포탄 " + shotgun.getBlankBullets() + "발");
                listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
            }
        } else {
            if (listener != null) {
                listener.addLog("[딜러]: 어떤 행동을 할까...");
            }
        }
    }

    public void checkGameEnd() {
        int playerLife = player.getLife();
        int dealerLife = dealer.getLife();
        if (playerLife <= 0 || dealerLife <= 0) {
            String message = playerLife <= 0 ? "패배했습니다! ㅠㅡㅠ 다음에는 승리해봅시다~~!" : "승리!! 축하합니다~! 딜러를 물리쳤습니다~~";
            stopDealerAction();
            listener.gameEnded(message);
        }
    }

    private void stopDealerAction() {
        if (actionTimer != null) {
            actionTimer.stop();  // 타이머를 중지합니다.
            actionTimer = null;  // 타이머 참조를 제거합니다.
        }
    }

    public void restart() {
        stopDealerAction();
        Random random = new Random();
        int startLife = random.nextInt(7) + 2;
        now = 0;
        player.setInit(startLife, new String[]{"내 아이템 리스트"}, true);
        dealer.setInit(startLife, new String[]{"딜러 아이템 리스트"}, false);
        shotgun.resetBullets();

        listener.updatePlayerTurn(true);
    }

    public int getPlayerLife() {
        return player.getLife();
    }

    public int getComLife() {
        return dealer.getLife();
    }

    public String[] getPlayerItems() {
        return player.getItemList();
    }

    public String[] getComItems() {
        return dealer.getItemList();
    }

    public int getRealBullet() {
        return shotgun.getRealBullets();
    }

    public int getBlankBullet() {
        return shotgun.getBlankBullets();
    }

    // UI 업데이트를 위한 리스너 인터페이스 정의
    public interface IGameListener {
        void onPlayerLivesUpdated(int lives, String message);
        void onDealerLivesUpdated(int lives, String message);
        void bulletsUpdated(int realBullet, int blankBullet);
        void zeroBullet(String message, int realBullet, int blankBullet);
        void showReloadWarning(String message);
        void updatePlayerTurn(boolean isMyTurn);
        void addLog(String message);
        void gameEnded(String message);
    }
}
