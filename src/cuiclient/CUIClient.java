/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import cuiclient.connection.LoginReceiver;
import cuiclient.connection.PlayReceiver;
import cuiclient.connection.ServerThread;
import cuiclient.game.GameMaster;
import java.util.Scanner;



/**
 *
 * @author dell_user
 */
public class CUIClient {

    ServerThread thread;
    GameMaster gameMaster;
    
    int teamId;
    
    CUIClient(){
        
        
        
        
    }
    
    void start(){
        Scanner s = new Scanner(System.in);
        
        //  ååãå?¥å?
        System.out.println("Input your name (English only).");
        System.out.println("If press Enter,use default name.");
        String name = s.nextLine();
        
        //  ãµã¼ããå¥å?
        System.out.println("Input server adress.");
        System.out.println("If press Enter,use default name,'localhost'");
        String server = s.nextLine();
        
        //  ãã?¼ããå¥å?
        /*
        System.out.println("Input port number");
        System.out.println("If press Enter,use default portm13306");
        int port = s.nextInt();
        */
        
        //  ãµã¼ãæ¥ç¶ç¨ã®ã¹ã¬ã?ããéå§ãã?
        thread = new ServerThread(name.isEmpty()?"YASUDAKEN":name, new MyPlayReceiver(),new MyLoginReceiver());
       
         //  ãµã¼ãã?¼ã«æ¥ç¶ãã?
         if( !thread.connectServer(server.isEmpty()?"localhost":server,13306) ) return;
         log("connected server.");
       
         // ã¹ã¬ã?ããéå§ãã?
         thread.start();
         log("thread start.");
        
         
         // ä»¥å¾ãã¹ã¦ã®ã?ãã¨ãã?¯ãªã·ã¼ãã?¼ãä»ãã¦è¡ãã¾ã?
         
         
         
        //  ã¡ã¤ã³ã¹ã¬ã?ãã?¯ç©ºã«ã¼ã?
         while(true){
             
         }
         
        
    }
    
    void log(String str){
        System.out.println(str);
    }

    void startAI(){
        log("start AI.");

        //MinMax minMax = new MinMax(teamId);
        //minMax.setParams(0.686014,0.730685,0.520478,0.206630,0.265467);
        
        //MinMax.ReturnValue result = minMax.minmax(4, gameBoard);
        AlphaBeta alphaBeta = new AlphaBeta(teamId);
        alphaBeta.setParams(0.686014, 0.730685, 0.520478, 0.206630, 0.265467);

        //AlphaBeta.ReturnValue result = alphaBeta.alphabeta(3, gameBoard);
        
        /*
        Hand hand = result.optimized;
        System.out.print(hand);
        System.out.println(" score:"+result.score);
        this.thread.sendPlayMessage(hand.index, hand.x, hand.y);
        */
        
        log("end AI.");
    }


    void search(){
        
    }
    

    
    
    /**
     * class MyLoginReceiver
     * Loginã«é¢ããã³ãã³ãããµã¼ãããåä¿¡ããã¨ãã?®å¦ç?ã§ã?
     */
    private class MyLoginReceiver implements LoginReceiver{

        @Override
        public void onReceiveTeamId(int id) {
            teamId = id;
        }

        @Override
        public void onReceiveAdversary(String name) {
            
        }
        
    }
    
    
    /**
     * class MyPlayReceiver
     * Playã«é¢ããã³ãã³ãããµã¼ãããåä¿¡ããã¨ãã?®å¦ç?ã§ã?
     */
    private class MyPlayReceiver implements PlayReceiver{
            
        //  æ?å­ãéä¿¡ããã®ã§OKãå¾?æ©ãã?
        boolean waitOK;
        
        public MyPlayReceiver(){
            gameMaster = new GameMaster();
        }
        
        @Override
        public void onReceiveDoPlay() {
        }

        @Override
        public void onReceiveUnit(int T, int N, int X, int Y) {
            gameMaster.setUnitLocation(X, Y, N, T);
        }

        @Override
        public void onReceiveMultiLine() {
            //  ããã§ã²ã¼ã?ãã?¼ããåæåãã?
            //  å¨é¨å¼ã°ããããåæåããå¿?è¦ããã?®ãå¾®å¦?
        }

        @Override
        public void onReceiveTower(int N, int T) {
            gameMaster.setTower(N, T);
        }

        @Override
        public void onReceiveScore(int T, int S) {
            gameMaster.setPoint(T, S);
        }

        @Override
        public void onReceiveLineEnd() {
            //  ãã?¼ããæ´æ°ãããã?®ã§AIãä½¿ã?ã¾ã?
            waitOK = true;
            startAI();
        }

        @Override
        public void onReceiveOK() {
            if(waitOK){ //  PlayMessageãé?ã£ããã¨ã®å¾?æ©ç¶æãã©ã?ãå¤å®?
                waitOK = false;
                //  æ¬¡ã®ã¿ã¼ã³ã¸é²ãã¾ã?
                gameMaster.nextPhase();
            }
        }

        @Override
        public void onReceiveCoundNotMove() {
        }

        @Override
        public void onReceivePlayed() {
            //  æ¬¡ã®ã¿ã¼ã³ã¸é²ãã¾ã?
            gameMaster.nextPhase();
        }
      
        

        
    }
    
    

    
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CUIClient().start();
        
        
    }
    
}
