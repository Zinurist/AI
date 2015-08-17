package chess.game;


import chess.gui.ChessPanel;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

public enum ChessPiece {

	pawn("chess/resources/pawn.png","chess/resources/pawnw.png"),
	castle("chess/resources/castle.png","chess/resources/castlew.png"),
	bishop("chess/resources/bishop.png","chess/resources/bishopw.png"),
	knight("chess/resources/knight.png","chess/resources/knightw.png"),
	queen("chess/resources/queen.png","chess/resources/queenw.png"),
	king("chess/resources/king.png","chess/resources/kingw.png");
	
	public BufferedImage black;
	public BufferedImage white;
	
	ChessPiece(String bPath, String wPath){
		try {
			black=ImageIO.read(ChessPanel.class.getClassLoader().getResourceAsStream(bPath));
			white=ImageIO.read(ChessPanel.class.getClassLoader().getResourceAsStream(wPath));
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(null, e.getMessage(), "Error", JOptionPane.OK_CANCEL_OPTION, JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
}
