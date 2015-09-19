/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.ai;

import cuiclient.GameBoard;
import cuiclient.Hand;
import cuiclient.game.GameMaster;
import cuiclient.game.TurnCounter;
import cuiclient.game.VirtualGameMaster;
import java.util.ArrayList;
import java.util.LinkedList;



/**
 * 完全修正版AlphaBeta
 */
public class AlphaBeta {
    
   // private LinkedList<GameMaster> inQueue;   //  展開待ち行列
   // private LinkedList<VirtualGameMaster> outQueue;   //  展開済み評価待ち行列
    private int id; //  プレイヤーのID
    
    private Hand[] optimizedHandList;  //  depthごとの最善手
    private ArrayList<Hand> moveList;   //  動きの候補リスト
    
    //  係数
    private static final double[] K = {0.686014, 0.730685, 0.520478, 0.206630, 0.265467};
    
    public AlphaBeta(int id){
        this.id = id;
    }
    
    public ReturnValue alphabeta(int depth,GameMaster master){
        optimizedHandList = new Hand[depth+1];
        for(int i=0; i<optimizedHandList.length; i++) optimizedHandList[i] = new Hand(-1,-1,-1,-1);
        double score = alphabeta(depth,master,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        printHand();
        return new ReturnValue( score,optimizedHandList[depth] );
    }
   
    
    private double alphabeta(int depth,GameMaster master,double alpha,double beta){
        //  一番最下層までもぐったら静的評価します
        if(depth==0){
            double score = master.evaluate(id,K);
            return score;
        }
        
        //  ノードの展開を行う
        //  優先順位付きで実現可能手をキューに入れる
        LinkedList<GameMaster> inQueue = new LinkedList();
        LinkedList<VirtualGameMaster>  outQueue = new LinkedList();
        inQueue.add(master);  //  初期ノード追加
        expand(inQueue,outQueue);
        
        //  展開するノードがない
        if( outQueue.isEmpty() ){
            return master.evaluate(id,K);
        }
        
        //  展開したノードを用いて評価を再帰的に行う
        VirtualGameMaster tmp;
        double score;
        
        if(master.whoIsPlay()==this.id){
            while(!outQueue.isEmpty()){
                tmp = outQueue.removeFirst();
                score = alphabeta(depth - 1, tmp, alpha, beta);    //  再帰した結果を使って結果とする
                if(score > alpha){ //  alphaの高いやつを選択
                    alpha = score;  //  alphaを更新
                    this.optimizedHandList[depth]  = tmp.getLastHand();
                }
                if(alpha >=beta ){
                    return alpha;   //  betaカット
                }
            }
            return alpha;
        }else{  //  敵のターン
            while (!outQueue.isEmpty()) {
                tmp = outQueue.removeFirst();
                score = alphabeta(depth-1,tmp,alpha,beta);  //  再帰呼び出し
                if(score<beta){
                    beta = score;   //  ベータ値更新
                    this.optimizedHandList[depth] = tmp.getLastHand();
                }
                if (alpha >= beta) {
                    return beta; /* アルファカット */
                }
            }
            return beta;
        }
    }
    
    
    /**
     * ノードを展開する
     */
    private void expand(LinkedList<GameMaster> inQueue,LinkedList<VirtualGameMaster> outQueue){
        
        //  乱数によって動かす方向を決定するための配列
        final int[] movex = {-1, 0, 1, -1, 1, -1, 0, 1};
        final int[] movey = {-1, -1, -1, 0, 0, 1, 1, 1};
        
        while(!inQueue.isEmpty()){
            GameMaster tmp = inQueue.removeFirst();
            //  すべての手について実現可能手を見つける
            for (int index = 0; index < 4; index++) {
                for (int move = 0; move < 8; move++) {
                    //  実現できない場合はスルー
                    if (!tmp.checkMove(movex[move], movey[move], index))  continue;
                    //  実現できる場合はコピーを作成
                    Hand hand = tmp.createHand(movex[move], movey[move], index);
                    VirtualGameMaster copy = new VirtualGameMaster(tmp,hand);
                    //  コピーを動かす
                    copy.movePos(hand.x,hand.y,hand.index);
                    //  ターンを変える
                    copy.nextPhase();
                    
                    //  指した後の手番が変わらない場合は連続で指すため、
                    //  展開済みノードに追加せず、もう一回inQueueに戻す
                    if(copy.getTurnState()==TurnCounter.STATE_PLAY_TURN3){
                        inQueue.add(copy);
                   //   連続しない場合は展開済みノードに追加する
                    }else{
                        outQueue.add(copy);
                    }
                }
            }
            
        }
        
    }
    
    /**
     * 
     */
    private void printHand(){
        for(int i=0; i<this.optimizedHandList.length; i++){
            System.out.println(this.optimizedHandList[i].toString());
        }
    }
    
    
    
    
    
    
    /**
     * 得点と最善手を示すクラス mimaxの返却型
     */
    public class ReturnValue {

        public double score;
        public Hand optimized;

        public ReturnValue() {
        }

        public ReturnValue(double score, Hand hand) {
            this.score = score;
            this.optimized = hand;
        }
    }


    
            
}
