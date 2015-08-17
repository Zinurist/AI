package chess.gui;

import chess.game.Chess;
import chess.game.ChessPiece;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class ChessPanel extends JPanel {

	private Chess chess;
	
	public ChessPanel(Chess chess){
		this.chess=chess;
		setPreferredSize(new Dimension(768,768));
	}
	
	public void setChess(Chess chess) {
		this.chess=chess;
	}
	
	@Override
	public void paintComponent(Graphics g){
		g.drawImage(Chess.background, 0, 0, 768, 768, null);
		
		int piece;
		ChessPiece p;
		for(int x=0; x<8; x++){
			for(int y=0; y<8; y++){
				piece=chess.getPieceAt(x, y);
				if(piece!=-1){
					p=ChessPiece.values()[piece/2];
					g.drawImage( ((piece%2)==0? p.white : p.black) ,x*96,y*96,96,96,null);
				}
				
				//draw rectangles from here on
				
				if(chess.getMoveAt(x,y)==1){
					g.setColor(Color.GREEN);
					g.drawRect(x*96,y*96,96,96);
					g.drawRect(x*96+1,y*96+1,94,94);
					g.drawRect(x*96+2,y*96+2,92,92);
				}else if(chess.getMoveAt(x,y)==2){
					g.setColor(Color.RED);
					g.drawRect(x*96,y*96,96,96);
					g.drawRect(x*96+1,y*96+1,94,94);
					g.drawRect(x*96+2,y*96+2,92,92);
				}
				
			}
		}
		
		if(chess.selectedPiece()){
			g.setColor(Color.BLUE);
			int x=chess.getSelectedPiece()[0];
			int y=chess.getSelectedPiece()[1];
			g.drawRect(x*96,y*96,96,96);
			g.drawRect(x*96+1,y*96+1,94,94);
			g.drawRect(x*96+2,y*96+2,92,92);
			//TODO draw possible moves
		}
	}
}
