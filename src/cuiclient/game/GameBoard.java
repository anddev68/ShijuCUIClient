/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;


import java.awt.Point;

/**
 *
 * @author dell_user
 */
public class GameBoard {
    Point[][] unitLocation;
    int[] tower;
    
    public GameBoard(){
        tower = new int[3];
        tower[0] = tower[1] = tower[2] = -1;
        unitLocation = new Point[2][4];
        for(int i=0; i<2; i++){
            for(int j=0; j<4; j++){
                unitLocation[i][j] = new Point();
            }
        }
    }
    
    public GameBoard(GameBoard board){
        tower = new int[3];
        for(int i=0; i<3; i++) tower[i] = board.tower[i];
        
        unitLocation = new Point[2][4];
        for (int i = 0; i < unitLocation.length; i++) {
            for (int j = 0; j < unitLocation[i].length; j++) {
                unitLocation[i][j] = new Point();
                unitLocation[i][j].x = board.unitLocation[i][j].x;
                unitLocation[i][j].y = board.unitLocation[i][j].y;
            }
        }
    }
    
    public void setUnitLocation(int x,int y,int index,int id){
        this.unitLocation[id][index].x = x;
        this.unitLocation[id][index].y = y;
    }
    public void setTower(int index,int id){
        this.tower[index] = id;
    }
    
    
    /**
     * ユニットの位置を返す
     * @param allUnitIndex 盤面上に振られた敵味方共通のユニットのID
     * @return 
     */
    public Point getUnitLocation(int allUnitIndex){
        return unitLocation[getUnitOwnerId(allUnitIndex)][getUnitIndex(allUnitIndex)];
    }
    
    
    /**
     * ユニットの所有者IDを返す
     * @param allUnitIndex 盤面上に振られたユニットのID
     *      このIDは全ユニットでユニーク
     *      0から3までは0を、4から7は1を返す
     * @return 
     */
    public int getUnitOwnerId(int allUnitIndex){
        return (allUnitIndex<4) ? 0: 1;
    }
    
    /**
     * ユニットのIndexを返す
     * @param allUnitIndex 盤面上に振られたユニットのID
     * @return 
     */
    public int getUnitIndex(int allUnitIndex){
        return allUnitIndex % 4;
    }
    
    /**
     * 現在敵の所有権または未所有となっている一番短いタワーまでの距離を算出する
     */
    public int getEnemyTowerDistance(int id,int index){
        int eId = id==0 ? 1:0;
        int min = 0;
        for(int i=0; i<3; i++){
            if(this.tower[i]!=id){
                int dist = distance(unitLocation[id][index],towerPos[i]);
                if(min>dist) min = dist;
            }
        }
        return min;
        
    }
    
    
    
    
    // -----------------------------------------------------------------------------------------
    //     静的メソッド類
    //     ボードの状態とは関係なく
    //      初期化や評価の計算時に利用するメソッド類
    // ------------------------------------------------------------------------------------------
    
    /**
     * ２点の距離を計算（上下左右、斜めのどこでも１歩）
     */
    public static int distance(Point a, Point b) {
        if (a.x == b.x) {
            return Math.abs(a.y - b.y);
        } else if (a.y == b.y) {
            return Math.abs(a.x - b.x);
        } else {
            //斜めに近づく場合は長い方と同じだけで大丈夫
            int xdef = Math.abs(a.x - b.x);
            int ydef = Math.abs(a.y - b.y);
            if (xdef > ydef) {
                return xdef;
            } else {
                return ydef;
            }
        }
    }
    
    public static int distance(int x1,int y1,int x2,int y2) {
        if (x1 == x2) {
            return Math.abs(y1 - y2);
        } else if (y1 == y2) {
            return Math.abs(x1 - x2);
        } else {
            //斜めに近づく場合は長い方と同じだけで大丈夫
            int xdef = Math.abs(x1 - x2);
            int ydef = Math.abs(y1 - y2);
            if (xdef > ydef) {
                return xdef;
            } else {
                return ydef;
            }
        }
    }

    

    /**
     * 物理的に移動可能かどうか判断
     * フィールド内または陣地への移動はできません
     * @param x x座標
     *      有効数値[0-9]かつ陣地でない場合
     * @param y y座標
     *      有効数値[0-9]かつ陣地でない場合
     * @return 移動可能の場合true それ以外false
     */
    public static boolean availableArea(int x, int y) {
        if (x < 0) {
            return false;
        }
        if (x >= 9) {
            return false;
        }
        if (y >= 9) {
            return false;
        }
        if (y < 0) {
            return false;
        }
        
        if(x==4 && y==7) return false;
        if(x==4 && y==1) return false;
        
        return true;
    }
    
    
    /**
     * 一番短いタワーまでの距離を算出する
     *  タワーの場所は問わない
     * @see GameBoard#towerPos
     * @param p
     * @return タワーまでの距離
     */
    public static int distanceTower(Point p) {
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < 3; i++) {
            int tmp = distance(p, towerPos[i]);
            if (tmp < min) {
                min = tmp;
            }
        }
        return min;
    }
    
    public static int distanceTower(int x,int y) {
        return distanceTower(new Point(x,y));
    }
    

    
    
    /**
     * 対戦用テーブル　battleTable[0][1] = 青竜vs玄武 = 1(前者の勝ち) 値が-1は後者の勝ち　0は引き分け
     */
    public static final int[][] battleTable = {{0, 1, 0, -1},
    {-1, 0, 1, 0},
    {0, -1, 0, 1},
    {1, 0, -1, 0}};

    public static final Point[] towerPos = {
        new Point(1, 4),
        new Point(4, 4),
        new Point(7, 4)
    };

    public static final Point[] area = {
        new Point(4, 7),
        new Point(4, 1)
    };
    
}
