/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.connection;

/**
 *
 * @author dell_user
 */
public interface PlayReceiver {
    void onReceiveDoPlay();
    void onReceiveUnit(int T,int N,int X,int Y);
    void onReceiveMultiLine();
    void onReceiveTower(int N,int T);
    void onReceiveScore(int T,int S);
    void onReceiveLineEnd();
    void onReceiveOK();
    void onReceiveCoundNotMove();
    void onReceivePlayed();
    
}
