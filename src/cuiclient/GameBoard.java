/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cuiclient;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;


/**
 * ゲームボードクラス
 * ゲームに関する状態や情報およびそれを操作するメソッド類が準備されています
 */
public class GameBoard {

    
//
//  定数宣言
//
    
    /** 対戦用テーブル　battleTable[0][1] = 青竜vs玄武 = 1(前者の勝ち) 値が-1は後者の勝ち　0は引き分け*/
    public static final int[][] battleTable = { { 0, 1, 0,-1},
                                           {-1, 0, 1, 0},
                                           { 0,-1, 0, 1},
                                           { 1, 0,-1, 0} };
    
    public static final Point[] towerPos = {
        new Point(1,4),
        new Point(4,4),
        new Point(7,4)
    };
    
    public static final Point[] area = {
        new Point(4,7),
        new Point(4,1)
    };
    
    /**
     * ターン判別コード
     * 仕様に準拠
     */
    public static final int STATE_PLAY_TURN1 = 11;
    public static final int STATE_PLAY_TURN2 = 12;
    public static final int STATE_PLAY_TURN3 = 13;
    public static final int STATE_PLAY_TURN4 = 14;
    
    
    
    /** ユニットの位置 */
    public Point[][] unitLocation;
    /** 塔の保持状態 */
    public int[] towerHold;
    /** チームの得点 */
    public int[] teamPoint;

    /** ターン情報　*/
    public int turnState; 
    /** 先行チームのID */
    public int firstTeamId;
    
    
    /**
     * 最後に動かした情報　
     */
    public int lastIndex;
    public int lastX;
    public int lastY;
    public int lastId;
    
    
    
    /**
     * 最後に動かした手の情報を保存しておく
     */
    public static int HAND_HISTORY_NUM = 3; //  保存最大数
    private LinkedList<Hand> handHistory = new LinkedList<>();
    
    /**
     * 最初から最後まで通しで利用する変数
     * 手を保存しておく
     */
    private static LinkedList<Hand> staticHandHistory = new LinkedList<>();
    public static void initStaticHandHistory(){
        staticHandHistory = new LinkedList<>();
    }
    public static void addHandHistory(int x,int y,int index,int id){
        staticHandHistory.addLast(new Hand(x,y,index,id));
        if(staticHandHistory.size()>HAND_HISTORY_NUM){
            staticHandHistory.removeFirst();
        }
    }
    
    

 //   
 //
 // コンストラクタ
 // 2種類のコンストラクタを用意
 //
 // サーバもしくは任意にシュミレートしたデータを基に仮想ボードを作成するコンストラクタと
 // 同じゲームボードを複製するコンストラクタ
 //
 //
    
    /**
     * 現在の状態をコピーし仮想ボードを作る
     * @param turnState ターンの状態 11or12or13or14
     * @param firstTeamID 先行のチームID
     * @param teamPoint チームの点数
     * @param towerHold タワーの保持状態
     * @param unit ユニットの位置
     */
    public GameBoard(int turnState,int firstTeamID,int[] teamPoint,int[] towerHold,Point[][] unit){

        this.turnState = turnState;
        this.firstTeamId = firstTeamID;
        this.teamPoint = Arrays.copyOf(teamPoint, 2);
        this.towerHold = Arrays.copyOf(towerHold,3);
  
        unitLocation = new Point[2][4];
        for(int i=0; i<unitLocation.length; i++){
            for(int j=0; j<unitLocation[i].length; j++){
                unitLocation[i][j] = new Point();
                unitLocation[i][j].x = unit[i][j].x;
                unitLocation[i][j].y = unit[i][j].y;
            }
        }
        this.handHistory = new LinkedList(staticHandHistory);
        
    }
    
