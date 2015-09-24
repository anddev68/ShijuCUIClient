/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import cuiclient.ai.RelationHelper;
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
        
        //  ここで1回読んでおくことでRelationパラメータを初期化する
        RelationHelper.getInstance();
        
        Scanner s = new Scanner(System.in);
        
        //  名前を�?��?
        System.out.println("Input your name (English only).");
        System.out.println("If press Enter,use default name.");
        String name = s.nextLine();
        
        //  サーバのアドレスを入力する
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
        
        //System.out.print("current score:");
        //System.out.println( gameMaster.evaluate(0, null) );
        
        
        System.out.print("start AI ");
        

        //  タワーの本数が優勢であればAlphaBetaで行く
        System.out.println();
        cuiclient.ai.AlphaBeta alphaBeta = new cuiclient.ai.AlphaBeta(teamId);
        cuiclient.ai.AlphaBeta.ReturnValue result = alphaBeta.alphabeta(4, gameMaster);
        System.out.println();
               
        Hand hand = result.optimized;
        System.out.println();
        System.out.print(hand);
        System.out.println(" score:"+result.score);
        this.thread.sendPlayMessage(hand.getIndex(), hand.getX(), hand.getY());
        
        
        log("end AI.");
    }


    /**
     * エラー処理を加えてAIの思考を開始する
     * 
     */
    void startAIWithCovering(){
        
    }
    
    /**
     * エラーなどが起きた場合とりあえず何かを送ります
     * これはゲームボードを加味します
     */
    void sendRecoveredPlayMessage(){
        
    }
    

    
    /**
     * テストメソッド
     * 評価関数が適切に動いているかどうかをチェックするためのもの
     */
    void test(){
        gameMaster = new GameMaster();    
        gameMaster.setUnitLocation(4, 7, 0, 0);
        gameMaster.setUnitLocation(4, 7, 1, 0);
        gameMaster.setUnitLocation(4, 7, 2, 0);
        gameMaster.setUnitLocation(4, 6, 3, 0);
        gameMaster.setUnitLocation(4, 2, 0, 1);
        gameMaster.setUnitLocation(5, 2, 1, 1);
        gameMaster.setUnitLocation(4, 1, 2, 1);
        gameMaster.setUnitLocation(4, 1, 3, 1);
        gameMaster.setTower(0, -1);
        gameMaster.setTower(1, -1);
        gameMaster.setTower(2, -1);
        double score = gameMaster.evaluate(0, 0);
        System.out.println("score:");
        System.out.println(score);
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
            
        //  ボードを受信し、OK待機状態になります
        boolean waitOK;
        
        //  couldMoveが呼ばれたときはリカバリーモードへ移行する
        //  次にOKが呼ばれるまでリカバリーモードで動かします「
        boolean onRecoveryMode;
        
        
        //  couldmoveが呼ばれた回数
        int errorCount;
        
        public MyPlayReceiver(){
            gameMaster = new GameMaster();
            errorCount = 0;
            onRecoveryMode = false;
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
            if(!onRecoveryMode)
                startAI();
        }

        @Override
        public void onReceiveOK() {
            if(waitOK){ //  PlayMessageを送ったときのOKのみに反応するように設定
                waitOK = false;
                //  次のターンへ進めま�?
                gameMaster.nextPhase();
                //  リカバリーモードは切る
                onRecoveryMode = false;
            }
        }

        @Override
        public void onReceiveCoundNotMove() {
            System.out.println("Cound Not Move");
            
            //  これは動けなかった場合に帰ってくる
            //  動けなかった場合は適当になんでもいいので送る
            onRecoveryMode = true;
            thread.sendPlayMessage(0,errorCount%9, errorCount/9);
            errorCount++;
            if(errorCount>81) errorCount  = 0;
            
            
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
        //  new CUIClient().test();
        
    }
    
}
