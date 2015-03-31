package com.thomsonreuters.datagen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

public class dataGen {
	
	@Option(name="-patents",usage="number of patents")
    private int numPatents = 1;
	
	@Option(name="-literature",usage="number of literature records")
    private int numLiterature = 1;

  public static void main(String[] args) throws IOException, JSONException, org.apache.wink.json4j.JSONException, InterruptedException {
      new dataGen().doMain(args);
  }

private void doMain(String[] args) throws JSONException, IOException, InterruptedException {
	CmdLineParser parser = new CmdLineParser(this);
    
    // if you have a wider console, you could increase the value;
    // here 80 is also the default
    parser.setUsageWidth(80);
	
	try {
        // parse the arguments.
        parser.parseArgument(args);

        // you can parse additional arguments if you want.
        // parser.parseArgument("more","args");

        // after parsing arguments, you should check
        // if enough arguments are given.
        //if( arguments.isEmpty() )
       //     throw new CmdLineException(parser,"No argument is given");

    } catch( CmdLineException e ) {
        // if there's a problem in the command line,
        // you'll get this exception. this will report
        // an error message.
        System.err.println(e.getMessage());
        System.err.println("java SampleMain [options...] arguments...");
        // print the list of available options
        parser.printUsage(System.err);
        System.err.println();


        return;
    }
	
	
	
	String patentStr   = "{"+"'tabName' : 'Patents'," 
                          + "'id' : 'UID'," 
                          + "'patNum':'USGRANTPATENT'," 
                          + "'pubdate': 'DATE',"
                          + "'Assignee' : 'COMPANY_NAME', " 
                          + "'inventor' : 'NAME'," 
                          + "'Address' : 'ADDRESS', " 
                          + "'NonPatentCitations':'LITDOC'," 
                          + "'abstract': 'SENTENCE'," 
                          + "}";
    String litStr=     "{"+ "'tabName' : 'Literature'," 
                          + "'id' : 'UID'," 
                          + "'wos-id':'LITDOC'," 
                          + "'pubdate': 'DATE',"
                          + "'Assignee' : 'COMPANY_NAME', " 
                          + "'inventor' : 'NAME'," 
                          + "'Address' : 'ADDRESS', " 
                          + "'refPatent':'USGRANTPATENT'," 
                          + "'abstract': 'SENTENCE'," 
                          + "}";
    genData(patentStr, numPatents,"USGRANTPATENT");
    genData(litStr, numLiterature,"LITDOC");
    Thread.sleep(60000);
}
  
  public static void genData(String jsonStr, int num, String type) throws org.apache.wink.json4j.JSONException, IOException {
    OrderedJSONObject jsonObj = new OrderedJSONObject(jsonStr);
    int rec = 0;
    String tabName = jsonObj.getString("tabName");
    File file = new File(tabName+".txt");
    FileOutputStream out = new FileOutputStream(file);
    OutputStreamWriter osw = new OutputStreamWriter(out); 
    while ( rec++ < num) {
      String data = dataUtil.genDataRecord(jsonObj,type);
      //System.out.println(data);
      osw.write(data + "\n");
    }
    osw.close();
  }
}
