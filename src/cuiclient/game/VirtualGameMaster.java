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
    
    private Hand hand;
    private int priority;
    
    public VirtualGameMaster(GameMaster org,Hand hand){
        super(org);
        
        if (org instanceof VirtualGameMaster) {
            //  VirtualGameMasterノードである場合は
            //  同じIDによる2手差しの可能性がある
            VirtualGameMaster vgm = (VirtualGameMaster) org;
            this.priority = vgm.priority;
            if(vgm.hand.getPlayerId() == hand.getPlayerId()){
                //  同じIDの場合はコピーして追加する
                this.hand = Hand.createHandInsertFront(vgm.hand, hand);
                
            }else{
                //  2手差しでない場合は新規作成
                this.hand = hand;
                
            }   
            
        }else{
            //  GameMasterノードである場合は前指した手がないため新しい手を保存する
            this.hand = hand;
            priority = 0;
        }
        

        
        
    }
    
    
    public Hand getLastHand(){
        return hand;
    }
    
    public void setPriority(int priority){
        this.priority = priority;
    }

    @Override
    public int compareTo(VirtualGameMaster o) {
        return this.priority - o.priority;
    }
}
