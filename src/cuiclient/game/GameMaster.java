/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

import cuiclient.Hand;
import static cuiclient.game.GameBoard.towerPos;
import static cuiclient.game.GameBoard.area;
import static cuiclient.game.GameBoard.battleTable;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 */
public class GameMaster implements TurnCounter.Callback{
    
    public GameBoard gameBoard;
    public PointController pointController;
    public TurnCounter turnCounter;

    
    public GameMaster(){
        gameBoard = new GameBoard();
        pointController = new PointController();
        turnCounter = new TurnCounter(this);
    }
    
    public GameMaster(GameMaster org){
        this.turnCounter = new TurnCounter(org.turnCounter);
        this.gameBoard = new GameBoard(org.gameBoard);
        this.pointController = new PointController(org.pointController);
    }

    
    @Override
    public void onChangeTurn() {
        //  総当たりで重なっているマスを検索し戦闘処理を行う
        this.bruteForceBattle();
        //  タワーの塗り替えボーナスを加える
        this.calcTowerChangingPoint();
        //  タワーの連続保持ボーナスを加える
        this.calcTowerHoldingPoint();
    }
    
    
    /**
     * 盤面の総合評価
     * プレイヤーIDを入れると、そのプレイヤーに対しての評価を返す
     * @param id 
     * @param k 係数
     */
    public double evaluate(int id,double... k){
        int eId = (id==0) ? 1: 0;
        return pointController.getPoint(id) - pointController.getPoint(eId) + 50;
    }
    
    /**
     * 指定した位置にユニットを動かす
     * @param x x軸に動かすマスの数
     * @param y y軸に動かすマスの数
     * @param index 
     * @return trure 成功 false 失敗
     */
    public boolean movePos(int x,int y,int index){
        //  範囲外参照チェック
        if (!cuiclient.GameBoard.availableArea(x, y)) {
            System.out.println("範囲外への移動が行われた");
            return false;
        }
        
        int id = turnCounter.whoIsPlay();

        //  距離が0もしくは2以上の移動は無効
        if (cuiclient.GameBoard.distance(gameBoard.unitLocation[id][index], new Point(x, y)) != 1) {
            System.out.print("移動する距離が0または2以上です:");
            System.out.println("[" + id + "] " + x + "," + y);
            return false;
        }
        
        return true;
    }
    
    
    /**
     * ユニットを任意の方向に動かす
     *
     * @param vecX x軸方向にどのように動かすか -1,0,1
     * @param vecY y方向にどのように動かすか -1,0,1
     * @param index 動かすユニット番号 0-3
     * @return trure 成功 false 失敗
     */
    public boolean move(int vecX, int vecY, int index) {
        int id = this.whoIsPlay();

        int x = gameBoard.unitLocation[id][index].x + vecX;
        int y = gameBoard.unitLocation[id][index].y + vecY;
        
        return movePos(x,y,index);
        
        
        
    }
    
    
    /**
     * 動かせるかどうかチェックする
     * 実際に動かすことはしない
     * @param x 動かすマスの数
     * @param y
     * @param index
     * @return true 成功 false 失敗
     */
    public boolean checkMovePos(int x,int y,int index){
        int id = turnCounter.whoIsPlay();
        
        //  距離が0もしくは2以上の移動は無効
        if (cuiclient.GameBoard.distance(gameBoard.unitLocation[id][index], new Point(x, y)) != 1) {
            //System.out.print("移動する距離が0または2以上です:");
            //System.out.println("[" + id + "] " + x + "," + y);
            return false;
        }
        
        //  範囲参照チェック
        return GameBoard.availableArea(x, y);
    }
    
    
    /**
     * その方向に動かせるかどうかチェックする
     * 内部的にはx,yを足し算で演算してからcheckMovePosを読んでます
     * @param vecX
     * @param vecY
     * @param index
     * @return 
     */
    public boolean checkMove(int vecX, int vecY, int index) {
        int id = this.whoIsPlay();
        int x = gameBoard.unitLocation[id][index].x + vecX;
        int y = gameBoard.unitLocation[id][index].y + vecY;
        return checkMovePos(x,y,index);
    }
    
