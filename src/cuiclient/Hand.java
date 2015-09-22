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
     * 2手分は保存できるようにした
     */
    public class Hand{
        
        private int x,y,index,playerId;
        private int x2,y2,index2,playerId2;
        private boolean enable2;    //  2手目を有効にするかどうか
        
        public Hand(int x,int y,int index,int id){ this.x = x; this.y = y; this.index = index; this.playerId = id; enable2 = false;}
        public Hand(Hand org){this.x = org.x; this.y = org.y; this.index = org.index; this.playerId = org.playerId; enable2 = false;}
        

        
        public boolean equals(int x,int y,int index,int id){ 
            return (this.x==x&&this.y==y&&this.index==index&&this.playerId==id);
        }
        
        

        
        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            sb.append( ""+x+","+y+" index:"+index+" id:"+playerId );
            if(enable2){
                sb.append(" -> " + x2 + "," + y2 + " index:" + index2 + " id:" + playerId2);
            }
            return sb.toString();
        }        
        
        
        
        //  -------------------------------------------------------
        //  ゲッターメソッド
        //  ---------------------------------------------------------
        public int getX() {
            return this.x;
        }

        public int getY() {
            return this.y;
        }

        public int getIndex() {
            return this.index;
        }

        public int getPlayerId() {
            return this.playerId;
        }        
        
        
        //  ---------------------------
        //
        //  -------------------------------
        
        
        /**
         * 先頭に最新手を加えてコピーして手を作成します
         * 2手分用意できます
         * @param org オリジナル
         * @param temp オリジナルに対して追加する手
         * @return
         * @deprecated
         */
        public static Hand createHandInsertFront(Hand org,Hand temp){
            Hand hand = new Hand(org);
            hand.index2 = temp.index;
            hand.x2 = temp.x;
            hand.y2 = temp.y;
            hand.playerId2 = temp.playerId2;
            hand.enable2 = true;
            
            return hand;
        }
        
    }
