/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import lib68.ai.Node;
import lib68.ai.alphabeta.GameNode;


/**
 *
 * @author kano
 */
public class ShijuGameNode extends GameNode implements Comparable{

    //  ボード情報
    public GameBoard board;
    
    //  親
    ShijuGameNode parent;
    ArrayList<ShijuGameNode> children;
    
    //最良子
    ShijuGameNode bestChild;
    double bestChildScore;
    
    //全ノードを通してプレイヤーとなるID（すべて共通である）
    int playerTeamId;
    
    //  遺伝子用パラメータ
    double k1,k2,k3,k4;
    
    //  スコア
    double score;
    
    
    public ShijuGameNode(GameBoard g,int playerTeamId,double k1,double k2,double k3,double k4){
        board = new GameBoard(g);
        children = new ArrayList<>();
        bestChildScore = Double.NEGATIVE_INFINITY;
        bestChild = null;
        this.playerTeamId = playerTeamId;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
    }
    
    

    
    
    
    @Override
    public boolean isPlayerTurn() {
        return board.whoIsPlay() == playerTeamId;
    }

    @Override
    public double getScore() {
        return score;
    }

    @Override
    public void print() {
        System.out.println("ID"+board.lastId +" ["+board.lastIndex+"] "+board.lastX+","+board.lastY+" score:"+getScore());
    }

    @Override
    public Node getParent() {
        return parent;
    }

    @Override
    public int getChildrenSize() {
        return children.size();
    }

    @Override
    public Node getChild(int index) {
        return children.get(index);
    }

    @Override
    public void expand() {
        
        /**
         * 子ノードを定石を使って拡張する
         */
        children = new ArrayList<>();
        for(GameBoard g : this.board.extpand()){
            ShijuGameNode node = new ShijuGameNode(g,this.playerTeamId,k1,k2,k3,k4);
            node.parent = this;
            children.add(node);
        }
        
        
    }

    @Override
    public void setScore(double d) {
        this.score = d;
    }

    
    /* 
        NEGA_MAX用評価関数 
        現在の盤面は打った結果である
            
        最後に打った手が敵の晩ならそのまま返す
        最後に打った手が自分の手ならマイナスを付ける
        根がマックスを適用できるようにした
        
        */
    @Override
    public double evaluate() {
        double score = 0.0;
        int enemyTeamId = playerTeamId==0 ? 1: 0;
        
        //  点数さによる評価
        //  バイアスをかけておかないとマイナスになる可能性がある 
        score += k1* ( board.teamPoint[playerTeamId] - board.teamPoint[enemyTeamId] +100);
        
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
        for(int i=0; i<3; i++){
            if(board.towerHold[i]==playerTeamId) tower++; 
            else tower--;
        }
        score += k3 * (tower+3);
        
        //  相性評価
        int length = 0;
        for (int i = 0; i < 3; i++) {     //全ての駒に対して
            int MNE = mostNear(playerTeamId, i, enemyTeamId);  //MNE = mostNearEnemy
            Point P = this.board.unitLocation[playerTeamId][i];
            Point E = this.board.unitLocation[enemyTeamId][MNE];
            if (multiple(i, playerTeamId) < multiple(MNE, enemyTeamId)) //相手のほうが多い
            {
                length -= GameBoard.distance(P, E);
            } else if (multiple(MNE, enemyTeamId) < multiple(i, playerTeamId)) //自分のほうが多い
            {
                length += multiple(MNE, enemyTeamId);
            } else {       //同数
                if ((i != 3 && i == MNE + 1) || (i == 3 && MNE == 0)) //勝てる時
                {
                    length += multiple(MNE, enemyTeamId);
                } else if ((i != 0 && i == MNE - 1) || (i == 0 && MNE == 3))//負ける時
                {
                    length -= GameBoard.distance(P, E);
                }
                //引き分けは加算無し
            }
        }
        score += k4 * length;
        
        
        //  最後に打った手が自分の手ならマイナスを付けておく
        //if(board.lastId==playerTeamId) score = -score;
        //  現在の局面が相手なら
        if(playerTeamId==board.whoIsPlay()) score = - score;
        
        this.score = score;
        return this.score;
    }
    
    //複数で重なっている数
    public int multiple(int i, int id) {
        int count = 0;
        int X = this.board.unitLocation[id][i].x;
        int Y = this.board.unitLocation[id][i].y;
        for (int j = 0; j < 3; j++) {
            if (i != j && this.board.unitLocation[id][j].x == X && this.board.unitLocation[id][j].y == Y) {
                count++;
            }
        }
        return count;
    }

    //最も近いユニットナンバー
    public int mostNear(int id1, int i, int id2) {
        int nearUnit = 0;
        int nearLength = 10;
        int length = 0;
        for (int j = 0; j < 3; j++) {
            length = GameBoard.distance(this.board.unitLocation[id1][i], this.board.unitLocation[id2][j]);
            if (length < nearLength) {
                nearUnit = j;
                nearLength = length;
            }
        }
        return nearUnit;
    }
    

    @Override
    public void sort() {
        Collections.sort(this.children);
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(((ShijuGameNode)o).getScore() , this.getScore());
    }

    
    /**
     * 木構造的に出力します
     */
    public void printRecursive(){
        for(int i=0; i<this.getChildrenSize(); i++){
            this.getChild(i).print();
        }
    }

    
    
}
