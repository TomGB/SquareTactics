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
	Tafl tafl;
	boolean mouseIsDown,up,down,left,right,space,showPow;
	int mX=0,mY=0;
	int gridSpace=(sizeY-100)/8;
	int pieceRad = gridSpace/2;
	int pieceSpace=pieceRad/2;
	int selectSpacing=4;
	int textHeight = 40;
	int textWidth = 200;
	int frameX = 80;
	int frameY = 70;
	Font f = new Font("Dialog", Font.PLAIN, 16);

	TextBox reset,load,save,rules,undo,blackWinText,whiteWinText,whiteTurnText,blackTurnText;

	String 	rulesText = "Black is attacking and goes first, white is defending it's King (the pink piece in\nthe middle).\n\n"
			+ "To win black must surround the King on 4 sides or white must reach one of the\nfour corners with its King.\n\n"
			+ "Pieces may move any distance in a strait line,horisontally or vertically,\nunless another piece blocks its path.\n\n"
			+ "Only the King may sit in the red spaces.\n\n"
			+ "To take an enemy piece, on your turn suround it on opposite sides with\ntwo pieces.\n\n"
			+ "The red spaces count as enemy pieces when it is not your turn and can be\n"
			+ "used in capturing either side, unless the King is on it in which case it counts as\nwhite only.";

	Clip nomSound;
	AudioInputStream nomSoundStream;
	Image undoimg, background;
	BufferedImage allChessPieces;
	Image[] chessPieces = new Image[12];

	public UserInteraction(Tafl _tafl){

		setTitle("Tafl");

		tafl = _tafl;

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

		reset = new TextBox(370,10,80,40,"Reset");
		load = new TextBox(460,10,80,40,"Load");
		save = new TextBox(550,10,80,40,"Save");
		rules = new TextBox(640,10,80,40,"Rules");
		undo = new TextBox(70,10,50,40,undoimg);
		blackWinText = new TextBox((sizeX-textWidth)/2,(sizeY-textHeight)/2,textWidth,textHeight,"Black has Won");
		whiteWinText = new TextBox((sizeX-textWidth)/2,(sizeY-textHeight)/2,textWidth,textHeight,"White has Won");
		whiteTurnText = new TextBox(130,10,textWidth,textHeight,"White's Turn");
		blackTurnText = new TextBox(130,10,textWidth,textHeight,"Black's Turn");

		setResizable( false );

		JPanel drawing = new JPanel(){
			public static final long serialVersionUID = 1L;
			public void paint(Graphics g){
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g.setFont(f);
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); //anti alias to make lines smooth
				AffineTransform at = g2.getTransform();

				g.drawImage(background,50,50,sizeX-100-3,sizeY-100-3,null);

				for (int i=0; i<tafl.board.width; i++) {
					for (int j=0; j<tafl.board.height; j++) {

						g.setColor(new Color(255,255,255,100));
						if(((i+j*8)+(j%2))%2==0){
							g.setColor(new Color(100,100,100,100));
						}
						g.fillRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);
						g.setColor(Color.black);
						g.drawRect(50+gridSpace*i,50+gridSpace*j,gridSpace,gridSpace);

						Piece tempPiece = tafl.board.get(i,j);
						if(tempPiece!=null){
							if(icons){
								drawPiece(tempPiece,i,j,g);
							}else{
								drawPieceImage(tempPiece,i,j,g);
							}
						}
					}
				}

				if(tafl.pinningKing.size()>0){
					g.setColor(new Color(255,0,0,150));
					for (BoardMoves location: tafl.pinningKing) {
						// p("pinning: "+location.x+" "+location.y);
						g.fillRect(50+gridSpace*location.x,50+gridSpace*location.y,gridSpace,gridSpace);
					}
				}

				if(tafl.selected){
					for (int i=0; i<tafl.possibleMoves.size(); i++) {
						BoardMoves move = tafl.possibleMoves.get(i);
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

				if(tafl.blackWin){
					blackWinText.draw(true,g);
				}else if(tafl.whiteWin){
					whiteWinText.draw(true, g);
				}else if(tafl.rules){
					g.setColor(new Color(255,255,255,210));
					g.fillRect(60,60,sizeX-120,sizeY-120);
					g.setColor(Color.black);
					g.drawRect(60,60,sizeX-120,sizeY-120);
					for (String line : rulesText.split("\n")){
	        			g.drawString(line, frameX, frameY += g.getFontMetrics().getHeight());
					}
					frameY = 70;

					if(tafl.whiteTurn){
						whiteTurnText.draw(false,g);
					}else{
						blackTurnText.draw(false,g);
					}
					undo.draw(false,g);
					save.draw(false,g);
					load.draw(false,g);
					reset.draw(false,g);
					rules.draw(true,g);

				}else{
					if(tafl.whiteTurn){
						whiteTurnText.draw(true,g);
					}else{
						blackTurnText.draw(true,g);
					}
					undo.draw(true,g);
					save.draw(true,g);
					load.draw(true,g);
					reset.draw(true,g);
					rules.draw(true,g);

					if(tafl.selected){
						g.setColor(Color.blue);
						g.drawRect(50+gridSpace*tafl.selX+selectSpacing,50+gridSpace*tafl.selY+selectSpacing,gridSpace-selectSpacing*2,gridSpace-selectSpacing*2);
					}
				}
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
	public void drawPieceImage(Piece piece, int i, int j, Graphics g){
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

		g.drawImage(chessPieces[imageNum],50+gridSpace*i+18,50+gridSpace*j,gridSpace-34,gridSpace,null);
	}
	public void setKey(boolean state, int key){
		// 	 if(key==87){	up=state;}
		// else if(key==83){	down=state;}
		// else if(key==65){	left=state;}
		// else if(key==68){	right=state;}
		// else if(key==32){	space=state;}
		if(key==68){
			tafl.debug=state;
			p("Debug set: "+tafl.debug);
		} // d key
	}
	public void setMouse(int _mX, int _mY){
		mX=_mX;
		mY=_mY;
		if((!tafl.whiteWin && !tafl.blackWin&&undo.inside(mX,mY))||save.inside(mX,mY)||load.inside(mX,mY)||reset.inside(mX,mY)||rules.inside(mX,mY)){
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}else{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	public void setMouse(boolean state){
		if(state){
			mouseIsDown=true;
		}else{
			mouseIsDown=false;
			float tempX=((float)(mX-50)/gridSpace);
			float tempY=((float)(mY-50)/gridSpace);
			if(tempX<tafl.board.width && tempX>=0 && tempY<tafl.board.height && tempY>=0){
				p("update call");
				tafl.update((int)tempX,(int)tempY);
			}else if(!tafl.whiteWin && !tafl.blackWin && undo.inside(mX,mY)){
				p("undo clicked");
				tafl.undo();
			}else if(rules.inside(mX,mY)){
				p("display rules");
				tafl.rules();
			}else if(!tafl.rules&&save.inside(mX,mY)){
				p("save game");
				tafl.save();
			}else if(!tafl.rules&&load.inside(mX,mY)){
				p("load game");
				tafl.load();
			}else if(!tafl.rules&&reset.inside(mX,mY)){
				p("reset game");
				tafl.reset();
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