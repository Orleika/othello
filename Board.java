/*---------------------------------------------------------------
 *  Board
 *---------------------------------------------------------------*/
public class Board{

	public	 Piece		_pieces[][];	  /* 8×8のゲーム盤 */

	/**
	 *  <コンストラクタ>
	 *  ゲーム盤オブジェクトを作成する
	 */
	public Board(){
		_pieces = new Piece[8][8];
		for(int i=0;i < 8;i++){
			for(int j=0;j < 8;j++){
				_pieces[i][j] = null;
			}
		}
	}

	/**
	 *  ゲーム盤に最初の4つのピースだけを置いて、初期状態にする。
	 *
	 *  < 初期状態のゲーム盤 >
	 *    ０１２３４５６７
	 *  ０□□□□□□□□
	 *  １□□□□□□□□
	 *  ２□□□□□□□□
	 *  ３□□□○●□□□
	 *  ４□□□●○□□□
	 *  ５□□□□□□□□
	 *  ６□□□□□□□□
	 *  ７□□□□□□□□
	 */
	public void init(){
		for(int i=0;i < 8;i++){
			for(int j=0;j < 8;j++){
				_pieces[i][j] = null;
			}
		}
		_pieces[3][3] = new Piece(Piece.WHITE);
		_pieces[4][4] = new Piece(Piece.WHITE);
		_pieces[3][4] = new Piece(Piece.BLACK);
		_pieces[4][3] = new Piece(Piece.BLACK);
	}

	/**
	 *  指定された場所のピースを返す
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 */
	public Piece getPiece(int x,int y){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(null);
		}

