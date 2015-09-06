/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.connection;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 *
 * @author dell_user
 */
public class Parser {
    
    public final int STATE_NOCONECTION = 00;
    public final int STATE_INIT = 01;
    public final int STATE_GAME = 10;
    public final int STATE_VIEW_GETBOARD = 11;
    public final int STATE_PLAY = 12;
    public final int STATE_PLAY_GETBOARD = 13;
    public final int STATE_FINISH = 20;
    public final int STATE_FINISH_GETBOARD = 21;
    
    private Pattern MSGPTN = Pattern.compile("([0-9]+) (.*)");
    private Pattern TEAMIDMSGPTN = Pattern.compile("102 TEAMID ([0-1])");
    private Pattern ADVERSARYMSGPTN = Pattern.compile("104 ADVERSARY (.*)");
    private Pattern GAMEENDMSGPTN = Pattern.compile("502 GAMEEND ([0-1])");
    private ArrayList<String> boardInfo;
    
    private PlayReceiver playReceiver;
    
    public Parser(){
        
    }
    
    
    public void parse(String str){
        
        
        
    }
    
    
    
    
}
