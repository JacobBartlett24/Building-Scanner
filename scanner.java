import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class scanner {

    private boolean error;

    terminalToken id;
    terminalToken number;

    public void setError(boolean newError) {
    this.error = newError;
    }

    public boolean getError() {
    return error;
    }

    public String[] scan(File file) throws IOException {

    //Read file in and make it readable character by character
    FileReader fr = new FileReader("foo.txt");
    BufferedReader br = new BufferedReader(fr);

    //Object array of IdObject class
    IdObject obj = new IdObject();

    // "prep" the first character into current int
    int currentInt = br.read(); //integer representation of current character
    int nextInt = 0; //integer representation of next character
    int count = 0;  //counts how many tokens have been returned
    String[] output = new String[18];

    while (nextInt != -1) {  // until end of the array

        if (nextInt != 0) {
        currentInt = nextInt;
        }

        nextInt = br.read(); //peek next character

        char currentChar = (char) currentInt; // set current character
        char nextChar = (char) nextInt;

        if (currentChar == ' ' | currentInt == 10 | currentInt == 13) { // skips whitespace and new line (ASCII Code)
        continue;
        }

        //get symbols as tokens
        if (currentChar == '(') {
        count = storeToken(output, count, "lpar");
        } else if (currentChar == ')') {
        count = storeToken(output, count, "rpar");
        } else if (currentChar == '+') {
        count = storeToken(output, count, "plus");
        } else if (currentChar == '-') {
        count = storeToken(output, count, "minus");
        } else if (currentChar == '*') {
        count = storeToken(output, count, "times");
        }

        // if assign token return assign, if not return error (increment r to next char)
        else if (currentChar == ':' && nextChar == '=') {
        nextInt = br.read();
        count = storeToken(output, count, "assign");
        }


        //if it is a comment skip till not a comment
        else if (currentChar == '/') {

        if (nextChar == '*' | nextChar == '/') {

            nextInt = br.read();
            nextChar = (char) nextInt;

            while (currentChar != '*' && nextChar != '/') {

            currentInt = nextInt;
            currentChar = (char) currentInt;
            nextInt = br.read();
            nextChar = (char) nextInt;

            }

        }

        } else if (Character.isDigit(currentChar) | currentChar == '.') { // if character is digit start loop
        int decimalCount = 0;
        while ((Character.isDigit(currentChar)) | currentChar == '.') { // loop to find decimal or return number

            currentInt = nextInt;
            currentChar = (char) currentInt;
            nextInt = br.read();

            if (currentChar == '.') {
            decimalCount++;
            }
        }

        if (decimalCount > 1) { // if there is more than one decimal error out
            setError(true);
        }

        count = storeToken(output, count, "number");


        }

        //finds id token types
        else if (Character.isLetter(currentChar)) {

        int j = 0;
        char[] token = new char[16];

        while (Character.isLetter(currentChar) | Character.isDigit(currentChar)) { // loops till the next character is not
            // a digit or letter
            System.out.println("char" + currentChar);
            token[j] = currentChar;
            for(int i = 0; i < token.length; i++) {
                System.out.println(token[i]);
            }

            currentInt = nextInt;
            currentChar = (char) currentInt;

            nextInt = br.read();
            j++;
        }

        token = Arrays.copyOf(token, j); // makes a correct array size

        String string = new String(token);

        // stores either read, write, or id depending on the token
        if (string.equals("write")) {
            count = storeToken(output, count, "write");
        } else if (string.equals("read")) {
            count = storeToken(output, count, "read");
        } else {
            id = new terminalToken(string);
            count = storeToken(output, count, "id");
        }
        }

        //set error token if it does not match any other tokens
        else {

        setError(true);

        }


    }

    output[count] = "0";
    return output;

    }

    private int storeToken(String[] output, int count, String token) { //stores token and returns the count
        output[count] = token;
        count++;
        return count;
    }


    public static void main(String[] args) throws IOException {

    //initialize variables
    if(args.length == 0){
        System.err.print("\nNo input file\n\nexample:\n\njava scanner.java fileName\n");
        System.exit(1);
    }

    File myObj = new File(args[0]);
    scanner scanner = new scanner();
    String[] output;

    //set output to an array of tokens
    output = scanner.scan(myObj);

    //if the scanner returns an error end program, else print tokens
    if (scanner.getError()){

        System.out.println("error");

    }

    else {

    //   parser parser = new parser(output);
        printTokens(output);
    }

    }

    private static void printTokens(String[] output) {

    int i;

    System.out.print("(");

    for (i = 0; i < output.length; i++) {

        if (output[i + 1].equals("0")) {
        output[i+1] = "$$";
        System.out.print(output[i] + ", ");
        System.out.print(output[i+1]);
        break;
        }

        System.out.print(output[i] + ", ");

    }

    System.out.print(")");
    }

    }

class parser {