    public GameBoard(){
        this.teamPoint = new int[2];
        this.unitLocation = new Point[2][4];
        for (int i = 0; i < 4; i++) {
            this.unitLocation[0][i] = new Point();
            this.unitLocation[1][i] = new Point();
        }
        this.towerHold = new int[3];
    }
    
    
    /**
     * 同じのを実装する
     * @param c 複製用
     */
    public GameBoard(GameBoard c){
        this.turnState = c.turnState;
        this.firstTeamId = c.firstTeamId;
        this.teamPoint = Arrays.copyOf(c.teamPoint, 2);
        this.towerHold = Arrays.copyOf(c.towerHold,3);
        
        unitLocation = new Point[2][4];
        for(int i=0; i<unitLocation.length; i++){
            for(int j=0; j<unitLocation[i].length; j++){
                unitLocation[i][j] = new Point();
                unitLocation[i][j].x = c.unitLocation[i][j].x;
                unitLocation[i][j].y = c.unitLocation[i][j].y;
            }
        }
        handHistory = new LinkedList(c.handHistory);
        this.lastId = c.lastId;
        this.lastIndex = c.lastIndex;
        this.lastX = c.lastX;
        this.lastY = c.lastY;
    }
    
  

    

//
//  定石の定義
//  定石を関数化したものを置いておく
//
//  基本的にexpand()-子ノードを展開するときに
//  展開する必要があるかどうかを判定する
//  定石外の手はささない＝展開しない
//


    // 有効移動範囲配列
    private static int[][] effectiverange = 
                                    {{0,0,0,0,0,0,0,0,0},
                                     {0,0,0,0,0,0,0,0,0},
                                     {0,0,0,0,0,0,0,0,0},
                                     {0,0,0,0,0,0,0,0,0},
                                     {0,1,1,1,1,1,1,1,0},
                                     {0,0,1,1,1,1,1,0,0},
                                     {0,0,0,1,0,1,0,0,0},
                                     {0,0,0,0,0,0,0,0,0},
                                     {0,0,0,0,0,0,0,0,0}};
    
    /**
     * 定石1
     * 上記有効移動範囲行列を用いた移動範囲の制限
     * 移動可能範囲を無視した動きの場合はfalseが帰ります
     * 
     * playerid0が下側という設定です
     * @param movex
     * @param movey
     * @param x
     * @param y
     * @param nowplayerId
     * @return true 有効手 false 無効手
     */
    private static boolean formula1(int movex,int movey,int x,int y,int nowplayerId){
        int result = 0;
        if(0 == nowplayerId){
            result = effectiverange[y][x];
        }else{ 
            result = effectiverange[8-y][x];
        }
        return result == 1;
    }
    
    /**
     * 定石2
     * 過去n回分の同じ手は指すことがないようにする
     * 過去の手の中に同じ手が存在した場合はfalseが帰る
     * @param history
     * @param x
     * @param y
     * @param index
     * @param id
     * @return true 有効手 false 無効手
     */
    private static boolean formula2(LinkedList<Hand> history,int x,int y,int index,int id){
        for(Hand h : history){
            if(h.equals(x, y, index,id)){
                return false;
            }
        }
        return true;    //有効手
    }
    
