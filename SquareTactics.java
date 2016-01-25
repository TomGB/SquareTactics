import java.io.PrintWriter;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

class SquareTactics {

	Army black_army, white_army;

	UserInteraction myGUI;

	int boardWidth = 8, boardHeight = 8;
	Board board = new Board(boardWidth,boardHeight, 50,50);

	Board editArmyBoard = new Board(2,8, 50,50);

	Board editMovesBoard = new Board(5,5, 300,50);

	boolean whiteTurn=true, rules=false, checkMate = false;
	boolean debug=false, moveDebug = false;
	String current_stage = "main menu";
	ArtificialPlayer ai;

	ArrayList<BoardMoves> pinningKing = new ArrayList<BoardMoves>();
	ArrayList<BoardMoves> pinningTemp = new ArrayList<BoardMoves>();
	ArrayList<BoardMoves> possibleMoves = new ArrayList<BoardMoves>();

	Piece selectedPiece = null;
	Piece hoverPiece = null;
	int selX, selY;

	public SquareTactics(){
		ai = new ArtificialPlayer(this);
		editMovesBoard.clear();
		myGUI = new UserInteraction(this);
	}

	public void reset(){
		board.clear();
		selectedPiece = null;
		pinningKing.clear();
		possibleMoves = null;
		board.setUp(black_army, white_army);
		whiteTurn=true;
		myGUI.repaint();
	}

	public void setBlackArmy(Army black_army){
		this.black_army = black_army;
	}
	public void setWhiteArmy(Army white_army){
		this.white_army = white_army;
	}

	/*
	This method is called whenever the mouse is clicked,
	If no piece is selected then allow the selection of a piece that is the color of the current person's turn.
	If a piece is selected then the destination click is checked using the validMove method.
	Then a check for check and checkMate is carried out.
	Finally the GUI is re drawn.
	 */
	public void update(int posX, int posY){ //if left button clicked
		if(rules){
			rules=false;
		}else if(current_stage == "in game"){
			if(checkMate){ //reset if game has ended
				whiteTurn=true;
				checkMate=false;
				reset();
			}
			Piece tempPiece = board.get(posX,posY);
			if(tempPiece!=null&&(tempPiece.color==(whiteTurn?'w':'b')||debug)){
				selX=posX;
				selY=posY;
				selectedPiece=tempPiece;
				possibleMoves = calculatePieceMoves(selectedPiece, board, selX, selY, false);
				simulatePieceMoves(whiteTurn?'b':'w');
			}else if(selectedPiece != null){
				if(validMove(posX, posY)||debug){ //move piece
					selectedPiece.moveThePiece();
					if(board.get(posX,posY)!=null){
						myGUI.playSound();
					}
					board.turnNum++;
					board.saveHistory();
					board.set(posX,posY,selectedPiece);
					board.set(selX,selY,null);
					if(checkCheck(false,whiteTurn?'w':'b')){
						p("king in check");
						if(checkCheckMate(whiteTurn?'b':'w')){
							p("Check Mate!");
							checkMate=true;
						}
					}
					selectedPiece = null;
					whiteTurn = !whiteTurn;
					if(!whiteTurn){
						ai.takeTurn();
					}
				}
			}
		}else if(current_stage == "edit army"){
			Piece tempPiece = editArmyBoard.get(posX,posY);
			if(tempPiece!=null){
				selectedPiece = tempPiece;
				editMovesBoard.set(2,2,tempPiece);

				possibleMoves = calculatePieceMoves(tempPiece, editMovesBoard, tempPiece.loc_x, tempPiece.loc_y, false);
			}
		}
		myGUI.repaint();
	}

	/*
	This method uses the possibleMoves array list.
	It simulates moving the selected piece to each space it can usually move to.
	Then checkCheck is run to see if the movement of this piece has cleared a path
	for the King to be taken. If a path has been cleared then this is an invalid move
	and it is removed from the possibleMoves array.
	 */
	public void simulatePieceMoves(char color){
		for (int i=possibleMoves.size()-1; i>=0; i--) {
			board.turnNum++;
			board.saveHistory();
			board.set(possibleMoves.get(i).x,possibleMoves.get(i).y,selectedPiece);
			board.set(selX,selY,null);
			if(checkCheck(true,color)){
				possibleMoves.remove(i);
				// p("invalid move as the king could be captured");
			}
			board.loadHistory();
		}
	}

	/*
	Input a position x and y that you wish to move your piece to.
	This method returns if this move is in the possibleMoves array list.
	 */
	public boolean validMove(int x, int y){
		for (BoardMoves move : possibleMoves) {
			if(move.x==x&&move.y==y){
				return true;
			}
		}
		return false;
	}

	/*
	This method uses the pinningTemp and pinningKing array lists.
	For each piece calculate it's possible moves using calculatePieceMoves method.
	If there are no pieces pinning the king return false.
	 */
	public boolean checkCheck(boolean simulated, char color){
		if(simulated){
			pinningTemp.clear();
		}else{
			pinningKing.clear();
		}

		for (Piece piece : board.getByColor(color)){
			calculatePieceMoves(piece, board, piece.loc_x, piece.loc_y, simulated);
		}

		if(simulated){
			return pinningTemp.size()!=0;
		}else{
			return pinningKing.size()!=0;
		}
	}

