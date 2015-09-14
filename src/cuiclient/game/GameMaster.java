/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient.game;

/**
 *
 */
public class GameMaster implements TurnCounter.Callback{
    
    public GameBoard gameBoard;
    public PointController pointController;
    public TurnCounter turnCounter;

    
    public GameMaster(){
        gameBoard = new GameBoard();
        pointController = new PointController();
        turnCounter = new TurnCounter(this);
    }
    
    public GameMaster(GameMaster org){
        this.turnCounter = new TurnCounter(org.turnCounter);
        this.gameBoard = new GameBoard(org.gameBoard);
        this.pointController = new PointController(org.pointController);
    }

    
    @Override
    public void onChangeTurn() {
        //  戦闘処理を行う
        
        //  戦闘を終えたら点数加算処理を行う
    }
    
    
    /**
     * 盤面の総合評価
     * プレイヤーIDを入れると、そのプレイヤーに対しての評価を返す
     */
    public double evaluate(int id){
        return 0;
    }
    
    /**
     * 実際に動かす
     * @param x x軸に動かすマスの数
     * @param y y軸に動かすマスの数
     * @param index 
     * @return trure 成功 false 失敗
     */
    public boolean  move(int x,int y,int index){
        return false;
    }
    
    /**
     * 動かせるかどうかチェックする
     * 実際に動かすことはしない
     * @param x 動かすマスの数
     * @param y
     * @param index
     * @return true 成功 false 失敗
     */
    public boolean checkMove(int x,int y,int index){
        return false;
    }
    
    
    //  ----------------------------------------------------
    //  以下ラッパー（委譲クラスです）
    //  ------------------------------------------------------
    public void nextPhase(){ turnCounter.nextPhase(); }
    public void setPoint(int id,int p){ pointController.setPoint(id, p); }
    public void setTower(int index,int id){ gameBoard.setTower(index, id); }
    public void setUnitLocation(int x,int y,int index,int id){ gameBoard.setUnitLocation(x, y, index, id); }
    public int getTurnState(){ return turnCounter.getTurnState(); }
    public int whoIsPlay(){ return turnCounter.whoIsPlay(); }
}
