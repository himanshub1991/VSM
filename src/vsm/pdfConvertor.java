package vsm;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.w3c.dom.DOMException;
public class pdfConvertor {
    
    @SuppressWarnings("empty-statement")
    public void pdf2text(String filename) throws FileNotFoundException, UnsupportedEncodingException{
        String outFile = filename.replaceAll(".pdf","");
        outFile += ".csv";
        PrintWriter writer=new PrintWriter(outFile, "UTF-8");;
        int i,n;
        try {
            
            File file = new File(filename);
            PdfReader reader = new PdfReader(filename);
            n = reader.getNumberOfPages();
            String str=""; //Extracting the content from a particular page.
            int x=1;
            
            //checking the initial page
            for(i=1;i<=n;i++) {
                str = PdfTextExtractor.getTextFromPage(reader, i);
                if(str.contains("Early Publication:") ||  ( str.contains("Application No.")  && str.contains("Title of the invention")&& str.contains("Name of priority country") )) {
                    x=i;
                    break;
                }
            }
            
            //writer = new PrintWriter(outFile, "UTF-8");
          /*  for(int i=x;i<=n;i++) {
                str = PdfTextExtractor.getTextFromPage(reader, i);
                writer.println(str);
                writer.println("");
                writer.println("");
            }
          */
            //new code start
            for(i=x;i<=n;i++) {
                try {
                //HSSFRow dataRow1 = sampleDataSheet.createRow(count++);
                String str_ = PdfTextExtractor.getTextFromPage(reader, i);
                str=str_;
                String res = str.replaceAll("[\\r\\n]|[\\s]{2,10}", " ");
                String res1=res.replaceAll(",", "");
                String str1="",str2="",str3="",str11="",str22="",str33="";
                str=res1;
                if(str.contains("AMENDMENT UNDER SEC.57") || !str.contains("Application No")  || !str.contains("Title of the invention")|| !str.contains("Abstract")) break;
              /*  str=str.replaceAll(": ", ":");
                str=str.replaceAll("   ", " ");
                str=str.replaceAll("  ", " ");
                str=str.replaceAll("    ", " ");
                str=str.replaceAll("     ", " "); */
                //System.out.println(str);
                System.out.println("****************Result******************************");
                
                String[][] replacements = {{"The Patent Office Journal", ""}, 
                                            {"No. of Claims", ""},
                                            {":", ""},
                                           {"No. of Pages", ""}};

                //loop over the array and replace
                String strOutput = str;
                for(String[] replacement: replacements) {
                    strOutput = strOutput.replace(replacement[0], replacement[1]);
                }
                str=strOutput;                
                Pattern checkApp_no = Pattern.compile("([0-9]{1,5}(\\s)?/(\\s)?[A-Z]{3,5}(\\s)?/(\\s)?[0-9]{4}|IN(\\\\s)?/(\\\\s)?PCT(\\\\s)?/(\\s)?[0-9]{4}(\\s)?/(\\s)?[0-9]{1,5}(\\\\s)?/(\\\\s)?[A-Z]{3,5})");
		Matcher regexMatcher = checkApp_no.matcher( str );
		while ( regexMatcher.find() ){
			if (regexMatcher.group().length() != 0){
                                str1=regexMatcher.group().trim().toLowerCase();
                                str11=str1.replaceAll("application no.", "");
				System.out.print( str11 + " " );
			}
		}
                Pattern checkTitle = Pattern.compile("Title of the invention[^(]{10,}");
		Matcher TitleMatcher = checkTitle.matcher( str );
		while ( TitleMatcher.find() ){
			if (TitleMatcher.group().length() != 0){
                                str2=TitleMatcher.group().trim().toLowerCase();
                                str22=str2.replace("title of the invention","");
				System.out.print(str22  + " " );
			}
		}
             
                Pattern checkAbstract = Pattern.compile("Abstract[^*]{50,}");
		Matcher AbstractMatcher = checkAbstract.matcher( str );
		while ( AbstractMatcher.find() ){
			if (AbstractMatcher.group().length() != 0){
                                String str31,str32;
                                str3 = AbstractMatcher.group().trim().toLowerCase();
                                str31=str3.replace("abstract", "");
                                str33=str31.replace("pages", "");
				System.out.print( str33 + " " );
			}
		}
                if(str.contains("1567/MUM/2009"))
                {
                    System.out.println("break");
                }
                //if(str1.isEmpty()  || str3.isEmpty())
                {
                 //   System.out.println("error");
                }
               // else
                {
                    String new_str = str11 + "," + str22 + "," + str33;
                    writer.println(new_str);
                }
             /*   String ch="";
                int flag=0;
                if(str.contains("Application No") && str.contains("(19)")  && str.indexOf("Application No") < str.indexOf("(19)")) {
                    ch = str.substring(str.indexOf("Application No")+15,str.indexOf("(19)"));
                    writer.print(ch+";");
                }else flag=1;
                if(str.contains("Title of the invention:") && str.contains("(51)")  && str.indexOf("Title of the invention:") < str.indexOf("(51)")) {
                    ch = str.substring(str.indexOf("Title of the invention:")+23,str.indexOf("(51)"));
                    writer.print(ch+";\n");
                }else flag=1;
               *//* String wq=null;
                if(str.contains("(22)") && flag==0 ) {str = str.substring(str.indexOf("(22)"));
                
                if(str.contains("Date of filing of Application")&& flag==0)wq=str.substring(str.indexOf("Date of filing of Application")+30);
                else flag=1;
                if(str.contains("(43)")&& flag==0 && wq.indexOf("(43)") > 0 )ch = wq.substring(0,wq.indexOf("(43)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                str=wq;
                    System.out.println("date of filing of application = " + ch);
                    //dataRow1.createCell((short)1).setCellValue(ch);
                    writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("Publication Date")&& flag==0) {wq = str.substring(str.indexOf("Publication Date")+17);
                if(str.contains("(54)")&& flag==0 && wq.indexOf("(54)") > 0)ch = wq.substring(0,wq.indexOf("(54)"));
                else flag=1;
                str=wq;
                ch=ch.replaceAll(":", "");
                System.out.println("date of publication date = "+ch);
                //dataRow1.createCell((short)2).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("Title of the invention")&& flag==0){wq = str.substring(str.indexOf("Title of the invention")+23);
                str=wq;
                if(str.contains("(51)")&& flag==0&& wq.indexOf("(51)") > 0)wq=wq.substring(0,wq.indexOf("(51)"));
                else flag=1;
                wq=wq.replaceAll(":", "");
                System.out.println("Title = "+wq);
                //dataRow1.createCell((short)3).setCellValue(wq);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(51)")&& flag==0){str=str.substring(str.indexOf("(51)"));
                if(str.contains("International classification")&& flag==0)ch=str.substring(str.indexOf("International classification")+29);
                else flag=1;
                if(str.contains("(31)")&& flag==0&& ch.indexOf("(31)") > 0)ch=ch.substring(0,ch.indexOf("(31)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("international classification ="+ch);
                //dataRow1.createCell((short)4).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                int k=-1;

                if(str.contains("(31)")&& flag==0){wq=str.substring(str.indexOf("(31)"));
                if(str.contains("Priority Document")&& flag==0)ch=wq.substring(wq.indexOf("Priority Document")+21);
                else flag=1;
                if(str.contains("(32)")&& flag==0&& ch.indexOf("(32)") > 0)ch=ch.substring(0, ch.indexOf("(32)"));
               else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("Priority Document no="+ch);
                //dataRow1.createCell((short)5).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(32)")&& flag==0){wq=str.substring(str.indexOf("(32)"));
                if(str.contains("Priority Date")&& flag==0)ch=wq.substring(wq.indexOf("Priority Date")+14);
                else flag=1;
                if(str.contains("(33)")&& flag==0&& ch.indexOf("(33)") > 0)ch=ch.substring(0, ch.indexOf("(33)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("Priority Date="+ch);
                //dataRow1.createCell((short)6).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(33)")&& flag==0){wq=str.substring(str.indexOf("(33)"));
                if(str.contains("Name of priority country")&& flag==0)ch=wq.substring(wq.indexOf("Name of priority country")+25);
                else flag=1;
                if(str.contains("(86)")&& flag==0&& ch.indexOf("(86)") > 0)ch=ch.substring(0, ch.indexOf("(86)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("Name of priority country="+ch);
                //dataRow1.createCell((short)7).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(86)")&& flag==0){wq=str.substring(str.indexOf("(86)"));
                if(str.contains("Fil")&& flag==0)wq=wq.substring(wq.indexOf("Fil"));
                else flag=1;
                wq=wq.substring(wq.indexOf(" ")+1);
                wq=wq.substring(wq.indexOf(" ")+1);
                ch=wq.substring(0, wq.indexOf(" "));
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
               System.out.println("International Application no="+ch);
                //dataRow1.createCell((short)8).setCellValue(ch);
                writer.print(ch+"\t");
                ch=wq.substring(wq.indexOf(" ")+1);
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
                if(str.contains("(87)")&& flag==0&& ch.indexOf("(87)") > 0)ch=ch.substring(0, ch.indexOf("(87)"));
               else flag=1;
                System.out.println("Filing Date="+ch);
                //dataRow1.createCell((short)9).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
               // k=wq.indexOf(":");
                if(str.contains("(87)")&& flag==0)str = str.substring(str.indexOf("(87)"));
                else flag=1;

                
                if(str.contains("(87)")&& flag==0) {wq=str.substring(str.indexOf("(87)"));
               
                if(str.contains("Inter")&& flag==0)ch=wq.substring(wq.indexOf("Inter")+29);
                else flag=1;
                if(str.contains("(61)")&& flag==0&& ch.indexOf("(61)") > 0)ch=ch.substring(0, ch.indexOf("(61)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
               System.out.println("International public number="+ch);
                //dataRow1.createCell((short)10).setCellValue(ch);
               writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(61)")&& flag==0){wq=str.substring(str.indexOf("(61)"));
                if(str.contains("Fil")&& flag==0)wq=wq.substring(wq.indexOf("Fil"));
                else flag=1;
                wq=wq.substring(wq.indexOf(" ")+1);
                wq=wq.substring(wq.indexOf(" ")+1);
                ch=wq.substring(0, wq.indexOf(" "));
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
               System.out.println("patent of addition to Application no="+ch);
                //dataRow1.createCell((short)11).setCellValue(ch);
               writer.print(ch+"\t");

                ch=wq.substring(wq.indexOf(" ")+1);
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
                if(str.contains("(62)")&& flag==0&& ch.indexOf("(62)") > 0)ch=ch.substring(0, ch.indexOf("(62)"));
               else flag=1;
                System.out.println("Filing Date="+ch);
                //dataRow1.createCell((short)12).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(62)")&& flag==0) {wq=str.substring(str.indexOf("(62)"));
                if(str.contains("Fil")&& flag==0)wq=wq.substring(wq.indexOf("Fil"));
                else flag=1;
                wq=wq.substring(wq.indexOf(" ")+1);
                wq=wq.substring(wq.indexOf(" ")+1);
                ch=wq.substring(0, wq.indexOf(" "));
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
               System.out.println("divisional to Application no="+ch);
                //dataRow1.createCell((short)13).setCellValue(ch);
               writer.print(ch+"\t");
                ch=wq.substring(wq.indexOf(" ")+1);
              //  System.out.println(ch);
                ch=ch.replaceAll(":", "");
                if(str.contains("(71)")&& flag==0&& ch.indexOf("(71)") > 0)ch=ch.substring(0, ch.indexOf("(71)"));
               else flag=1;
                System.out.println("Filing Date="+ch);
                //dataRow1.createCell((short)14).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(71)")&& flag==0) {wq=str.substring(str.indexOf("(71)"));
                if(str.contains("Name of Applicant")&& flag==0)ch=wq.substring(wq.indexOf("Name of Applicant")+18);
                else flag=1;
                if(str.contains("Address of Applicant")&& flag==0&& ch.indexOf("Address of Applicant") > 0)ch=ch.substring(0, ch.indexOf("Address of Applicant"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("Name of Applicant="+ch);
                //dataRow1.createCell((short)15).setCellValue(ch);
                writer.print(ch+"\t");
              
                }else flag=1;
                if(str.contains("Address of Applicant")&& flag==0) {wq=str.substring(str.indexOf("Address of Applicant"));
                if(str.contains("Address of Applicant")&& flag==0)ch=wq.substring(wq.indexOf("Address of Applicant")+21);
                else flag=1;
                if(str.contains("(72)") && flag==0 && ch.indexOf("72") > 0)ch=ch.substring(0, ch.indexOf("(72)"));
                else flag=1;
                ch=ch.replaceAll(":", "");
                System.out.println("Address="+ch);
                //dataRow1.createCell((short)16).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(72)")&& flag==0){
                    wq=str.substring(str.indexOf("(72)"));
                if(str.contains("Name of Inventor")&& flag==0)ch=wq.substring(wq.indexOf("Name of Inventor")+17);
                else flag=1;
                    if(str.contains("(57)")&& flag==0&& ch.indexOf("57") > 0)ch=ch.substring(0, ch.indexOf("(57)"));
                else flag=1;
                    ch=ch.replaceAll(":", "");
                System.out.println("Name of Inventor="+ch);
                //dataRow1.createCell((short)17).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("(57)")&& flag==0) {
                    wq=str.substring(str.indexOf("(57)"));

                if(str.contains("Abstract")&& flag==0)ch=wq.substring(wq.indexOf("Abstract")+9);
                else flag=1;
                    if(str.contains("No. of Pages")&& flag==0&& ch.indexOf("No. of Pages") > 0)ch=ch.substring(0, ch.indexOf("No. of Pages"));
                else {
                          //  System.out.println("Q"+ch+"Q");
                        //    ch=ch.substring(ch.indexOf("Abstract")+8);
                    }
                ch=ch.replaceAll(":", "");
                System.out.println("Abstract="+ch);
                //dataRow1.createCell((short)18).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("No. of Pages")&& flag==0) {
                    wq=str.substring(str.indexOf("No. of Pages"));

                if(str.contains("No. of Pages")&& flag==0)ch=wq.substring(wq.indexOf("No. of Pages")+13);
                else flag=1;
                    if(str.contains("No. of Claims")&& flag==0&& ch.indexOf("No. of Claims") > 0)ch=ch.substring(0, ch.indexOf("No. of Claims"));
                ch=ch.replaceAll(":", "");
                System.out.println("No. of Pages="+ch);
                    //dataRow1.createCell((short)19).setCellValue(ch);
                writer.print(ch+"\t");
                }else flag=1;
                if(str.contains("No. of Claims")&& flag==0) {
                    wq=str.substring(str.indexOf("No. of Claims"));

                if(str.contains("No. of Claims")&& flag==0)ch=wq.substring(wq.indexOf("No. of Claims")+14);
                else flag=1;
                    if(ch.indexOf(" ") > 0)ch=ch.substring(0, ch.indexOf(" "));
                ch=ch.replaceAll(":", "");
                System.out.println("No. of Claims="+ch);
                //dataRow1.createCell((short)20).setCellValue(ch);
                writer.print(ch+"\t");
                //System.out.println(count);
                }else flag=1;  */
    /*          }  wq=str.substring(str.indexOf("The Patent Office Journal"));
                ch=wq.substring(wq.indexOf(" ")+23);
                ch=ch.substring(0, ch.indexOf(" "));
               // System.out.println("The Patent Office Journal="+ch);
                k=wq.indexOf(":");
                str = str.substring(0,str.indexOf("The Patent Office Journal")) + str.substring(str.indexOf("The Patent Office Journal")+k+ch.length()+1);
      */      }catch (DOMException e) {
                        System.out.println(e);
                }
            }
            //new code end
            
        } catch (IOException ex) {
            Logger.getLogger(pdfConvertor.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            
            writer.close();
        }
        
    }
    
}
