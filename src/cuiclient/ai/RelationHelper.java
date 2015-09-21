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
    
    //  評価テーブル
    private static double[][][][][][][][] PARAM_TABLE;

    
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
     * @return
     */
    public double evaluate(Relation r){
        return PARAM_TABLE[r.pPos.x][r.pPos.y][r.pIndex][r.pId][r.qPos.x][r.qPos.y][r.qIndex][r.qId];
    }
    
    
    
    /**
     * パターン1でパラメータを作成
     * パターン1→p==qかつ、5段目までの距離とする
     * @see PARAM_TABLE
     */
    private void initalizeParamWithDefault(){
        PARAM_TABLE = new double[9][9][4][2][9][9][4][2];
        for (int a = 0; a < 9; a++) {
            for (int b = 0; b < 9; b++) {
                for (int c = 0; c < 4; c++) {
                    for(int d=0; d<2; d++){
                        for (int e = 0; e < 9; e++) {
                            for (int f = 0; f < 9; f++) {
                                for (int g = 0; g < 4; g++) {
                                    for(int h=0; h<2; h++){
                                        //  Rp=Rs,p=sのときは同じコマ
                                        //  同じコマの時はタワーへの距離
                                        
                                        if(a==e&&b==f&&c==g&&d==h)
                                            PARAM_TABLE[a][b][c][d][e][f][g][h] = 100- Math.pow(b-4 , 2);
                                        else
                                            PARAM_TABLE[a][b][c][d][e][f][g][h] = 0;
                                               
                                        //  PARAM_TABLE[a][b][c][d][e][f][g][h] = Math.random();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }    
    }
    
    
    
    
    
    private static RelationHelper helper;
    public static RelationHelper getInstance(){
        if(helper==null){
            helper = new RelationHelper();
        }
        return helper;
    }
    
}
