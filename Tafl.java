import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class Tafl {
	boolean selected=false, whiteTurn=true, blackWin=false, whiteWin=false, debug=false, moveDebug = false;
	ArrayList<BoardMoves> pinningKing = new ArrayList<BoardMoves>();
	Piece selectedPiece=null;
	int selX, selY;
	boolean gameOver=false, rules=false;
	int boardWidth = 8;
	int boardHeight = 8;
	Board board = new Board(boardWidth,boardHeight);
	UserInteraction myGUI;
	ArrayList<BoardMoves> possibleMoves = new ArrayList<BoardMoves>();

	public Tafl(){
		board.clear();
		board.setUp();
		myGUI = new UserInteraction(this);
	}

	public void reset(){
		board.clear();
		board.setUp();
		whiteTurn=false;
		myGUI.repaint();
	}

	public void update(int posX, int posY){ //if left button clicked
		if(rules){
			rules=false;
		}else{
			if(whiteWin||blackWin){ //reset if game has ended
				whiteTurn=false;
				whiteWin=false;
				blackWin=false;
				board.clear();
				board.setUp();
			}
			if((!whiteTurn&&board.isBlack(posX,posY))||(whiteTurn&&board.isWhite(posX,posY))||(debug&&(board.isBlack(posX,posY)||board.isWhite(posX,posY)))){
				selX=posX;
				selY=posY;
				selected=true;
				selectedPiece=board.get(selX,selY);
				calculatePieceMoves(selectedPiece, selX, selY);
			}else if(selected){
				if(validMove(posX, posY)||debug){ //move piece
					selectedPiece.hasMoved = true;
					if(board.get(posX,posY)!=null){
						myGUI.playSound();
					}
					board.turnNum++;
					board.saveHistory();
					board.set(posX,posY,selectedPiece);
					board.set(selX,selY,null);
					selected=false;
					if(checkCheck()){
						p("king in check");
					}
					whiteTurn = !whiteTurn;
					blackWin=board.checkKing();
					whiteWin=board.checkWin();
				}
			}
		}
		myGUI.repaint();
	}

	public boolean validMove(int x, int y){
		for (BoardMoves move : possibleMoves) {
			if(move.x==x&&move.y==y){
				return true;
			}
		}
		return false;
	}

	public boolean checkCheck(){
		pinningKing.clear();

		for (int i=0; i<board.width; i++) {
			for (int j=0; j<board.height; j++) {
				Piece tempPiece = board.get(i,j);
				if(tempPiece!=null){
					if((whiteTurn&&tempPiece.getColor()=='w')||((!whiteTurn)&&tempPiece.getColor()=='b')){
						calculatePieceMoves(tempPiece,i,j);
					}
				}
			}
		}

		return pinningKing.size()!=0;
	}

	public void calculatePieceMoves(Piece currentPiece, int x, int y){
		possibleMoves.clear();
		for (Move move : currentPiece.moves) {

			boolean possibleSeccond = false;
			int i = 1;
			int tempX = x;
			int tempY = y;

			boolean endLoop = false;

			while(!endLoop){
				if(currentPiece.getColor()=='w'){
					tempX = tempX-move.y;
					tempY = tempY-move.x;
				}else{
					tempX = tempX+move.y;
					tempY = tempY+move.x;
				}

				if(currentPiece.wrapping){
					if(tempY >= 8){
						tempY = tempY-8;
					}else if(tempY < 0){
						tempY = tempY+8;
					}
				}

				if(move.moveType=="Jump"||move.moveType=="Step") {
					if(i==2&&!(((move.doubleFirst&&!currentPiece.hasMoved)||(currentPiece.doubleMove))&&possibleSeccond)){
						endLoop = true;
					}else if(i>2){
						endLoop = true;
					}
				}

				if(tempX>7||tempX<0||tempY>7||tempY<0){
					if(moveDebug){
						p("out of bounds");
					}
					endLoop=true;
				}

				if(!endLoop){

					if(board.get(tempX,tempY)==null){
						if(!move.canOnlyCapture()){
							possibleMoves.add(new BoardMoves(tempX, tempY, move.moveType));
							if(moveDebug){
								p("empty space");
							}
							possibleSeccond = true;
						}
					}else if(board.get(tempX,tempY).getColor()==currentPiece.getColor()){
						endLoop=true;
						if(moveDebug){
							p("friendly");
						}
					}else if(!move.canOnlyMove()&&board.get(tempX,tempY).getColor()!=currentPiece.getColor()){
						possibleMoves.add(new BoardMoves(tempX, tempY, move.moveType));
						endLoop=true;
						if(board.get(tempX,tempY).name=='k'){
							pinningKing.add(new BoardMoves(x,y,"checkPiece"));
						}
						if(moveDebug){
							p("enemy");
						}
					}
				}
				i++;
			}
		}
	}

	public void undo(){
		if(board.turnNum>0){
			whiteTurn = !whiteTurn;
			board.loadHistory();
			selected = false;
			selectedPiece = null;
			possibleMoves.clear();
			myGUI.repaint();
		}
	}

	public void rules(){
		rules=!rules;
		myGUI.repaint();
	}

	public void save(){
		try{
			PrintWriter savefile= new PrintWriter(new File("taflsave.txt"));
			savefile.println("tafl save");
			savefile.println(whiteTurn?"White's Turn":"Black's Turn");
			for (int j=0; j<boardHeight; j++) {
				for (int i=0; i<boardWidth; i++) {
					savefile.print(board.get(i,j));
				}
			}
			savefile.flush();
			p("save done");
		}catch(Exception e){
			p("error saving");
		}
	}

	public void load(){
		// try(BufferedReader br = new BufferedReader(new FileReader("taflsave.txt"))) {
		// 	br.readLine();
		// 	String turn = br.readLine();
	 //        String line = br.readLine();
	 //        for (int i=0; i<line.length(); i++) {
	 //        	board.set(i%boardWidth, i/boardHeight, line.charAt(i));
		// 	}
		// 	p(turn);
		// 	whiteTurn = (turn.equals("White's Turn"));
		// 	myGUI.repaint();
  //  		}catch(Exception e){
  //  			p("error reading file");
  //  		}
	}
	public static void p(Object o){System.out.println(o);}
	public static int r(int x){return (int)(Math.random()*x);}
	public static void main(String args[]){new Tafl();}
}