    /**
     * 定石3
     * 自滅へは走らない
     * endTurn()はすべてのマスについて判定しているが、
     * formula3は動いたユニットのみチェックする。高速化処理。
     * 
     * 相性が負けている場合
     * あるいは2vs1になる可能性が少しでもある場合
     * のいずれかのとき、この関数はfalseを返す
     * 
     * @return
     */
    private static boolean formula3(Point[][] unitLocation,int x,int y,int index,int pId,int firstPlayerId,int turnState){
        int eId = (pId==0) ? 1 : 0;
        Point targetPoint = new Point(x,y);
                
        //  動ける距離
        int canEDist = 0;
        int canPDist = 0;
        
        //  残り行動回数の分、距離を引く
        switch(turnState){
            case STATE_PLAY_TURN1 : //  残り2回ずつなので
                canPDist =2;
                canEDist =2;
                break;
            case STATE_PLAY_TURN2:  //  先行プレイヤーは1回、後は2回
                canPDist = (firstPlayerId==pId) ? 1 : 2;
                canEDist = (firstPlayerId==eId)? 1: 2;
                break;
            case STATE_PLAY_TURN3:  //  どちらも1回ずつ
                canPDist =1;
                canEDist =1;
                break;
            case STATE_PLAY_TURN4:  //  後攻のみあと1回
                if(firstPlayerId==pId) canEDist=1;
                else canPDist=1;
        }
        
        //  すべての到達する可能性のあるユニットについてのバトルを試したいが
        //  とりあえずは2vs1と苦手なユニットが来るのを避ける感じで行きましょう
        
        //  苦手なユニットを回避する
        //  0は3, 1は0, 2は1, 3は2に弱い
        int remain = 0;
        int eIndex = (index==0) ? 3: index-1;
        remain = distance(targetPoint,unitLocation[eId][eIndex]);
        if(remain<=canEDist){   //  到達可能
            //  回避
            return false;
        }
        
        //  到達できないならまあいいでしょう
        return true;
        
        
        
        
        
        
    }
    
    /**
     * 定石4
     * 以下の条件をすべて満たすとき、そのユニットを動かすのは無効とする
     * ・現在タワーのマスにいる
     * ・その場所に[unit+2]/4で割ったものが来れない場合
     * ・負けるユニットが来ない場合
     * 
     * ただしすべてのユニットがタワー位置にいる場合、打つ手がなくなるので注意
     * @return 
     */
    private static boolean formula4(Point[][] unitLocation,int index,int pId,int turnState,int firstPlayerId){
        int eId = (pId==0) ? 1 : 0;
        
        //  タワーの位置にいるかどうか
        //  いない場合は子の定石を利用しない
        boolean flag = false;
        for(Point p :towerPos){
            if(p.x==unitLocation[pId][index].x && p.y==unitLocation[pId][index].y){
                flag = true;
            }
        }
        if(flag) return true;   //  その手は有効手である
        
        
         //  動ける距離
        int canEDist = 0;
        int canPDist = 0;
        
        //  残り行動回数の分、距離を引く
        switch(turnState){
            case STATE_PLAY_TURN1 : //  残り2回ずつなので
                canPDist =2;
                canEDist =2;
                break;
            case STATE_PLAY_TURN2:  //  先行プレイヤーは1回、後は2回
                canPDist = (firstPlayerId==pId) ? 1 : 2;
                canEDist = (firstPlayerId==eId)? 1: 2;
                break;
            case STATE_PLAY_TURN3:  //  どちらも1回ずつ
                canPDist =1;
                canEDist =1;
                break;
            case STATE_PLAY_TURN4:  //  後攻のみあと1回
                if(firstPlayerId==pId) canEDist=1;
                else canPDist=1;
        }
        
        //  苦手なユニットをとの距離を取得
        //  0は3, 1は0, 2は1, 3は2に弱い
        int dist1;
        int eIndex = (index==0) ? 3: index-1;
        dist1 = distance(unitLocation[pId][index],unitLocation[eId][eIndex]);
        
        //  援軍との距離を取得
        int dist2 = 0;
        dist2 = distance(unitLocation[pId][index],unitLocation[pId][(index+2)%4]);
        
        //  援軍が到達できる場合はその手は有効である
        if(dist2<=canPDist) return true;
        
        //  苦手なユニットが到達できない場合はその手は有効である
        if(dist1>canEDist) return true;
        
        //  その手は無効
        return false;
    }
    

