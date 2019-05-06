import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
/**
 * class to encode Borrow Wheeler Transforms using League Table Weights algorithm
 * @author A. Hechachena
 * The inverse BWT is adopted and reshaped from unknown source for testing purposes.
 */

public class BWT {
    //============================================================= declaire global variables
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZqwertyuiopasdfghjklzxcvbnm[]#',<>?~@|.;:()0123456789";
    public static String inputString;         // this is the text string to be processed.
    public static int inputStringLength;      // length of the above.
    public static char[] uniqueCharactersSet; // unique characters in the input string.
    public static int[] uniqueVectorsSums = new int[256];
    public static char[] inputArr2;           // contains twice the elements of inputArr2 to avoid calculations of reference
    public static int[][] uniqueColumnsWeights = new int[256][]; // for every unique character there is a unique column of weights
    public static int[][] vectorsZeroPositions = new int[256][]; // for every unique character there is a unique column of zero positions
    public static int[] weightsVector;        // this is the final vector containing weights of the input string characters.
    public static String decodedString;
    public static String encodedString;
    //===================================================================================
    //============================================================= encoding methods.
    // the input inputString is the inputString containing the original text to be B W Transformed
    // some functions use arrays instead of strings for access ease.
    // all first five function containing the word unique are part of pre-calculations to reduce time.
    //1) =========== return the unique characters from the input inputString  as an char[] array ==============
    public static void getUniqueCharacters(){
        String uniqueCharacters = "";
        char currentChar;
        for (int i = 0; i < inputStringLength; i++){
            currentChar = inputString.charAt(i);
            if (uniqueCharacters.indexOf(currentChar) >= 0){}else{
                uniqueCharacters += currentChar; // adding characters
            }
        }
        uniqueCharactersSet = uniqueCharacters.toCharArray();
    }
    //2) ========= get all zero positions in the original string starting from character.. ==================
    public  static int[] getUniqueVectorZeroPositions(Character character){
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < inputStringLength; i++){
            if(inputString.charAt(i) == character){
                list.add(i);
            }
        }
        return list.stream().mapToInt(Integer::intValue).toArray();
    }
    //3) ============================= do the above for every unique string ======================================
    public static void getUniqueVectorsZeroPositions(){
        for(char columnName : uniqueCharactersSet) {
            vectorsZeroPositions[columnName] = getUniqueVectorZeroPositions(columnName);
        }
    }
    //4) ============================= get non zero weights for every unique string ================================
    public static void getUniqueVectorsWeights(){
        int[] columnWeights = new int[inputStringLength];
        char columnName;
        for(char rowName : uniqueCharactersSet) {
            for (int i = 0; i < inputStringLength; i++) {
                columnName = inputArr2[i];
                if (rowName > columnName) {
                    columnWeights[i] = 1;
                }else{
                if (rowName < columnName) {
                    columnWeights[i] = -1;
                }}
            }
            uniqueColumnsWeights[rowName] = columnWeights;
            columnWeights = new int[inputStringLength]; // start next cycle with an empty object.
        }
    }
    //5) =============================== calculate unique vectors sums ================================================
    public  static void getUniqueVectorsSum(){
        int columnSum;
        for(int character : uniqueCharactersSet) {
            columnSum = IntStream.of(uniqueColumnsWeights[character]).sum();
            uniqueVectorsSums[character] = columnSum;
        }
    }
    //
    //============================ start right to left calculations ================================================
    //6) =========================== loop through all vectors and apply pre calculated sums ========================
    public  static void getNonZeroVectorsSum(){
        for(int i = 0; i < inputStringLength; i++){
            weightsVector[i] = uniqueVectorsSums[inputArr2[i]];
        }
    }
    //7) ================= calculate the sums of zero cells for every column ==================================
    public static void addZeroSums(){
        int[] referenceColumn  = uniqueColumnsWeights[inputArr2[inputStringLength-1]];
        int[] targetColumn;
        int columnSum;
        char columnName;
        int referenceCell;
        for(int i = inputStringLength-2;i >=0;i--){
            columnName = inputArr2[i];
            targetColumn = uniqueColumnsWeights[columnName];
            columnSum = 0;
            for(int j:vectorsZeroPositions[columnName]){
                referenceCell = referenceColumn[j+1];
                columnSum +=  referenceCell;
                targetColumn[j] = referenceCell;
            }
            weightsVector[i] += columnSum;
            referenceColumn = targetColumn;
        }
    }
    //=========================================================================================
    //8) ========== final function to generate encodedString ===================================
    public  static  void bwt_encode(){
        //------------------------------------------------
        getUniqueCharacters();
        getUniqueVectorsWeights();
        getUniqueVectorsSum();
        getNonZeroVectorsSum();
        getUniqueVectorsZeroPositions();
        addZeroSums();
        // ==========
        char[] resultFinal = new char[inputStringLength];
        int inputStringLength1 = inputStringLength - 1;
        encodedString = "";
        //======== derive index from weights and sort final string
        for(int i = 0;i < inputStringLength;i++){
            weightsVector[i] = (weightsVector[i] + inputStringLength)/2;
        }
        for(int i = 0;i < inputStringLength;i++){
            resultFinal[weightsVector[i]] = inputArr2[i + inputStringLength1];
        }
        for(int i = 0;i < inputStringLength;i++){
            encodedString = encodedString + resultFinal[i];
        }
    }
    //===================================================================================================
    //9) =============================== generate random string =========================================
    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        int character;
        while (count-- != 0) {
            character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    //10) ========== generate random string flag = 0 random else repeate the given pattern ===============
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
    //========================================================================================
    //========================================================================================
    /**
     * decode BWT
     * @author adopted by A.H from unknown source.
     */
    //=============================  decoding ================================================

    public  static int[]  getIndex(int size, int[] buckets, int[] strEncoded, int[] indices){
        for (int i = 0; i < size; i++){
            buckets[strEncoded[i]] = buckets[strEncoded[i]] + 1;
            indices[buckets[strEncoded[i]]] = i;
        }
       return indices;
    }
    //    ================== generate decodedString ==============================
    public  static void bwt_decode()
    {
        // =============== variables declarations ========
        char[] charArr = encodedString.toCharArray();
        char[] strDecoded1 = new char[inputStringLength];
        int[] strEncoded = new int[inputStringLength];
        for (int i = 0; i < inputStringLength; i++) {
            strEncoded[i] = (int)charArr[i];
        }
        int size = inputStringLength;
        int[] F = strEncoded.clone();
        int[] strDecoded = new int[size];
        int[] indices = new int[size];
        int[] buckets = new int[256];
        // =============== process ===========================
        for(int i = 0;i < size; i++){buckets[strEncoded[i]] = buckets[strEncoded[i]] + 1;}
        Arrays.sort(F);
        int j = 0;
        for (int i = 0;i < 256; i++)
        {
            while (i > F[j] & j < (size-1))
            {
                j = j + 1;
            }
            buckets[i] = j-1;
        }
        //============
        indices = getIndex(size, buckets, strEncoded, indices);
        //============
        j = 0;
        for (int i = 0; i < size; i++)
        {
            strDecoded[i] = strEncoded[j];
            j = indices[j];
        }
        //======================== shaping output ====================================
        int g = 0;
        for(int i = 0;i < inputStringLength; i++){if(strDecoded[i] == 33){g = i;break;}}
        for(int i = 0;i < inputStringLength; i++){strDecoded1[i] = (char)strDecoded[i];}
        String s = String.valueOf(strDecoded1);
        decodedString = s.substring(g+1,size) + s.substring(0, g);
    }
    //=========================================================================================
    //=========================================================================================
    //11) ================================ maim method ========================================
    public static void main(String[] args) {
        // set flag to 0 to get a repeat pattern data, set flag to 1 to generate random 'counts'
        inputString = generateStringSample(0, 10, 90000);
        inputStringLength = inputString.length();
        inputArr2 = (inputString + inputString).toCharArray();
        weightsVector = new int[inputStringLength];
        //------------------------ timing encoding process ------------------------
        long startTime = System.nanoTime();
        //---------------------------------------
        bwt_encode();
        System.out.println(encodedString);
        //---------------------------------------
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 10000000;// add two zeroes to get seconds.
        System.out.println("============= duration in 1/100 second ==============");
        System.out.println(duration);
        System.out.println("============= number of characters used as input ====");
        System.out.println(inputString.length());
        //------------------------ timing decoding process ------------------------
        startTime = System.nanoTime();
        //---------------------------------------
        bwt_decode();
        System.out.println(decodedString);
        //---------------------------------------
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 10000000;// add two zeroes to get seconds.
        System.out.println("============= duration in 1/100 second ==============");
        System.out.println(duration);
        System.out.println("============= number of characters used as input ====");
        System.out.println(inputString.length());
    }
    //========================================================================
    //========================================================================
}