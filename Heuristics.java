/*---------------------------------------------------------------
 *  Heuristics
 *
 *  任意の座標のヒューリスティック値を表すクラス
 *
 *---------------------------------------------------------------*/
public class Heuristics {

	protected Coordinates		_coordinates;		/* 座標 */
	protected double		_score = 0;		/* 得点 */

	/**
	 *  < コンストラクタ >
	 *
	 * @param	Coordinates	coordinates	座標
	 */

	public Heuristics(Coordinates coordinates,Board _board,Piece myPiece,int step){
		_coordinates = coordinates;
		Piece enemyPiece = new Piece(true);
		Board vir_board = new Board();
		double x[] = new double[100];
		Estim y[] = new Estim[100];
		int conclnum = 0;   /* ルールの数 */
		int mcount = 0;
		int ecount = 0;     /* 敵が置ける場所の個数 */

		/* ファジィ集合の定義 */
		/* 取れる個数 */
		Fuzzy get_count1 = new Fuzzy(1, 1, 1, 3);
		Fuzzy get_count2 = new Fuzzy(5, 10, 10, 20);

		/* 置ける個数 */
		Fuzzy set_count1 = new Fuzzy(0, 1, 1, 5);
		Fuzzy set_count2 = new Fuzzy(9, 10, 10, 20);

		/* 隅 */
		Fuzzy corner1 = new Fuzzy(0,0,0,0);
		Fuzzy corner2 = new Fuzzy(7,7,7,7);

		/* X打ち */
		Fuzzy x1 = new Fuzzy(1, 1, 1, 1);
		Fuzzy x2 = new Fuzzy(6, 6, 6, 6);

		/* B打ち */
		Fuzzy about_b = new Fuzzy(0, 3, 4, 7);

		/* C打ち */
		Fuzzy about_c1 = new Fuzzy(1, 1, 1, 1);
		Fuzzy about_c2 = new Fuzzy(6, 6, 6, 6);

		/* A打ち */
		Fuzzy about_a1 = new Fuzzy(2, 2, 2, 2);
		Fuzzy about_a2 = new Fuzzy(5, 5, 5, 5);

		/* ボックス打ち */
		Fuzzy about_box = new Fuzzy(0, 3, 4, 7);

		/* 評価値 */
		Estim rule_estim;
		Estim worst = new Estim(1,1,1,0,0,0,0,0,0,0,0);
		Estim very_bad  = new Estim(1,1,0.5,0,0,0,0,0,0,0,0);
		Estim bad  = new Estim(0,0,0.5,1,0.5,0,0,0,0,0,0);
		Estim usual= new Estim(0,0,0,0,0.5,1,0.5,0,0,0,0);
		Estim good = new Estim(0,0,0,0,0,0,0.5,1,0.5,0,0);
		Estim very_good = new Estim(0,0,0,0,0,0,0,0,0.5,1,1);
		Estim best = new Estim(0,0,0,0,0,0,0,0,1,1,1);

     System.out.println("座標 x,y: "+"("+_coordinates.x+","+_coordinates.y+")");

/* 現在の盤面におけるルール */
	/* ルール0　盤面に応じた取得個数による評価 */
	int get_mcount = _board.getReversePiece(_coordinates.x, _coordinates.y, myPiece);
	if (step == 0) {
		x[0]=Fuzzy.truth(get_mcount,get_count1);
		y[++conclnum] = Estim.concl(x[0],usual);
		x[1]=Fuzzy.truth(get_mcount,get_count2);
		y[++conclnum] = Estim.concl(x[1],bad);
	} else if (step ==2) {
		x[0]=Fuzzy.truth(get_mcount,get_count1);
		y[++conclnum] = Estim.concl(x[0],bad);
		x[1]=Fuzzy.truth(get_mcount,get_count2);
		y[++conclnum] = Estim.concl(x[1],good);
	}

	System.out.println("取得個数評価(少量)：  "+x[0]);
	System.out.println("取得個数評価(大量)：  "+x[1]);

	/* ルール1　星打ちの評価はとても高い */
	x[1]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[2]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[3]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[4]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[0]=Estim.max(x, 4);
	y[++conclnum] = Estim.concl(x[0],best);

	System.out.println("ルール1の適合度：  "+x[0]);

	/* ルール2　X打ちの評価はとても低い */
	x[1]=Fuzzy.truth(_coordinates.x,x1)*
	     Fuzzy.truth(_coordinates.y,x1);
	x[2]=Fuzzy.truth(_coordinates.x,x1)*
	     Fuzzy.truth(_coordinates.y,x2);
	x[3]=Fuzzy.truth(_coordinates.x,x2)*
	     Fuzzy.truth(_coordinates.y,x1);
	x[4]=Fuzzy.truth(_coordinates.x,x2)*
	     Fuzzy.truth(_coordinates.y,x2);
	x[0]=Estim.max(x, 4);
	y[++conclnum] = Estim.concl(x[0],worst);

	System.out.println("ルール2の適合度：  "+x[0]);

	/* ルール3　B打ちの評価は高い */
	x[1]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,about_b);
	x[2]=Fuzzy.truth(_coordinates.x,about_b)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[3]=Fuzzy.truth(_coordinates.x,about_b)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[4]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,about_b);
	x[0]=Estim.max(x, 4);
	y[++conclnum] = Estim.concl(x[0],good);

	System.out.println("ルール3の適合度：  "+x[0]);

	/* ルール4　ボックス打ちの評価は高い */
	x[0]=Fuzzy.truth(_coordinates.x,about_box)*
	     Fuzzy.truth(_coordinates.y,about_box);
	y[++conclnum] = Estim.concl(x[0],very_good);

	System.out.println("ルール4の適合度：  "+x[0]);

	/* ルール5　C打ちの評価は低い */
	x[1]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,about_c1);
	x[2]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,about_c2);
	x[3]=Fuzzy.truth(_coordinates.x,about_c1)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[4]=Fuzzy.truth(_coordinates.x,about_c1)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[5]=Fuzzy.truth(_coordinates.x,about_c2)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[6]=Fuzzy.truth(_coordinates.x,about_c2)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[7]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,about_c1);
	x[8]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,about_c2);
	x[0]=Estim.max(x, 8);
	if (step == 2) {
		rule_estim = usual;
	} else {
		rule_estim = very_bad;
	}
	y[++conclnum] = Estim.concl(x[0],rule_estim);

	System.out.println("ルール5の適合度：  "+x[0]);

	/* ルール6　A打ちの評価は高い */
	x[1]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,about_a1);
	x[2]=Fuzzy.truth(_coordinates.x,corner1)*
	     Fuzzy.truth(_coordinates.y,about_a2);
	x[3]=Fuzzy.truth(_coordinates.x,about_a1)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[4]=Fuzzy.truth(_coordinates.x,about_a1)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[5]=Fuzzy.truth(_coordinates.x,about_a2)*
	     Fuzzy.truth(_coordinates.y,corner1);
	x[6]=Fuzzy.truth(_coordinates.x,about_a2)*
	     Fuzzy.truth(_coordinates.y,corner2);
	x[7]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,about_a1);
	x[8]=Fuzzy.truth(_coordinates.x,corner2)*
	     Fuzzy.truth(_coordinates.y,about_a2);
	x[0]=Estim.max(x, 8);
	y[++conclnum] = Estim.concl(x[0],very_good);

	System.out.println("ルール6の適合度：  "+x[0]);

		/* 仮の盤面作成 */
		for(int i=0; i<=7; i++){
		  for(int j=0; j<=7; j++){
		    if(_board._pieces[i][j] == null){
		     vir_board._pieces[i][j] = null;
		    }else{
		     vir_board._pieces[i][j] = new Piece(_board._pieces[i][j]);
		    }
		  }
		}

		/* 仮盤面に現在の座標にコマを置く */
		vir_board.putPiece(coordinates.x,coordinates.y,
				   new Piece(myPiece));

		/* 敵の色を調べる*/
		if(myPiece.getColor()==Piece.WHITE)
			enemyPiece = Piece.Black;
		else
			enemyPiece = Piece.White;


