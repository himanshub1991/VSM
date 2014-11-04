

package vsm;

import com.google.code.externalsorting.ExternalSort;
import static edu.mit.jwi.item.POS.NOUN;
import edu.mit.jwi.morph.WordnetStemmer;
import edu.sussex.nlp.jws.JWS;
import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
public class VSM {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    static final String DB_URL = "jdbc:mysql://localhost/vsm";
    static final String USER = "root";
    static final String PASS = "root";
    static final String STOPWORDS = "stopwords1.txt";
    
    public static void main(String[] args) throws FileNotFoundException {
        
        //pdf2word();
        //Connection conn=excel2mysql();
        
        //sorts the keywords
        keywordSort();
        
        
        //remove spaces, toLowercase, remove stopwords
            //tokenize(conn);
            //currentDirectory(new File("C:\\Users\\Krishna\\Documents\\NetBeansProjects\\VSM"));
            //  stopwords in single line
            //  stopWords();
    }
    public static void keywordSort(){
        try {
            File inputfile = new File("col.csv");
            File outputfile = new File("col1.csv");
            ExternalSort.sort(inputfile,outputfile);
        } catch (IOException ex) {
            Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static void pdf2word() throws FileNotFoundException{
        pdfConvertor pdf = new pdfConvertor();
        String filepath="";
        File folder = new File("E:\\project\\dataProject\\myPatents\\2014");
        for (final File fileEntry : folder.listFiles()) {
                if(fileEntry.getName().contains(".pdf"))
                {
                    filepath="";
                    filepath+="E:\\project\\dataProject\\myPatents\\2014";
                    filepath+="\\";
                    filepath+=fileEntry.getName();
                    try {
                        pdf.pdf2text(filepath);
                    } catch (UnsupportedEncodingException ex) {
                        Logger.getLogger(VSM.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
           
        }
        
    }
    public static Connection excel2mysql(){
        DBase db = new DBase();
        Connection conn=db.connect(DB_URL, USER, PASS);
        
        //Creates a new Table  change the query
        //createTable(db,conn);
        //String query = "ALTER TABLE records AUTO_INCREMENT=1";
        // import txt data into mysql server  file has to be copied to server  C:\xampp\mysql\data\vsm  
        
        File folder = new File("C:\\xampp\\mysql\\data\\vsm");
        for (final File fileEntry : folder.listFiles()) {
                if(fileEntry.getName().contains(".csv"))
                    db.importData(conn, fileEntry.getName());
           
        }
        return conn;
    }
    public static void currentDirectory(File folder){
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                currentDirectory(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }
    public static void createTable(DBase db,Connection conn){
        
        String sql="CREATE TABLE records " +
                           "(id INT PRIMARY KEY auto_increment, " +
                           " app_no VARCHAR(25) unique, " + 
                           " title VARCHAR(2500) not null, " + 
                           " abstract VARCHAR(5200) not null " + 
                           " )"; 
        //String query = "ALTER TABLE records AUTO_INCREMENT=1";
        String sql1="CREATE TABLE terms " +
                    "(id INT PRIMARY KEY auto_increment, "  +
                    "term varchar(30) , "  +
                    "doc_no INT , "  +
                    "frequency INT "  +
                    " )";
        //String query = "ALTER TABLE terms AUTO_INCREMENT=1";
                   db.createTable(conn,sql1);
    }
    public static void tokenize(Connection conn) throws FileNotFoundException{
        HashMap AllWords = new HashMap ();
        Statement stmt;
        List list = new ArrayList();
        String query;
        ResultSet rows;
        List <String> stopWords = readStopWords();
        Pattern checkRegex = Pattern.compile("[a-zA-Z]{2,}");
        JWS ws = new JWS("C:\\Program Files (x86)\\WordNet","2.1");  
        WordnetStemmer stem =  new WordnetStemmer(ws.getDictionary());
        PrintWriter pr= new PrintWriter("index.csv");
        PrintWriter col = new PrintWriter("col.csv");
        try
        {
            stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            query = "Select id,abstract from records" ;
            rows = stmt.executeQuery(query);
            while (rows.next()) {
                //   System.out.println(rows.getString("title"));
                System.out.println(rows.getString("abstract"));
                HashMap <String,Integer> doc = new HashMap<String, Integer>();
                int i = Integer.parseInt(rows.getString("id"));
                Matcher regexMatcher = checkRegex.matcher( rows.getString("abstract") );
                while ( regexMatcher.find() ){
                     if (regexMatcher.group().length() != 0){
                         String word = regexMatcher.group().trim().toLowerCase();
                         if (!stopWords.contains(word)){
                             String stemmedword="";
                             try{
                                stemmedword = (String) AllWords.get( word );
                             }catch(NullPointerException e){
                                 e.printStackTrace();
                             }
                             if(stemmedword != null){
                                 pr.println(stemmedword +","+i);
                             }
                             else{
                                try{
                                   System.out.print( word + " " );
                                   list = stem.findStems(word,null);
                                }catch(java.lang.IllegalArgumentException e){
                                 e.printStackTrace();
                                }
                                if(!list.isEmpty()){
                                    stemmedword = (String) list.get(0);
                                    AllWords.put( word, stemmedword );
                                    pr.println(stemmedword +","+i);
                                }
                                else{
                                    stemmedword=word;
                                    AllWords.put( word, word );
                                    pr.println(word +","+i);
                                }
                             }
                             if(!doc.containsKey(stemmedword)) {
                                doc.put(stemmedword,1);
                             }
                             else {
                                doc.put(stemmedword, doc.get(stemmedword)+1);
                             }
                         }
                     }
		}
                Iterator it = doc.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                    col.println(pairs.getKey() + "," + i + "," + pairs.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
            pr.close();
            col.close();
            PrintWriter writer = new PrintWriter("C:\\xampp\\mysql\\data\\vsm\\stemmed.csv");
            Iterator it = AllWords.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                writer.println(pairs.getKey() + "," + pairs.getValue());
                it.remove(); // avoids a ConcurrentModificationException
            }
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
            stmt = null;
        }
    }
    public static void regexChecker(String theRegex, String str2Check){
                List <String> stopWords = readStopWords();
		Pattern checkRegex = Pattern.compile(theRegex);
		Matcher regexMatcher = checkRegex.matcher( str2Check );
		while ( regexMatcher.find() ){
			if (regexMatcher.group().length() != 0){
                            if (!stopWords.contains(regexMatcher.group().trim().toLowerCase()))
				System.out.print( regexMatcher.group().trim().toLowerCase() + " " );
                                
                                /*
				System.out.println( "Start Index: " + regexMatcher.start());
				System.out.println( "Start Index: " + regexMatcher.end());
                                */
			}
		}
		System.out.println("\n");
		
	}
    public static void stopWords(){
        Pattern checkRegex = Pattern.compile("[a-zA-Z0-9']*");
        Matcher regexMatcher;
        String str;
        
        File file = new File("stopwords.txt");
        try {
            Scanner sc = new Scanner(file);
            PrintWriter writer = new PrintWriter("stopwords1.txt", "UTF-8");
            while (sc.hasNextLine()) {
                str = sc.nextLine();
                regexMatcher = checkRegex.matcher(str);
                while ( regexMatcher.find() ){
			if (regexMatcher.group().length() != 0){
                            writer.println(regexMatcher.group().trim());
			}
		}
            }
            sc.close();
            writer.close();
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
            
    
    public static List <String> readStopWords(){
        List <String> stopWords = new ArrayList(); 

	try
	    {
		Scanner scanner = new Scanner(new File(STOPWORDS));
		while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    stopWords.add(line);
                }
		scanner.close();
	    }
	catch (FileNotFoundException e)
	    {
		System.err.println(e.getMessage());
		System.exit(-1);
	    }

	return stopWords;
    }
}
