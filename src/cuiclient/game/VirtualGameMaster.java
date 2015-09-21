/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

import cuiclient.Hand;

/**
 * 
 */
public class VirtualGameMaster extends GameMaster implements Comparable<VirtualGameMaster>{
    
    private Hand lastHand;
    private Hand lastHand2;
    private int priority;
    
    public VirtualGameMaster(GameMaster org,Hand hand){
        super(org);
        lastHand = hand;
        priority = 0;
    }
    
    public VirtualGameMaster(VirtualGameMaster org,Hand hand){
        super(org);
        this.lastHand = hand;
        this.lastHand2 = org.lastHand2;
        priority = org.priority;
    }
    
    public Hand getLastHand(){
        return lastHand;
    }
    
    public void setPriority(int priority){
        this.priority = priority;
    }

    @Override
    public int compareTo(VirtualGameMaster o) {
        return this.priority - o.priority;
    }
}
