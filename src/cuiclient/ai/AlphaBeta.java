/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.ai;

import cuiclient.GameBoard;
import cuiclient.Hand;
import cuiclient.game.GameMaster;



/**
 * 完全修正版AlphaBeta
 */
public class AlphaBeta {
    
    
    public AlphaBeta(){
        
    }
    
    public ReturnValue alphabeta(int depth,GameMaster master){
        return alphabeta(depth,master,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY);
    }
   
    private ReturnValue alphabeta(int depth,GameMaster master,double alpha,double beta){
        //  一番最下層までもぐったら静的評価します
        if(depth==0){
            double score = master.evaluate();
            return new ReturnValue(score,null);
        }
        
        //  ノードの展開を行う
        
        
        
        
        return null;
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

    /**
     * 次の手とボード
     */
    private class NextBoard {

        GameBoard board;
        Hand hand;

        NextBoard(GameBoard board, Hand hand) {
            this.board = board;
            this.hand = hand;
        }
    }
    
            
}
