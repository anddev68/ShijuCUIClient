/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

/**
 *
 * @author dell_user
 */
public class PointController {
    
    private int[] point;
    
    public PointController(){
        point = new int[2];
    }
    
    public void setPoint(int id,int p){
        point[id] = p;
    }
    
}
