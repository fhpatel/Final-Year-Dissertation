package ReedSol;

import java.util.ArrayList;
import java.util.Arrays;

public class ExpLog {
	public ArrayList<int[]> logSquared =  new ArrayList<int []>();
	
	public int[] log = new int[] {-1,    0,    1,   25,    2,   50,   26,  198,
        3,  223,   51,  238,   27,  104,  199,   75,
        4,  100,  224,   14,   52,  141,  239,  129,
        28,  193,  105,  248,  200,    8,   76,  113,
        5,  138,  101,   47,  225,   36,   15,   33,
        53,  147,  142,  218,  240,   18,  130,   69,
        29,  181,  194,  125,  106,   39,  249,  185,
        201,  154,    9,  120,   77,  228,  114,  166,
        6,  191,  139,   98,  102,  221,   48,  253,
        226,  152,   37,  179,   16,  145,   34,  136,
        54,  208,  148,  206,  143,  150,  219,  189,
        241,  210,   19,   92,  131,   56,   70,   64,
        30,   66,  182,  163,  195,   72,  126,  110,
        107,   58,   40,   84,  250,  133,  186,   61,
        202,   94,  155,  159,   10,   21,  121,   43,
        78,  212,  229,  172,  115,  243,  167,   87,
        7,  112,  192,  247,  140,  128,   99,   13,
        103,   74,  222,  237,   49,  197,  254,   24,
        227,  165,  153,  119,   38,  184,  180,  124,
        17,   68,  146,  217,   35,   32,  137,   46,
        55,   63,  209,   91,  149,  188,  207,  205,
        144,  135,  151,  178,  220,  252,  190,   97,
        242,   86,  211,  171,   20,   42,   93,  158,
        132,   60,   57,   83,   71,  109,   65,  162,
        31,   45,   67,  216,  183,  123,  164,  118,
        196,   23,   73,  236,  127,   12,  111,  246,
        108,  161,   59,   82,   41,  157,   85,  170,
        251,   96,  134,  177,  187,  204,   62,   90,
        203,   89,   95,  176,  156,  169,  160,   81,
        11,  245,   22,  235,  122,  117,   44,  215,
        79,  174,  213,  233,  230,  231,  173,  232,
        116,  214,  244,  234,  168,   80,   88,  175
}; 
	
	public ExpLog() {
		setUp();
	}
	/*
	 * setUp() calculates every possible variation that logTable[a] + logTable[b] can result in
	 */
	public void setUp() {
		int result = 0;
		for(int i = 0; i < log.length; i++) {
			int [] temp = new int[256];
			for(int x = 0; x < log.length; x++) {
				result = log[i] + log[x];
				temp[x] = result;
			}
			logSquared.add(i,temp);
		}
		
		for(int i = 0; i < log.length; i++) {
			int [] temp = new int[256];
			for(int x = 0; x < log.length; x++) {
				result = log[i] + log[x];
				temp[x] = result;
			}
			i = i + log.length;
			logSquared.add(i,temp);
		}
	}
	
	public int expLookUp(int a, int b) {

		return logSquared.get(a)[b];
	}
}
