import java.util.*;
import main.Embed;
import main.Extract;

class decrypt
{
	public static void main(String args[])
	{
		String a[]={"qwertyasdfgh","details/"+args[0],"upload/"+args[1]};
		Extract.main(a);
		String out=aes.input("details/"+args[0]);
		System.out.println(out);
	}
}