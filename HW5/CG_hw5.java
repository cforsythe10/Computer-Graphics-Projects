import java.io.*;
import java.util.*;

public class CG_hw5 {
	private static String[] fileLines = new String[67];
	private static String[] pixelMap = new String[501];
	private static float zBuffer[][] = new float[501][501];
	private static int VPLowX = 0, VPHighX = 500, VPLowY = 0, VPHighY = 500;
	private static float WWLowX, WWHighX, WWLowY, WWHighY;
	private static float PRPX = 0.0f, PRPY = 0.0f, PRPZ = 1.0f;
	private static float VRPX = 0.0f, VRPY = 0.0f, VRPZ = 0.0f;
	private static float VPNX = 0.0f, VPNY = 0.0f, VPNZ = -1.0f;
	private static float VUPX = 0.0f, VUPY = 1.0f, VUPZ = 0.0f;
	private static float uMin = -0.7f, uMax = 0.7f, vMin = -0.7f, vMax = 0.7f;
	private static float frontPlane = 0.6f, backPlane = -0.6f;
	private static float zClose, zFar;
	private static boolean parallelProjection = false, backfaceCulling = false;
	private static float frontFace = 0.6f, backFace = -0.6f;
	private static ArrayList<ArrayList<Float>> vertices = new ArrayList<ArrayList<Float>>();
	private static ArrayList<ArrayList<Integer>> faces = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> drawCoords = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> polygons = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> polygonsTempHolder = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> intersectionPoints = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> edges = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<String> redShades = new ArrayList<String>();
	private static ArrayList<String> greenShades = new ArrayList<String>();
	private static ArrayList<String> blueShades = new ArrayList<String>();
	
