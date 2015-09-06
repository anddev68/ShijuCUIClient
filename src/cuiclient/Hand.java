/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;

/**
 *
 * @author kano
 */
    /**
     * 指した手の情報
     */
    public class Hand{
        public int x,y,index,playerId;
        public Hand(int x,int y,int index,int id){ this.x = x; this.y = y; this.index = index; this.playerId = id; }
        public boolean equals(int x,int y,int index,int id){ 
            return (this.x==x&&this.y==y&&this.index==index&&this.playerId==id);
        }
        
        @Override
        public String toString() {
            return "" + x + "," + y + " index:" + index + " id:" + playerId;
        }
    }
