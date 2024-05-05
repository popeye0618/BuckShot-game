package entity;

import java.util.Random;

public class Shotgun {
    private int[] bullets;
    private int realBullets = 1;
    private int blankBullets = 1;

    public Shotgun() {
        resetBullets();
    }

    public void resetBullets() {
        realBullets = 1;
        blankBullets = 1;
        Random random = new Random();
        int arrayLength = random.nextInt(9) + 2; // 최소 2개에서 최대 10개의 길이
        bullets = new int[arrayLength];

        bullets[0] = 0;
        bullets[1] = 1;

        for (int i = 2; i < bullets.length; i++) {
            bullets[i] = random.nextInt(2);
            if (bullets[i] == 0) {
                blankBullets += 1;
            } else {
                realBullets += 1;
            }
        }

        // 배열을 섞음
        shuffleArray(bullets);
    }

    private void shuffleArray(int[] array) {
        Random rand = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            int index = rand.nextInt(i + 1);
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
    }

    public int[] getBullets() {
        return bullets;
    }

    public int getRealBullets() {
        return realBullets;
    }

    public int getBlankBullets() {
        return blankBullets;
    }

    public void setRealBullets(int realBullets) {
        this.realBullets = realBullets;
    }

    public void setBlankBullets(int blankBullets) {
        this.blankBullets = blankBullets;
    }
}