	public static void main(String args[]) throws Exception{
		String file1 = "bound-sprellpsd.smf", file2 = "" ,file3 = "";
		for(int i = 0; i < args.length; i++) {
			String option = args[i];
			switch(option) {
				case "-f":
					file1 = args[++i];
					break;
				case "-g":
					file2 = args[++i];
					break;
				case "-i":
					file3 = args[++i];
					break;
				case "-j":
					VPLowX = Integer.parseInt(args[++i]);
					break;
				case "-k":
					VPLowY = Integer.parseInt(args[++i]);
					break;
				case "-o":
					VPHighX = Integer.parseInt(args[++i]);
					break;
				case "-p":
					VPHighY = Integer.parseInt(args[++i]);
					break;
				case "-x":
					PRPX = Float.parseFloat(args[++i]);
					break;
				case "-y":
					PRPY = Float.parseFloat(args[++i]);
					break;
				case "-z":
					PRPZ = Float.parseFloat(args[++i]);
					break;
				case "-X":
					VRPX = Float.parseFloat(args[++i]);
					break;
				case "-Y":
					VRPY = Float.parseFloat(args[++i]);
					break;
				case "-Z":
					VRPZ = Float.parseFloat(args[++i]);
					break;
				case "-q":
					VPNX = Float.parseFloat(args[++i]);
					break;
				case "-r":
					VPNY = Float.parseFloat(args[++i]);
					break;
				case "-w":
					VPNZ = Float.parseFloat(args[++i]);
					break;
				case "-Q":
					VUPX = Float.parseFloat(args[++i]);
					break;
				case "-R":
					VUPY = Float.parseFloat(args[++i]);
					break;
				case "-W":
					VUPZ = Float.parseFloat(args[++i]);
					break;
				case "-u":
					uMin = Float.parseFloat(args[++i]);
					break;
				case "-v":
					vMin = Float.parseFloat(args[++i]);
					break;
				case "-U":
					uMax = Float.parseFloat(args[++i]);
					break;
				case "-V":
					vMax = Float.parseFloat(args[++i]);
					break;
				case "-P":
					parallelProjection = true;
					break;
				case "-b":
					backfaceCulling = true;
					break;
				case "-F":
					frontFace = Float.parseFloat(args[++i]);
					break;
				case "-B":
					backFace = Float.parseFloat(args[++i]);
					break;
			}
		}
		fileLines[0] = "/* XPM */";
		fileLines[1] = "static char *quad_bw[] = {";
		fileLines[2] = "/* columns rows colors chars-per-pixel */";
		fileLines[3] = "\"501 501 61 1\",";
		fileLines[4] = "\"  c #000000\",";
		
		fileLines[5] = "\"a c #0d0000\",";
		fileLines[6] = "\"b c #1a0000\",";
		fileLines[7] = "\"c c #270000\",";
		fileLines[8] = "\"d c #340000\",";
		fileLines[9] = "\"e c #410000\",";
		fileLines[10] = "\"f c #4e0000\",";
		fileLines[11] = "\"g c #5b0000\",";
		fileLines[12] = "\"h c #680000\",";
		fileLines[13] = "\"i c #750000\",";
		fileLines[14] = "\"j c #820000\",";
		fileLines[15] = "\"k c #8f0000\",";
		fileLines[16] = "\"l c #9c0000\",";
		fileLines[17] = "\"m c #a90000\",";
		fileLines[18] = "\"n c #b60000\",";
		fileLines[19] = "\"o c #c30000\",";
		fileLines[20] = "\"p c #d00000\",";
		fileLines[21] = "\"q c #dd0000\",";
		fileLines[22] = "\"r c #ea0000\",";
		fileLines[23] = "\"s c #f70000\",";
		fileLines[24] = "\"t c #ff0000\",";
		
		fileLines[25] = "\"u c #000d00\",";
		fileLines[26] = "\"v c #001a00\",";
		fileLines[27] = "\"w c #002700\",";
		fileLines[28] = "\"x c #003400\",";
		fileLines[29] = "\"y c #004100\",";
		fileLines[30] = "\"z c #004e00\",";
		fileLines[31] = "\"A c #005b00\",";
		fileLines[32] = "\"B c #006800\",";
		fileLines[33] = "\"C c #007500\",";
		fileLines[34] = "\"D c #008200\",";
		fileLines[35] = "\"E c #008f00\",";
		fileLines[36] = "\"F c #009c00\",";
		fileLines[37] = "\"G c #00a900\",";
		fileLines[38] = "\"H c #00b600\",";
		fileLines[39] = "\"I c #00c300\",";
		fileLines[40] = "\"J c #00d000\",";
		fileLines[41] = "\"K c #00dd00\",";
		fileLines[42] = "\"L c #00ea00\",";
		fileLines[43] = "\"M c #00f700\",";
		fileLines[44] = "\"N c #00ff00\",";
		
		fileLines[45] = "\"O c #00000d\",";
		fileLines[46] = "\"P c #00001a\",";
		fileLines[47] = "\"Q c #000027\",";
		fileLines[48] = "\"R c #000034\",";
		fileLines[49] = "\"S c #000041\",";
		fileLines[50] = "\"T c #00004e\",";
		fileLines[51] = "\"U c #00005b\",";
		fileLines[52] = "\"V c #000068\",";
		fileLines[53] = "\"W c #000075\",";
		fileLines[54] = "\"X c #000082\",";
		fileLines[55] = "\"Y c #00008f\",";
		fileLines[56] = "\"Z c #00009c\",";
		fileLines[57] = "\"1 c #0000a9\",";
		fileLines[58] = "\"2 c #0000b6\",";
		fileLines[59] = "\"3 c #0000c3\",";
		fileLines[60] = "\"4 c #0000d0\",";
		fileLines[61] = "\"5 c #0000dd\",";
		fileLines[62] = "\"6 c #0000ea\",";
		fileLines[63] = "\"7 c #0000f7\",";
		fileLines[64] = "\"8 c #0000ff\",";
		
		fileLines[65] = "/* pixels */";
	
		fileLines[66] = "};";
		
		if(parallelProjection) {
			WWLowX = -1.0f;
			WWHighX = 1.0f;
			WWLowY = -1.0f;
			WWHighY = 1.0f;
		
			zClose = 0;
			zFar = -1;
		} else {
			WWLowX = -Math.abs(PRPZ / (backFace - PRPZ));
			WWHighX = Math.abs(PRPZ / (backFace - PRPZ));
			WWLowY = -Math.abs(PRPZ / (backFace - PRPZ));
			WWHighY = Math.abs(PRPZ / (backFace - PRPZ));
			
			zClose = (PRPZ - frontFace) / (backFace - PRPZ);
			zFar = -1;
		}
		
		initializeMap();
		initializeShades();
		for(int i = 0; i < 501; i++) {
			for(int j = 0; j < 501; j++) {
				zBuffer[i][j] = -1;
			}
		}
		
		String filePath = new File(".").getCanonicalPath();
		filePath += "/" + file1;
		File inputFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		interperetInput(br);

		transformVertices();
		project();

		definePolygons();
		scaleToWindow();

		fillPolygons(1);
		
		if(!file2.isEmpty()) {
			cleanDataStructs();
			
			filePath = new File(".").getCanonicalPath();
			filePath += "/" + file2;
			inputFile = new File(filePath);
			br = new BufferedReader(new FileReader(inputFile));
			
			interperetInput(br);
			
			transformVertices();
			
			project();
			definePolygons();
		
			scaleToWindow();
			fillPolygons(2);
		}
		
		if(!file3.isEmpty()) {
			cleanDataStructs();
			
			filePath = new File(".").getCanonicalPath();
			filePath += "/" + file3;
			inputFile = new File(filePath);
			br = new BufferedReader(new FileReader(inputFile));
			
			interperetInput(br);

			transformVertices();
			project();
			definePolygons();
			
			scaleToWindow();
			fillPolygons(3);
		}

		
		generateXPM();
	}
	
	public static void initializeMap() {
		String line = "";
		for(int i = 0; i < 501; i++) {
			line += " ";
		}	
		for(int i = 0; i < pixelMap.length; i++) {
			pixelMap[i] = line;
		}
	}
	
