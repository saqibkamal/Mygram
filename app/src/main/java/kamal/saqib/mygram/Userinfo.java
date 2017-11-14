package kamal.saqib.mygram;


import java.util.ArrayList;

/**
 * Created by Saqib kamal on 01-10-2017.
 */

public class Userinfo {

    public String name,address,email;
    int defaultprofilepic;
    ArrayList<String> urllist=new ArrayList<String >(),allowed_user_email=new ArrayList<String>();


    Userinfo()
    {

    }



    public Userinfo(String a, String b,String c){
        this.name=a;
        this.address=b;
        this.defaultprofilepic=-1;
        this.email=c;



    }
    public String getname(){
        return this.name;
    }
    public String getEmail(){
        return this.email;
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
    public void add_allowed_user(String s){
        this.allowed_user_email.add(s);
    }
    public void remove_url(int i){
        this.urllist.remove(i);
    }
    public ArrayList<String> get_urllist(){

        return this.urllist;
    }
    public ArrayList<String> get_allowed_userlist(){
        return this.allowed_user_email;
    }

}
