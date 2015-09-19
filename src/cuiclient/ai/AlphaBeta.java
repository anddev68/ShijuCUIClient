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
 * @version 1.0.0
 */
public class AlphaBeta {
    
   // private LinkedList<GameMaster> inQueue;   //  展開待ち行列
   // private LinkedList<VirtualGameMaster> outQueue;   //  展開済み評価待ち行列
    private int id; //  プレイヤーのID
    
    private Hand[] optimizedHandList;  //  depthごとの最善手
    
    //  係数
    private static final double[] K = {0.686014, 0.730685, 0.520478, 0.206630, 0.265467};
    
    public AlphaBeta(int id){
        this.id = id;
    }
    
    /**
     * AlphaBetaの外部公開用メソッド
     * 実体は4つの引数のほうです。これを利用すると評価値だけではなく最適解を得ることもできます。
     * また、Infinityの指定も必要ありません。外側からはこれを呼ぶようにしてください
     * @see AlphaBeta#alphabeta(int, cuiclient.game.GameMaster, double, double) 
     * @param depth 先読み手数
     * @param master 現在のルートとなる盤面
     * @return 最適解とその評価値
     */
    public ReturnValue alphabeta(int depth,GameMaster master){
        optimizedHandList = new Hand[depth+1];
        for(int i=0; i<optimizedHandList.length; i++) optimizedHandList[i] = new Hand(-1,-1,-1,-1);
        double score = alphabeta(depth,master,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
        return new ReturnValue( score,optimizedHandList[depth] );
    }
   
    
    /**
     * AlphaBetaメソッド
     * AlphaBeta法で最適解を求めます
     * @see AlphaBeta#K 係数
     * @see AlphaBeta#optimizedHandList 最適解
     * @see AlphaBeta#expand(java.util.LinkedList, java.util.LinkedList) 子ノード展開用メソッド
     * @param depth 深さ
     *      最大先読み手数を指定する
     * @param master
     *      ゲームの盤面を指定する
     * @param alpha
     *      AlphaBetaのAlpha値です
     * @param beta
     *      AlphaBetaのBeta値です
     * @return 
     */
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
     * ゲームボードを展開するメソッド
     * これは本来ゲームボードに持たせるべきかもしれないが、AIによって展開の仕方を変えることを考慮しAlphaBetaAIに持たせた
     * @param inQueue 展開待ち行列
     *      Not NULL,必ず最初に1ついれておくこと
     *      このQueueの先頭にあるものから順番に展開していく
     *      展開中、2手指す必要が出てきたものに関してはinQueueに動かしたあとのものを入れ、もう一度展開しなおします
     * @param outQueue 展開終了（評価待ち行列）
     *      Not NULL,必ずインスタンスを生成しておくこと
     *      このQueueに展開処理を終えたものを順番に入れていきます
     * 
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
                    boolean result = copy.movePos(hand.x,hand.y,hand.index);

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