	/*
	This method utilizes the calcualtePieceMoves and the simulatePieceMoves methods.
	It runs each of these methods to get the list of possibeMoves for each piece.
	Then if there is no single legal move that a piece can take the game must be Checkmate.
	 */
	public boolean checkCheckMate(char color){

		for (Piece piece : board.getByColor(color)){
			selX=piece.loc_x;
			selY=piece.loc_y;
			selectedPiece=piece;
			possibleMoves = calculatePieceMoves(piece, board, piece.loc_x, piece.loc_y, false);
			simulatePieceMoves(whiteTurn?'w':'b');
			if(possibleMoves.size()>0){
				selectedPiece = null;
				possibleMoves = null;
				return false;
			}
		}
		return true;
	}

	/*
	This method returns an array list of the legal moves that a piece can make
	(not including moves that put their own king in check).
	For each of the moves that a piece can do follow the movement path and save valid moves to the tempMoves array list.
	If a piece could potentially capture the enemy king then add the location of this piece to the pinningKing array list.
	return the array list of possible moves the piece can do.
	 */
	public ArrayList<BoardMoves> calculatePieceMoves(Piece current_piece, Board _board, int x, int y, boolean simulated){

		// where can pieces move logic
		// 
		// for each move, where can the piece move based on the move type?
		// 
		// 		while
		// 			if position where piece would move to is friendly then cancel loop
		// 	
		// 			else if position where piece would move to is empty,
		// 				if this move can be to an empty space
		// 					allow piece to move there, continue loop
		// 				else
		// 					cancel loop
		// 			else if position where piece would move to is an enemy,
		// 				if this move can be a capturing move
		// 					allow piece to move there and end loop
		// 				else
		// 					cancel loop
		//
		// 			if move is sliding
		// 				repeat movement checks for the next location on the board
		// 			if move is a double
		// 				repeat movement checks for the next location on the board
		// 				record that this is the second move
		// 			if move is a double first move and the piece has not moved
		// 				repeat movement checks for the next location on the board
		// 				record that this is the second move
		// 		repeat while loop if !end_loop
		// 	end for loop

		ArrayList<BoardMoves> tempMoves = new ArrayList<BoardMoves>();
		for (Move move : current_piece.moves) {

			int move_number = 1;
			int temp_x = x;
			int temp_y = y;

			boolean end_loop = false;

			do{

				if(current_piece.getColor()=='w'){
					temp_x = temp_x - move.y;
					temp_y = temp_y - move.x;
				}else{
					temp_x = temp_x + move.y;
					temp_y = temp_y + move.x;
				}

				if(current_piece.wrapping){
					if(temp_y >= 8){
						temp_y = temp_y-8;
					}else if(temp_y < 0){
						temp_y = temp_y+8;
					}
				}

				boolean out_of_bounds = true;

				if(_board == editMovesBoard){
					if(temp_x<=4&&temp_x>=0&&temp_y<=4&&temp_y>=0)
						out_of_bounds = false;
				}else if(temp_x<=7&&temp_x>=0&&temp_y<=7&&temp_y>=0){
					out_of_bounds = false;
				}

				if(!out_of_bounds){

					// not out of bounds
					Piece temp_piece = _board.get(temp_x, temp_y);

					if(temp_piece == null){
						// there is no piece there
						if(move.canMoveToEmptySpace()){
							tempMoves.add(new BoardMoves(temp_x, temp_y, move.move_type, move));
						}else{
							end_loop = true;
						}

						if(_board == editMovesBoard){
							if(move.canOnlyCapture()){
								tempMoves.add(new BoardMoves(temp_x, temp_y, move.move_type, move));
								end_loop = true;
							}
						}
					}else if(temp_piece.getColor() == current_piece.getColor()){
						// piece is friendly
						end_loop = true;

					}else if(temp_piece.getColor() != current_piece.getColor()){
						// piece is enemy
						if(move.canCaptureEnemyPiece()){
							tempMoves.add(new BoardMoves(temp_x, temp_y, move.move_type, move));

							if(_board.get(temp_x,temp_y).name=='k'){
								if(simulated){
									pinningTemp.add(new BoardMoves(x,y,"checkPiece"));
								}else{
									pinningKing.add(new BoardMoves(x,y,"checkPiece"));
									// p("added to pinning king array");
								}
							}
						}
						end_loop = true;

					}

					if(move.move_type == "Jump" || move.move_type == "Step"){

						if(move_number == 1){

							if(move.hasDoubleFirstMove() && !current_piece.getHasMoved()){
								// move is a double first move and the piece has not moved
								// continue loop
							}else if(current_piece.getDoubleMove()){
								// continue loop
							}else{
								end_loop = true;
							}
						}else if(move_number == 2){
							end_loop = true;
						}
					}else if(move.move_type == "slide"){
						// continue loop
					}

					move_number++;
				}else{
					end_loop=true;
				}

			}while(!end_loop);
				
		}
		return tempMoves;
	}

	public void undo(){
		if(board.turnNum>0){
			whiteTurn = !whiteTurn;
			board.loadHistory();
			selectedPiece = null;
			possibleMoves = null;
			pinningKing.clear();
			myGUI.repaint();
		}
	}

	public void rules(){
		rules=!rules;
		myGUI.repaint();
	}

	public void save(){
		try{
			PrintWriter savefile= new PrintWriter(new File("Square_Tactics_save.txt"));
			savefile.println("Square Tactics save");
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
		// try(BufferedReader br = new BufferedReader(new FileReader("Square_Tactics_save.txt"))) {
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
	public static void main(String args[]){new SquareTactics();}
}