	public static void cleanDataStructs() {
		vertices = new ArrayList<ArrayList<Float>>();
		faces = new ArrayList<ArrayList<Integer>>();
		drawCoords = new ArrayList<ArrayList<ArrayList<Float>>>();
		polygons = new ArrayList<ArrayList<ArrayList<Float>>>();
		polygonsTempHolder = new ArrayList<ArrayList<ArrayList<Float>>>();
		intersectionPoints = new ArrayList<ArrayList<ArrayList<Float>>>();
		edges = new ArrayList<ArrayList<ArrayList<Float>>>();
	}
	
	public static void initializeShades() {
		redShades.add("a");
		redShades.add("b");
		redShades.add("c");
		redShades.add("d");
		redShades.add("e");
		redShades.add("f");
		redShades.add("g");
		redShades.add("h");
		redShades.add("i");
		redShades.add("j");
		redShades.add("k");
		redShades.add("l");
		redShades.add("m");
		redShades.add("n");
		redShades.add("o");
		redShades.add("p");
		redShades.add("q");
		redShades.add("r");
		redShades.add("s");
		redShades.add("t");
		
		greenShades.add("u");
		greenShades.add("v");
		greenShades.add("w");
		greenShades.add("x");
		greenShades.add("y");
		greenShades.add("z");
		greenShades.add("A");
		greenShades.add("B");
		greenShades.add("C");
		greenShades.add("D");
		greenShades.add("E");
		greenShades.add("F");
		greenShades.add("G");
		greenShades.add("H");
		greenShades.add("I");
		greenShades.add("J");
		greenShades.add("K");
		greenShades.add("L");
		greenShades.add("M");
		greenShades.add("N");
		
		blueShades.add("O");
		blueShades.add("P");
		blueShades.add("Q");
		blueShades.add("R");
		blueShades.add("S");
		blueShades.add("T");
		blueShades.add("U");
		blueShades.add("V");
		blueShades.add("W");
		blueShades.add("X");
		blueShades.add("Y");
		blueShades.add("Z");
		blueShades.add("1");
		blueShades.add("2");
		blueShades.add("3");
		blueShades.add("4");
		blueShades.add("5");
		blueShades.add("6");
		blueShades.add("7");
		blueShades.add("8");
	}
	
	public static void interperetInput(BufferedReader br) throws IOException {
		String fileLine = "";
		while((fileLine = br.readLine()) != null) {
			Scanner lineReader = new Scanner(fileLine);
			String type = "";
			if(lineReader.hasNext()) type = lineReader.next();
			
			if(type.equals("v")) {
				ArrayList<Float> v = new ArrayList<Float>();
				v.add(Float.parseFloat(lineReader.next()));
				v.add(Float.parseFloat(lineReader.next()));
				v.add(Float.parseFloat(lineReader.next()));
				v.add(1.0f);
				
				vertices.add(v);
			}else if(type.equals("f")) {
				ArrayList<Integer> f = new ArrayList<Integer>();
				f.add(lineReader.nextInt() - 1);
				f.add(lineReader.nextInt() - 1);
				f.add(lineReader.nextInt() - 1);
				
				faces.add(f);
			}
		}
	}
	
	public static void writePixel(int x, int y, String pixelVal) {
		if(x <= pixelMap[0].length() && y < pixelMap.length && x >= 0 && y >=0) {
			String line = pixelMap[y];
			if (x < pixelMap[0].length()) {
				String begin = line.substring(0, x), end = line.substring(x+1);
				line = begin + pixelVal + end;
				pixelMap[y] = line;
			} else {
				line = line.substring(0, x) + pixelVal;
				pixelMap[y] = line;
			}
		}
	}
	
	public static void generateXPM() {
		for(int i = 0; i < fileLines.length - 1; i++) {
			System.out.print(fileLines[i] + "\n");
		}
		String whiteSpace = "%-" + pixelMap[0].length() + "s";
		for(int i = pixelMap.length - 1; i >= 0; i--) {
			System.out.print("\"");
			System.out.print(pixelMap[i]);
			System.out.print("\",\n");
		}
		System.out.print(fileLines[fileLines.length - 1] + "\n");
	}
	
	public static float yMax(ArrayList<ArrayList<Float>> l) {
		float max = l.get(0).get(1);
		
		for(int i = 0; i<l.size(); i++) {
			float temp = l.get(i).get(1);
			if(Float.compare(max, temp) < 0) max = temp;
		}
		
		return max;
	}
	
	public static float yMin(ArrayList<ArrayList<Float>> l) {
		float min = l.get(0).get(1);
		
		for(int i = 0; i<l.size(); i++) {
			float temp = l.get(i).get(1);
			if(Float.compare(min, temp) > 0) min = temp;
		}
		
		return min;
	}
	
