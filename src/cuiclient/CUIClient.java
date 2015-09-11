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
        
        //  名前を�?��?
        System.out.println("Input your name (English only).");
        System.out.println("If press Enter,use default name.");
        String name = s.nextLine();
        
        //  サーバを入�?
        System.out.println("Input server adress.");
        System.out.println("If press Enter,use default name,'localhost'");
        String server = s.nextLine();
        
        //  ポ�?�トを入�?
        /*
        System.out.println("Input port number");
        System.out.println("If press Enter,use default portm13306");
        int port = s.nextInt();
        */
        
        //  サーバ接続用のスレ�?ドを開始す�?
        thread = new ServerThread(name.isEmpty()?"YASUDAKEN":name, new MyPlayReceiver(),new MyLoginReceiver());
       
         //  サーバ�?�に接続す�?
         if( !thread.connectServer(server.isEmpty()?"localhost":server,13306) ) return;
         log("connected server.");
       
         // スレ�?ドを開始す�?
         thread.start();
         log("thread start.");
        
         
         // 以後すべての�?りとり�?�リシーバ�?�を介して行いま�?
         
         
         
        //  メインスレ�?ド�?�空ルー�?
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
     * Loginに関するコマンドをサーバから受信したとき�?�処�?で�?
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
     * Playに関するコマンドをサーバから受信したとき�?�処�?で�?
     */
    private class MyPlayReceiver implements PlayReceiver{
            
        //  �?字を送信したのでOKを�?機す�?
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
            //  ここでゲー�?ボ�?�ドを初期化す�?
            //  全部呼ばれるから初期化する�?要ある�?�か微�?
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
            //  ボ�?�ドが更新された�?�でAIを使�?ま�?
            waitOK = true;
            startAI();
        }

        @Override
        public void onReceiveOK() {
            if(waitOK){ //  PlayMessageを�?�ったあとの�?機状態かど�?か判�?
                waitOK = false;
                //  次のターンへ進めま�?
                gameMaster.nextPhase();
            }
        }

        @Override
        public void onReceiveCoundNotMove() {
        }

        @Override
        public void onReceivePlayed() {
            //  次のターンへ進めま�?
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
