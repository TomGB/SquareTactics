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

	int sizeX=800, sizeY=800;
	int boardWidth = sizeX<sizeY?sizeX:sizeY;
	SquareTactics squaretactics;
	boolean mouseIsDown,up,down,left,right,space,showPow;
	int mX=0,mY=0;
	int gridSpace=(boardWidth-100)/8;
	int pieceRad = gridSpace/2;
	int pieceSpace=pieceRad/2;
	int selectSpacing=4;
	int textHeight = 40;
	int textWidth = 200;
	int frameX = 80;
	int frameY = 70;
	Font f = new Font("Dialog", Font.PLAIN, 16);

	TextBox reset,load,save,rules,undo,startgame, editarmy, blackWinText,whiteWinText,whiteTurnText,blackTurnText;

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
		startgame = new TextBox(squaretactics, 70,10,300,40, "Start Default Chess Game"){
			public void do_action(){
				p("Start Default Chess Game");
				squaretactics.current_stage = "in game";
				squaretactics.myGUI.repaint();
			}
		};
		editarmy = new TextBox(squaretactics, 70,80,300,40,"Edit Army"){
			public void do_action(){
				p("edit army");
				squaretactics.current_stage = "edit army";
				squaretactics.myGUI.repaint();
			}
		};

		blackWinText = new TextBox(squaretactics, (sizeX-textWidth)/2,(boardWidth-textHeight)/2,textWidth,textHeight,"Black has Won");
		whiteWinText = new TextBox(squaretactics, (sizeX-textWidth)/2,(boardWidth-textHeight)/2,textWidth,textHeight,"White has Won");
		whiteTurnText = new TextBox(squaretactics, 130,10,textWidth,textHeight,"White's Turn");
		blackTurnText = new TextBox(squaretactics, 130,10,textWidth,textHeight,"Black's Turn");

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
					startgame.set_visible(true);
					editarmy.set_visible(true);

				}else if(squaretactics.current_stage == "in game"){

					g.drawImage(background,50,50,boardWidth-100-3,boardWidth-100-3,null);

					for (int i=0; i<squaretactics.board.width; i++) {
						for (int j=0; j<squaretactics.board.height; j++) {

							g.setColor(new Color(255,255,255,200));
							if(((i+j*8)+(j%2))%2==0){
								g.setColor(new Color(100,100,100,200));
							}
							g.fillRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
							g.setColor(Color.black);
							g.drawRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

							Piece tempPiece = squaretactics.board.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.board, i,j,g);
								}
							}
						}
					}

					if(squaretactics.pinningKing.size()>0){
						g.setColor(new Color(255,0,0,150));
						for (BoardMoves location: squaretactics.pinningKing) {
							// p("pinning: "+location.x+" "+location.y);
							g.fillRect(50+gridSpace*location.x,50+gridSpace*location.y,gridSpace,gridSpace);
						}
					}

					if(squaretactics.selectedPiece!=null){
						for (int i=0; i<squaretactics.possibleMoves.size(); i++) {
							BoardMoves move = squaretactics.possibleMoves.get(i);
							if(move.moveType=="Jump"){
								g.setColor(new Color(200,200,0,50));
							}else if(move.moveType=="Step"){
								g.setColor(new Color(0,0,200,50));
							}else if(move.moveType=="Slide"){
								g.setColor(new Color(200,0,0,50));
							}
							g.fillRect(50+gridSpace*move.x,50+gridSpace*move.y,gridSpace,gridSpace);
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
							g.fillRect(60,60,sizeX-120,sizeY-120);
							g.setColor(Color.black);
							g.drawRect(60,60,sizeX-120,sizeY-120);
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
								g.drawRect(50+gridSpace*squaretactics.selX+selectSpacing,50+gridSpace*squaretactics.selY+selectSpacing,gridSpace-selectSpacing*2,gridSpace-selectSpacing*2);
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

					//number of points left in a bar with each piece as a color taking up part of the bar
					//help box telling you hoe to use the interface
					//pieces
					//moves

					for (int i=0; i<5; i++) {
						for (int j=0; j<5; j++) {

							g.setColor(new Color(255,255,255,200));
							g.fillRect(300+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
							g.setColor(Color.black);
							g.drawRect(300+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

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
							g.fillRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
							g.setColor(Color.black);
							g.drawRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

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
							g.fillRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
							g.setColor(Color.black);
							g.drawRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

							Piece tempPiece = squaretactics.editArmyBoard.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.editArmyBoard, i,j,g);
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
							g.fillRect(300+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
							g.setColor(Color.black);
							g.drawRect(300+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

							Piece tempPiece = squaretactics.editMovesBoard.get(i,j);
							if(tempPiece!=null){
								if(icons){
									drawPiece(tempPiece,i,j,g);
								}else{
									drawPieceImage(tempPiece, squaretactics.editMovesBoard, i,j,g);
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

							if(move.moveType=="Jump"){
								g.setColor(new Color(200,200,0,50));
							}else if(move.moveType=="Step"){
								g.setColor(new Color(0,0,200,50));
							}else if(move.moveType=="Slide"){
								g.setColor(new Color(200,0,0,50));
							}
							
							g.fillRect(300+gridSpace*move.x,50+gridSpace*move.y,gridSpace,gridSpace);

							if(move.moveProperties.doubleFirst){
								moveText = "Only First Move";
								g.drawString(moveText, 300+10+gridSpace*move.x,50+20+(textLine*20)+gridSpace*move.y);
								textLine++;
							}
							if(move.moveProperties.restricted && !move.moveProperties.capture){
								moveText = "Only Move";
								g.drawString(moveText, 300+10+gridSpace*move.x,50+20+(textLine*20)+gridSpace*move.y);
								textLine++;
							}

							p(300+gridSpace*move.x +","+ 50+gridSpace*move.y +","+ gridSpace +","+ gridSpace);
						}

						g.setColor(Color.BLACK);

						int textLine = 0;

						g.drawString("Piece Name: "+squaretactics.selectedPiece.name, 300, 500 + textLine*20);
						textLine++;
						g.drawString("Number of Moves: "+squaretactics.selectedPiece.moves.size(), 300, 500 + textLine*20);
						textLine++;
						for (Move thisMove : squaretactics.selectedPiece.moves) {
							g.drawString("Move Type: "+thisMove.moveType+", Direction: "+thisMove.x+","+thisMove.y, 300, 500 + textLine*20);
							textLine++;
						}
						g.drawString("Wrapping: "+squaretactics.selectedPiece.wrapping, 300, 500 + textLine*20);
						textLine++;
						g.drawString("Double Move: "+squaretactics.selectedPiece.doubleMove, 300, 500 + textLine*20);
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
		this.setSize(sizeX,sizeY);
		repaint();
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE); //exit application when x is clicked
	}
	public void drawPiece(Piece piece, int i, int j, Graphics g){
		g.setColor(piece.getColor()=='b'?Color.black:Color.white);
		g.fillOval(50+gridSpace*i+pieceSpace,50+gridSpace*j+pieceSpace,pieceRad,pieceRad);
		g.setColor(piece.getColor()=='b'?Color.white:Color.black);
		g.drawOval(50+gridSpace*i+pieceSpace,50+gridSpace*j+pieceSpace,pieceRad,pieceRad);
		g.drawString(""+piece.getName(),50+gridSpace*i+pieceSpace+14,50+gridSpace*j+pieceSpace+23);
	}
	public void drawPieceImage(Piece piece, Board _board, int i, int j, Graphics g){
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

		g.drawImage(chessPieces[imageNum], _board.drawPositionX + gridSpace * i + 18, _board.drawPositionY + gridSpace * j, gridSpace-34,gridSpace,null);
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

			float tempX=((float)(mX-50)/gridSpace);
			float tempY=((float)(mY-50)/gridSpace);
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
			mouseIsDown=true;
		}else{

			TextBox.CHECK_CLICK(mX, mY);

			if(squaretactics.current_stage == "in game"){
				mouseIsDown=false;
				float tempX=((float)(mX-50)/gridSpace);
				float tempY=((float)(mY-50)/gridSpace);
				if(tempX<squaretactics.board.width && tempX>=0 && tempY<squaretactics.board.height && tempY>=0){
					// p("update call");
					squaretactics.update((int)tempX,(int)tempY);
				// }else if(!squaretactics.checkMate){
					// TextBox.CHECK_CLICK(mX, mY);
				// }else{
				// 	TextBox.CHECK_CLICK(mX, mY);
				}
			}else if(squaretactics.current_stage == "edit army"){
				mouseIsDown=false;
				float tempX=((float)(mX-50)/gridSpace);
				float tempY=((float)(mY-50)/gridSpace);
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