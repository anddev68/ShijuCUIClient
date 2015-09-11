/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

/**
 *  ターンカウンター
 *  このゲームはターンとフェーズのカウントが複雑であるため、単なるint型ではなくクラスで持つことにします
 * 
 *  ４＋戦闘フェーズを終えると、ターンカウントを1増やします
 */
public class TurnCounter {
    
    public interface Callback{
        public void onChangeTurn();
    }
    
    /**
     * ターンステータス＝フェーズ判別コード 仕様に準拠しています
     * このコードは変えないでください
     */
    public static final int STATE_PLAY_TURN1 = 11;
    public static final int STATE_PLAY_TURN2 = 12;
    public static final int STATE_PLAY_TURN3 = 13;
    public static final int STATE_PLAY_TURN4 = 14;    
    
    private int totalTurn;
    private int turnState;
    private int firstPlayerId;  //  先行プレイヤーId
    
    private Callback callback;
    
    
    public TurnCounter(Callback cb){
        this.callback = cb;
        this.firstPlayerId = 0;
        this.turnState = STATE_PLAY_TURN1;
        this.totalTurn = 0;
    }
    
    public TurnCounter(TurnCounter tc){
        this.totalTurn = tc.totalTurn;
        this.turnState = tc.turnState;
        this.callback = tc.callback;
        
    }
    
    public TurnCounter deepCopy(){
        return null;
    }
    
    /**
     * 次のフェーズへ進めます
     * ４を越えたら1へ戻し、ターンカウントを増やします
     * Callback#onChangeTurn()を呼ぶので、ゲームボードで捕獲してください
     */
    public void nextPhase(){
        turnState++;
        if(turnState==STATE_PLAY_TURN4+1){
            this.callback.onChangeTurn();
            turnState=STATE_PLAY_TURN1;
            this.totalTurn++;
        }
    }
    
    
    
}