    /**
     * 盤面状態を見て手を生成します
     */
    public Hand createHand(int vecX,int vecY,int index){
        int id = this.whoIsPlay();
        int x = gameBoard.unitLocation[id][index].x + vecX;
        int y = gameBoard.unitLocation[id][index].y + vecY;
        Hand hand = new Hand(x,y,index,id);
        return hand;
    }
    
    
    
    
    /**
     * 重なったユニットでバトルを行う
     * u1とu2は同じマスに存在するＩＤが異なるユニットのIndexのこと
     * 
     * @param u1 id0のユニット
     * @param u2 id1のユニット
     * @return 勝利したほうのプレイヤーID
     */
    private int battleUnits(ArrayList<Integer> u1, ArrayList<Integer> u2) {
        if (u1.isEmpty() && u2.isEmpty()) {
            return -1;
        } else if (u1.size() > u2.size()) {
            return 0;
        } else if (u1.size() < u2.size()) {
            return 1;
        } else {
            //System.out.println("battle start.");

            //同数の場合
            if (u1.size() == 1) {
                int a = u1.get(0);
                int b = u2.get(0);
                if (battleTable[a][b] == 1) {
                    return 0;   //  0の勝ち
                } else if (battleTable[a][b] == -1) {
                    return 1;
                } else {
                    return 2;
                }
            } else if (u1.size() == 2) {
                int count = 0;
                for (int a : u1) {
                    for (int b : u2) {
                        count += battleTable[a][b];
                    }
                }
                if (count > 0) {
                    return 0;
                } else if (count < 0) {
                    return 1;
                } else {
                    return 2;
                }
            }
            //3：3はルール上ありえない
            return -1;
        }
    }
    
    
    /**
     * 関数ブルートフォースバトル
     * すべてのマスについて総当たりでバトルを行う
     * 結果：
     * 負けたほうのユニットは陣地に戻す
     * 戦闘結果点数を加算する
     * 
     * @see gameboard
     * @see pointController
     */
    private void bruteForceBattle(){
        for (int i = 0; i < 9; i++) { //  yについて
            for (int j = 0; j < 9; j++) { //  xについて
                ArrayList<Integer> unit0 = new ArrayList<Integer>();
                ArrayList<Integer> unit1 = new ArrayList<Integer>();
                //  同じ位置にいるものを追加
                for (int k = 0; k < 4; k++) {
                    if (gameBoard.unitLocation[0][k].x == j && gameBoard.unitLocation[0][k].y == i) {
                        unit0.add(k);
                    }
                    if (gameBoard.unitLocation[1][k].x == j && gameBoard.unitLocation[1][k].y == i) {
                        unit1.add(k);
                    }
                }
                //  重なったユニット同士でバトル
                int result = battleUnits(unit0, unit1);
                switch (result) {
                    case 0: //  player0の勝利
                        backUnits(unit1, 1);
                        for (int var : unit1) {
                            pointController.addPoint(0, 2);
                        }
                        break;
                    case 1: //  player1の勝利
                        backUnits(unit0, 0);
                        for (int var : unit0) {
                            pointController.addPoint(1, 2);
                        }
                        break;
                    case 2: //  引き分
                        backUnits(unit0, 0);
                        backUnits(unit1, 1);
                        break;
                    default:   //   元に戻す処理
                        break;

                }
            }
        }

    }
    
    /**
     * ユニットを元に戻す
     */
    private void backUnits(ArrayList<Integer> player, int playerId) {
        if (playerId == 0) {
            for (Integer i : player) {
                gameBoard.unitLocation[0][i].x = area[0].x;
                gameBoard.unitLocation[0][i].y = area[0].y;
                //   System.out.println("ユニットを元に戻した");
            }

        } else if (playerId == 1) {
            for (Integer i : player) {
                gameBoard.unitLocation[1][i].x = area[1].x;
                gameBoard.unitLocation[1][i].y = area[1].y;
                //   System.out.println("ユニットを元に戻した");
            }
        }

    }
    
    
    /**
     * タワー保持によるボーナスを計算する
     */
    private void calcTowerHoldingPoint() {
        //タワーの保持状態を確認し、加点する
        for (int i = 0; i < gameBoard.tower.length; i++) {
            if (gameBoard.tower[i] == 0) {
                pointController.addPoint(0, 1);
            } else if (gameBoard.tower[i] == 1) {
                pointController.addPoint(1, 1);
            }
        }
    }
    
    
    /**
     * タワー塗り替えによるボーナスを計算する
     * @see gameBoard
     * @see pointController
     * 
     */
    private void calcTowerChangingPoint(){
        //  ターン終了時にその位置にいて、タワーの所持者が異なる場合タワーを塗り替える
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.unitLocation[0][i].equals(towerPos[j]) && gameBoard.tower[j] != 0) {
                    gameBoard.tower[j] = 0;
                    //System.out.println("タワーを0に塗り替えた");
                    //  ポイント加算
                    pointController.addPoint(0, 1);
                } else if (gameBoard.unitLocation[1][i].equals(towerPos[j]) && gameBoard.tower[j] != 1) {
                    gameBoard.tower[j] = 1;
                    //System.out.println("タワーを1に塗り替えた");
                    //  ポイント加算
                    pointController.addPoint(1, 1);
                }
            }
        }
    }
    
    
    
    
    //  ----------------------------------------------------
    //  以下ラッパー（委譲クラスです）
    //  ------------------------------------------------------
    public void nextPhase(){ turnCounter.nextPhase(); }
    public void setPoint(int id,int p){ pointController.setPoint(id, p); }
    public void setTower(int index,int id){ gameBoard.setTower(index, id); }
    public void setUnitLocation(int x,int y,int index,int id){ gameBoard.setUnitLocation(x, y, index, id); }
    public int getTurnState(){ return turnCounter.getTurnState(); }
    public int whoIsPlay(){ return turnCounter.whoIsPlay(); }
}
