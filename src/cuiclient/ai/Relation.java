/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.ai;

import java.awt.Point;

/**
 *  2コマ間の関係を表すクラス
 *  この関係は次の論文を参考にしています
 *  http://www.graco.c.u-tokyo.ac.jp/~kaneko/papers/gpw03.pdf
 * 
 * この論文によると
 * 評価値(s) = sum_{ value(R(p,q)) }で求められるらしいです
 * R = ((73, 銀, 先手),(82, 飛, 後手))のように表すことにする
 * 
 * 「飛車は敵陣にいる方が価値が高い [7] といった 一つの駒と位置のみの評価も，同じ駒の関係 (Rp,p) を調整することで組み込むことが可能である．」
 *  p=qのときは同じコマで含まれる
 *  p=qのみの評価とし、他は0とすると、駒の評価だけとなる
 * 
 * 
 */
public class Relation {
    public Point pPos,qPos;    //  位置
    public int pIndex,qIndex;  //  駒の種類
    public int pId,qId;    //  所有者

    @Override
    public String toString(){
        String str = String.format("R=((%d%d,%d,%d),(%d%d,%d,%d))", pPos.x,pPos.y,
                pIndex,pId,qPos.x,qPos.y,qIndex,qId);
        return str;
    }
   

    
}
