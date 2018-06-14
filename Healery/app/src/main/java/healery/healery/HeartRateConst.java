package healery.healery;

public class HeartRateConst {
    // age
    private static final int EIGHTTEEN_TO_TWENTYFIVE = 25;
    private static final int TWENTYSIX_TO_THIRTYFIVE = 35;
    private static final int THRITYSIX_TO_FOURTYFIVE = 45;
    private static final int FOURTYSIX_TO_FIFTYFIVE = 55;
    private static final int FIFTYSIX_TO_SIXTYFIVE = 65;

    public static final int[] getHeartRate(String gender, int age) {
        int[] result = new int[3];
        if(gender.equals("male")) {
            if(age < EIGHTTEEN_TO_TWENTYFIVE) result = new int[]{65, 73,  82};
            else if(age < TWENTYSIX_TO_THIRTYFIVE) result = new int[]{65, 74, 82};
            else if(age < THRITYSIX_TO_FOURTYFIVE) result = new int[]{66, 75, 83};
            else if(age < FOURTYSIX_TO_FIFTYFIVE) result = new int[]{67, 76, 84};
            else if(age < FIFTYSIX_TO_SIXTYFIVE) result = new int[]{67, 75, 82};
            else  result = new int[]{65, 73, 80};
        } else if(gender.equals("female")) {
            if(age < EIGHTTEEN_TO_TWENTYFIVE) result = new int[]{69, 78,  85};
            else if(age < TWENTYSIX_TO_THIRTYFIVE) result = new int[]{68, 76, 83};
            else if(age < THRITYSIX_TO_FOURTYFIVE) result = new int[]{69, 78, 85};
            else if(age < FOURTYSIX_TO_FIFTYFIVE) result = new int[]{69, 77, 84};
            else if(age < FIFTYSIX_TO_SIXTYFIVE) result = new int[]{68, 76, 84};
            else  result = new int[]{68, 76, 85};
        }
        return result;
    }
}
