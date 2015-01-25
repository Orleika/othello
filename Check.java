/*---------------------------------------------------------------
 *  チェック
 *---------------------------------------------------------------*/
public class Check{

	public static void conclstate(Estim eee)
	{
		System.out.println(eee.e[0]+","+eee.e[1]+","+eee.e[2]+","+eee.e[3]+","+eee.e[4]+","+eee.e[5]+","+eee.e[6]+","+eee.e[7]+","+eee.e[8]+","+eee.e[9]+","+eee.e[10]);
	}

	public static void boardstate(Board bbb,Piece ppp)
	{
		for(int i=0; i<=7; i++){
		   for(int j=0; j<=7; j++){
		     if(bbb.getPiece2(j,i,ppp)!=-1)
		         System.out.print(" ");
		     System.out.print(bbb.getPiece2(j,i,ppp));
		   }
		System.out.print("\n");
		}
	}
}

