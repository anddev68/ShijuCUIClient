/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

import java.awt.Point;

/**
 * AlphaBetaを適用できるようにボードを変更してみた
 */
public class AlphaBetaBoard {
    
    
    
    
    
    
    
    
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
