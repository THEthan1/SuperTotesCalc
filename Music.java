package ethan.glick.supestotescalc;

//to contain constant value of whether or not the use wants the music to be muted
public class Music {

	    private static boolean isMuted = false;
		
	    //switches from muted to unmuted and vice-versa
	    public static void swapMute()
	    {
	    	isMuted = !isMuted;
	    }
	    
	    //returns the whether or not the music is muted (is intended to be muted)
	    public static boolean isMuted()
	    {
	    	return isMuted;
	    }

}