	public static float getZ(ArrayList<ArrayList<Float>> polygon, float xx1, float yy1, float xx2, float yy2, float xx3, float yy3) {
		float x1 = polygon.get(0).get(0);
		float y1 = polygon.get(0).get(1);
		float z1 = polygon.get(0).get(2);
		float x2 = polygon.get(1).get(0);
		float y2 = polygon.get(1).get(1);
		float z2 = polygon.get(1).get(2);
		float x3 = polygon.get(2).get(0);
		float y3 = polygon.get(2).get(1);
		float z3 = polygon.get(2).get(2);
		
		float len1 = (float) Math.sqrt(((xx1 - x1) * (xx1 - x1)) + ((yy1 - y1) * (yy1 - y1)));
		float len2 = (float) Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
		
		float zz1 = z1 + (len1 / len2) * (z2 - z1);
		
		len1 = (float) Math.sqrt(((xx2 - x1) * (xx2 - x1)) + ((yy2 - y1) * (yy2 - y1)));
		len2 = (float) Math.sqrt(((x3 - x1) * (x3 - x1)) + ((y3 - y1) * (y3 - y1)));
		
		float zz2 = z1 + (len1 / len2) * (z3 - z1);
		
		len1 = (float) Math.sqrt(((xx3 - xx1) * (xx3 - xx1)) + ((yy3 - yy1) * (yy3 - yy1)));
		len2 = (float) Math.sqrt(((xx2 - xx1) * (xx2 - xx1)) + ((yy2 - yy1) * (yy2 - yy1)));
		
		return zz1 + (len1 / len2) * (zz2 - zz1);
	}
	
	public static float[][] multiplyMatricies(float[][] first, float[][] second){
		float[][] result = new float[first.length][second[0].length];
		
		for(int i = 0; i < first.length; i++) {
			for(int j = 0; j < second[0].length; j++) {
				for(int k = 0; k < first[0].length; k++) {
					result[i][j] += first[i][k] * second[k][j];
				}
			}
		}
		
		return result;
	}
	
	public static void sort() {
		for(int i = 0; i < intersectionPoints.size(); i++) {
			Collections.sort(intersectionPoints.get(i), new Comparator<ArrayList<Float>>() {
				public int compare(ArrayList<Float> l1, ArrayList<Float> l2) { return l1.get(0).compareTo(l2.get(0)); }
			});
		}
	}
	
	public static ArrayList<Float> crossProduct(float x1, float y1, float z1, float x2, float y2, float z2) {
		ArrayList<Float> result = new ArrayList<Float>();
		
		result.add((y1 * z2) - (z1 * y2));
		result.add((z1 * x2) - (x1 * z2));
		result.add((x1 * y2) - (y1 * x2));
		
		return result;
	}
	
	public static void project() {
		if(!parallelProjection) {
			float ZOffsetDivisor = PRPZ / (backFace - PRPZ);
			for(int i = 0; i < vertices.size(); i++) {
				ArrayList<Float> vert = vertices.get(i);

				float ZOffset = vert.get(2) / ZOffsetDivisor;
				if(ZOffset == 0) ZOffset = 1;
				
				ArrayList<Float> temp = new ArrayList<Float>();
				temp.add(vert.get(0) / ZOffset);
				temp.add(vert.get(1) / ZOffset);
				temp.add(vert.get(2));

				vertices.set(i, temp);
			}
		}
	}
	
	public static void definePolygons() {
		for(int i = 0; i < faces.size(); i++) {
			ArrayList<ArrayList<Float>> p = new ArrayList<ArrayList<Float>>();
			
			ArrayList<Integer> face = faces.get(i);
			p.add(vertices.get(face.get(0)));
			p.add(vertices.get(face.get(1)));
			p.add(vertices.get(face.get(2)));
			
			polygons.add(p);
		}
	}
	
	public static void backfaceCulling(){
		for(int i = 0; i < faces.size(); i++) {
			ArrayList<ArrayList<Float>> p = new ArrayList<ArrayList<Float>>();
			
			ArrayList<Integer> face = faces.get(i);
			
			float x1 = vertices.get(face.get(0)).get(0);
			float y1 = vertices.get(face.get(0)).get(1);
			float z1 = vertices.get(face.get(0)).get(2);
			
			float x2 = vertices.get(face.get(1)).get(0);
			float y2 = vertices.get(face.get(1)).get(1);
			float z2 = vertices.get(face.get(1)).get(2);
			
			float x3 = vertices.get(face.get(2)).get(0);
			float y3 = vertices.get(face.get(2)).get(1);
			float z3 = vertices.get(face.get(2)).get(2);
			
			ArrayList<Float> crossed = crossProduct(x2-x1, y2-y1, z2-z1, x3-x1, y3-y1, z3-z1);
			
			if(crossed.get(2) >= 0) {
				p.add(vertices.get(face.get(0)));
				p.add(vertices.get(face.get(1)));
				p.add(vertices.get(face.get(2)));
				
				polygons.add(p);
			}
		} 
	}
	
