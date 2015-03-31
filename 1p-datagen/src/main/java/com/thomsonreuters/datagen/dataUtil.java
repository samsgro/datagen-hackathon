package com.thomsonreuters.datagen;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.company.Company;
import io.codearte.jfairy.producer.person.Address;
import io.codearte.jfairy.producer.person.Person;
import io.codearte.jfairy.producer.text.TextProducer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.wink.json4j.OrderedJSONObject;

public class dataUtil {
  static Fairy fairy = Fairy.create();
  static TextProducer text = fairy.textProducer();
  
  public static String genDataRecord(OrderedJSONObject JsonObj, String dtype) throws org.apache.wink.json4j.JSONException, IOException {
    Iterator<?> keys = JsonObj.getOrder();
    String tabName = JsonObj.getString("tabName");
 
    String filename= tabName+"_ids.txt";
    FileWriter fw = new FileWriter(filename,true); //the true will append the new data
    //appends the string to the file
    
    int size = JsonObj.size();
    String data = "";
     while (keys.hasNext()) {
      String parameter = keys.next().toString();
      String datatype = JsonObj.getString(parameter);
      String datavalue  = dataUtil.dataGenerator(datatype);
      data = data + datavalue;
      if (StringUtils.equals(datatype, dtype)) {
        fw.write(datavalue+"\n");
      }
      if ( size-- != 1 && (!StringUtils.isBlank(datavalue))) {
        data = data + ",";
      }
    }
    fw.close();
    return data;
  }
  
  public  static String dataGenerator(String datatype){
    String data = "";
    switch(datatype) {
      case "UID" :
        Integer dt=  genId(1,20000000);
        data = dt.toString();
        break;
      case "TEXT" :
        data = data + textGen(25);
        break;
      case "SENTENCE" :
        data = data + textGen(70);
        break;
      case "USGRANTPATENT" :
        data = data + getusrantPatents(2000);
        break;
      case "LITDOC" :
        data = data + getwosDocs();
        break;
      case "COMPANY_NAME" :
        data = data + getCompanyName();
        break;
      case "ADDRESS" :
        data = data + getAddress();
        break;
      case "NAME" :
        data = data + getName();
        break;
      case "DATE" :
        data = data + getRandomDates();
        break;    
      default:
        break;
        
    }
    return data;

  }
  private static int genId(int min, int max) {
    return (min + (int)(Math.random()*max)); 
  }
  private static long genId(long min, long max) {
    return (min + (long)(Math.random()*max)); 
  }

  private static Timestamp getRandomTimeBetweenTwoDates (String startDate, String endDate) {
    long offset = Timestamp.valueOf(startDate+ " 00:00:00").getTime();
    long end = Timestamp.valueOf(endDate+" 00:00:00").getTime();
    long diff = end - offset + 1;
    Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
    return rand;
  }
  public static String getRandomDates () {
    long offset = Timestamp.valueOf("1900-01-01 00:00:00").getTime();
    long end = Timestamp.valueOf("2020-01-01 00:00:00").getTime();
    long diff = end - offset + 1;
    Timestamp rand = new Timestamp(offset + (long)(Math.random() * diff));
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    return DATE_FORMAT.format(rand);
  }
  
  public static Date randomDate(Date start, Date end) {
      return new Date((long) (start.getTime() + Math.random() * (end.getTime() - start.getTime())));
  }

  public static String textGen(int size) {
    return text.randomString(size);
  }
  
  public static String getCompanyName() {
    Company company = fairy.company();
    return company.name();
  }
  
  public static String getName() {
    Person person = fairy.person();
    return person.fullName();
  }
  public static String getAddress() {
    Person person = fairy.person();
    Address address = person.getAddress();
    return address.streetNumber() + " " +  address.street() + " " +   address.getCity()  + " " +  address.getPostalCode();
  }
  public static String getdomain() {
    Company company = fairy.company();
    return company.domain();
  }
  public static String getusrantPatents(int year) {
    String usgrantPatent  = "US";
    String kindCode = (year < 2000)? "A" : "B2";
    int lowseed = ( year < 1950) ? 100000 : 1000000;
    int highseed = ( year < 1950) ? 900000 : 9000000;
    return usgrantPatent + genId(lowseed,highseed) + kindCode;
  }
  
  public static String getwosDocs() {
    String wosDocument  = "WOS:";
    long lowseed = 0000000010000000L;
    long highseed =9999999999999999L;
    return wosDocument + genId(lowseed,highseed);
    
  }
}
