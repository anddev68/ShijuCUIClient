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
public class MinMax {
    
    int playerTeamId;
    double k1,k2,k3,k4,k5;
    
    public MinMax(int id){this.playerTeamId = id;}
    
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
    public ReturnValue minmax(int depth,GameBoard board){
        double score;
        
        //  先読み手数を上回ったらその時点で終了
        if(depth==0){
            score = STATIC_VALUE(board);
            return new ReturnValue(score,null);
        }
        
        //  子ノードを展開する
        ArrayList<NextBoard> nextBoardList = new ArrayList();
        int id = board.whoIsPlay();
        for(int j=0; j<4; j++){
            for (int i = 0; i < 8; i++) {
                int x = board.unitLocation[id][j].x + movex[i];
                int y = board.unitLocation[id][j].y + movey[i];
            
                //  範囲外への移動の場合は有効手ではない
                if(!GameBoard.availableArea(x, y)) continue;
                
                //  移動範囲は定跡により縛る
                if(!GameBoard.formula1(movex[i], movey[i], x, y, id)) continue;
                
                //  ボードをディープコピーして動かす
                GameBoard tmp = new GameBoard(board);
                if ( tmp.movePos(x, y, j) ){
                    //  候補に追加
                    nextBoardList.add( new NextBoard(tmp,new Hand(x,y,j,board.whoIsPlay())) );
                }
               
            }    
        }

        if(nextBoardList.isEmpty()){
            System.out.println("展開するノードがありません");
            score = STATIC_VALUE(board);
            return new ReturnValue(score,null);
        }
        
        ReturnValue best = null;
        GameBoard bestBoard = null;
        for(NextBoard i : nextBoardList){
            ReturnValue result = this.minmax(depth-1, i.board);
            result.optimized = i.hand;
            
            if(best!=null){
                if( board.whoIsPlay() == this.playerTeamId && best.score < result.score ) {
                    best = new ReturnValue(result.score,i.hand);
                    bestBoard = i.board;
                }else if( board.whoIsPlay() != this.playerTeamId && best.score > result.score){
                    best = new ReturnValue(result.score,i.hand);
                    bestBoard = i.board;
                }
            }else{
                best = result;
                bestBoard = i.board;
            }
        }
        return best;
        
        
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
