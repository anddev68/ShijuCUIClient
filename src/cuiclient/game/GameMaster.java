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

    
    @Override
    public void onChangeTurn() {
        
    }
    
    //  ----------------------------------------------------
    //  以下ラッパー（委譲クラスです）
    //  ------------------------------------------------------
    public void nextPhase(){ turnCounter.nextPhase(); }
    public void setPoint(int id,int p){ pointController.setPoint(id, p); }
    public void setTower(int index,int id){ gameBoard.setTower(index, id); }
    public void setUnitLocation(int x,int y,int index,int id){ gameBoard.setUnitLocation(x, y, index, id); }
    
    
}
