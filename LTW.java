import java.util.ArrayList;
import java.util.stream.IntStream;

public class BWT {
    //1) ====================== get unique characters in a string ==============
    public static char[] getUniqueCharacters(String string, int stringLength){
        String temp = "";
        char current;
        for (int i = 0; i < stringLength; i++){
            current = string.charAt(i);
            if (temp.indexOf(current) >= 0){}else{
                temp += current; // adding characters
            }
        }
        return temp.toCharArray();
    }
    //2) ====================== get all zero positions in a string ==================
    public  static int[] getUniqueVectorZeroPositions(String string, int stringLength, Character character){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < stringLength; i++){
            if(string.charAt(i) == character){
                list.add(i);
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
    //3) ============================= do the above for every unique vector ===========================================
    public static int[][] getUniqueVectorsZeroPositions(String string, char[] uniqueCharactersSet,int stringLength){
        int resultList[][] = new int[256][];
        for(char character : uniqueCharactersSet) {
            resultList[character] = getUniqueVectorZeroPositions(string, stringLength, character);
        }
        return resultList;
    }

    //4) ============================= get non zero weights for every unique vector ====================================
    public static int[][] getUniqueVectorsWeights(char[] arr, int stringLength, char[] uniqueChars){
        int[] result = new int[stringLength];
        int resultList[][] = new int[256][];
        char col;
        for(char character : uniqueChars) {
            for (int i = 0; i < stringLength; i++) {
                col = arr[i];
                if (character > col) {
                    result[i] = 1;
                }else{
                if (character < col) {
                    result[i] = -1;
                }}
            }
            resultList[character] = result;
            result = new int[stringLength]; // start next cycle with an empty object.
        }
        return resultList;
    }
    //5) =============================== calculate unique vectors sums ================================================
    public  static int[] getUniqueVectorsSum(int[][] vectorsOfWeights, char[] characterSet){
        int[] sums = new int[256];
        int sum;
        for(int character : characterSet){
            sum = IntStream.of(vectorsOfWeights[character]).sum();
            sums[character] = sum;
        }
        return sums;
    }
    //6) ================================= loop through all vectors and apply sum =====================================
    public  static int[] getNonZeroVectorsSum(char[] arr, int stringLength, int[] uniqueVectorsSum){
        int[] nonZeroSums = new  int[stringLength];
        for(int i = 0; i < stringLength; i++){
            nonZeroSums[i] = uniqueVectorsSum[arr[i]];
        }
        return nonZeroSums;
    }
    //7) ========================================== calculate the sums of zero cells ==================================
    public static int[] addZeroSums(char[] arr,  int[][] vectorsZeroPositions, int[] NonZeroVectorsSum, int[][]
            uniqueVectorsWeights, int stringLength){
        int[] reference  = uniqueVectorsWeights[arr[stringLength-1]];
        int[] target;
        int sum;
        char col;
        int ref;
        for(int i = stringLength-2;i >=0;i--){
            col = arr[i];
            target = uniqueVectorsWeights[col];
            sum = 0;
            for(int j:vectorsZeroPositions[col]){
                ref = reference[j + 1];
                sum +=  ref;
                target[j] = ref;
            }
            NonZeroVectorsSum[i] += sum;
            reference = target;
        }
        return NonZeroVectorsSum;
    }
    //-----------------------------------------------------------------
    //8) =============================== generate random string =========================================
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        int character;
        while (count-- != 0) {
            character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    //9) =============================== generate random string flag = 0 random else rep ===============
    public static String generateStringSample(int flag, int rep, int count) {
        String string;
        if(flag == 1){
            string = randomAlphaNumeric(count);
        }else{
            string = "SIX.MIXED.PIXIES.SIFT.SIXTY.PIXIE.DUST.BOXES";
            //string = "sssssssssssssussssssssssspssssssssssssssssss";
            for (int i = 0; i < rep; i++) { string = string + string; }
        }
        string = string + '$';
        return string;
    }
    //10) ============================= generate BWT weights ==================================
    public  static  int[] generateBwtWeights(String string){
        int stringLength = string.length();
        //------------------------------------------------
        char[] arr = string.toCharArray();
        char[] uniqueCharactersSet =   getUniqueCharacters(string, stringLength);
        int[][] uniqueVectorsWeights = getUniqueVectorsWeights(arr, stringLength, uniqueCharactersSet);
        int[] uniqueVectorsSums =      getUniqueVectorsSum(uniqueVectorsWeights, uniqueCharactersSet);
        int[] NonZeroVectorsSum =      getNonZeroVectorsSum(arr, stringLength, uniqueVectorsSums);
        int[][] vectorsZeroPositions = getUniqueVectorsZeroPositions(string, uniqueCharactersSet, stringLength);
        int[] finalRes = addZeroSums(arr,  vectorsZeroPositions, NonZeroVectorsSum, uniqueVectorsWeights, stringLength);
        return finalRes;
    }
    //================================ maim method ========================================
    //11) ================================ maim method ========================================
    public static void main(String[] args) {
        //string "SIX.MIXED.PIXIES.SIFT.SIXTY.PIXIE.DUST.BOXES";
        String string = generateStringSample(0, 10, 90000);
        //------------------------------------------------
        long startTime = System.nanoTime();
        int[] finalResults = generateBwtWeights(string);
        //---------------------------------------
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 10000000;// add two zeroes to get seconds.
        System.out.println("============= duration in 1/100 second ==============");
        System.out.println(duration);
        System.out.println("============= number of characters used as input ====");
        System.out.println(string.length());
    }
    //========================================================================
    //========================================================================
}
