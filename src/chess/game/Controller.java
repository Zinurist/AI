package chess.game;

import chess.gui.ChessFrame;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class Controller implements MouseListener{

	private Chess chess;
	private ChessFrame cf;
	
	public Controller(Chess chess,ChessFrame cf){
		this.chess=chess;
		this.cf=cf;
		
	}
	
	public Chess getChess() {
		return chess;
	}
	
	public void setChess(Chess chess){
		this.chess=chess;
	}
	
	public int getGameState(){
		return chess.getCheckMateState();
	}
	
	public String getPlayerColor(){
		return chess.isWhitesTurn()?"white":"black";
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getButton()==1){
			int x=e.getX()/96;
			int y=e.getY()/96;
			cf.update(chess.click(x, y));
		}else{
			chess.randomMove();
			cf.update(true);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

}