    // assigns all token types
    String program     = "Program";
    String stmt_list   = "stmt_list";
    String stmt        = "stmt";
    String factor_tail = "factor_tail";
    String factor      = "factor";
    String expr        = "expr";
    String term_tail   = "term_tail";
    String term        = "term";
    String id          = "id";
    String read        = "read";
    String write       = "write";
    String assign      = "assign";
    String number      = "number";
    String lpar        = "lpar";
    String rpar        = "rpar";
    String minus       = "minus";
    String plus        = "plus";
    String times       = "times";
    String endSymbol   = "$$";
    String epsilonProduction = "\u03B5";

    // array for the output of scan
    String[] output;
    int count = 0;  //increments output array
    int spaces = 0; //indentation increment
    String token;   //matching token

    // construct to start the routine
    parser(String[] output) {
        this.output = output;
        token = this.output[count];
        program(); //starts sequence
    }

    public void setCount(int count) {
    this.count = count;
    }

    // prints the leading token ex: <Program> including indentation
    private void printIndentEntry(String string){
    string = "<" + string + ">";
    for (int i = 0; i < spaces; i++){
        string = " " + string;
    }
    spaces += 1;
    System.out.println(string);
    }

    private void printMatch(String string){
        for (int i = 0; i < spaces+3; i++){
            string = " " + string;
        }
        System.out.println(string);
    }

    // script to print indents
    private void printScript(String string) {
    printIndentEntry(string);
    match(string);
    printIndentExit(string);
    }

    // prints the exiting token ex: </Program> including indentation
    private void printIndentExit(String string){
    spaces -= 1;
    string = "</" + string + ">";
    for (int i = 0; i < spaces; i++){
        string = " " + string;
    }
    System.out.println(string);
    }

    // error function
    private void parseError() {
    }

    // detects if the input matches the rules of a programs
    private void match (String expectedToken) {
    if (token.equals(expectedToken)) {
        printMatch(expectedToken);
        setCount(count + 1);
        token = output[count];
    }
    }

    private void program () {

        printIndentEntry(program);

        if(token.equals(endSymbol) || token.equals(id) || token.equals(read) || token.equals(write)) {
            stmt_list();
            printIndentExit(program);
            match(endSymbol);
        } else parseError();

    }

    private void stmt_list () {
    if(token.equals(id) || token.equals(read) || token.equals(write)) {
        printIndentEntry(stmt_list);
        stmt();
        stmt_list();
        printIndentExit(stmt_list);
    }else if (token.equals(endSymbol)){
        printMatch(epsilonProduction);
    }else parseError();
    }

    private void stmt () {

    printIndentEntry(stmt);

    if(token.equals(id)){

        printScript(id);
        printScript(assign);
        expr();
        printIndentExit(stmt);

    }
    else if(token.equals(read)){

        printScript(read);
        printScript(id);
        printIndentExit(stmt);

    }
    else if (token.equals(write)){

        match(write);
        expr();
        printIndentExit(stmt);

    } else parseError();
    }

    private void expr () {
    printIndentEntry(expr);
    if(token.equals(id) || token.equals(number) || token.equals(lpar) ) {

        term();
        term_tail();
        printIndentExit(expr);

    }else parseError();
    }

    private void term_tail () {
    printIndentEntry(term_tail);
    if (token.equals(plus) || token.equals(minus)) {
        add_op();
        term();
        term_tail();
        printIndentExit(term_tail);
    }else if (token.equals(id) || token.equals(rpar) || token.equals(read) || token.equals(write) ) {
        printMatch(epsilonProduction);
    } else parseError();
    }

    private void term () {
    printIndentEntry(term);
    if(token.equals(id) || token.equals(number) || token.equals(lpar) ) {
        factor();
        factor_tail();
        printIndentExit(term);
    }else parseError();
    }

    private void factor_tail () {
    printIndentEntry(factor_tail);
    if(token.equals(times)) {
        mult_op();
        factor();
        factor_tail();
        printIndentExit(factor_tail);
    } else if (token.equals(plus) || token.equals(minus) || token.equals(rpar)) {
        printMatch(epsilonProduction);
    } else parseError();
    }

    private void factor () {
    printIndentEntry(factor);
    if(token.equals(id)){
        printScript(id);
        printIndentExit(factor);
    }else if (token.equals(number)){
        printScript(number);
    }else if (token.equals(lpar)){
        match(lpar);
        expr();
        match(rpar);
    }
    }

    private void add_op () {
    if (token.equals(plus))
    match(plus);
    else if(token.equals(minus))
    match(minus);
    else parseError();
    }

    private void mult_op () {
    if (token.equals(times))
    match(times);
    else parseError();
    }
}

class terminalToken{

    String value;
    String idName = "id";
    String numberName = "number";

    terminalToken(){

    }

    terminalToken (String value){
        this.value = value;
    }

    public void setName(String name) {
        this.value = name;
    }

    public String getValue() {
        return this.value;
    }
}
