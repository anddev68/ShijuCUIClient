/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.ai;

import cuiclient.game.GameBoard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *  Relationを扱いやすくしたクラス
 *  ヘルパーメソッドを呼びます
 */
public class RelationHelper {
    
    /**
     * 評価テーブル
     * id=0（下から見たときのテーブル）
     * 位置は以下のように設定する
     * 0,1,2,...
     * 9,10....
     * ..........81
     * 種類は0~3
     * 
     * 全パターンは
     * [x,y][kind][owner][x,y][kind][owner] (81*4)*(81*4)
     * 
     * R = ((73, 銀, 先手),(82, 飛,後手))の形にしたいので
     */
    private static int[][][][][][] PARAM_TABLE;

    
    /**
     * 内部コンストラクタ
     * 外部からは呼べないのでgetInstance()を読んでください
     */
    private RelationHelper(){
        //  パラメータファイルがある場合は読み込む
        
        //  パラメータファイルがない場合はランダムで作成する
        this.initalizeParamWithDefault();
    }
    
    
    
    /**
     * 関係パラメータファイルを作成する
     * 初期値はDoubleの取りうる範囲でランダムとする
     * @param fileName 作成するファイルの名前
     * @throws FileNotFoundException
     */
    private void createParamFile(String fileName) throws FileNotFoundException {
        PrintStream ps = new PrintStream(new FileOutputStream(new File(fileName)));
        for (int a = 0; a < 9; a++) {
            for (int b = 0; b < 9; b++) {
                for (int c = 0; c < 4; c++) {
                    for (int d = 0; d < 2; d++) {
                        for (int e = 0; e < 9; e++) {
                            for (int f = 0; f < 9; f++) {
                                for (int g = 0; g < 4; g++) {
                                    for (int h = 0; h < 2; h++) {
                                        String str = String.format("%d%d,%d,%d,%d%d,%d,%d)", a, b,c, d, e, f, g, h);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    /**
     * 関係パラメータファイルからパラメータリストを生成する
     */
    private void readParamFile(String fileName){
        
    }
    
    
    /**
     * 評価テーブルを用いて評価する
     * @see RelationHelper#PARAM_TABLE
     * @param r 関係
     *      この関係をもとに評価する
     *      評価パラメータは別ファイルで管理していて、プログラム開始時に読み込む
     *      読み込んだ評価パラメータからRelationを用いて引く
     *      pが自分のユニット,qが敵のユニット
     * @param id プレイヤーのID
     *      idのプレイヤーからみた評価値を返します
     * @return
     */
    public double evaluate(Relation r,int id){
        //  評価テーブルはid=0から見たときの値です
        //  id==1の場合は上下反転する必要あり
        
        double score = PARAM_TABLE[r.pPos.x+r.pPos.y*9][r.pIndex][r.pId][r.qPos.x+r.qPos.y*9][r.qIndex][r.qId];
        if(id==1) score *=-1;
        return score;
    }
    
    
    
    /**
     * パターン1でパラメータを作成
     * パターン1→p==qかつ、5段目までの距離とする
     * @see PARAM_TABLE
     */
    private void initalizeParamWithDefault(){
        PARAM_TABLE = new int[81][4][2][81][4][2];
        for(int a=0; a<81; a++){
            for(int b=0; b<4; b++){
                for(int c=0; c<81; c++){
                    for(int d=0; d<4; d++){
                        for(int e=0; e<2; e++){
                            for(int f=0; f<2; f++){
                                //  すべてのパターンにおいて、ベースとなる点数（タワーまでの距離）を追加する
                                //  タワーまでの距離が大きいと評価を悪くする
                                //  味方の距離はマイナスで、敵の距離はプラスで計算する
                               // int d1 = (int) ( -Math.pow(GameBoard.distanceTower(a % 9, a / 9), 2)  *( e==0? 1: -1 ));
                                //int d2 = (int) ( -Math.pow(GameBoard.distanceTower(c% 9, c / 9), 2) * (f==0? 1: -1) );
                                PARAM_TABLE[a][b][e][c][d][f] = (int) (-Math.pow(GameBoard.distanceTower(a % 9, a / 9), 2));
                                //PARAM_TABLE[a][b][e][c][d][f] = 0;
                                
                                
                                //  p==qのとき は位置評価のみ
                                if (a == c && b == d && e==f) {
                                    //  PARAM_TABLE[a][b][e][c][d][f] = (int) (-Math.pow(GameBoard.distanceTower(a % 9, a / 9), 2));
                                
                                
                                //  同じIDのとき、味方ユニット間の弱点マッチ（縦と横の関係）同士の結合度が高いものに点数を加える
                                }else if(e==f && GameBoard.battleTable[b][d]==0){
                                    PARAM_TABLE[a][b][e][c][d][f] += (int)(-GameBoard.distance(a % 9, a / 9, c % 9, c / 9));
                                //  同じIDのとき、味方ユニット間同士の結合度に点数を悪くする    
                                } else if (e == f  && GameBoard.battleTable[b][d] !=0) {
                                    PARAM_TABLE[a][b][e][c][d][f] += (int) (GameBoard.distance(a % 9, a / 9, c % 9, c / 9));
                                //  異なるIDのとき、相性が良いものには距離が近いほうがよい
                                }else if(e!=f && GameBoard.battleTable[b][d]==1){
                                    PARAM_TABLE[a][b][e][c][d][f] +=(int) (-GameBoard.battleTable[b][d] * GameBoard.distance(a % 9, a / 9, c % 9, c / 9));
                                //  異なるIDのとき、相性がわるいものには距離が遠いほうがよい    
                                }else if(e!=f && GameBoard.battleTable[b][d] == -1){
                                    PARAM_TABLE[a][b][e][c][d][f] = (int) (GameBoard.battleTable[b][d] * GameBoard.distance(a % 9, a / 9, c % 9, c / 9)); //  相性が悪いものへの距離
                                    //  それ以外の場合は評価を行わない    
                                }else{
                                    
                                }
                                
                                //  敵の場合はすべて逆にする
                                if (e == 1) PARAM_TABLE[a][b][e][c][d][f] *= -1;
                                
                            }
                        }
                        

                        }
                    }
                }
            }   //  テーブル作成多重ループここまで
        
        
        /*
        
        for(int i=0; i<9; i++){
            for(int k=0; k<9; k++){
                    System.out.print(String.format("%3d", PARAM_TABLE[i*9+k][3][i * 9 + k][3]));
            }
            System.out.println();
        }
        System.out.println();
                */
                
    }
    
    
    
    
    
    private static RelationHelper helper;
    public static RelationHelper getInstance(){
        if(helper==null){
            helper = new RelationHelper();
        }
        return helper;
    }
    
}
