package chess.game;

import chess.gui.ChessPanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public class Chess {
	
	public static final int PW=0;//pawn
	public static final int PB=1;
	public static final int CW=2;//castle
	public static final int CB=3;
	public static final int BW=4;//bishop
	public static final int BB=5;
	public static final int HW=6;//horse=knight
	public static final int HB=7;
	public static final int QW=8;//queen
	public static final int QB=9;
	public static final int KW=10;//king
	public static final int KB=11;
	public static BufferedImage background;
	
	
	{
		
		try {
			background=ImageIO.read(ChessPanel.class.getClassLoader().getResourceAsStream("chess/resources/board2.png"));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, e.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		
	}
	

	/**
	 * odd number=black, even number=white
	 * 0-pawn
	 * 2-castle
	 * 4-bishop
	 * 6-knight
	 * 8-queen
	 * 10-king
	 */
	private byte[][] board;
	
	/**
	 * state of legal:
	 * 0-illegal move
	 * 1-legal move
	 * 2-legal, but blocked, therefore illegal (grayed out/red)
	 */
	private byte[][] moves;
	private byte numOfMoves;
	private String moveLog;
	
	/**
	 * [0]: white king at [0],[1]
	 * [1]: black king at [0],[1]
	 */
	private byte[][] kingsPos;
	
	private int player;//0=white, 1=black
	
	private int[] active;
	
	/**
	 * 0-all ok
	 * 1-check @ white king
	 * 2-check @ black king
	 * 3-checkmate @ white king
	 * 4-checkmate @ black king
	 * @return
	 */
	private int cmState;
	
	
	public String getMoveLog(){
		return moveLog;
	}
	
	public void addMoveLog(int xFrom, int yFrom, int xTo, int yTo){
		moveLog+=xFrom+" "+yFrom+" "+xTo+" "+yTo+" ";
	}
	
	public byte getPieceAt(int x, int y){
		return board[x][y];
	}
	
	public void setPieceAt(int x, int y, byte piece){
		board[x][y]=piece;
	}
	
	public byte getMoveAt(int x, int y){
		return moves[x][y];
	}
	
	public void setMoveAt(int x, int y, byte move){
		moves[x][y]=move;
	}
	
	public boolean selectedPiece(){
		return active[0]!=-1;
	}
	
	public int[] getSelectedPiece(){
		return active;
	}
	
	public int getCheckMateState(){
		return cmState;
	}
	
	public boolean isWhitesTurn(){
		return player==0;
	}
	
	public void resetMoves(){
		moves=new byte[8][8];
		numOfMoves=0;
	}
	
	public void addMove(int x, int y, int type){
		moves[x][y]=(byte)type;
		if(type==1){
			numOfMoves++;
		}
	}
	
	public Chess(){
		
		moveLog="";
		board=new byte[8][8];
		resetMoves();
		
		for(int x=0; x<8;x++){
			board[x][1]=PB;
			board[x][6]=PW;
		}
		
		board[0][0]=CB;
		board[1][0]=HB;
		board[2][0]=BB;
		board[3][0]=QB;
		board[4][0]=KB;
		board[5][0]=BB;
		board[6][0]=HB;
		board[7][0]=CB;
		
		board[0][7]=CW;
		board[1][7]=HW;
		board[2][7]=BW;
		board[3][7]=QW;
		board[4][7]=KW;
		board[5][7]=BW;
		board[6][7]=HW;
		board[7][7]=CW;
		
		for(int x=0; x<8;x++){
			for(int y=2; y<6;y++){
				board[x][y]=-1;
			}
		}
		
		player=0;
		
		active=new int[2];
		active[0]=-1;
		active[1]=-1;
		
		cmState=0;
		kingsPos=new byte[2][2];
		kingsPos[0][0]=4;
		kingsPos[0][1]=7;
		kingsPos[1][0]=4;
		kingsPos[1][1]=0;
	}
	
	public void randomMove(){
		List<int[]> pieces=new ArrayList<int[]>();
		for(int x=0; x<8;x++){
			for(int y=0; y<8;y++){
				if(board[x][y]%2 == player){
					int[] t=new int[2];
					t[0]=x;
					t[1]=y;
					pieces.add(t);
				}
			}
		}
		Random r=new Random();
		
		do{
			int[] num=pieces.get(r.nextInt(pieces.size()));
			click(num[0],num[1]);
		}while(numOfMoves==0);
		
		int moveNum=r.nextInt(numOfMoves);
		
		loop:for(int x=0; x<8;x++){
			for(int y=0; y<8;y++){
				if(moves[x][y] == 1){
					if(moveNum==0){
						click(x,y);
						break loop;
					}else{
						moveNum--;
					}
				}
			}
		}
		
	}
	
	public boolean click(int x, int y){

		if(active[0]==x && active[1]==y){
				active[0]=-1;
				resetMoves();
		//on own piece to select
		}else if(board[x][y]!=-1 && (board[x][y]%2 == player)){
			resetMoves();
			
			active[0]=x;
			active[1]=y;
			ChessPiece p=ChessPiece.values()[board[x][y]/2];
			determineMoves(p,x,y);
			return true;
		}//piece already slected, player clicks: empty place to move/enemy to attack
		else if(selectedPiece()){
			
			//check if move is legal
			if(moves[x][y] == 1){
				//TODO check if king will survive
				if(board[active[0]][active[1]]==KW){
					kingsPos[0][0]=(byte) x;
					kingsPos[0][1]=(byte) y;
				}else if(board[active[0]][active[1]]==KB){
					kingsPos[1][0]=(byte) x;
					kingsPos[1][1]=(byte) y;
				}
				//System.out.println("King posss: white: "+kingsPos[0][0]+" "+kingsPos[0][1]+", black: "+kingsPos[1][0]+" "+kingsPos[1][1]);
				
				addMoveLog(active[0],active[1],x,y);
				board[x][y] = board[active[0]][active[1]];
				board[active[0]][active[1]]=-1;
				active[0]=-1;
				
				player=(player==0)?1:0;
				updateCheckmateState(player);
				
				resetMoves();
				return true;
			}
		}
		return false;
	}
	
	
	public void determineMoves(ChessPiece p, int x, int y){
		int color=(board[x][y]%2);
		switch(p){
		case pawn:		
			checkPawn(color, x, y);
			break;
		case castle:
			checkCastle(color, x, y,true);
			break;
		case bishop:
			checkBishop(color, x, y,true);
			break;
		case knight:
			checkKnight(color, x, y,true);
			break;
		case queen:
			checkQueen(color, x, y);
			break;
		case king:
			checkKing(color, x, y);
			break;
		}
	}
	
	private void checkPawn(int color, int x, int y){
		//TODO without try catch
		int vector= color==0? -1:1;
		try{		
			addMove(x,y+vector,((board[x][y+vector]==-1) ? 1:2));
		}catch(ArrayIndexOutOfBoundsException ae){}
		try{
			addMove(x-1,y+vector,((board[x-1][y+vector]!=-1) && (board[x-1][y+vector]%2 != color) ? 1:2));
		}catch(ArrayIndexOutOfBoundsException ae){}
		try{
			addMove(x+1,y+vector,((board[x+1][y+vector]!=-1) && (board[x+1][y+vector]%2 != color) ? 1:2));
		}catch(ArrayIndexOutOfBoundsException ae){}
		try{
			//first move->2 fields possible
			if(((color==0 && y==6) || (color==1 && y==1)) && (board[x][y+vector]==-1)){
				vector= color==0? -2:2;
				addMove(x,y+vector,((board[x][y+vector]==-1) ? 1:2));
			}
		}catch(ArrayIndexOutOfBoundsException ae){}
	}
	
	private boolean checkCastle(int color, int x, int y, boolean record){
		int[][] vector={{1,0},{0,1},{-1,0},{0,-1}};
		return checkRay(color,x,y,vector,record,true);
	}
	
	private boolean checkBishop(int color, int x, int y, boolean record){
		int[][] vector={{1,1},{1,-1},{-1,1},{-1,-1}};
		return checkRay(color,x,y,vector,record,false);
	}
	
	private boolean checkRay(int color, int x, int y, int[][] vector, boolean record, boolean castleRequest){
		int nx, ny;
		boolean kingKill=false;
		for(int[] v:vector){
			nx=x;
			ny=y;
			while(true){
				nx+=v[0];
				ny+=v[1];
				try{
					if(board[nx][ny]==-1){
						if(record){
							addMove(nx,ny,1);
						}
					}else{
						if(record){
							addMove(nx,ny,((board[nx][ny]%2 != color) ? 1:2));
						}else{
							if(color==0){
								if(board[nx][ny]==QB || (castleRequest && board[nx][ny]==CB) || (!castleRequest && board[nx][ny]==BB)){
									kingKill=true;
								}
							}else{
								if(board[nx][ny]==QW || (castleRequest && board[nx][ny]==CW) || (!castleRequest && board[nx][ny]==BW)){
									kingKill=true;
								}
							}
						}
						break;
					}
				}catch(ArrayIndexOutOfBoundsException ae){
					break;
				}
			}	
		}
		return kingKill;
	}
	
	private boolean checkKnight(int color, int x, int y, boolean record){
		int[][] vector={{1,-2},{2,-1},{2,1},{1,2},{-1,2},{-2,1},{-2,-1},{-1,-2}};
		int nx, ny;
		boolean legal;
		boolean kingKill=false;;
		for(int[] v:vector){
			try{
				nx=x+v[0];
				ny=y+v[1];
				legal=((board[nx][ny]==-1) || (board[nx][ny]%2 != color));
				if(record){
					addMove(nx,ny,( legal ? 1:2));
				}else{
					if(color==0){
						if(board[nx][ny] == HB){
							kingKill=true;
						}
					}else{
						if(board[nx][ny] == HW){
							kingKill=true;
						}
					}
				}
			}catch(ArrayIndexOutOfBoundsException ae){
				//outside of field
			}
		}
		return kingKill;
	}
	
	private void checkQueen(int color, int x, int y){
		checkCastle(color, x, y,true);
		checkBishop(color, x, y,true);
	}
	
	/**
	 * Returns:
	 * false- all ok
	 * true- every move is in check
	 */
	private boolean checkKing(int color, int x, int y){
		int nx,ny;
		boolean kingSurvives=false;
		boolean legal;
		for(int vx=1; vx>=-1;vx--){
			for(int vy=1; vy>=-1;vy--){
				if(!(vx==0 && vy==0)){
					try{
						nx=x+vx;
						ny=y+vy;
						
						//      standard check-----------------------------------   AND  king doesnt die------------
						legal=( ((board[nx][ny]==-1) || (board[nx][ny]%2 != color))  &&  !checkKingLethal(color,nx,ny));
						//System.out.println(legal+" "+(board[nx][ny]==-1)+" "+(board[nx][ny]%2 != color)+" "+checkKingLethal(color,nx,ny));
						if(legal){
							kingSurvives=true;
						}
						addMove(nx,ny,( legal ? 1:2));
					}catch(ArrayIndexOutOfBoundsException ae){
						//outside of field
					}
				}
			}
		}
		return !kingSurvives;
	}
	
	
	private boolean checkKingLethal(int color, int x, int y){
		int king= (color==0?KW:KB);
		//if allied guy stands there
		if(board[x][y]%2==color && board[x][y]!=king){
			return false;
		}else{
			boolean pawnAttack=false;
			try{
				if(color==0){
					//System.out.println("WHIIITE king at "+x+" "+y);
					pawnAttack=board[x+1][y-1]==PB || board[x-1][y-1]==PB;
				}else{
					//System.out.println("BLAAACK king at "+x+" "+y);
					pawnAttack=board[x+1][y+1]==PW || board[x-1][y+1]==PW;
				}
			}catch(ArrayIndexOutOfBoundsException ae){
				//outside of field
			}
			
			boolean kingAttack=false;
			int difX=Math.abs(kingsPos[color==0?1:0][0]-x);
			int difY=Math.abs(kingsPos[color==0?1:0][1]-y);
			if(difX<=1 && difY<=1){
				kingAttack=true;
			}
			
			//System.out.println("Knight c: "+checkKnight(color,x,y,false));
			//System.out.println("bih c: "+checkBishop(color,x,y,false));
			//System.out.println("ccast c: "+checkCastle(color,x,y,false));
			//System.out.println("paw c: "+pawnAttack);
			
			
			return checkKnight(color,x,y,false) || checkBishop(color,x,y,false) || checkCastle(color,x,y,false) || pawnAttack || kingAttack;
		}
	}
	
	
	public void updateCheckmateState(int color){
		//TODO king itsself blocks checkRay->solving needed
		cmState=0;
		if(checkKingLethal(color,kingsPos[color][0],kingsPos[color][1])){
			cmState=1+color;
			if(checkKing(color,kingsPos[color][0],kingsPos[color][1])){
				cmState=3+color;
			}
		}
	}
	
}
