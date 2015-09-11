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
        
        //  蜷榊燕繧貞�･蜉�
        System.out.println("Input your name (English only).");
        System.out.println("If press Enter,use default name.");
        String name = s.nextLine();
        
        //  繧ｵ繝ｼ繝舌ｒ蜈･蜉�
        System.out.println("Input server adress.");
        System.out.println("If press Enter,use default name,'localhost'");
        String server = s.nextLine();
        
        //  繝昴�ｼ繝医ｒ蜈･蜉�
        /*
        System.out.println("Input port number");
        System.out.println("If press Enter,use default portm13306");
        int port = s.nextInt();
        */
        
        //  繧ｵ繝ｼ繝先磁邯夂畑縺ｮ繧ｹ繝ｬ繝�繝峨ｒ髢句ｧ九☆繧�
        thread = new ServerThread(name.isEmpty()?"YASUDAKEN":name, new MyPlayReceiver(),new MyLoginReceiver());
       
         //  繧ｵ繝ｼ繝舌�ｼ縺ｫ謗･邯壹☆繧�
         if( !thread.connectServer(server.isEmpty()?"localhost":server,13306) ) return;
         log("connected server.");
       
         // 繧ｹ繝ｬ繝�繝峨ｒ髢句ｧ九☆繧�
         thread.start();
         log("thread start.");
        
         
         // 莉･蠕後☆縺ｹ縺ｦ縺ｮ繧�繧翫→繧翫�ｯ繝ｪ繧ｷ繝ｼ繝舌�ｼ繧剃ｻ九＠縺ｦ陦後＞縺ｾ縺�
         
         
         
        //  繝｡繧､繝ｳ繧ｹ繝ｬ繝�繝峨�ｯ遨ｺ繝ｫ繝ｼ繝�
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

        AlphaBeta.ReturnValue result = alphaBeta.alphabeta(4, gameBoard);
        
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
     * Login縺ｫ髢｢縺吶ｋ繧ｳ繝槭Φ繝峨ｒ繧ｵ繝ｼ繝舌°繧牙女菫｡縺励◆縺ｨ縺阪�ｮ蜃ｦ逅�縺ｧ縺�
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
     * Play縺ｫ髢｢縺吶ｋ繧ｳ繝槭Φ繝峨ｒ繧ｵ繝ｼ繝舌°繧牙女菫｡縺励◆縺ｨ縺阪�ｮ蜃ｦ逅�縺ｧ縺�
     */
    private class MyPlayReceiver implements PlayReceiver{
        //  蜀�驛ｨ繧ｿ繝ｼ繝ｳ繧ｫ繧ｦ繝ｳ繝� [0-3]
        //  謨ｵ縺�1謇区遠縺､縺玖�ｪ蛻�縺�1謇区遠縺､縺斐→縺ｫ1縺薙★縺､蠅励ｄ縺励��
        //  4縺ｫ縺ｪ縺｣縺滓凾轤ｹ縺ｧ0縺ｫ謌ｻ縺励※蜈郁｡悟ｾ梧判蜈･繧梧崛縺医ｒ陦後＞縺ｾ縺�
        int turn = 0;
        int firstTeamId = 0;    //  蜈郁｡後�励Ξ繧､繝､繝ｼ縺ｯ蠢�縺�0縺ｧ縺�
            
        //  譁�蟄励ｒ騾∽ｿ｡縺励◆縺ｮ縺ｧOK繧貞ｾ�讖溘☆繧�
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
            //  縺薙％縺ｧ繧ｲ繝ｼ繝�繝懊�ｼ繝峨ｒ蛻晄悄蛹悶☆繧�
            //  蜈ｨ驛ｨ蜻ｼ縺ｰ繧後ｋ縺九ｉ蛻晄悄蛹悶☆繧句ｿ�隕√≠繧九�ｮ縺句ｾｮ螯�
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
            //  繝懊�ｼ繝峨�ｮ繧ｿ繝ｼ繝ｳ繧貞､画峩縺吶ｋ
            gameBoard.turnState = GameBoard.STATE_PLAY_TURN1 + turn;
            gameBoard.firstTeamId = firstTeamId;
            
            //  繝懊�ｼ繝峨′譖ｴ譁ｰ縺輔ｌ縺溘�ｮ縺ｧAI繧剃ｽｿ縺�縺ｾ縺�
            waitOK = true;
            startAI();
        }

        @Override
        public void onReceiveOK() {
            if(waitOK){ //  PlayMessage繧帝�√▲縺溘≠縺ｨ縺ｮ蠕�讖溽憾諷九°縺ｩ縺�縺句愛螳�
                waitOK = false;
                //  谺｡縺ｮ繧ｿ繝ｼ繝ｳ縺ｸ騾ｲ繧√∪縺�
                nextTurn();
            }
        }

        @Override
        public void onReceiveCoundNotMove() {
        }

        @Override
        public void onReceivePlayed() {
            //  谺｡縺ｮ繧ｿ繝ｼ繝ｳ縺ｸ騾ｲ繧√∪縺�
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
     * 繧ｿ繝ｼ繝ｳ邂｡逅�逕ｨ縺ｮ繧ｯ繝ｩ繧ｹ縺ｧ縺�
     * AI縺ｫ蠢�隕√↑縺溘ａ荳�譎ら噪縺ｫ菴懈�舌＠縺ｦ縺翫″縺ｾ縺�
     * 縺ゅ→縺ｧ蠢�縺夊ｨｭ險医＠縺ｪ縺翫＠縺ｦ縺上□縺輔＞
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
