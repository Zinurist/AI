package chess.gui;

import chess.game.Chess;
import chess.game.Controller;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ChessFrame extends JFrame implements Runnable {
	
	private ChessPanel cp;
	private Controller cont;
	
	private JPanel contentPane;
	private JTextArea tLog;
	
	private JMenuBar mbar;
	private JMenu mFile;
	private JMenuItem miExit,miRestart,miSaveLog;
	
	public ChessFrame(Chess c){
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		
		cp=new ChessPanel(c);
		cont=new Controller(c,this);
		cp.addMouseListener(cont);
		
		tLog=new JTextArea("It's white's turn!");
		tLog.setEditable(false);
		
		contentPane=new JPanel(new BorderLayout());
		contentPane.add(cp, BorderLayout.CENTER);
		contentPane.add(tLog, BorderLayout.SOUTH);
		setContentPane(contentPane);
		
		mbar=new JMenuBar();
		mFile=new JMenu("File");
		
		miSaveLog=new JMenuItem("Save move log");
		miSaveLog.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String path=Save.saveDialog("the move log","log.txt",false);
				if(!path.isEmpty()){
					Save s=new Save(path);
					s.openFileWriter();
					s.write("\r\n","",cont.getChess().getMoveLog());
					s.closeFileWriter();
				}
			}
		});
		
		miRestart=new JMenuItem("Restart game");
		miRestart.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				restart();
			}
		});
		
		miExit=new JMenuItem("Exit");
		miExit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.exit(0);
			}
		});

		mFile.add(miSaveLog);
		mFile.add(miRestart);
		mFile.add(miExit);
		
		mbar.add(mFile);
		setJMenuBar(mbar);
		
		pack();
	}
	
	public Chess getChess(){
		return cont.getChess();
	}
	
	public void update(boolean moveSuccess){
		String log="error";
		switch(cont.getGameState()){
		case 0:log="It's "+cont.getPlayerColor()+"'s turn!"; break;
		case 1:log="It's "+cont.getPlayerColor()+"'s turn! White is in Check!"; break;
		case 2:log="It's "+cont.getPlayerColor()+"'s turn! Black is in Check"; break;
		case 3:log="Checkmate! Black wins!"; break;
		case 4:log="Checkmate! White wins!"; break;
		}
		
		if(moveSuccess){
			tLog.setText(log);
		}else{
			tLog.setText("Illegal move! "+log);
		}
		cp.repaint();
	}
	
	
	public void restart(){
		tLog.setText("It's white's turn!");
		Chess c=new Chess();
		cp.setChess(c);
		cont.setChess(c);
	}
	
	public void restart(Chess chess){
		cp.setChess(chess);
		cont.setChess(chess);
		
		String log="";
		switch(cont.getGameState()){
		case 0:log="It's "+cont.getPlayerColor()+"'s turn!"; break;
		case 1:log="It's "+cont.getPlayerColor()+"'s turn! White is in Check!"; break;
		case 2:log="It's "+cont.getPlayerColor()+"'s turn! Black is in Check"; break;
		case 3:log="Checkmate! Black wins!"; break;
		case 4:log="Checkmate! White wins!"; break;
		}
		tLog.setText(log);
	}

	@Override
	public void run() {
		for(int i=0; i<400; i++){
			for(int k=0; k<20; k++){
				cont.getChess().randomMove();
			}
			
			Save s=new Save("log.txt");
			s.openFileWriter();
			s.write("\r\n","",cont.getChess().getMoveLog());
			s.closeFileWriter();
			
			restart();
		}
		System.out.println("DONE!");
	}
	
}
