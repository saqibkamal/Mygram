package kamal.saqib.mygram;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Saqib kamal on 01-10-2017.
 */

public class Userinfo {

    public String name,address;
    int defaultprofilepic;
    ArrayList<String> urllist=new ArrayList<String>();

    Userinfo()
    {

    }



    public Userinfo(String a, String b){
        this.name=a;
        this.address=b;
        this.defaultprofilepic=-1;



    }
    public String getname(){
        return this.name;
    }
    public String getAddress(){
        return this.address;
    }
    public int getDefaultprofilepic(){
        return this.defaultprofilepic;
    }

    public void setDefaultprofilepic(int i){
        this.defaultprofilepic=i;
    }

    public void add_url(String s){
       this.urllist.add(s);
    }
    public ArrayList<String> get_urllist(){

        return this.urllist;
    }

}