/* 置いた後の盤面におけるルール */

		for(int i=0;i < 8;i++){
		   for(int j=0;j < 8;j++){
		      if(vir_board.getPiece(i,j) == null){
			 if(vir_board.getReversePiece(i,j,enemyPiece) != 0){
			    Coordinates coordinate = new Coordinates(i,j);
			    ecount++;

	/* ルール10 次に自分が置くことのできる場所数の評価 */
	mcount = vir_board.getPuttablePieceNum(myPiece);
	x[0]=Fuzzy.truth(mcount,set_count1);
	y[++conclnum] = Estim.concl(x[0],bad);

	System.out.println("自設置可能場所の適応度(少ない)：  "+x[0]);

  /* ルール11 敵の座標が角ならば，評価はとても悪い */
	x[1]=Fuzzy.truth(coordinate.x,corner1)*
	     Fuzzy.truth(coordinate.y,corner1);
	x[2]=Fuzzy.truth(coordinate.x,corner1)*
	     Fuzzy.truth(coordinate.y,corner2);
	x[3]=Fuzzy.truth(coordinate.x,corner2)*
	     Fuzzy.truth(coordinate.y,corner1);
	x[4]=Fuzzy.truth(coordinate.x,corner2)*
	     Fuzzy.truth(coordinate.y,corner2);
	x[0]=Estim.max(x, 4);
	y[++conclnum] = Estim.concl(x[0],worst);

	System.out.println("ルール11の適合度：  "+x[0]);

	/* ルール12 敵の座標がB打ちならば，評価は悪い */
	// x[1]=Fuzzy.truth(coordinate.x,corner1)*
	//      Fuzzy.truth(coordinate.y,about_b);
	// x[2]=Fuzzy.truth(coordinate.x,about_b)*
	//      Fuzzy.truth(coordinate.y,corner1);
	// x[3]=Fuzzy.truth(coordinate.x,about_b)*
	//      Fuzzy.truth(coordinate.y,corner2);
	// x[4]=Fuzzy.truth(coordinate.x,corner2)*
	//      Fuzzy.truth(coordinate.y,about_b);
	// x[0]=Estim.max(x, 4);
	// y[++conclnum] = Estim.concl(x[0],bad);

	// System.out.println("ルール12の適合度：  "+x[0]);

	/* ルール13　 敵の座標がX打ちならば，評価は高い */
	// x[1]=Fuzzy.truth(coordinate.x,x1)*
	//      Fuzzy.truth(coordinate.y,x1);
	// x[2]=Fuzzy.truth(coordinate.x,x1)*
	//      Fuzzy.truth(coordinate.y,x2);
	// x[3]=Fuzzy.truth(coordinate.x,x2)*
	//      Fuzzy.truth(coordinate.y,x1);
	// x[4]=Fuzzy.truth(coordinate.x,x2)*
	//      Fuzzy.truth(coordinate.y,x2);
	// x[0]=Estim.max(x, 4);
	// y[++conclnum] = Estim.concl(x[0],good);

	// System.out.println("ルール13の適合度：  "+x[0]);

	/* ルール14　敵の座標がA打ちならば，評価は低い */
	x[1]=Fuzzy.truth(coordinate.x,corner1)*
	     Fuzzy.truth(coordinate.y,about_a1);
	x[2]=Fuzzy.truth(coordinate.x,corner1)*
	     Fuzzy.truth(coordinate.y,about_a2);
	x[3]=Fuzzy.truth(coordinate.x,about_a1)*
 	     Fuzzy.truth(coordinate.y,corner1);
	x[4]=Fuzzy.truth(coordinate.x,about_a1)*
	     Fuzzy.truth(coordinate.y,corner2);
	x[5]=Fuzzy.truth(coordinate.x,about_a2)*
	     Fuzzy.truth(coordinate.y,corner1);
	x[6]=Fuzzy.truth(coordinate.x,about_a2)*
	     Fuzzy.truth(coordinate.y,corner2);
	x[7]=Fuzzy.truth(coordinate.x,corner2)*
	     Fuzzy.truth(coordinate.y,about_a1);
	x[8]=Fuzzy.truth(coordinate.x,corner2)*
	     Fuzzy.truth(coordinate.y,about_a2);
	x[0]=Estim.max(x, 8);
	y[++conclnum] = Estim.concl(x[0],very_bad);
	System.out.println("ルール14の適合度：  "+x[0]);

		         }
		      }
		   }
		}

	/* ルール10 敵が置くことのできる場所数の評価 */
	x[0]=Fuzzy.truth(ecount,set_count1);
	y[++conclnum] = Estim.concl(x[0],good);

	System.out.println("敵設置可能場所の適応度：  "+x[0]);


		/* 後件部を統合する */
		y[0] = Estim.aggregation(conclnum,y);

		System.out.println("統合した後の後件部");

		Check.conclstate(y[0]);


		/* 脱ファジィ化をし，スコアを返す */
		_score = Estim.defuzzy(y[0]);

	System.out.println("脱ファジィ： "+_score+"\n");
	System.out.println("-- My AI --");
	}

	/**
	 *  対応する座標を返す
	 */
	public Coordinates getCoordinates(){
		return(_coordinates);
	}

	/**
	 *  得点を返す
	 */
	public double getScore(){
		return(_score);
	}
}