    /**
     * 定石5
     * タワーの列以外の後退を禁止する
     * 
     */
    
    
 //   
 //
 // 公開メソッド
 // 
 //
 
    
    //  乱数によって動かす方向を決定するための配列
    static final int[] movex = {-1,0,1,-1,1,-1,0,1};
    static int[] movey = {-1,-1,-1,0,0,1,1,1};  
    
    
    /**
     * 定石を適用し、考えられるすべての手を実行した子ノードのゲームボードを返します
     * @return 
     */
    public ArrayList<GameBoard> extpand(){
        
        /**
         * 子ノードを定石を使って拡張する
         */
        ArrayList<GameBoard> children = new ArrayList<>();
        
        //  くじ引き
        ArrayList<Integer> omikuji = new ArrayList<Integer>();
        omikuji.add(0);
        omikuji.add(1);
        omikuji.add(2);
        omikuji.add(3);
        
        int id = this.whoIsPlay();
        
        //  タワーに存在するユニットの数を取得
        int towerUnit = 0;
        for(int j=0; j<4; j++){
            if(isTowerPos(unitLocation[id][j])) towerUnit++;
        }
        
        //  実行可能手をすべて展開
        for(int j=0; j<4; j++){
            int index = omikuji.remove((int)(omikuji.size()*Math.random()));
            
            //  すべてタワーにいない場合に限り
            //  タワーマスにいて苦手とするユニットが接近していなければタワーにいる
            if(towerUnit<3){
                boolean f4 = formula4(unitLocation,index,id,this.turnState,this.firstTeamId);
                if(!f4) continue;
            }
            
            for(int i=0; i<8; i++){

                int x = this.unitLocation[id][index].x + movex[i];
                int y = this.unitLocation[id][index].y + movey[i];
                
                //  x,yが範囲外なら無視
                if(!availableArea(x,y)){
                    continue;
                }

                    
                boolean f1 = formula1(movex[i],movey[i],x,y,id); // 移動範囲漏れチェック
                if(!f1) continue; //  定石外
                    
                //boolean f2 = formula2(handHistory,x,y,index,id);    //  過去の動き重複チェック
                //if(!f2) continue;   //  定石外 

                //  ボードを複製し、動かす
                GameBoard tmp = new GameBoard(this);
                tmp.move(movex[i], movey[i], index);
                tmp.handHistory.addLast(new Hand(x,y,index,id));
                //  一定数以上の履歴は破棄する
                if(tmp.handHistory.size() > HAND_HISTORY_NUM){
                    tmp.handHistory.removeFirst();
                }
                //  候補に追加
                children.add(tmp);

            }
        }
        return children;
    }
    
