/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.ai;

import cuiclient.game.GameMaster;

/**
 *  AlphaBetaから呼ばれる評価するためのクラスです
 *  評価関数を簡単に変えられるように別のクラスにしています
 */
public interface Evaluator {
    public double evaluate(GameMaster root);
}
