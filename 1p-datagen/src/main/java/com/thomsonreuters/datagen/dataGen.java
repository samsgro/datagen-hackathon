package com.thomsonreuters.datagen;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.apache.wink.json4j.JSONException;
import org.apache.wink.json4j.OrderedJSONObject;

public class dataGen {

  public static void main(String[] args) throws IOException, JSONException, org.apache.wink.json4j.JSONException {
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
    genData(patentStr, 5,"USGRANTPATENT");
    genData(litStr, 5,"LITDOC");
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
      System.out.println(data);
      osw.write(data + "\n");
    }
    osw.close();
  }
}