    /**
     * ランダムにゲーム終了までプレイする
     * @return ture プレイヤーの勝ち
     */
    public boolean doPlayout(){
       
        //  ゲームボードをコピーしおておく
        GameBoard playout = new GameBoard(this);
        
        //  プレイアウト専用移動履歴
        LinkedList<Hand> history = new LinkedList<>();
    
        //  ゲーム終了まで繰り返す
        while(playout.isGameEnd()==-1){
            
            //  移動候補手
            ArrayList<Hand> canditate = new ArrayList();
            
            
            
            int id = playout.whoIsPlay();
            for(int j=0; j<4; j++){ //  すべての手について
                
                //  タワーマスにいて苦手とするユニットが接近していなければタワーにいる
                boolean f4 = formula4(unitLocation,j,id,this.turnState,this.firstTeamId);
                if(!f4) continue;
                
                
                for(int i=0; i<8; i++){
                    
                    int x = playout.unitLocation[id][j].x + movex[i];
                    int y = playout.unitLocation[id][j].y + movey[i];
                    
                    //  x,yが範囲外なら無視
                    if(!availableArea(x,y)){
                        continue;
                    }

                    //  定石1を適用
                    boolean f1 = formula1(movex[i],movey[i],x,y,id);
                    if(!f1) continue;   //  定石外

                    //  定石2を適用
                    boolean f2 = formula2(history,x,y,j,id);
                    if(!f2) continue;   //  定石外
                    
                    //  定石3
                    /*
                    boolean f3 = formula3(playout.unitLocation,x,y,j,id,playout.firstTeamId,playout.turnState);
                    if(!f3) continue;
                    */
                    
                    //  ここまできたら候補に追加
                    canditate.add(new Hand(x,y,j,id));
                }
            }
            
            //  候補からランダム選択
            //  候補が1個もない場合はgetAnyHand()でなんでもいいから動かす
            Hand hand;
            if(canditate.size()==0){
               hand = getAnyHand(this.whoIsPlay());
               
            }else{
                int rand = (int)(canditate.size() * Math.random());
                hand = canditate.get(rand);  
            }
            

            //  動かす
            boolean result = playout.movePos(hand.x, hand.y, hand.index);
           
            //  ログに保存
            history.addLast(hand);
            if(history.size()>5)
                history.removeFirst();
        }
    
        //  勝ちの場合は勝ちの回数を増やす
        //  ここを修正
        //  チームIDではなく現在プレイ中のプレイヤーID
        if( playout.isGameEnd()== this.whoIsPlay()){
            return true;
       }
        return false;
   }
    
    
    
    
    /**
     * 実行可能な手をなんでもいいからとにかく返す関数
     * 手がなくなった場合など、とにかく進めたい場合に利用する
     * @return 手
     */
    public Hand getAnyHand(int id){
        for(int i=0; i<8; i++){
            int x = movex[i] + unitLocation[id][0].x;
            int y = movey[i] + unitLocation[id][0].y;
            if( availableArea(x,y) ){
                return new Hand(x,y,0,id);
            }
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    
 //
 // 仮想ボードに対してなんらかの処理を行う
 // 非公開メソッド
 //
 // これらは基本的にexpand()で呼ばれることになる
 //
 
    
    /**
     * ユニットを任意の場所に動かす
     * 
     * 2015/08/31追加
     * 失敗する条件は次の通り
     * ・現在の座標と移動先の座標が同じ場合
     * ・座標が範囲外を参照した場合
     * ・移動距離が1マス以上を超えた場合
     * 
     * 
     * @param x 動かした先の座標
     * @param y 動かした先の座標
     * @param index 動かすユニット番号 0-3
     * @return trure 成功 false 失敗
     */
    public boolean movePos(int x,int y,int index){
        int id = this.whoIsPlay();
        
        //  範囲外参照チェック
        if(!GameBoard.availableArea(x, y)){
            return false;
        }
        
        //  距離が0もしくは2以上の移動は無効
        if( GameBoard.distance(unitLocation[id][index],new Point(x,y)) !=1){
            return false;
        }

        
        
        
        unitLocation[id][index].x = x;
        unitLocation[id][index].y = y;
        
        this.lastIndex = index;
        this.lastX = x;
        this.lastY = y;
        this.lastId = this.whoIsPlay();
        
        nextPhase();
        return true;
    }
    
    
    /**
     * ユニットを任意の方向に動かす
     * @param vecX x軸方向にどのように動かすか -1,0,1
     * @param vecY y方向にどのように動かすか -1,0,1
     * @param index 動かすユニット番号 0-3
     * @return trure 成功 false 失敗
     */
    public boolean move(int vecX,int vecY,int index){
         int id = this.whoIsPlay();
        
        unitLocation[id][index].x += vecX;
        unitLocation[id][index].y += vecY;
        
        this.lastIndex = index;
        this.lastX = unitLocation[id][index].x;
        this.lastY = unitLocation[id][index].y;
        this.lastId = this.whoIsPlay();
        
        nextPhase();

        return true;
    } 
    
   
    /**
     * 終了条件チェック
     * @return -1 終了ではない/0 プレイヤー0の勝利/1 プレイヤー1の勝利
     */
    private int isGameEnd(){
        if(teamPoint[0]>=50)
            return 0;
        else if(teamPoint[1]>=50)
            return 1;
        return -1;
    }
   
    /**
     * プレイヤーのIdを返す
     */
    /*
    public int nowPlayerId(){
        //  return (firstTeamId+GameBoard.teamId+1)%2;
        if( whoIsPlay()==teamId ) return teamId;
        return enemyteamId;
    }*/
    
    /**
     * 次の手番へ進める
     * trueが帰るとターンの切り替えを行った
     */
    private boolean nextPhase(){
        if(this.turnState == STATE_PLAY_TURN1){
            this.turnState = STATE_PLAY_TURN2;
        } else if(this.turnState == STATE_PLAY_TURN2){
            this.turnState = STATE_PLAY_TURN3;
        } else if(this.turnState == STATE_PLAY_TURN3){
            this.turnState = STATE_PLAY_TURN4;
        } else if(this.turnState == STATE_PLAY_TURN4){
            //  フェーズの初期化
            this.turnState = STATE_PLAY_TURN1;
            //  戦後入れ替え
            if(firstTeamId==1)
                firstTeamId = 0;
            else
                firstTeamId = 1;

            //  ここで切り替え時の点数加算塔の処理
            endTurn();
            
            return true;
        }
        //  ターンを切り替えてない
        return false;
    }
    
    /**
     * ターン終了時の処理
     */
    private void endTurn(){
        //  ぶつかったときの処理
        //  1セルごとに検索
        
        
        
        for(int i=0; i<9; i++){ //  yについて
            for(int j=0; j<9; j++){ //  xについて
                ArrayList<Integer> unit0 = new ArrayList<Integer>();
                 ArrayList<Integer> unit1 = new ArrayList<Integer>();
                //  同じ位置にいるものを追加
                for(int k=0; k<4; k++){
                    if(unitLocation[0][k].x == j && unitLocation[0][k].y == i){
                        unit0.add(k);
                    }
                    if(unitLocation[1][k].x == j && unitLocation[1][k].y == i){
                        unit1.add(k);
                    }                    
                }  
                        //  重なったユニット同士でバトル
                    int result = battleUnits(unit0,unit1);
                    switch(result){
                        case 0: //  player0の勝利
                            backUnits(unit1,1);
                            for(int var : unit1){
                                teamPoint[0] += 2;
                            }
                            break;
                        case 1: //  player1の勝利
                            backUnits(unit0,0);
                            for(int var : unit0){
                                teamPoint[1] += 2;
                            }
                            break;
                        case 2: //  引き分
                            backUnits(unit0,0);
                            backUnits(unit1,1);
                            break;
                        default:   //   元に戻す処理
                            break;
                            
                    }
            }
        }

        
        //  ターン終了時にその位置にいて、タワーの所持者が異なる場合タワーを塗り替える
        for(int i=0; i<4; i++){
            for(int j=0; j<3; j++){
                if( this.unitLocation[0][i].equals(towerPos[j]) && this.towerHold[j]!=0 ){
                    this.towerHold[j] = 0;
                    //System.out.println("タワーを0に塗り替えた");
                    //  ポイント加算
                    this.teamPoint[0]++;
                }
                else if( this.unitLocation[1][i].equals(towerPos[j]) && this.towerHold[j]!=1 ){
                    this.towerHold[j] = 1;
                    //System.out.println("タワーを1に塗り替えた");
                    //  ポイント加算
                    this.teamPoint[1]++;
                }
            }
        }
        
        //  タワー保持によるボーナス
        calcTowerPoint();
        
    }
    
    /**
     * バトル処理
     */
    private int battleUnits(ArrayList<Integer> u1, ArrayList<Integer> u2){
        if(u1.isEmpty() && u2.isEmpty()){
            return -1;
        } else if(u1.size() > u2.size()){
            return 0;
        } else if(u1.size() < u2.size()){
            return 1;
        } else{
            //System.out.println("battle start.");
            
            //同数の場合
            if(u1.size() == 1){
                int a = u1.get(0);
                int b = u2.get(0);
                if(battleTable[a][b] == 1){
                    return 0;   //  0の勝ち
                } else if (battleTable[a][b] == -1){
                    return 1;
                } else {
                    return 2;
                }
            } else if(u1.size() == 2) {
                int count = 0;
                for(int a:u1){
                    for(int b:u2){
                        count += battleTable[a][b];
                    }
                }
                if(count > 0){
                    return 0;
                } else if (count < 0){
                    return 1;
                } else {
                    return 2;
                }
            }
            //3：3はルール上ありえない
            return -1;
        }
    }    
    
    
    /**
     * ユニットを元に戻す
     */
    private void backUnits(ArrayList<Integer> player,int playerId){
       if(playerId==0){
           for(Integer i : player){
               this.unitLocation[0][i].x = area[0].x;
               this.unitLocation[0][i].y = area[0].y;
               //   System.out.println("ユニットを元に戻した");
           }
                               
       }else if(playerId==1){
           for(Integer i : player){
               this.unitLocation[1][i].x = area[1].x;
               this.unitLocation[1][i].y = area[1].y;
               //   System.out.println("ユニットを元に戻した");
           }
       }
    }
    
    
    
    /** 次の手がどちらかを返す */
    public int whoIsPlay(){
        if(this.turnState == STATE_PLAY_TURN1){
            return this.firstTeamId;
        } else if(this.turnState == STATE_PLAY_TURN2){
            return (this.firstTeamId+1)%2;
        } else if(this.turnState == STATE_PLAY_TURN3){
            return (this.firstTeamId+1)%2;
        } else if(this.turnState == STATE_PLAY_TURN4){
            return this.firstTeamId;
        }
        return -1;
    }
    
    

    
    /**
     * タワー保持によるボーナス
     */
    private void calcTowerPoint(){
        
        //タワーの保持状態を確認し、加点する
        for(int i=0;i<this.towerHold.length;i++){
            if(this.towerHold[i] == 0){
                this.teamPoint[0]++;
            } else if(this.towerHold[i] == 1){
                this.teamPoint[1]++;
            }
        }
    }
    
    /**
     * 状態を表示する
     */
    public void print(){
        System.out.println("---   PRINT GAME BOARD   ---");
        
        //  ユニット情報の表示
        System.out.print("unit[0] ");
        for(int i=0; i<4; i++)
            System.out.print(unitLocation[0][i].x+","+unitLocation[0][i].y+" ");
        System.out.println();
        System.out.print("unit[1] ");
        for(int i=0; i<4; i++)
            System.out.print(unitLocation[1][i].x+","+unitLocation[1][i].y+" ");
        System.out.println();
        
        //  点数情報の表示
        System.out.println("teamPoint={"+ teamPoint[0] + "," + teamPoint[1] +"}");
        
        //  タワーの保持状態の表示
        System.out.println("towerHold={"+ towerHold[0] + "," + towerHold[1] + "," + towerHold[2] +"}");
        
    }
    
    
 //
 // クラス定義
 //
    

    
    
     /** ２点の距離を計算（上下左右、斜めのどこでも１歩） */
    public static int distance(Point a,Point b){
        if(a.x == b.x){
            return Math.abs(a.y - b.y);
        } else if(a.y == b.y){
            return Math.abs(a.x - b.x);
        } else {
            //斜めに近づく場合は長い方と同じだけで大丈夫
            int xdef = Math.abs(a.x - b.x);
            int ydef = Math.abs(a.y - b.y);
            if(xdef > ydef){
                return xdef;
            } else {
                return ydef;
            }
        }
    }   
    
    
     /** 物理的に移動可能かどうか判断 */
    public static boolean availableArea(int x,int y){
        if(x<0) return false;
        if(x>=9) return false;
        if(y>=9) return false;
        if(y<0) return false;
        return true;
    }
    
    
    /** タワーと等しいかどうか判定 */
    public static boolean isTowerPos(Point p){
        for(int i=0; i<3; i++){
            if(distance(p,towerPos[i])==0) return true;
        }
        return false;
    }
    
    /** 一番短いタワーまでの距離を算出 */
    public static int distanceTower(Point p){
        int min=Integer.MAX_VALUE;
        for(int i=0; i<3; i++){
            int tmp = distance(p,towerPos[i]);
            if(tmp<min) min = tmp;
        }
        return min;      
    }
    
}
