package com.rares.poller.model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Service {
  public static final String nameColumn = "name";
  public static final String urlColumn = "URL";
  public static final String statusColumn = "status";
  public static final String lastUpdateColumn = "lastUpdate";
  //Might also add the day of the week, like Saturday (but be aware the js formater got different results from this -> Saturday transformed to 6)
  private static final SimpleDateFormat formatter = new SimpleDateFormat("h:MM:ss");

  //this will be the id, performance reasons :)
  private String name;
  private String URL;
  private int status = 0;
  //Used Date and epoch time, but we don't do any operations, switched to string ( I don't recommand this but I got annoyed about Date errors and it gets late )
  //Faster, but if we change date format we need to update everything
  private String lastUpdate;

  private Service(){};

  public Service(String name, String URL, int status, String lastUpdate) {
    this.name = name;
    this.URL = URL;
    this.status = status;
    this.lastUpdate = lastUpdate;
  }

  public Service(String name, String URL) {
    this.name = name;
    this.URL = URL;
    this.status = 0;
    this.lastUpdate = formatter.format(new Date().getTime());
  }

  public String getName() {
    return name;
  }

  public String getURL() {
    return URL;
  }

  public int getStatus() {
    return status;
  }

  public String getLastUpdate() {
    return lastUpdate;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public void setLastUpdateToBeNow(){
    this.lastUpdate = formatter.format(new Date());
  }
  @Override
  public String toString() {
    return String.format("{name:%s,url:%s,status:%s,lastUpdate:%s}", this.name, this.URL, this.status, this.lastUpdate);
  }
}
