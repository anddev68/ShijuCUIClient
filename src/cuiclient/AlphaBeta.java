/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import java.awt.Point;
import java.util.ArrayList;

/**
 * 完全に修正したMinMax
 * 継承使ったりするとおかしくなるのでこっちを使用してください
 */
public class AlphaBeta {
    
    int playerTeamId;
    double k1,k2,k3,k4,k5;
    
    public AlphaBeta(int id){this.playerTeamId = id;}
    
    public void setParams(double... args){
        this.k1 = args[0];
        this.k2 = args[1];
        this.k3 = args[2];
        this.k4 = args[3];
        this.k5  =args[4];
    }
    
    //  乱数によって動かす方向を決定するための配列
    static final int[] movex = {-1, 0, 1, -1, 1, -1, 0, 1};
    static final int[] movey = {-1, -1, -1, 0, 0, 1, 1, 1};
    
    /**
     * minmaxにより最善手を導出します
     * @param depth
     * @param board
     * @return 最善手と点数
     */
    public ReturnValue alphabeta(int depth,GameBoard board){
        return alphabeta(depth,board,new ReturnValue(Double.NEGATIVE_INFINITY,null),
                new ReturnValue(Double.POSITIVE_INFINITY,null));
    }
    
    public ReturnValue alphabeta(int depth,GameBoard board,ReturnValue alpha,ReturnValue beta){
        double score;
        
        //  先読み手数を上回ったらその時点で終了
        if(depth==0){
            //System.out.println("読み手数が終了しました");
            score = STATIC_VALUE(board);
            return new ReturnValue(score,null);
        }
        
        //  子ノードを展開する
        ArrayList<NextBoard> nextBoardList = new ArrayList();
        int id = board.whoIsPlay();
        
        //  すべての手について実現可能手を見つける
        for (int index = 0; index < 4; index++) {
            for (int move = 0; move < 8; move++) {
                //  実現できない場合は手を飛ばす
                if (!board.checkMove(movex[move], movey[move], index, false)) continue;
                //  実現できる場合においてはコピーを作成して動かす
                GameBoard tmp = new GameBoard(board);
                tmp.move(movex[move], movey[move], index);
                int x = board.unitLocation[id][index].x + movex[move];
                int y = board.unitLocation[id][index].y + movey[move];
                Hand hand = new Hand(x,y,index,id);
                //  動かしたあとが異なるIDの場合はここで終了
                if(tmp.turnState!=GameBoard.STATE_PLAY_TURN3){
                    nextBoardList.add(new NextBoard(tmp,hand));
                    continue;
                }
                //  2手連続で指す
                for (int index2 = 0; index2 < 4; index2++) {
                    for (int move2 = 0; move2 < 8; move2++) {
                        if (!tmp.checkMove(movex[move2], movey[move2], index2, false)) {
                            continue;
                        }
                        //  実現できる場合はコピーして追加
                        GameBoard tmp2 = new GameBoard(tmp);
                        tmp2.move(movex[move2], movey[move2], index2);
                        nextBoardList.add(new NextBoard(tmp,hand));
                    }
                }
                
            }
         }
       

        if(nextBoardList.isEmpty()){
            System.out.println("展開するノードがありません");
            score = STATIC_VALUE(board);
            return new ReturnValue(score,null);
        }
        //System.out.println("expanded: "+nextBoardList.size());
        ReturnValue alpha2 = new ReturnValue();
        alpha2.score = alpha.score;
        ReturnValue beta2 = new ReturnValue();
        beta2.score = beta.score;
        
        if( board.whoIsPlay() == this.playerTeamId ){
            for (NextBoard i : nextBoardList) {
                ReturnValue result = alphabeta(depth - 1, i.board, alpha, beta);    
                if(alpha2.score  < result.score){    //  大きいほうを選択する
                    alpha2.score = result.score;
                    alpha2.optimized = i.hand;
                }  
                if(alpha2.score>=beta2.score){
                    beta2.optimized = i.hand;
                    return beta2;    //  カット
                }
            }
            return alpha2;
        }else{
            for (NextBoard i : nextBoardList) {
                ReturnValue result = alphabeta(depth - 1, i.board, alpha, beta);
                if(result.score < beta2.score){
                    beta2 = result;  //  小さいほうを選択する
                    beta2.optimized = i.hand;
                } 
                if (alpha2.score >= beta2.score) {
                    alpha2.optimized = i.hand;
                    return alpha2;   //  カット
                }
            }            
            return beta2;
        }

    }
    
    
    public double STATIC_VALUE(GameBoard board) {
        double score = 0.0;
        int enemyTeamId = playerTeamId == 0 ? 1 : 0;

        //  点数さによる評価
        //  バイアスをかけておかないとマイナスになる可能性がある 
        score += k1 * (board.teamPoint[playerTeamId] - board.teamPoint[enemyTeamId] + 100);

        //  自分のユニットとタワーまでの距離
        //  2015/09/03変更
        //  平均値が小さく、分散が小さいものを選択する
        int eDist = 0;
        int pDist = 0;
        for (int i = 0; i < 4; i++) {
            pDist += Math.pow(GameBoard.distanceTower(board.unitLocation[playerTeamId][i]), 2);  //  距離が多くなればなるほど点数が下がる
            eDist += Math.pow(GameBoard.distanceTower(board.unitLocation[enemyTeamId][i]), 2);  //  
        }

        score += k2 * (-pDist + eDist + 100);

        //  保持状態
        int tower = 0;
        for (int i = 0; i < 3; i++) {
            if (board.towerHold[i] == playerTeamId) {
                tower++;
            } else {
                tower--;
            }
        }
        score += k3 * (tower + 3);

        //  相性評価
        int length = 0;
        for (int i = 0; i < 3; i++) {     //全ての駒に対して
            int MNE = board.mostNear(playerTeamId, i, enemyTeamId);  //MNE = mostNearEnemy
            Point P = board.unitLocation[playerTeamId][i];
            Point E = board.unitLocation[enemyTeamId][MNE];
            /*            if(multiple(i,playerTeamId)<multiple(MNE,enemyTeamId))      //相手のほうが多い
             length -= GameBoard.distance(P, E);
             else if(multiple(MNE,enemyTeamId) < multiple(i,playerTeamId))       //自分のほうが多い
             length += multiple(MNE,enemyTeamId);
             else{       //同数
             if((i!=3 && i==MNE+1)||(i==3 && MNE==0))    //勝てる時
             length += GameBoard.distance(P, E);
             else if((i!=0 &&i==MNE-1)||(i==0 && MNE==3))//負ける時
             length -= multiple(MNE,enemyTeamId);
             //引き分けは加算無し
             }*/

            // (相手の数-自分の数) * 距離 の分だけ変化
            if ((i != 3 && i == MNE + 1) || (i == 3 && MNE == 0)) //勝てる時
            {
                length += (board.multiple(i, playerTeamId) - board.multiple(MNE, enemyTeamId)) * GameBoard.distance(P, E);
            } else if ((i != 0 && i == MNE - 1) || (i == 0 && MNE == 3))//負ける時
            {
                length -= (board.multiple(i, playerTeamId) - board.multiple(MNE, enemyTeamId)) * GameBoard.distance(P, E);
            }

        }
        score += k4 * length;

        //タワーの取りやすさ
        int count = 0;
        for (int i = 0; i < 3; i++) {
            if (board.towerHold[i] != playerTeamId) {
                count -= board.peripheral(GameBoard.distanceTowerNumber(board.unitLocation[playerTeamId][i]), enemyTeamId)
                        * GameBoard.distanceTower(board.unitLocation[playerTeamId][i]);
            }
        }

        score += k5 * count;
        
        

        return score;
    }
    
    
    
    /**
     * 得点と最善手を示すクラス
     * mimaxの返却型
     */
    public class ReturnValue{
        public double score;
        public Hand optimized;
        public ReturnValue(){}
        public ReturnValue(double score,Hand hand){
            this.score = score;
            this.optimized = hand;
        }
    }
    
    /**
     * 次の手とボード
     */
    private class NextBoard{
        GameBoard board;
        Hand hand;
        NextBoard(GameBoard board,Hand hand){
            this.board = board;
            this.hand = hand;
        }
    }
    
    
    
}
