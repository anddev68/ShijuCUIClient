/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import cuiclient.connection.LoginReceiver;
import cuiclient.connection.PlayReceiver;
import cuiclient.connection.ServerThread;
import java.util.Scanner;



/**
 *
 * @author dell_user
 */
public class CUIClient {

    ServerThread thread;
    GameBoard gameBoard;
    
    int teamId;
    
    CUIClient(){
        
        
        
        
    }
    
    void start(){
        Scanner s = new Scanner(System.in);
        
        //  名前を入力
        System.out.println("Input your name (English only).");
        System.out.println("If press Enter,use default name.");
        String name = s.nextLine();
        
        //  サーバを入力
        System.out.println("Input server adress.");
        System.out.println("If press Enter,use default name,'localhost'");
        String server = s.nextLine();
        
        //  ポートを入力
        /*
        System.out.println("Input port number");
        System.out.println("If press Enter,use default portm13306");
        int port = s.nextInt();
        */
        
        //  サーバ接続用のスレッドを開始する
        thread = new ServerThread(name.isEmpty()?"YASUDAKEN":name, new MyPlayReceiver(),new MyLoginReceiver());
       
         //  サーバーに接続する
         if( !thread.connectServer(server.isEmpty()?"localhost":server,13306) ) return;
         log("connected server.");
       
         // スレッドを開始する
         thread.start();
         log("thread start.");
        
         
         // 以後すべてのやりとりはリシーバーを介して行います
         
         
         
        //  メインスレッドは空ループ
         while(true){
             
         }
         
        
    }
    
    void log(String str){
        System.out.println(str);
    }

    void startAI(){
        log("start AI.");

        MinMax minMax = new MinMax(teamId);
        minMax.setParams(0.686014,0.730685,0.520478,0.206630,0.265467);
        
        MinMax.ReturnValue result = minMax.minmax(4, gameBoard);
        Hand hand = result.optimized;
        System.out.print(hand);
        System.out.println(" score:"+result.score);
        this.thread.sendPlayMessage(hand.index, hand.x, hand.y);
        
        log("end AI.");
    }


    void search(){
        
    }
    

    
    
    /**
     * class MyLoginReceiver
     * Loginに関するコマンドをサーバから受信したときの処理です
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
     * Playに関するコマンドをサーバから受信したときの処理です
     */
    private class MyPlayReceiver implements PlayReceiver{
        //  内部ターンカウント [0-3]
        //  敵が1手打つか自分が1手打つごとに1こずつ増やし、
        //  4になった時点で0に戻して先行後攻入れ替えを行います
        int turn = 0;
        int firstTeamId = 0;    //  先行プレイヤーは必ず0です
            
        //  文字を送信したのでOKを待機する
        boolean waitOK;
        
        public MyPlayReceiver(){
            gameBoard = new GameBoard();
        }
        
        @Override
        public void onReceiveDoPlay() {
        }

        @Override
        public void onReceiveUnit(int T, int N, int X, int Y) {
            gameBoard.unitLocation[T][N].x = X;
            gameBoard.unitLocation[T][N].y = Y;
        }

        @Override
        public void onReceiveMultiLine() {
            //  ここでゲームボードを初期化する
            //  全部呼ばれるから初期化する必要あるのか微妙
        }

        @Override
        public void onReceiveTower(int N, int T) {
            gameBoard.towerHold[N] = T;
        }

        @Override
        public void onReceiveScore(int T, int S) {
            gameBoard.teamPoint[T] = S;
        }

        @Override
        public void onReceiveLineEnd() {
            //  ボードのターンを変更する
            gameBoard.turnState = GameBoard.STATE_PLAY_TURN1 + turn;
            gameBoard.firstTeamId = firstTeamId;
            
            //  ボードが更新されたのでAIを使います
            waitOK = true;
            startAI();
        }

        @Override
        public void onReceiveOK() {
            if(waitOK){ //  PlayMessageを送ったあとの待機状態かどうか判定
                waitOK = false;
                //  次のターンへ進めます
                nextTurn();
            }
        }

        @Override
        public void onReceiveCoundNotMove() {
        }

        @Override
        public void onReceivePlayed() {
            //  次のターンへ進めます
            nextTurn();
        }
        
        void nextTurn(){
            log("nextTurn() "+turn);
            turn++;
            if(turn>=4){
                turn = 0;
                firstTeamId = (firstTeamId==0)? 1: 0;
            }
        }
        
    }
    
    
    /**
     * ターン管理用のクラスです
     * AIに必要なため一時的に作成しておきます
     * あとで必ず設計しなおしてください
     */
    private class TurnCounter{
        
    }
    
    
    
    
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new CUIClient().start();
        
        
    }
    
}
