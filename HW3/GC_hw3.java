import java.io.*;
import java.util.*;

public class CG_hw3 {
	public static String[] fileLines = new String[8];
	public static String[] pixelMap;

	public static void main(String[] args) throws Exception {
		String file = "hw3_split.ps";
		double scale = 1.0;
		int rotation = 0, translateX = 0, translateY = 0;
		int lowerX = 0, lowerY = 0, upperX = 500, upperY = 500;
		int VPLowX = 0, VPLowY = 0, VPHighX = 250, VPHighY = 250;
		for(int i = 0; i < args.length; i++) {
			String option = args[i];
			switch(option) {
				case "-f":
						file = args[++i];
						break;
				case "-s":
						scale = Double.parseDouble(args[++i]);
						break;
				case "-r":
						rotation = Integer.parseInt(args[++i]);
						break;
				case "-m":
						translateX = Integer.parseInt(args[++i]);
						break;
				case "-n":
						translateY = Integer.parseInt(args[++i]);
						break;
				case "-a":
						lowerX = Integer.parseInt(args[++i]);
						break;
				case "-b":
					lowerY = Integer.parseInt(args[++i]);
					break;
				case "-c":
					upperX = Integer.parseInt(args[++i]);
					break;
				case "-d":
					upperY = Integer.parseInt(args[++i]);
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
			}
		}
		fileLines[0] = "/* XPM */";
		fileLines[1] = "static char *quad_bw[] = {";
		fileLines[2] = "/* columns rows colors chars-per-pixel */";
		fileLines[3] = "\"500 500 2 1\",";
		fileLines[4] = "/* pixels */";
		fileLines[5] = "\"@ c #000000\",";
		fileLines[6] = "\"  c #FFFFFF\",";
		fileLines[7] = "};";
		
		initializeMap(lowerX, lowerY, upperX, upperY);
		
		String filePath = new File(".").getCanonicalPath();
		filePath += "/" + file;
		File inputFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		interpretInput(br, scale, rotation, translateX, translateY, lowerX, lowerY, upperX, upperY);
		
		generateXPM(VPLowX, VPLowY, VPHighX, VPHighY);
	}
	
	public static void initializeMap(int lowerX, int lowerY, int upperX, int upperY) {
		pixelMap = new String[(upperY + 1) - lowerY];
		int lineNum = 0;
		for(int j = lowerY; j <= upperY; j++) {
			String line = "";
			for(int i = lowerX; i <= upperX; i++) {
				line += " ";
			}
			pixelMap[lineNum] = line;
			lineNum++;
		}
	}
	
	public static void interpretInput(BufferedReader br, double scale, int rotation, int translateX, int translateY, 
			int lowerXBound, int lowerYBound, int upperXBound, int upperYBound) throws Exception {
		ArrayList<Integer> coords = new ArrayList();
		String fileLine = "";
		boolean parseLines = false;
		while ((fileLine = br.readLine()) != null) {
			if(fileLine.equals("%%%BEGIN")) {
				parseLines = true;
			}
			else if(parseLines && !fileLine.equals("%%%END") && !fileLine.matches(" *")) {
				Scanner parseLine = new Scanner(fileLine);
				while(parseLine.hasNextInt()) {
					coords.add(parseLine.nextInt());
				}
				String type = parseLine.next();
				
				if(type.equals("Line")) {
					Integer[] coordsArr = {coords.get(0), coords.get(1), coords.get(2), coords.get(3)};
					Integer[] transformedCoords = transformCoords(coordsArr, scale, rotation, translateX, translateY, lowerXBound, lowerYBound, upperXBound, upperYBound);
					drawLine(transformedCoords[0], transformedCoords[1], transformedCoords[2], transformedCoords[3]);
					coords.clear();
				} else if(type.equals("moveto") || type.equals("lineto")) {
					//do nothing
				} else if(type.equals("stroke")) {
					ArrayList<Integer[]> coordPairs = new ArrayList<Integer[]>();
					for(int i = 0; i <= coords.size() - 2; i += 2) {
						Integer[] pair = {coords.get(i), coords.get(i+1)};
						coordPairs.add(transformCoords(pair, scale, rotation, translateX, translateY, lowerXBound, lowerYBound, upperXBound, upperYBound));
					}
					clipPolygon(coordPairs, scale, rotation, translateX, translateY, lowerXBound, lowerYBound, upperXBound, upperYBound);
					coords.clear();
				}
			}
			else if(fileLine.equals("%%%END")) parseLines = false;
		}
	}
	
