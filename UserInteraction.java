import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.io.*;
import javax.imageio.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.sound.sampled.*;

public class UserInteraction extends JFrame{
	public static final long serialVersionUID = 1L;

	boolean icons = false;

	int size_x=800, size_y=800;
	int boardWidth = size_x<size_y?size_x:size_y;
	SquareTactics squaretactics;
	boolean mouse_is_down,up,down,left,right,space,showPow;
	int mX=0,mY=0;
	int grid_space=(boardWidth-100)/8;
	int piece_rad = grid_space/2;
	int piece_space=piece_rad/2;
	int select_spacing=4;
	int text_height = 40;
	int textWidth = 200;
	int frameX = 80;
	int frameY = 70;
	Font f = new Font("Dialog", Font.PLAIN, 16);

	TextBox reset,load,save,rules,undo,start_chess_game, start_dfa_game, editarmy, blackWinText,whiteWinText,whiteTurnText,blackTurnText;

	String 	rulesText = "Square Tactics is similar to chess in that you must put\nthe opponents King in check mate.\nThis is where the similarities end!";

	Clip nomSound;
	AudioInputStream nomSoundStream;
	Image undoimg, background;
	BufferedImage allChessPieces;
	Image[] chessPieces = new Image[12];

	public UserInteraction(SquareTactics _squaretactics){

		setTitle("Square Tactics");

		squaretactics = _squaretactics;

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();

		try{
			undoimg = ImageIO.read(new File("assets/undo.png"));
		}catch(IOException e){
			p("error reading image");
		}
		try{
			background = ImageIO.read(new File("assets/background.jpg"));
		}catch(IOException e){
			p("error reading image");
		}

		try{
			allChessPieces = ImageIO.read(new File("assets/chess.png"));
		}catch(IOException e){
			p("error reading image");
		}

		try{
			nomSoundStream = AudioSystem.getAudioInputStream(new File("assets/nom.wav"));
			AudioFormat format = nomSoundStream.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, format);
			nomSound = (Clip) AudioSystem.getLine(info);
			nomSound.open(nomSoundStream);
		}catch(Exception e){
			p("error reading sound");
		}

		int imageNum = 0;

		for (int i=0; i<12; i++) {
			chessPieces[i] = allChessPieces.getSubimage((i>=6?10:0)+202*(i%6),8+320*(i<6?0:1),202,320);
		}

		reset = new TextBox(squaretactics, 370,10,80,40,"Reset"){
			public void do_action(){
				p("reset game");
				squaretactics.reset();
			}
		};
		load = new TextBox(squaretactics, 460,10,80,40,"Load"){
			public void do_action(){
				p("load game");
				squaretactics.load();
			}
		};
		save = new TextBox(squaretactics, 550,10,80,40,"Save"){
			public void do_action(){
				p("save game");
				squaretactics.save();
			}
		};
		rules = new TextBox(squaretactics, 640,10,80,40,"Rules"){
			public void do_action(){
				p("display rules");
				squaretactics.rules();
			}
		};
		undo = new TextBox(squaretactics, 70,10,50,40,undoimg){
			public void do_action(){
				p("undo clicked");
				squaretactics.undo();
			}
		};
		start_chess_game = new TextBox(squaretactics, 70,80,300,40, "Start Chess Game"){
			public void do_action(){
				p("Start Default Chess Game");
				squaretactics.current_stage = "in game";
				squaretactics.setBlackArmy(new Chess());
				squaretactics.setWhiteArmy(new Chess());
				squaretactics.reset();
			}
		};
		start_dfa_game = new TextBox(squaretactics, 70,150,300,40, "Start Death From Above Game"){
			public void do_action(){
				p("Start Death From Above Chess Game");
				squaretactics.current_stage = "in game";
				squaretactics.setBlackArmy(new DeathFromAbove());
				squaretactics.setWhiteArmy(new DeathFromAbove());
				squaretactics.reset();
			}
		};
		editarmy = new TextBox(squaretactics, 70,220,300,40,"Edit Army"){
			public void do_action(){
				p("edit army");
				squaretactics.current_stage = "edit army";
				squaretactics.myGUI.repaint();
			}
		};

