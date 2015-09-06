/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import java.awt.Point;
import java.util.ArrayList;
import lib68.ai.minmax.GameTreeNode;

/**
 *
 * @author dell_user
 */
public class ShijuGameTreeNode extends GameTreeNode{

    //  ボード情報
    public GameBoard board;    
    
    //全ノードを通してプレイヤーとなるID（すべて共通である）
    int playerTeamId;
    
    //子ノード
    ArrayList<GameTreeNode> children;
    
    //  定数
    double k1,k2,k3,k4;
    
    //  評価値
    double score;
    
    
    public ShijuGameTreeNode(GameBoard g, int playerTeamId, double k1, double k2, double k3, double k4) {
        board = new GameBoard(g);
        children = new ArrayList<>();
        this.playerTeamId = playerTeamId;
        this.k1 = k1;
        this.k2 = k2;
        this.k3 = k3;
        this.k4 = k4;
    }
    

    @Override
    public double STATIC_VALUE() {
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
        /*
        if (playerTeamId == board.whoIsPlay()) {
            score = -score;
        }
                */
        this.score = score;
        return score;
    }

    @Override
    public boolean isPlayerTurn() {
        return board.whoIsPlay() == this.playerTeamId;
    }

    @Override
    public ArrayList<GameTreeNode> expandChildren() {
        children = new ArrayList();
        for (GameBoard g : this.board.extpand()) {
            ShijuGameTreeNode node = new ShijuGameTreeNode(g, this.playerTeamId, k1, k2, k3, k4);
            children.add(node);
        }
        return children;
    }
    
    @Override
    public void setScore(double d){
        this.score = d;
    }
    
    public void print() {
        System.out.println("ID" + board.lastId + " [" + board.lastIndex + "] " + board.lastX + "," + board.lastY + " score:" );
    }
    
    public ArrayList getChildren(){
        return this.children;
    }
    
    public double getScore(){
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
    
}