	public static void projectionWithCulling() {
		if(!parallelProjection) {
			float ZOffsetDivisor = PRPZ / (backFace - PRPZ);
			for(int i = 0; i < polygons.size(); i++) {
				for(int j = 0; j < polygons.get(i).size(); j++) {
					ArrayList<Float> vert = polygons.get(i).get(j);
					
					float ZOffset = vert.get(2) / ZOffsetDivisor;
					
					ArrayList<Float> temp = new ArrayList<Float>();
					temp.add(vert.get(0) / ZOffset);
					temp.add(vert.get(1) / ZOffset);
					
					polygons.get(i).set(j, temp);
				}
			}
		} else {
			for(int i = 0; i < polygons.size(); i++) {
				for(int j = 0; j < polygons.get(i).size(); j++) {
					ArrayList<Float> vert = polygons.get(i).get(j);
					
					ArrayList<Float> temp = new ArrayList<Float>();
					temp.add(vert.get(0));
					temp.add(vert.get(1));
					
					polygons.get(i).set(j, temp);
				}
			}
		}
	}
	
	public static void transformVertices() {
		ArrayList<Float> transList = new ArrayList<Float>();
		float[][] transMatrix = new float[4][4], transMatrix2 = new float[4][4], resultsMatrix;
		
		float len = (float) Math.sqrt((VPNX * VPNX) + (VPNY * VPNY) + (VPNZ * VPNZ));
		transMatrix[2][0] = VPNX / len;
		transMatrix[2][1] = VPNY / len;
		transMatrix[2][2] = VPNZ / len;
		transMatrix[2][3] = 0;

		transList = crossProduct(VUPX, VUPY, VUPZ, transMatrix[2][0], transMatrix[2][1], transMatrix[2][2]);
		len = (float) Math.sqrt((transList.get(0) * transList.get(0)) + (transList.get(1) * transList.get(1)) + (transList.get(2) * transList.get(2)));
		
		transMatrix[0][0] = transList.get(0) / len;
		transMatrix[0][1] = transList.get(1) / len;
		transMatrix[0][2] = transList.get(2) / len;
		transMatrix[0][3] = 0;

		transList = crossProduct(transMatrix[2][0], transMatrix[2][1], transMatrix[2][2], transMatrix[0][0], transMatrix[0][1], transMatrix[0][2]);
		
		transMatrix[1][0] = transList.get(0);
		transMatrix[1][1] = transList.get(1);
		transMatrix[1][2] = transList.get(2);
		transMatrix[1][3] = 0;
		transMatrix[3][0] = 0;
		transMatrix[3][1] = 0;
		transMatrix[3][2] = 0;
		transMatrix[3][3] = 1;
		
		transMatrix2[0][0] = 1;
		transMatrix2[0][1] = 0;
		transMatrix2[0][2] = 0;
		transMatrix2[0][3] = -VRPX;
		transMatrix2[1][0] = 0;
		transMatrix2[1][1] = 1;
		transMatrix2[1][2] = 0;
		transMatrix2[1][3] = -VRPY;
		transMatrix2[2][0] = 0;
		transMatrix2[2][1] = 0;
		transMatrix2[2][2] = 1;
		transMatrix2[2][3] = -VRPZ;
		transMatrix2[3][0] = 0;
		transMatrix2[3][1] = 0;
		transMatrix2[3][2] = 0;
		transMatrix2[3][3] = 1;		
		
		resultsMatrix = multiplyMatricies(transMatrix, transMatrix2);
		
		transMatrix2[0][0] = 1;
		transMatrix2[0][1] = 0;
		transMatrix2[0][2] = ((0.5f * (uMax + uMin)) - PRPX) / PRPZ;
		transMatrix2[0][3] = 0;
		transMatrix2[1][0] = 0;
		transMatrix2[1][1] = 1;
		transMatrix2[1][2] = ((0.5f * (vMax + vMin)) - PRPY) / PRPZ;
		transMatrix2[1][3] = 0;
		transMatrix2[2][0] = 0;
		transMatrix2[2][1] = 0;
		transMatrix2[2][2] = 1;
		transMatrix2[2][3] = 0;
		transMatrix2[3][0] = 0;
		transMatrix2[3][1] = 0;
		transMatrix2[3][2] = 0;
		transMatrix2[3][3] = 1;
		
		if(parallelProjection) {
			resultsMatrix = multiplyMatricies(transMatrix2, resultsMatrix);
			
			transMatrix2 = new float[4][4];
			
			transMatrix[0][0] = 1;
			transMatrix[0][1] = 0;
			transMatrix[0][2] = 0;
			transMatrix[0][3] = -(uMax + uMin) / 2;
			transMatrix[1][0] = 0;
			transMatrix[1][1] = 1;
			transMatrix[1][2] = 0;
			transMatrix[1][3] = -(vMax + vMin) / 2;
			transMatrix[2][0] = 0;
			transMatrix[2][1] = 0;
			transMatrix[2][2] = 1;
			transMatrix[2][3] = -frontFace;
			transMatrix[3][0] = 0;
			transMatrix[3][1] = 0;
			transMatrix[3][2] = 0;
			transMatrix[3][3] = 1;
			
			resultsMatrix = multiplyMatricies(transMatrix, resultsMatrix);
			
			transMatrix = new float[4][4];
			
			transMatrix[0][0] = 2 / (uMax - uMin);
			transMatrix[0][1] = 0;
			transMatrix[0][2] = 0;
			transMatrix[0][3] = 0;
			transMatrix[1][0] = 0;
			transMatrix[1][1] = 2 / (vMax - vMin);
			transMatrix[1][2] = 0;
			transMatrix[1][3] = 0;
			transMatrix[2][0] = 0;
			transMatrix[2][1] = 0;
			transMatrix[2][2] = 1 / (frontFace - backFace);
			transMatrix[2][3] = 0;
			transMatrix[3][0] = 0;
			transMatrix[3][1] = 0;
			transMatrix[3][2] = 0;
			transMatrix[3][3] = 1;
			
			resultsMatrix = multiplyMatricies(transMatrix, resultsMatrix);
			
			for(int i = 0; i < vertices.size(); i++) {
				float[][] vert = new float[4][1];
				vert[0][0] = vertices.get(i).get(0);
				vert[1][0] = vertices.get(i).get(1);
				vert[2][0] = vertices.get(i).get(2);
				vert[3][0] = vertices.get(i).get(3);
				
				vert = multiplyMatricies(resultsMatrix, vert);
				
				ArrayList<Float> newVertex = new ArrayList<Float>();
				newVertex.add(vert[0][0]);
				newVertex.add(vert[1][0]);
				newVertex.add(vert[2][0]);
				
				vertices.set(i, newVertex);
			}
		} else {
			transMatrix = new float[4][4];
			
			transMatrix[0][0] = 1;
			transMatrix[0][1] = 0;
			transMatrix[0][2] = 0;
			transMatrix[0][3] = -PRPX;
			transMatrix[1][0] = 0;
			transMatrix[1][1] = 1;
			transMatrix[1][2] = 0;
			transMatrix[1][3] = -PRPY;
			transMatrix[2][0] = 0;
			transMatrix[2][1] = 0;
			transMatrix[2][2] = 1;
			transMatrix[2][3] = -PRPZ;
			transMatrix[3][0] = 0;
			transMatrix[3][1] = 0;
			transMatrix[3][2] = 0;
			transMatrix[3][3] = 1;
			
			resultsMatrix = multiplyMatricies(transMatrix, resultsMatrix);			
			resultsMatrix = multiplyMatricies(transMatrix2, resultsMatrix);
			
			transMatrix = new float[4][4];
			
			transMatrix[0][0] = (2 * PRPZ) / ((uMax - uMin) * (PRPZ - backFace));
			transMatrix[0][1] = 0;
			transMatrix[0][2] = 0;
			transMatrix[0][3] = 0;
			transMatrix[1][0] = 0;
			transMatrix[1][1] = (2 * PRPZ) / ((vMax - vMin) * (PRPZ - backFace));
			transMatrix[1][2] = 0;
			transMatrix[1][3] = 0;
			transMatrix[2][0] = 0;
			transMatrix[2][1] = 0;
			transMatrix[2][2] = 1 / (PRPZ - backFace);
			transMatrix[2][3] = 0;
			transMatrix[3][0] = 0;
			transMatrix[3][1] = 0;
			transMatrix[3][2] = 0;
			transMatrix[3][3] = 1;
			
			resultsMatrix = multiplyMatricies(transMatrix, resultsMatrix);
			
			for(int i = 0; i < vertices.size(); i++) {
				float[][] vert = new float[4][1];
				vert[0][0] = vertices.get(i).get(0);
				vert[1][0] = vertices.get(i).get(1);
				vert[2][0] = vertices.get(i).get(2);
				vert[3][0] = vertices.get(i).get(3);
				
				vert = multiplyMatricies(resultsMatrix, vert);
				
				ArrayList<Float> newVertex = new ArrayList<Float>();
				newVertex.add(vert[0][0]);
				newVertex.add(vert[1][0]);
				newVertex.add(vert[2][0]);
				
				vertices.set(i, newVertex);
			}
		}
	}
	
