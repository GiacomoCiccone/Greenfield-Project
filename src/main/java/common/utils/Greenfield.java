package common.utils;

public class Greenfield {

    public static final int DISTRICT_SIZE = 5;

    public static Position getRandomPositionInsideDistrict(int district) {
        int x = 0, y = 0;
        switch (district) {
            case 1:
                x = (int) (Math.random() * DISTRICT_SIZE);
                y = (int) (Math.random() * DISTRICT_SIZE);
                break;
            case 2:
                x = DISTRICT_SIZE + (int) (Math.random() * DISTRICT_SIZE);
                y = (int) (Math.random() * DISTRICT_SIZE);
                break;
            case 3:
                x = DISTRICT_SIZE + (int) (Math.random() * DISTRICT_SIZE);
                y = DISTRICT_SIZE + (int) (Math.random() * DISTRICT_SIZE);
                break;
            case 4:
                x = (int) (Math.random() * DISTRICT_SIZE);
                y = DISTRICT_SIZE + (int) (Math.random() * DISTRICT_SIZE);
                break;
            default:
                throw new IllegalArgumentException("Invalid district number");
        }

        return new Position(x, y);
    }

    public static int getDistrictFromPosition(Position position) {
        if (position.getX() < 0 || position.getX() >= DISTRICT_SIZE * 2
                || position.getY() < 0 || position.getY() >= DISTRICT_SIZE * 2) {
            throw new IllegalArgumentException("Invalid position");
        }

        if (position.getX() < DISTRICT_SIZE) {
            if (position.getY() < DISTRICT_SIZE) {
                return 1;
            } else {
                return 4;
            }
        } else {
            if (position.getY() < DISTRICT_SIZE) {
                return 2;
            } else {
                return 3;
            }
        }
    }
}
