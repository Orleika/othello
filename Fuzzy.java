/*---------------------------------------------------------------
 *  Fuzzy
 *---------------------------------------------------------------*/
public class Fuzzy{
	public double a;
	public double b;
	public double c;
	public double d;

	/* 台形の定義 */
	public Fuzzy(double a,double b,double c,double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	/* 前件部の適合度算出 */
	public static double truth(double x,Fuzzy fff){
		if(fff.a < x && x < fff.b)
			return((x-fff.a)/(fff.b-fff.a));
		else if(fff.b <= x && x <= fff.c)
			return(1);
		else if(fff.c < x && x < fff.d)
			return((x-fff.d)/(fff.c-fff.d));
		else
			return(0);
	}

	/* ある範囲の個数 */
	public static double num(Fuzzy f1,Fuzzy f2,Fuzzy p,Board _board,Piece myPiece){
		double point = 0;
		for(int i=0; i<=7; i++)
		  for(int j=0; j<=7; j++)
			point = point + Estim.min(truth(i,f1),truth(j,f2))*truth(_board.getPiece2(i,j,myPiece),p);

		return(point);
	}
}


/*---------------------------------------------------------------
 *  Estim
 *---------------------------------------------------------------*/
class Estim{
	public double e[] = new double[11];

	/* 評価のファジィ集合 */
	public Estim(double x0,double x1,double x2,double x3,double x4,
	       double x5,double x6,double x7,double x8,double x9,double x10){
		 this.e[0] = x0;
		 this.e[1] = x1;
		 this.e[2] = x2;
		 this.e[3] = x3;
		 this.e[4] = x4;
		 this.e[5] = x5;
		 this.e[6] = x6;
		 this.e[7] = x7;
		 this.e[8] = x8;
		 this.e[9] = x9;
		 this.e[10]= x10;
	}

	/* 後件部への演算 (頭切り法) */
	public static Estim concl(double x,Estim eee){
		Estim aaa = new Estim(0,0,0,0,0,0,0,0,0,0,0);
		for(int i=0; i<=10; i++)
			aaa.e[i] = min(x,eee.e[i]);
		return(aaa);
	}

	/* 後件部統合 */
	public static Estim aggregation(int conclnum,Estim y[]){
		Estim aaa = new Estim(0,0,0,0,0,0,0,0,0,0,0);
		for(int i=0; i<=10; i++){
		  aaa.e[i] = 0;
		  for(int j=1; j<=conclnum; j++)
		      aaa.e[i] = max(aaa.e[i],y[j].e[i]);
		}
		return(aaa);
	}

	/* 脱ファジィ化 */
	public static double defuzzy(Estim eee){
		double res1=0,res2=0;
		for(int i=0; i<=10; i++)
		 {
			res1 = res1 + eee.e[i];
			res2 = res2 + i * eee.e[i];
		 }
		return(res2/res1);
	}

	/* MIN */
	public static double min(double x,double y){
		if(x < y)
			return(x);
		else
			return(y);
	}

	/* MIN Array */
	public static double min(double x[], int n) {
		double min = x[1];
		for(int i = 1; i <= n; i++) {
			min = min(x[i], min);
		}
		return min;
	}

	/* MAX */
	public static double max(double x,double y){
		if(x > y)
			return(x);
		else
			return(y);
	}

	/* MAX Array */
	public static double max(double x[], int n) {
		double max = x[1];
		for(int i = 1; i <= n; i++) {
			max = max(x[i], max);
		}
		return max;
	}
}