	public static void scaleToWindow() {
		for(int i = 0; i < polygons.size(); i++) {
			ArrayList<ArrayList<Float>> polygon = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < polygons.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				coords.add(polygons.get(i).get(j).get(0) - WWLowX);
				coords.add(polygons.get(i).get(j).get(1) - WWLowY);
				coords.add(polygons.get(i).get(j).get(2));
				
				polygon.add(coords);
			}
			
			polygonsTempHolder.add(polygon);
		}
		
		polygons.clear();
		polygons.addAll(polygonsTempHolder);
		
		polygonsTempHolder.clear();
		
		for(int i = 0; i < polygons.size(); i++) {
			ArrayList<ArrayList<Float>> polygon = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < polygons.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				float x = polygons.get(i).get(j).get(0);
				float y = polygons.get(i).get(j).get(1);
				coords.add(x * ((VPHighX - VPLowX) / (WWHighX - WWLowX)));
				coords.add(y * ((VPHighY - VPLowY) / (WWHighY - WWLowY)));
				coords.add(polygons.get(i).get(j).get(2));
				
				polygon.add(coords);
			}
			
			polygonsTempHolder.add(polygon);
		}
		polygons.clear();
		polygons.addAll(polygonsTempHolder);
		
		polygonsTempHolder.clear();
		for(int i = 0; i < polygons.size(); i++) {
			ArrayList<ArrayList<Float>> polygon = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < polygons.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				coords.add(polygons.get(i).get(j).get(0) + VPLowX);
				coords.add(polygons.get(i).get(j).get(1) + VPLowY);
				coords.add(polygons.get(i).get(j).get(2));
				
				polygon.add(coords);
			}
			
			polygonsTempHolder.add(polygon);
		}

		drawCoords.addAll(polygonsTempHolder);
	}
	
	public static void clipPolygons() {
		if(parallelProjection) {
			WWLowX = -1.0f;
			WWHighX = 1.0f;
			WWLowY = -1.0f;
			WWHighY = 1.0f;
		} else {
			WWLowX = -Math.abs(PRPZ / (backFace - PRPZ));
			WWHighX = Math.abs(PRPZ / (backFace - PRPZ));
			WWLowY = -Math.abs(PRPZ / (backFace - PRPZ));
			WWHighY = Math.abs(PRPZ / (backFace - PRPZ));
		}
		clipLines(0);
		clipLines(1);
		clipLines(2);
		clipLines(3);
	}
	
	public static void clipLines(int wall) {
		for(int i = 0; i < polygons.size(); i++) {
			ArrayList<ArrayList<Float>> polygon = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < polygons.get(i).size() - 1; j++) {
				float startX = polygons.get(i).get(j).get(0);
				float startY = polygons.get(i).get(j).get(1);
				float endX = polygons.get(i).get(j + 1).get(0);
				float endY = polygons.get(i).get(j + 1).get(1);
				
				if(inside(startX, startY, wall)) {
					if(j == 0) {
						ArrayList<Float> coords = new ArrayList<Float>();
						coords.add(startX);
						coords.add(startY);
						
						polygon.add(coords);
					}
					
					if(inside(endX, endY, wall)) {
						ArrayList<Float> coords = new ArrayList<Float>();
						coords.add(endX);
						coords.add(endY);
						
						polygon.add(coords);
					} else {
						float[] inter = intersection(startX, startY, endX, endY, wall);
						ArrayList<Float> coords = new ArrayList<Float>();
						
						coords.add(inter[0]);
						coords.add(inter[1]);
						
						polygon.add(coords);
					}
				} else {
					if(inside(endX, endY, wall)) {
						float[] inter = intersection(endX, endY, startX, startY, wall);
						ArrayList<Float> coords = new ArrayList<Float>();
						
						coords.add(inter[0]);
						coords.add(inter[1]);
						
						polygon.add(coords);
					}
				}
			}
			if(!polygon.isEmpty()) polygonsTempHolder.add(polygon);
			polygon = new ArrayList<ArrayList<Float>>();
		}
		
		polygons.clear();
		
		for(int i = 0; i < polygonsTempHolder.size(); i++) {
			ArrayList<ArrayList<Float>> polygon = new ArrayList<ArrayList<Float>>();
			for(int j = 0; j < polygonsTempHolder.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				coords.add(polygonsTempHolder.get(i).get(j).get(0));
				coords.add(polygonsTempHolder.get(i).get(j).get(1));
				
				polygon.add(coords);
			}
			polygons.add(polygon);
		}
		
		if(wall == 3) for(int i = 0; i < polygons.size(); i++) polygons.get(i).add(polygons.get(i).get(0));
		
		polygonsTempHolder.clear();
	}
	
	public static boolean inside(float x, float y, int wall) {
		boolean isInside = false;

		if(wall == 0 && y < WWHighY) isInside = true;
		else if(wall == 1 && y > WWLowY) isInside = true;
		else if(wall == 2 && x > WWLowX) isInside = true;
		else if(wall == 3 && x < WWHighX) isInside = true;
		
		return isInside;
	}
	
	public static float[] intersection(float startX, float startY, float endX, float endY, int wall) {
		float[] returnCoords = new float[2];
		
		float dx = endX - startX;
		float dy = endY - startY;
		float m = dy/dx;
		
		if(dx == 0 || dy == 0) {
			if(wall == 0) {
				returnCoords[0] = startX;
				returnCoords[1] = WWHighY;
			} else if(wall == 1) {
				returnCoords[0] = startX;
				returnCoords[1] = WWLowY;
			} else if(wall == 2) {
				returnCoords[0] = WWLowX;
				returnCoords[1] = startY;
			} else if(wall == 3) {
				returnCoords[0] = WWHighX;
				returnCoords[1] = startY;
			}
		} else {
			if(wall == 0) {
				returnCoords[0] = (WWHighY - startY) / m + startX;
				returnCoords[1] = WWHighY;
			} else if(wall == 1) {
				returnCoords[0] = (WWLowY - startY) / m + endX;
				returnCoords[1] = WWLowY;
			} else if(wall == 2) {
				returnCoords[0] = WWLowX;
				returnCoords[1] = m * (WWLowX - startX) + startY;
			} else if(wall == 3) {
				returnCoords[0] = WWHighX;
				returnCoords[1] = m * (WWHighX - startX) + startY;
			}
		}
		
		return returnCoords;
	}
	
	public static void addIntersection(float x1, float y1, float x2, float y2, float y, float yMin) {
		ArrayList<Float> coords = new ArrayList<Float>();
		float x = 0.0f;
		if(y2 - y1 > -1 && y2 - y1 < 1) {
			x = x1;
		} else {
			float dx = x2 - x1;
			float dy = y2 - y1;
			
			x = x1 + (dx/dy) * (y - y1);
		}
			
		coords.add(x);
		coords.add(y);

		intersectionPoints.get(Math.round(y - yMin)).add(coords);
	}
	
	public static void scanFillPolygon(ArrayList<ArrayList<Float>> polygon, int color) {
		for(int i = 0; i < intersectionPoints.size(); i++) {
			for(int j = 0; j < intersectionPoints.get(i).size() - 1; j++) {
				if(intersectionPoints.get(i).size() > 1) {
					float x1 = intersectionPoints.get(i).get(j).get(0);
					float y1 = intersectionPoints.get(i).get(j).get(1);
					float x2 = intersectionPoints.get(i).get(j + 1).get(0);
					float y2 = intersectionPoints.get(i).get(j + 1).get(1);
					float xx = x1;
					
					while(Float.compare(xx, x2) < 0 && !(xx != xx) && !(x2 != x2)) {
						float zz = getZ(polygon, x1, y1, x2, y2, xx, y1);
						
						if(xx < 0) xx = 0;
						if(y1 < 0) y1 = 0;
						if(x2 < 0) x2 = 0;
						if(xx > 500) xx = 500;
						if(y1 > 500) y1 = 500;
						if(x2 > 500) x2 = 500;
							
						if((zz <= frontFace) && (zz > zBuffer[Math.round(y1)][Math.round(xx)])) {
							zBuffer[Math.round(y1)][Math.round(xx)] = zz;
							
							int shade = Math.round((20 * (zz - zFar) / (zClose - zFar)));
							
							if(shade < 0) shade = 0;
							if(shade > 19) shade = 19;
							
							if(color == 1) writePixel(Math.round(xx), Math.round(y1), redShades.get(shade));
							else if(color == 2) writePixel(Math.round(xx), Math.round(y1), greenShades.get(shade));
							else if(color == 3) writePixel(Math.round(xx), Math.round(y1), blueShades.get(shade));
						}
						
						xx++;
					}
				}
			}
		}
	}
	
	public static void fillPolygons(int color) {
		for(int i = 0; i < polygons.size(); i++) {
			float yMin = yMin(polygons.get(i));
			float yMax = yMax(polygons.get(i));
			
			for(int j = (int) yMin; j <= Math.round(yMax) + 1; j++) {
				edges.add(new ArrayList<ArrayList<Float>>());
				intersectionPoints.add(new ArrayList<ArrayList<Float>>());
			}
			
			for(int j = 0; j < polygons.get(i).size(); j++) {
				float y1 = 0.0f, y2 = 0.0f;
				if(j == polygons.get(i).size() - 1) {
					y1 = polygons.get(i).get(j).get(1);
					y2 = polygons.get(i).get(0).get(1);
				} else {
					y1 = polygons.get(i).get(j).get(1);
					y2 = polygons.get(i).get(j + 1).get(1);
				}
				
				for(int y = (int) yMin; y <= Math.round(yMax); y++) {
					if (y >= Math.round(y1) && y <= Math.round(y2) 
							|| (Math.round(y) >= Math.round(y2) && Math.round(y) <= Math.round(y1))) {
						ArrayList<Float> coords = new ArrayList<Float>();
						float x1 = 0.0f, x2 = 0.0f;
						if(j == polygons.get(i).size() - 1) {
							x1 = polygons.get(i).get(j).get(0);
							x2 = polygons.get(i).get(0).get(0);
						} else {
							x1 = polygons.get(i).get(j).get(0);
							x2 = polygons.get(i).get(j + 1).get(0);
						}
						
						coords.add(x1);
						coords.add(y1);
						coords.add(x2);
						coords.add(y2);

						edges.get(Math.round(y - yMin)).add(coords);
					}
				}
			}

			for(int y = Math.round(yMin); y <= Math.round(yMax); y++) {
				intersectionPoints = new ArrayList<ArrayList<ArrayList<Float>>>();
				for(int j = (int) yMin; j <= Math.ceil(yMax); j++) intersectionPoints.add(new ArrayList<ArrayList<Float>>());
				for(int j = 0; j < edges.get(Math.round(y - yMin)).size(); j++){
					float xStart = edges.get(Math.round(y - yMin)).get(j).get(0);
					float yStart = edges.get(Math.round(y - yMin)).get(j).get(1);
					float xEnd = edges.get(Math.round(y-yMin)).get(j).get(2);
					float yEnd = edges.get(Math.round(y-yMin)).get(j).get(3);
					
					addIntersection(xStart, yStart, xEnd, yEnd, y, yMin);
				}
				sort();
				scanFillPolygon(polygons.get(i), color);
			}
			edges.clear();
			intersectionPoints.clear();
		}
	}
}
