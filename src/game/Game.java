package game;
import entity.*;

//Todo: 턴 개념 구현(로그에 턴 시작 시 누구의 턴인지, 턴마다 앞에 [누구 턴] 이렇게 보여주면 좋을듯),
// 딜러 동작 구현, 승패 구현, 로그 초기화 구현, 아이템 구현
public class Game {
    private Player player;
    private Player dealer;
    private Shotgun shotgun;
    private int now = 0;
    private IGameListener listener; // UI 업데이트를 위한 리스너 인터페이스

    public Game(IGameListener listener) {
        this.player = new Player(8, new String[]{"내 아이템 리스트"}, true);
        this.dealer = new Player(8, new String[]{"딜러 아이템 리스트"}, false);
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
        if (listener != null) {
            String message;
            if (getShot) {
                shotgun.setRealBullets(shotgun.getRealBullets() - 1);
                message = "펑~! 당신의 체력이 깎였습니다. => 내 남은 체력: " + player.getLife();
            } else {
                shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
                message = "휴~~ 공포탄이었다.. => 내 남은 체력: " + player.getLife();
            }
            listener.onPlayerLivesUpdated(player.getLife(), message);
            listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
        }
        now ++;
    }

    public void fireToCom() {
        if (!checkBullets()) { // 총알을 확인하고 총알이 없으면 리로드 요청
            if (listener != null) {
                listener.showReloadWarning("실탄이 부족합니다. 재장전 해야 합니다!");
            }
            return; // 메소드 종료
        }
        int[] bullets = shotgun.getBullets();
        boolean getShot = player.fireToEnemy(bullets[now], dealer);
        if (listener != null) {
            String message;
            if (getShot) {
                shotgun.setRealBullets(shotgun.getRealBullets() - 1);
                message = "탕~! 딜러의 체력이 깎였습니다. => 남은 딜러 체력: " + dealer.getLife();
            } else {
                shotgun.setBlankBullets(shotgun.getBlankBullets() - 1);
                message = "오마깟~~ 공포탄이었다.. => 남은 딜러 체력: " + dealer.getLife();
            }
            listener.onEnemyLivesUpdated(dealer.getLife(), message);
            listener.bulletsUpdated(shotgun.getRealBullets(), shotgun.getBlankBullets());
        }
        now ++;
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
        void onEnemyLivesUpdated(int lives, String message);
        void bulletsUpdated(int realBullet, int blankBullet);
        void zeroBullet(String message, int realBullet, int blankBullet);
        void showReloadWarning(String message);
        void updatePlayerTurn(boolean isMyTurn);
    }
}