		blackWinText = new TextBox(squaretactics, (size_x-textWidth)/2,(boardWidth-text_height)/2,textWidth,text_height,"Black has Won");
		whiteWinText = new TextBox(squaretactics, (size_x-textWidth)/2,(boardWidth-text_height)/2,textWidth,text_height,"White has Won");
		whiteTurnText = new TextBox(squaretactics, 130,10,textWidth,text_height,"White's Turn");
		blackTurnText = new TextBox(squaretactics, 130,10,textWidth,text_height,"Black's Turn");

		blackWinText.set_inactive();
		whiteWinText.set_inactive();
		whiteTurnText.set_inactive();
		blackTurnText.set_inactive();

		// setResizable( false );

		JPanel drawing = new JPanel(){
			public static final long serialVersionUID = 1L;
			public void paint(Graphics g){
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g.setFont(f);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //anti alias to make lines smooth
				AffineTransform at = g2.getTransform();

				TextBox.HIDE_ALL();

				if(squaretactics.current_stage == "main menu"){
					g.drawImage(background,50,50,boardWidth-100-3,boardWidth-100-3,null);
					start_chess_game.set_visible(true);
					start_dfa_game.set_visible(true);
					editarmy.set_visible(true);

				}else if(squaretactics.current_stage == "in game"){

					g.drawImage(background,50,50,boardWidth-100-3,boardWidth-100-3,null);

					for (int i=0; i<squaretactics.board.width; i++) {
						for (int j=0; j<squaretactics.board.height; j++) {

							g.setColor(new Color(255,255,255,200));
							if(((i+j*8)+(j%2))%2==0){
								g.setColor(new Color(100,100,100,200));
							}
							g.fillRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);
							g.setColor(Color.black);
							g.drawRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);

							Piece tempPiece = squaretactics.board.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.board, i,j,g, false);
								}
							}
						}
					}

					if(squaretactics.pinningKing.size()>0){
						g.setColor(new Color(255,0,0,150));
						for (BoardMoves location: squaretactics.pinningKing) {
							// p("pinning: "+location.x+" "+location.y);
							g.fillRect(50+grid_space*location.x,50+grid_space*location.y,grid_space,grid_space);
						}
					}

					if(squaretactics.selectedPiece!=null){
						for (int i=0; i<squaretactics.possibleMoves.size(); i++) {
							BoardMoves move = squaretactics.possibleMoves.get(i);
							if(move.move_type=="Jump"){
								g.setColor(new Color(200,200,0,50));
							}else if(move.move_type=="Step"){
								g.setColor(new Color(0,0,200,50));
							}else if(move.move_type=="Slide"){
								g.setColor(new Color(200,0,0,50));
							}
							g.fillRect(50+grid_space*move.x,50+grid_space*move.y,grid_space,grid_space);
						}
					}

					if(squaretactics.checkMate&&squaretactics.whiteTurn){
						blackWinText.set_visible(true);
					}else if(squaretactics.checkMate&&!squaretactics.whiteTurn){
						whiteWinText.set_visible(true);
					}else{
						if(squaretactics.rules){

							undo.set_inactive();
							save.set_inactive();
							load.set_inactive();
							reset.set_inactive();
							whiteTurnText.set_inactive();
							blackTurnText.set_inactive();

							g.setColor(new Color(255,255,255,210));
							g.fillRect(60,60,size_x-120,size_y-120);
							g.setColor(Color.black);
							g.drawRect(60,60,size_x-120,size_y-120);
							for (String line : rulesText.split("\n")){
			        			g.drawString(line, frameX, frameY += g.getFontMetrics().getHeight());
							}
							frameY = 70;

						}else{

							undo.set_active();
							save.set_active();
							load.set_active();
							reset.set_active();

							if(squaretactics.selectedPiece!=null){
								g.setColor(Color.blue);
								g.drawRect(50+grid_space*squaretactics.selX+select_spacing,50+grid_space*squaretactics.selY+select_spacing,grid_space-select_spacing*2,grid_space-select_spacing*2);
							}
						}



						undo.set_visible(true);
						save.set_visible(true);
						load.set_visible(true);
						reset.set_visible(true);
						rules.set_visible(true);
						whiteTurnText.set_active();
						blackTurnText.set_active();

						if(squaretactics.whiteTurn){
							whiteTurnText.set_visible(true);
						}else{
							blackTurnText.set_visible(true);
						}

					}
				}else if(squaretactics.current_stage == "edit army"){

					p("army cost= "+ squaretactics.editArmyBoard.getArmyScore());

					//number of points left in a bar with each piece as a color taking up part of the bar
					//help box telling you hoe to use the interface
					//pieces
					//moves

					for (int i=0; i<5; i++) {
						for (int j=0; j<5; j++) {

							g.setColor(new Color(255,255,255,200));
							g.fillRect(300+grid_space*i,50+grid_space*j,grid_space,grid_space);
							g.setColor(Color.black);
							g.drawRect(300+grid_space*i,50+grid_space*j,grid_space,grid_space);

							// Piece tempPiece = squaretactics.board.get(i,j);
							// if(tempPiece!=null){
							// 	if(icons){
							// 		drawPiece(tempPiece,i,j,g);
							// 	}else{
							// 		drawPieceImage(tempPiece, squaretactics.board, i,j,g);
							// 	}
							// }
						}
					}

					for (int i=0; i<2; i++) {
						for (int j=0; j<8; j++) {

							g.setColor(new Color(255,255,255,200));
							g.fillRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);
							g.setColor(Color.black);
							g.drawRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);

							// Piece tempPiece = squaretactics.board.get(i,j);
							// if(tempPiece!=null){
							// 	if(icons){
							// 		drawPiece(tempPiece,i,j,g);
							// 	}else{
							// 		drawPieceImage(tempPiece, squaretactics.board, i,j,g);
							// 	}
							// }
						}
					}

					for (int i=0; i<2; i++) {
						for (int j=0; j<8; j++) {

							g.setColor(new Color(255,255,255,200));
							if(((i+j*8)+(j%2))%2==0){
								g.setColor(new Color(100,100,100,200));
							}
							g.fillRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);
							g.setColor(Color.black);
							g.drawRect(50+grid_space*i,50+grid_space*j,grid_space,grid_space);

							Piece tempPiece = squaretactics.editArmyBoard.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.editArmyBoard, i,j,g ,true);
								}
							}
						}
					}

					for (int i=0; i<5; i++) {
						for (int j=0; j<5; j++) {

							g.setColor(new Color(255,255,255,200));
							if(((i+j*8)+(j%2))%2==0){
								g.setColor(new Color(100,100,100,200));
							}
							g.fillRect(300+grid_space*i,50+grid_space*j,grid_space,grid_space);
							g.setColor(Color.black);
							g.drawRect(300+grid_space*i,50+grid_space*j,grid_space,grid_space);

							Piece tempPiece = squaretactics.editMovesBoard.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.editMovesBoard, i,j,g, true);
								}
							}
						}
					}

					if(squaretactics.selectedPiece!=null){

						p("size: "+squaretactics.possibleMoves.size());

						for (int i=0; i<squaretactics.possibleMoves.size(); i++) {

							String moveText = "";
							int textLine = 0;

							BoardMoves move = squaretactics.possibleMoves.get(i);

							if(move.move_type=="Jump"){
								g.setColor(new Color(200,200,0,50));
							}else if(move.move_type=="Step"){
								g.setColor(new Color(0,0,200,50));
							}else if(move.move_type=="Slide"){
								g.setColor(new Color(200,0,0,50));
							}
							
							g.fillRect(300+grid_space*move.x,50+grid_space*move.y,grid_space,grid_space);


							//should there be symbols to indicate what a move is
							//on hover tips would also be useful, would this be a separate class?
							if(move.only_first_move){
								moveText = "Only First Move";
								int width_offset = g.getFontMetrics().stringWidth(moveText)/2;
								g.drawString(moveText, 300 + grid_space*move.x + 40 - width_offset, 50+20+(textLine*20)+grid_space*move.y);
								textLine++;
							}
							if(move.move_properties.restricted && !move.move_properties.capture){
								moveText = "Only Move";
								int width_offset = g.getFontMetrics().stringWidth(moveText)/2;
								g.drawString(moveText, 300 + grid_space*move.x + 40 - width_offset, 50+20+(textLine*20)+grid_space*move.y);
								textLine++;
							}
							if(move.move_properties.canOnlyCapture()){
								moveText = "Only Capture";
								int width_offset = g.getFontMetrics().stringWidth(moveText)/2;
								g.drawString(moveText, 300 + grid_space*move.x + 40 - width_offset, 50+20+(textLine*20)+grid_space*move.y);
								textLine++;
							}

							p(300+grid_space*move.x +","+ 50+grid_space*move.y +","+ grid_space +","+ grid_space);
						}

						g.setColor(Color.BLACK);

						int textLine = 0;

						g.drawString("Piece Name: "+squaretactics.selectedPiece.getName(), 300, 500 + textLine*20);
						textLine++;
						g.drawString("Piece Cost: "+squaretactics.selectedPiece.getCostValue(), 300, 500 + textLine*20);
						textLine++;
						g.drawString("Number of Moves: "+squaretactics.selectedPiece.moves.size(), 300, 500 + textLine*20);
						textLine++;
						for (Move thisMove : squaretactics.selectedPiece.moves) {
							g.drawString("Cost: "+thisMove.getCost()+" Move Type: "+thisMove.move_type+", Direction: "+thisMove.x+","+thisMove.y, 300, 500 + textLine*20);
							textLine++;
						}
						g.drawString("Wrapping: "+squaretactics.selectedPiece.wrapping, 300, 500 + textLine*20);
						textLine++;
						g.drawString("Double Move: "+squaretactics.selectedPiece.double_move, 300, 500 + textLine*20);
						textLine++;
					}

					
				}

				TextBox.DRAW_ALL(g);
			}
		};

		addKeyListener(new KeyListener(){ //NOT THIS BUT FRAME
			public void keyPressed(KeyEvent e)	{	setKey(true,e.getKeyCode());	}
			public void keyReleased(KeyEvent e)	{	setKey(false,e.getKeyCode());	}
			public void keyTyped(KeyEvent e){}
		});
		drawing.addMouseListener(new MouseListener(){
			public void mousePressed(MouseEvent e) {setMouse(true);}
			public void mouseReleased(MouseEvent e) {setMouse(false);}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {}
		});
		drawing.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent e) {setMouse(e.getX(),e.getY());}
			public void mouseDragged(MouseEvent e) {setMouse(e.getX(),e.getY());}
		});

		add("Center", drawing);
		this.setSize(size_x,size_y);
		repaint();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE); //exit application when x is clicked
	}
	public void drawPiece(Piece piece, int i, int j, Graphics g){
		g.setColor(piece.getColor()=='b'?Color.black:Color.white);
		g.fillOval(50+grid_space*i+piece_space,50+grid_space*j+piece_space,piece_rad,piece_rad);
		g.setColor(piece.getColor()=='b'?Color.white:Color.black);
		g.drawOval(50+grid_space*i+piece_space,50+grid_space*j+piece_space,piece_rad,piece_rad);
		g.drawString(""+piece.getName(),50+grid_space*i+piece_space+14,50+grid_space*j+piece_space+23);
	}
	public void drawPieceImage(Piece piece, Board _board, int i, int j, Graphics g, boolean show_cost){
		int imageNum=0;
		if(piece.getName()=='c'){
			imageNum = 1;
		}else if(piece.getName()=='h'){
			imageNum = 2;
		}else if(piece.getName()=='b'){
			imageNum = 3;
		}else if(piece.getName()=='q'){
			imageNum = 4;
		}else if(piece.getName()=='k'){
			imageNum = 5;
		}

		if(piece.getColor()=='b'){
			imageNum=11-imageNum;
		}

		g.drawImage(chessPieces[imageNum], _board.drawPositionX + grid_space * i + 18, _board.drawPositionY + grid_space * j, grid_space-34,grid_space,null);

		if(show_cost){
			g.setColor(Color.white);
			g.fillOval(_board.drawPositionX + grid_space * i + 8, _board.drawPositionY + grid_space * j + 10, 25, 25);

			int point_text_width = g.getFontMetrics().stringWidth(""+piece.getCostValue());
			g.setColor(Color.black);
			g.drawOval(_board.drawPositionX + grid_space * i + 8, _board.drawPositionY + grid_space * j + 10, 25, 25);
			g.setColor(Color.black);
			g.drawString(""+piece.getCostValue(), _board.drawPositionX + grid_space * i + 21 - (point_text_width/2), _board.drawPositionY + grid_space * j + 30);
		}
	}
	public void setKey(boolean state, int key){
		// 	 if(key==87){	up=state;}
		// else if(key==83){	down=state;}
		// else if(key==65){	left=state;}
		// else if(key==68){	right=state;}
		// else if(key==32){	space=state;}
		if(key==68){
			squaretactics.debug=state;
			p("Debug set: "+squaretactics.debug);
		} // d key
	}
	public void setMouse(int _mX, int _mY){
		mX=_mX;
		mY=_mY;

		if(squaretactics.current_stage == "in game"){
			if((!squaretactics.checkMate)){
				if(TextBox.CHECK_HOVER(mX, mY)){
					setCursor(new Cursor(Cursor.HAND_CURSOR));
				}else{
					setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				}
			}

			float tempX=((float)(mX-50)/grid_space);
			float tempY=((float)(mY-50)/grid_space);
			if(tempX<squaretactics.board.width && tempX>=0 && tempY<squaretactics.board.height && tempY>=0){
				Piece temp = squaretactics.board.get((int)tempX,(int)tempY);
				if(temp!=null){
					if(squaretactics.hoverPiece!=temp){
						squaretactics.hoverPiece = temp;
						// p("Location: "+temp.locX+" "+temp.locY);
					}
				}
			}
		}else if(squaretactics.current_stage == "main menu"){
			if(TextBox.CHECK_HOVER(mX, mY)){
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}else{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}
		}
	}
	public void setMouse(boolean state){
		if(state){
			mouse_is_down=true;
		}else{

			TextBox.CHECK_CLICK(mX, mY);

			if(squaretactics.current_stage == "in game"){
				mouse_is_down=false;
				float tempX=((float)(mX-50)/grid_space);
				float tempY=((float)(mY-50)/grid_space);
				if(tempX<squaretactics.board.width && tempX>=0 && tempY<squaretactics.board.height && tempY>=0){
					// p("update call");
					squaretactics.update((int)tempX,(int)tempY);
				// }else if(!squaretactics.checkMate){
					// TextBox.CHECK_CLICK(mX, mY);
				// }else{
				// 	TextBox.CHECK_CLICK(mX, mY);
				}
			}else if(squaretactics.current_stage == "edit army"){
				mouse_is_down=false;
				float tempX=((float)(mX-50)/grid_space);
				float tempY=((float)(mY-50)/grid_space);
				if(tempX<2 && tempX>=0 && tempY<8 && tempY>=0){
					// p("update call");
					squaretactics.update((int)tempX,(int)tempY);
				}
			}
		}
	}

	public void playSound(){
		try
		{
			nomSound.setMicrosecondPosition(0);
			nomSound.start();
		}
		catch (Exception e)
		{
			// a special way i'm handling logging in this application
		}
	}
	public static void p(Object o){System.out.println(o);}
	public static int r(int x){return (int)(Math.random()*x);}
}