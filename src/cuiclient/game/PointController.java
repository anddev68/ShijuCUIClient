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
    
    public PointController(PointController pc){
        point = new int[2];
        point[0] = pc.point[0];
        point[1] = pc.point[1];
    }
    
    public void setPoint(int id,int p){
        point[id] = p;
    }
    
    public void addPoint(int id,int p){
        point[id] +=p;
    }
    
    public int getPoint(int id){
        return point[id];
    }
    
}