		if(_pieces[x][y] != null){
			return(_pieces[x][y]);
		}else{
			return(null);
		}
	}

	public int getPiece2(int x,int y,Piece myPiece){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(-10);
		}

		if(_pieces[x][y] == null)
			return(0);
		else if(_pieces[x][y].getColor()==myPiece.getColor())
			return(1);
		else
			return(-1);
	}

	/**
	* 置ける場所数
	*
	* @param Piece	piece	自分の色
	*/
	public int getPuttablePieceNum(Piece piece) {
		int count = 0;
		for(int y=0;y < 8;y++){
		   for(int x=0;x < 8;x++){
		      if(this.getPiece(x,y) == null){
		      	if(this.getReversePiece(x,y,piece) != 0){
		      		count++;
		      	}
		      }
		   }
		}
		return count;
	}

	/**
	* 置いてある石数
	*
	*/
	public int getPutPieceNum() {
		int count = 0;
		for(int y=0;y < 8;y++){
		   for(int x=0;x < 8;x++){
		      if(this.getPiece(x,y) != null){
		      	count++;
		      }
		   }
		}
		return count;
	}

	/**
	 *  指定された場所のピースを返す
	 *
	 * @param	Coordinates		coordinates		座標
	 */
	public Piece getPiece(Coordinates coordinates){
		return(getPiece(coordinates.x,coordinates.y));
	}


	/**
	 *  指定された場所にピースを置く
	 *  ピースを置くことができればtrueを、置けなければfalseを返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 */
	public boolean setPiece(int x,int y,Piece piece){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(false);
		}

		if(_pieces[x][y] != null){
			return(false);
		}else{
			_pieces[x][y] = piece;
			return(true);
		}
	}


	/**
	 *  指定された場所にピースを置く
	 *  ピースを置くことができればtrueを、置けなければfalseを返す。
	 *
	 * @param	Coordinates		coordinates		座標
	 */
	public boolean setPiece(Coordinates coordinates,Piece piece){
		return(setPiece(coordinates.x,coordinates.y ,piece));
	}

	/**
	 *  指定された場所にピースを置く。
	 *  ピースを置くことができればtrueを、置けなければfalseを返す。
	 *  また、ピースを置く事によって、挟んだ相手のピースをひっくり返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 * @param	Piece	piece	ピース
	 */
	public boolean putPiece(int x,int y,Piece piece){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(false);
		}

		if(_pieces[x][y] != null){
			return(false);
		}else{
			if(reversePiece(x,y,piece) == 0){
				/*
				 * 相手ピースを1つも取れない場所には置けない
				 */
				return(false);
			}else{
				_pieces[x][y] = piece;
				return(true);
			}
		}
	}

	/**
	 *  指定された場所にピースを置く。
	 *  ピースを置くことができればtrueを、置けなければfalseを返す。
	 *  また、ピースを置く事によって、挟んだ相手のピースをひっくり返す。
	 *
	 * @param	Coordinates		coordinates		座標
	 * @param	Piece	piece	ピース
	 */
	public boolean putPiece(Coordinates coordinates,Piece piece){
		return(putPiece(coordinates.x,coordinates.y,piece));
	}

	/**
	 *  指定された位置に置いた時、取れる相手ピース数を返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 * @param	Piece	piece	ピース
	 */
	public int getReversePiece(int x,int y,Piece piece){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(0);
		}

		int point = 0;
		point += getReversePoint(x,y,piece, 1, 0);	/* 右方向 */
		point += getReversePoint(x,y,piece,-1, 0);	/* 左方向 */
		point += getReversePoint(x,y,piece, 0,-1);	/* 上方向 */
		point += getReversePoint(x,y,piece, 0, 1);	/* 下方向 */
		point += getReversePoint(x,y,piece, 1,-1);	/* 右上方向 */
		point += getReversePoint(x,y,piece, 1, 1);	/* 右下方向 */
		point += getReversePoint(x,y,piece,-1,-1);	/* 左上方向 */
		point += getReversePoint(x,y,piece,-1, 1);	/* 左下方向 */
		return(point);
	}

	/**
	 *  指定された位置に置いた時、取れる相手ピース数を返す。
	 *
	 * @param	Coordinates		coordinates		座標
	 * @param	Piece	piece	ピース
	 */
	public int getReversePiece(Coordinates coordinates,Piece piece){
		return(getReversePiece(coordinates.x,coordinates.y,piece));
	}

	/**
	 *  指定されたピースと同種類のピース数を返す
	 */
	public int getPoints(Piece piece){
		int point = 0;
		for(int i=0;i < 8;i++){
			for(int j=0;j < 8;j++){
				if(piece.equals(_pieces[i][j]) == true){
					point++;
				}
			}
		}
		return(point);
	}

	/**
	 *  ゲーム盤に置く場所が残っていなければtrueを、残っていればfalseを返す
	 *
	 * @param	Piece		piece		調べる色のピース
	 */
	public boolean checkPass(Piece piece){
		for(int y=0;y < 8;y++){
			for(int x=0;x < 8;x++){
				/*
				 * ピースが既に置かれている場所には置けない
				 */
				if(this.getPiece(x,y) == null){
					/*
					 * そこにピースを置いた時に取れるピース数が0でない
					 */
					if(this.getReversePiece(x,y,piece) != 0){
						return(false);
					}
				}
			}
		}
		return(true);
	}

	/**
	 *  ゲームが終わったか検査する。終わっていればtrueを、否であればfalseを返す
	 */
	public boolean checkEND(){
		if((this.checkPass(Piece.White) == true) && (this.checkPass(Piece.Black) == true)){
			return(true);
		}else{
			return(false);
		}
	}

	/**
	 *  ピースをひっくり返し、ひっくり返したピース数を返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 * @param	Piece	piece	ピース
	 */
	private int reversePiece(int x,int y,Piece piece){
		if(!(x>=0 && x < 8 && y>=0 && y < 8)){
			return(0);
		}

		int point = 0;
		int allPoint = 0;
		if((point = getReversePoint(x,y,piece,1,0)) != 0){
			reverseLine(x,y,1,0,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,-1,0)) != 0){
			reverseLine(x,y,-1,0,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,0,-1)) != 0){
			reverseLine(x,y,0,-1,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,0,1)) != 0){
			reverseLine(x,y,0,1,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,1,-1)) != 0){
			reverseLine(x,y,1,-1,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,-1,-1)) != 0){
			reverseLine(x,y,-1,-1,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,1,1)) != 0){
			reverseLine(x,y,1,1,point);
			allPoint += point;
		}
		if((point = getReversePoint(x,y,piece,-1,1)) != 0){
			reverseLine(x,y,-1,1,point);
			allPoint += point;
		}
		return(allPoint);
	}

	/**
	 *  xxとyyで指定された方向で、取れる相手ピース数を返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 * @param	Piece	piece	ピース
	 * @param	int		xx		X方向の増分
	 * @param	int		yy		Y方向の増分
	 */
	public int getReversePoint(int x,int y,Piece piece,int xx,int yy){
		Piece myPiece;
		Piece youPiece;
		if(piece.equals(Piece.White)){
			myPiece = Piece.White;
			youPiece = Piece.Black;
		}else{
			myPiece = Piece.Black;
			youPiece = Piece.White;
		}
		int point = 0;
		x+=xx;
		y+=yy;
		while((x >= 0) && (x < 8) && (y >= 0) && (y < 8)){
			if(youPiece.equals(_pieces[x][y])){
				point++;
			}else{
				if(_pieces[x][y] != null){
					return(point);
				}else{
					return(0);
				}
			}
			x+=xx;
			y+=yy;
		}
		return(0);
	}

	/**
	 *  xxとyyで指定された方向の相手ピース数をひっくり返す。
	 *
	 * @param	int		x		X座標
	 * @param	int		y		Y座標
	 * @param	int		xx		X方向の増分
	 * @param	int		yy		Y方向の増分
	 * @param	int		reverse	ひっくり返る数
	 */
	private void reverseLine(int x,int y,int xx,int yy,int reverse){
		x+=xx;
		y+=yy;
		for(int i=0;i < reverse;i++){
		    _pieces[x][y].reverse();

			x+=xx;
			y+=yy;
		}
	}
}
