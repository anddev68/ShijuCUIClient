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
    
    public void setUnitLocation(int x,int y,int index,int id){
        this.unitLocation[id][index].x = x;
        this.unitLocation[id][index].y = y;
    }
    public void setTower(int index,int id){
        this.tower[index] = id;
    }
    
}
