package healery.gadgetbridge.healery;

import android.app.Application;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.Random;
import healery.gadgetbridge.R;

public class DetailString extends Application {
    static int n = 13;
    static int defaultWeight = 50;
    static String categoryList[] = {
            "영상 시청","운동","게임","명상","산책",
            "음악 감상","카페","영화","음식","쇼핑",
            "수면", "노래방","커뮤니케이션"};
    String detailActivityList[][] = new String[n][];
    private int weight[] = new int[n];
    private int defaultRandomN = 3;
    private String pref; //pref는 category랑 동일
    static int detailActivityDrawableId[][] = new int[][] {
            {R.drawable.activity_0_0, R.drawable.activity_0_1, R.drawable.activity_0_2},
            {R.drawable.activity_1_0, R.drawable.activity_1_1, R.drawable.activity_1_2, R.drawable.activity_1_3},
            {R.drawable.activity_2_0, R.drawable.activity_2_1, R.drawable.activity_2_2},
            {R.drawable.activity_3_0, R.drawable.activity_3_1, R.drawable.activity_3_2, R.drawable.activity_3_3},
            {R.drawable.activity_4_0, R.drawable.activity_4_1, R.drawable.activity_4_2},
            {R.drawable.activity_5_0, R.drawable.activity_5_1, R.drawable.activity_5_2},
            {R.drawable.activity_6_0, R.drawable.activity_6_1, R.drawable.activity_6_2},
            {R.drawable.activity_7_0, R.drawable.activity_7_1, R.drawable.activity_7_2},
            {R.drawable.activity_8_0, R.drawable.activity_8_1, R.drawable.activity_8_2},
            {R.drawable.activity_9_0, R.drawable.activity_9_1, R.drawable.activity_9_2},
            {R.drawable.activity_10_0, R.drawable.activity_10_1, R.drawable.activity_10_2},
            {R.drawable.activity_11_0, R.drawable.activity_11_1, R.drawable.activity_11_2},
            {R.drawable.activity_12_0, R.drawable.activity_12_1, R.drawable.activity_12_2},
    };
    public DetailString(SharedPreferences setting){
        detailActivityList[0] = new String[] {//영상 시청
                "유튜브 인기 영상 “비긴어게인2 박정현_Someone like you”를 시청해보세요.",
                "인기 예능 \"라디오 스타\"를 시청해보세요.",
                "인기 드라마 “미스 함무라비”를 시청해보세요."};
        detailActivityList[1] = new String[] { //운동
                "카이스트 스포츠 컴플렉스에서 배드민턴을 해보세요.",
                "카이스트 수영장에서 수영을 해보세요.",
                "카이스트 스포츠 컴플렉스에서 농구를 해보세요.",
                "기지개를 쭉 펴고 스트레칭을 해보세요."};
        detailActivityList[2] = new String[] { //게임
                "구글플레이스토어 인기게임 \"카이저(12)\"를 해보세요.",
                "구글플레이스토어 인기게임 “배틀그라운드”를 해보세요.",
                "구글플레이스토어 인기게임 “Kick the Buddy”를 해보세요."};
        detailActivityList[3] = new String[] { //명상
                "\"The golden bell of my heart\"음악을 들으며 명상을 해보세요.",
                "\"Crystal Bowl Healing\" 음악을 들으며 명상을 해보세요.",
                "눈을 감고 10분동안 명상을 해보세요.",
                "자연의 소리를 들으며 명상해보세요."};
        detailActivityList[4] = new String[] { //산책
                "오리연못을 산책하고 오세요.",
                "투썸플레이스(카이스트점)까지 산책하고 오세요.",
                "스포츠 컴플렉스까지 산책하고 오세요."};
        detailActivityList[5] = new String[] { //음악감상
                "최신 음악 \"LATATA\"-(여자)아이들 노래를 들어보세요.",
                "최신 음악 \"여행\"-볼빨간사춘기 노래를 들어보세요.",
                "최신 음악 \"빙글뱅글\"-AOA 노래를 들어보세요."};
        detailActivityList[6] = new String[] { //카페
                "다빈치 랩에 가서 커피 한 잔을 마시고 오세요.",
                "투썸플레이스(카이스트점)에서 케이크 한 조각을 먹고 오세요.",
                "카페그랑(카이스트점)에서 베이글을 먹고 오세요."};
        detailActivityList[7] = new String[] { //영화
                "최신 영화 \"어벤져스: 인피니티 워\"를 보고 오세요.",
                "최신 영화 \"독전\"을 보고 오세요.",
                "최신 영화 \"쥬라기 월드: 폴른 킹덤\"을 보고 오세요."};
        detailActivityList[8] = new String[] { //음식
                "어은동 \"누오보나폴리\"에서 피자를 먹어보세요.",
                "궁동 \"봉추찜닭\"에서 찜닭을 먹어보세요.",
                "궁동 \"팬텀 팬피그\"에서 삼겹살을 먹어보세요."};
        detailActivityList[9] = new String[] { //쇼핑
                "여름 아이템 \"핸디 선풍기\"를 구입해보세요.",
                "핫 아이템 \"블루투스 마이크\"를 쇼핑해보세요.",
                "새로운 옷을 구입해보세요."};
        detailActivityList[10] = new String[] { //수면
                "유튜브에서 \"직접만든 마시멜로우 ASMR 먹방 리얼사운드\"를 들으면서 30분 수면을 취해보세요.",
                "유튜브에서 \"다양한 얼음 씹어 먹는소리\"를 들으면서 30분 수면을 취해보세요.",
                "유튜브에서 \"직접만든 양념치킨 리얼사운드 먹방\"을 들으면서 30분 수면을 취해보세요."};
        detailActivityList[11] = new String[] { //노래방
                "카이스트 동전 노래방을 이용해보세요.",
                "궁동 꿀잼동전노래방을 이용해보세요.",
                "궁동 싸이뮤직게임월드노래방을 이용해보세요."};
        detailActivityList[12] = new String[] { //커뮤니케이션
                "가장 친한 친구에게 카톡을 해보세요.",
                "가장 친한 친구와 통화해보세요.",
                "가족에게 전화해보세요."};
        //SharedPreferences setting = getApplicationContext().getSharedPreferences("setting", 0);
        SharedPreferences.Editor editor = setting.edit();
        for(int i=0;i<n;i++){
            String weightname = "weight" + String.valueOf(i);
            weight[i] = setting.getInt(weightname,defaultWeight);
            if (!setting.contains(weightname)) editor.putInt(weightname, defaultWeight);
        }
        editor.commit();
        String tmp="";
        for(int i=0;i<n;i++) tmp+='0';
        pref = setting.getString("category", tmp);
    }
    public int[] getRandomCategories(int randomN){
        //String randomStrings[] = new String[randomN];
        int prefRandomN = randomN-1;
        int prefcRandomN = 1;
        int prefN = pref.replace("0","").length();

        //여기부터 가중치랜덤 뽑는건데 너무 길게 짜버렸다고 한다..............;ㅁ;
        if (n == prefN){
            prefcRandomN = 0;
            prefRandomN = randomN;
        }
        else if (prefN == 0){
            prefRandomN = 0;
            prefcRandomN = randomN;
        }
        int weightsum = 0, weightcsum = 0;
        for (int i=0;i<n;i++){
            if (pref.charAt(i)=='1') weightsum += weight[i];
            else weightcsum += weight[i];
        }
        Random random = new Random();
        boolean v[] = new boolean[n];

        int categoryRandom [] = new int[prefRandomN];
        for (int i=0;i<prefRandomN;i++){
            int k = random.nextInt(weightsum);
            int j=0;
            for (;j<n;j++){
                if(pref.charAt(j)!='1'||v[j]==true) continue;
                k-=weight[j];
                if (k<0) break;
            }
            categoryRandom[i] = j;
            if (prefRandomN<=prefN){
                v[j] = true;
                weightsum -= weight[j];
            }
        }
        int categorycRandom [] = new int[prefcRandomN];
        for (int i=0;i<prefcRandomN;i++){
            int k=random.nextInt(weightcsum);
            int j=0;
            for(;j<n;j++){
                if (pref.charAt(j)=='1') continue;
                k-=weight[j];
                if(k<0) break;
            }
            categorycRandom[i] = j;
        }
        int randomResult[] = new int[randomN];
        System.arraycopy(categoryRandom, 0, randomResult, 0, prefRandomN);
        System.arraycopy(categorycRandom, 0, randomResult, prefRandomN, prefcRandomN);
        return randomResult;
    }
    public int[] getRandomCategories(){
        return getRandomCategories( defaultRandomN);
    }
    public String[] getRandomStringsFromCat(int[] cat){//cat은 고양이.. 야옹.. 가 아니라 category의 cat임ㅋ
        String[] randomString = new String[cat.length];
        for (int i=0;i<cat.length;i++){
            randomString[i] = getRandomStringFromCat(cat[i]);
        }
        return randomString;
    }
    public String getRandomStringFromCat(int cat){
        Random random = new Random();
        int k = random.nextInt(detailActivityList[cat].length);
        String randomString = new String(detailActivityList[cat][k]);
        return randomString;
    }
    public String[] getRandomStrings(int randomN){
        int[] cat = getRandomCategories(randomN);
        return getRandomStringsFromCat(cat);
    }
    public String[] getRandomStrings(){
        return getRandomStrings(defaultRandomN);
    }
    public int getCategoryFromString(String str){
        for(int i=0;i<n;i++){
            for(String s:detailActivityList[i]){
                if (s.equals(str)) return i;
            }
        }
        return -1;
    }
    public int getDrawableIdFromString(String str){
        for (int i=0;i<n;i++){
            for (int j=0;j<detailActivityList[i].length;j++){
                if (detailActivityList[i][j].equals(str)) return detailActivityDrawableId[i][j];
            }
        }
        return -1;
    }
}