	public static Integer[] transformCoords(Integer[] coords, double scale, int rotation, 
			int translateX, int translateY, int lowerXBound, int lowerYBound, int upperXBound, int upperYBound) {
		
		int transformStartX, transformStartY, transformEndX, transformEndY;
		transformStartX = coords[0] + translateX;
		transformStartY = coords[1] + translateY;
		double finalStartX = (double) transformStartX * scale;
		double finalStartY = (double) transformStartY * scale;
		double rads = ((double) rotation * Math.PI) / (double) 180;
		double tempX = finalStartX;
		finalStartX = (finalStartX * Math.cos(rads)) - (finalStartY * Math.sin(rads));
		finalStartY = (tempX * Math.sin(rads)) + (finalStartY * Math.cos(rads));
		
		/*transformEndX = endX + translateX;
		transformEndY = endY + translateY;
		double finalEndX = (double) transformEndX * scale;
		double finalEndY = (double) transformEndY * scale;
		double radsEnd = ((double) rotation * Math.PI) / (double) 180;
		double tempXEnd = finalEndX;
		finalEndX = (finalEndX * Math.cos(radsEnd)) - (finalEndY * Math.sin(radsEnd));
		finalEndY = (tempXEnd * Math.sin(radsEnd)) + (finalEndY * Math.cos(radsEnd));*/
		
		int x, y;
		x = (int)Math.round(finalStartX);
		y = (int)Math.round(finalStartY);
		//ex = (int)Math.round(finalEndX);
		//ey = (int)Math.round(finalEndY);
		
		Integer[] returnCoords = {x, y};
		return returnCoords;
	}
	
	public static void drawLine(int startX, int startY, int endX, int endY) {
		int dx, dy, D, sx, sy, ex, ey;
		int drawStartX = 0, drawStartY = 0, drawEndX = 0, drawEndY = 0;
		sx = startX;
		sy = startY;
		ex = endX;
		ey = endY;	
		drawStartX = sx;
		drawStartY = sy;
		drawEndX = ex;
		drawEndY = ey;
		int xs = drawStartX, xe = drawEndX, ys = drawStartY, ye = drawEndY;
		
		dx = Math.abs(xe - xs);
		dy = Math.abs(ye - ys);
		int dirX, dirY;
		if(xs < xe) dirX = 1;
		else dirX = -1;
		if(ys < ye) dirY = 1;
		else dirY = -1;
		
		D = dx - dy;
		
		writePixel(xs, ys);
		
		if(!((xs == xe) || (ys == ye))) {
			while(!((xs == xe) || (ys == ye))) {
				int e = D << 1;
				if(D >= -dy) {
					D -= dy;
					xs += dirX;
				}
				if (D < dx) {
					D += dx;
					ys += dirY;
				}
				
				writePixel(xs, ys);
			}
			if(xs == xe && ys != ye) {
				if(ys < ye) {
					ys++;
					writePixel(xs, ys);
				} else {
					ys--;
					writePixel(xs, ys);
				}
			} else if (ys == ye && xs != xe) {
				if(xs < xe) {
					xs++;
					writePixel(xs, ys);
				} else {
					xs--;
					writePixel(xs,ys);
				}
			}
		} else {
			if ((xs == xe) && (ys < ye)) {
				for(int y = ys; y <= ye; y++) {
					writePixel(xs, y);
				}
			} else if((xs == xe) && (ys > ye)) {
				for(int y = ys; y >= ye; y--) {
					writePixel(xs, y);
				}
			} else if((ys == ye) && (xs < xe)) {
				for(int x = xs; x <= xe; x++) {
					writePixel(x, ys);
				}
			} else {
				for(int x = xs; x >= xe; x--) {
					writePixel(x, ys);
				}
			}
		}
	}
	
	public static void clipPolygon(ArrayList<Integer[]> coords, double scale, int rotation, 
			int translateX, int translateY, int lowerXBound, int lowerYBound, int upperXBound, int upperYBound) {
		ArrayList<Integer[]> drawCoords = new ArrayList<Integer[]>();
		Integer[][] tempA = {{lowerXBound, lowerYBound}, {upperXBound, lowerYBound}, {upperXBound, upperYBound}, {lowerXBound, upperXBound}};
		ArrayList<Integer[]> clipWalls = new ArrayList<Integer[]>();
		clipWalls.add(tempA[0]);
		clipWalls.add(tempA[1]);
		clipWalls.add(tempA[2]);
		clipWalls.add(tempA[3]);
		
		int len = clipWalls.size();
		for (int i = 0; i < len; i++) {
			int len2 = coords.size();
			
			Integer[] A = clipWalls.get((i + len - 1) % len);
			Integer[] B = clipWalls.get(i);
			
			for(int j = 0; j < len2; j++) {
				Integer[] C = coords.get((j + len2 - 1) % len2);
				Integer[] D = coords.get(j);
				
				if(inside(A, B, D)) {
					if(!inside(A, B, C)) drawCoords.add(intersection(A, B, C, D));
					drawCoords.add(D);
				} else if(inside(A, B, C)) {
					drawCoords.add(intersection(A, B, C, D));
				}
			}
		}
		
		for(int i = 0; i < drawCoords.size() - 1; i++) {
			drawLine(drawCoords.get(i)[0], drawCoords.get(i)[1], drawCoords.get(i + 1)[0], drawCoords.get(i+ 1)[1]);
		}
		
		scanFillPolygon(drawCoords);
	}
	
