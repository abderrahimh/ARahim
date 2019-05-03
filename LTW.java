import java.util.ArrayList;
import java.util.stream.IntStream;

public class BWT {
    // the input string is the string containing the original text to be B W Transformed
    // some functions use arrays instead of strings for access ease.
    // all first five function containing the word unique are part of pre-calculations to reduce time.
    //1) =========== return the unique characters from the input string  as an char[] array ==============
    public static char[] getUniqueCharacters(String string, int stringLength){
        String uniqueCharacters = "";
        char currentChar;
        for (int i = 0; i < stringLength; i++){
            currentChar = string.charAt(i);
            if (uniqueCharacters.indexOf(currentChar) >= 0){}else{
                uniqueCharacters += currentChar; // adding characters
            }
        }
        return uniqueCharacters.toCharArray();
    }
    //2) ========= get all zero positions in the original string starting from character.. ==================
    public  static int[] getUniqueVectorZeroPositions(String string, int stringLength, Character character){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < stringLength; i++){
            if(string.charAt(i) == character){
                list.add(i);
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
    //3) ============================= do the above for every unique string ======================================
    public static int[][] getUniqueVectorsZeroPositions(String string, char[] uniqueCharactersSet,int stringLength){
        int resultList[][] = new int[256][];
        for(char character : uniqueCharactersSet) {
            resultList[character] = getUniqueVectorZeroPositions(string, stringLength, character);
        }
        return resultList;
    }

    //4) ============================= get non zero weights for every unique string ================================
    public static int[][] getUniqueVectorsWeights(char[] arr, int stringLength, char[] uniqueChars){
        int[] columnWeights = new int[stringLength];
        int[][] uniqueColumnsWeights = new int[256][];
        char columnName;
        for(char rowName : uniqueChars) {
            for (int i = 0; i < stringLength; i++) {
                columnName = arr[i];
                if (rowName > columnName) {
                    columnWeights[i] = 1;
                }else{
                if (rowName < columnName) {
                    columnWeights[i] = -1;
                }}
            }
            uniqueColumnsWeights[rowName] = columnWeights;
            columnWeights = new int[stringLength]; // start next cycle with an empty object.
        }
        return uniqueColumnsWeights;
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
    //
    //============================ start right to left calculations ================================================
    //6) =========================== loop through all vectors and apply pre calculated sums ========================
    public  static int[] getNonZeroVectorsSum(char[] arr, int stringLength, int[] uniqueVectorsSum){
        int[] nonZeroSums = new  int[stringLength];
        for(int i = 0; i < stringLength; i++){
            nonZeroSums[i] = uniqueVectorsSum[arr[i]];
        }
        return nonZeroSums;
    }
    //7) ================= calculate the sums of zero cells for every column ==================================
    public static int[] addZeroSums(char[] arr,  int[][] vectorsZeroPositions, int[] NonZeroVectorsSum, int[][]
            uniqueVectorsWeights, int stringLength){
        int[] referenceColumn  = uniqueVectorsWeights[arr[stringLength-1]];
        int[] targetColumn;
        int columnSum;
        char columnName;
        int referenceCell;
        for(int i = stringLength-2;i >=0;i--){
            columnName = arr[i];
            targetColumn = uniqueVectorsWeights[columnName];
            columnSum = 0;
            for(int j:vectorsZeroPositions[columnName]){
                referenceCell = referenceColumn[j+1];
                columnSum +=  referenceCell;
                targetColumn[j] = referenceCell;
            }
            NonZeroVectorsSum[i] += columnSum;
            referenceColumn = targetColumn;
        }
        return NonZeroVectorsSum;
    }
    //---------------------------------------------------------------------------------------------------
    //8) =============================== generate random string =========================================
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm[]#',<>?~@|.;:()0123456789";
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        int character;
        while (count-- != 0) {
            character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    //9) ========== generate random string flag = 0 random else repeate the given pattern ===============
    public static String generateStringSample(int flag, int rep, int count) {
        String string;
        if(flag == 1){
            string = randomAlphaNumeric(count);
        }else{
            string = "SIX.MIXED.PIXIES.SIFT.SIXTY.PIXIE.DUST.BOXES";
            for (int i = 0; i < rep; i++) { string = string + string; }
        }
        string = string + '!';
        return string;
    }
    //=========================================================================================
    //10) ========== final function to generate BWT weights ===================================
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
    //    ================================ maim method ========================================
    //11) ================================ maim method ========================================
    public static void main(String[] args) {
        // set flag to 0 to get a string composed or 'rep', set flag to 1 to generate number of random 'count'
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
