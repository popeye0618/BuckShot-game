package entity;

public class Player {
    private int life;
    private String[] itemList;
    private boolean isMyTurn;

    public Player(int life, String[] itemList, boolean isMyTurn) {
        this.life = life;
        this.itemList = itemList;
        this.isMyTurn = isMyTurn;
    }

    public boolean fireToMe(int bullet) {
        boolean result;
        if (bullet == 1) {
            this.decreaseLife(1);
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public boolean fireToEnemy(int bullet, Player enemy) {
        boolean result;
        if (bullet == 1) {
            enemy.decreaseLife(1);
            result = true;
        } else {
            result = false;
        }
        enemy.changeTurn();
        this.changeTurn();
        return result;
    }

    public int getLife() {
        return life;
    }

    public String[] getItemList() {
        return itemList;
    }

    public void decreaseLife(int life) {
        this.life -= life;
    }

    public void changeTurn() {
        isMyTurn = !isMyTurn;
    }

    public boolean isMyTurn() {
        return isMyTurn;
    }
}
