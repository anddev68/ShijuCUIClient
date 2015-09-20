/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

/**
 * ゲーム定跡
 */
public class GameFomula {
     // 有効移動範囲配列
    private final static int[][] effectiverange
            = {{0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 1, 1, 1, 1, 0, 0},
            {0, 0, 0, 1, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0}};

    /**
     * 定石1 上記有効移動範囲行列を用いた移動範囲の制限 移動可能範囲を無視した動きの場合はfalseが帰ります
     *
     * playerid0が下側という設定です
     *
     * @param movex
     * @param movey
     * @param x
     * @param y
     * @param nowplayerId
     * @return true 有効手 false 無効手
     */
    public static boolean formula1(int x, int y, int nowplayerId) {
        int result = 0;
        if (0 == nowplayerId) {
            result = effectiverange[y][x];
        } else {
            result = effectiverange[8 - y][x];
        }
        return result == 1;
    }
    
}