	public static void scanFillPolygon(ArrayList<Integer[]> coords) {
		int xmin = coords.get(0)[0], xmax = coords.get(0)[0];
		int ymin = coords.get(0)[1], ymax = coords.get(0)[1];
		for(int i = 1; i < coords.size(); i++) {
			if(xmin > coords.get(i)[0]) xmin = coords.get(i)[0];
			if(xmax < coords.get(i)[0]) xmax = coords.get(i)[0];
			if(ymin > coords.get(i)[1]) ymin = coords.get(i)[1];
			if(ymax < coords.get(i)[1]) ymax = coords.get(i)[1];
		}
		for(int i = ymin + 1; i <= ymax - 1; i++) {
			int numEdges = 0;
			String line = pixelMap[i];
			for(int j = xmin - 1; j <= xmax; j++) {
				if(line.charAt(j) == '@' && !isVertex(coords, i, j)) {
					if(line.charAt(j + 1) != '@') numEdges++;
					else if(line.charAt(j + 1) == '@' && line.charAt(j + 2) != '@') numEdges += 1;
					else if(line.substring(j, xmax + 1).contains("@")) numEdges = 2;
				} else if(isVertex(coords, j, i)) {
					if(numPixelsLeft(line.substring(j, xmax + 1)) % 2 == 1) {
						numEdges++;
					}
				}
				
				if(numPixelsLeft(line.substring(j, xmax + 1)) == 0) numEdges = 0;
				if(numPixelsLeft(line.substring(j, xmax + 1)) == 1) numEdges = 1;
				
				if(numEdges % 2 == 1) writePixel(j, i);
			}
		}
	}
	
	public static int numPixelsLeft(String line) {
		int num = 0;
		
		for(int i = 0; i < line.length(); i++) {
			if(line.charAt(i) == '@') num++;
		}

		return num;
	}
	
	public static boolean isVertex(ArrayList<Integer[]> coords, int x, int y) {
		boolean vert = false;
		for(int i = 0; i < coords.size(); i++) {
			if(coords.get(i)[0] == x && coords.get(i)[1] == y) vert = true;
		}
		return vert;
	}

	public static void writePixel(int x, int y) {
		if(x <= pixelMap[0].length() && y < pixelMap.length && x >= 0 && y >=0) {
			String line = pixelMap[y];
			if (x < pixelMap[0].length()) {
				String begin = line.substring(0, x), end = line.substring(x+1);
				line = begin + "@" + end;
				pixelMap[y] = line;
			} else {
				line = line.substring(0, x) + "@";
				pixelMap[y] = line;
			}
		}
	}
	
	public static boolean inside(Integer[] a, Integer[] b, Integer[] d) {
		return ((a[0] - d[0]) * (b[1] - d[1]) > (a[1] - d[1]) * (b[0] - d[0])); 
	}
	
	public static Integer[] intersection(Integer[] a, Integer[] b, Integer[] c, Integer[] d) {
		int A1 = b[1] - a[1];
		int B1 = a[0] - b[0];
		int C1 = A1 * a[0] + B1 *a[1];
		
		int A2 = d[1] - c[1];
		int B2 = c[0] - d[0];
		int C2 = A2 * c[0] + B2 * c[1];
		
		int det = A1 * B2 - A2 * B1;
		int x = (B2 * C1 - B1 * C2) / det;
		int y = (A1 * C2 - A2 * C1) / det;
		
		return new Integer[] {x, y}; 
	}
	
	public static void generateXPM(int VPLowX, int VPLowY, int VPHighX, int VPHighY) {
		for(int i = 0; i < 7; i++) {
			System.out.print(fileLines[i] + "\n");
		}
		String whiteSpace = "%-" + pixelMap[0].length() + "s";
		for(int i = pixelMap.length - 1; i > VPHighY; i--) {
			System.out.print("\"");
			System.out.print(String.format(whiteSpace, ""));
			System.out.print("\",\n");
		}
		for(int i = VPHighY; i >= VPLowY; i--) {
			System.out.print("\"");
			for(int j = 0; j < VPLowX; j++) {
				System.out.print(" ");
			}
			System.out.print(pixelMap[i].substring(VPLowX, VPHighX));
			for(int j = VPHighX + 1; j <= pixelMap[0].length(); j++) {
				System.out.print(" ");
			}
			System.out.print("\",\n");
		}
		for(int i = VPLowY - 1; i >= 0; i--) {
			System.out.print("\"");
			System.out.print(String.format(whiteSpace, ""));
			System.out.print("\",\n");
		}
		System.out.print(fileLines[7] + "\n");
	}
}