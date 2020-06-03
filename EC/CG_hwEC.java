import java.io.*;
import java.util.*;

public class CG_hwEC {
	private static double scale = 1.0;
	private static int VPLowX = 0, VPHighX = 200, VPLowY = 0, VPHighY = 200;
	private static int WWLowX = 0, WWHighX = 250, WWLowY = 0, WWHighY = 250;
	private static int rotation = 0, translateX = 0, translateY = 0;
	private static float increment = 0.05f;
	private static String[] fileLines = new String[8];
	private static String[] pixelMap = new String[501];
	private static ArrayList<ArrayList<Integer>> curves = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<ArrayList<Integer>> lines = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<ArrayList<Float>> processedLines = new ArrayList<ArrayList<Float>>();
	private static ArrayList<ArrayList<Float>> drawLines = new ArrayList<ArrayList<Float>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> curveApproxLines = new ArrayList<ArrayList<ArrayList<Float>>>();
	
	public static void main(String[] args) throws Exception {
		String file = "ExtraCredit.ps";
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
					WWLowX = Integer.parseInt(args[++i]);
					break;
				case "-b":
					WWLowY = Integer.parseInt(args[++i]);
					break;
				case "-c":
					WWHighX = Integer.parseInt(args[++i]);
					break;
				case "-d":
					WWHighY = Integer.parseInt(args[++i]);
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
				case "-L":
					increment = Float.parseFloat(args[++i]);
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
		
		initializeMap();
		
		String filePath = new File(".").getCanonicalPath();
		filePath += "/" + file;
		File inputFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		interpretInput(br);
		curvesToLines();
		transformCurves();
		clipLines();
		scaleToView();
		drawLines();
		
		generateXPM();
	}
	
	public static void generateXPM() {
		for(int i = 0; i < 7; i++) {
			System.out.print(fileLines[i] + "\n");
		}
		String whiteSpace = "%-" + pixelMap[0].length() + "s";
		for(int i = pixelMap.length - 1; i >= 0; i--) {
			System.out.print("\"");
			System.out.print(pixelMap[i]);
			System.out.print("\",\n");
		}
		System.out.print(fileLines[7] + "\n");
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
	
	public static void interpretInput(BufferedReader br) throws IOException {
		String fileLine = "";
		boolean read = false;
		ArrayList<Integer> curve = new ArrayList<Integer>();
		while((fileLine = br.readLine()) != null) {
			Scanner lineReader = new Scanner(fileLine);
			String line = "";
			if(lineReader.hasNext()) line = lineReader.nextLine();
			
			if(read && !line.trim().isEmpty() && !line.equals("%%%END")) {
				if(line.equals("stroke")) {
					curves.add(curve);
					curve = new ArrayList<Integer>();
					continue;
				}
				
				String[] lineVals = line.split(" ");
				
				if(lineVals[2].equals("moveto")) {
					curve.add(Integer.parseInt(lineVals[0]));
					curve.add(Integer.parseInt(lineVals[1]));
					
					continue;
				} else if(lineVals[4].equals("Line")) {
					ArrayList<Integer> coords = new ArrayList<Integer>();
					
					coords.add(Integer.parseInt(lineVals[0]));
					coords.add(Integer.parseInt(lineVals[1]));
					coords.add(Integer.parseInt(lineVals[2]));
					coords.add(Integer.parseInt(lineVals[3]));
					
					lines.add(coords);
					
					continue;
				} else if(lineVals[6].equals("curveto")) {
					curve.add(Integer.parseInt(lineVals[0]));
					curve.add(Integer.parseInt(lineVals[1]));
					curve.add(Integer.parseInt(lineVals[2]));
					curve.add(Integer.parseInt(lineVals[3]));
					curve.add(Integer.parseInt(lineVals[4]));
					curve.add(Integer.parseInt(lineVals[5]));
					
					continue;
				}
			}
			
			if(line.equals("%%%BEGIN")) read = true;
		}
	}
	
	public static void curvesToLines() {
		for(int i = 0; i < curves.size(); i++) {
			int x1 = curves.get(i).get(0);
			int y1 = curves.get(i).get(1);
			int x2 = curves.get(i).get(2);
			int y2 = curves.get(i).get(3);
			int x3 = curves.get(i).get(4);
			int y3 = curves.get(i).get(5);
			int x4 = curves.get(i).get(6);
			int y4 = curves.get(i).get(7);
			
			ArrayList<ArrayList<Float>> curve = new ArrayList<ArrayList<Float>>();
			int count = 0;
			
			for(float t = 0; t <= 1.0f; t += increment) {
				ArrayList<Float> line = new ArrayList<Float>();

				line.add((float) ((Math.pow(1-t, 3) * x1) + (3*t * Math.pow(1-t, 2) * x2) + (3*t*t*(1-t)*x3) + (t*t*t*x4))); 
				line.add((float) ((Math.pow(1-t, 3) * y1) + (3*t * Math.pow(1-t, 2) * y2) + (3*t*t*(1-t)*y3) + (t*t*t*y4)));

				curve.add(line);

				if(t + increment > 1f && count==0)
				{
					t = 1.0f;
					ArrayList<Float> line1 = new ArrayList<Float>();

					line1.add((float) ((Math.pow(1-t, 3) * x1) + (3*t * Math.pow(1-t, 2) * x2) + (3*t*t*(1-t)*x3) + (t*t*t*x4))); 
					line1.add((float) ((Math.pow(1-t, 3) * y1) + (3*t * Math.pow(1-t, 2) * y2) + (3*t*t*(1-t)*y3) + (t*t*t*y4)));

					curve.add(line1);	
					count++;
				}
			}
			
			curveApproxLines.add(curve);
		}
	}
	
	public static void transformCurves() {
		ArrayList<ArrayList<ArrayList<Float>>> curvesHolder1 = new ArrayList<ArrayList<ArrayList<Float>>>();
		ArrayList<ArrayList<ArrayList<Float>>> curvesHolder2 = new ArrayList<ArrayList<ArrayList<Float>>>();
		ArrayList<ArrayList<Float>> linesHolder1 = new ArrayList<ArrayList<Float>>();
		ArrayList<ArrayList<Float>> linesHolder2 = new ArrayList<ArrayList<Float>>();
		for(int i = 0; i < curveApproxLines.size(); i++) {
			ArrayList<ArrayList<Float>> curve = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < curveApproxLines.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				coords.add((curveApproxLines.get(i).get(j).get(0) * (float) scale));
				coords.add((curveApproxLines.get(i).get(j).get(1) * (float) scale));
				curve.add(coords);
			}
			curvesHolder1.add(curve);
		}
		
		for(int i = 0; i < lines.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			coords.add(((float) lines.get(i).get(0) * (float) scale));
			coords.add(((float) lines.get(i).get(1) * (float) scale));
			coords.add(((float) lines.get(i).get(2) * (float) scale));
			coords.add(((float) lines.get(i).get(3) * (float) scale));
			linesHolder1.add(coords);
		}
		
		for(int i = 0; i < curvesHolder1.size(); i++) {
			ArrayList<ArrayList<Float>> curve = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < curvesHolder1.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				float x = curvesHolder1.get(i).get(j).get(0);
				float y = curvesHolder1.get(i).get(j).get(1);
				
				coords.add((float) (x * Math.cos(Math.toRadians(rotation)) - y * Math.sin(Math.toRadians(rotation))));
				coords.add((float) (x * Math.sin(Math.toRadians(rotation)) + y * Math.cos(Math.toRadians(rotation))));
				
				curve.add(coords);
			}
			curvesHolder2.add(curve);
		}
		
		
		curvesHolder1 = new ArrayList<ArrayList<ArrayList<Float>>>();
		curvesHolder1.addAll(curvesHolder2);
		curvesHolder2 = new ArrayList<ArrayList<ArrayList<Float>>>();
		
		for(int i = 0; i < linesHolder1.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			float x1 = linesHolder1.get(i).get(0);
			float y1 = linesHolder1.get(i).get(1);
			float x2 = linesHolder1.get(i).get(2);
			float y2 = linesHolder1.get(i).get(3);
			
			coords.add((float) (x1 * Math.cos(Math.toRadians(rotation)) - y1 * Math.sin(Math.toRadians(rotation))));
			coords.add((float) (x1 * Math.sin(Math.toRadians(rotation)) + y1 * Math.cos(Math.toRadians(rotation))));
			coords.add((float) (x2 * Math.cos(Math.toRadians(rotation)) - y2 * Math.sin(Math.toRadians(rotation))));
			coords.add((float) (x2 * Math.sin(Math.toRadians(rotation)) + y2 * Math.cos(Math.toRadians(rotation))));
			
			linesHolder2.add(coords);
		}
		
		linesHolder1 = new ArrayList<ArrayList<Float>>();
		linesHolder1.addAll(linesHolder2);
		linesHolder2 = new ArrayList<ArrayList<Float>>();
		
		for(int i = 0; i < curvesHolder1.size(); i++) {
			ArrayList<ArrayList<Float>> curve = new ArrayList<ArrayList<Float>>();
			
			for(int j = 0; j < curvesHolder1.get(i).size(); j++) {
				ArrayList<Float> coords = new ArrayList<Float>();
				
				coords.add(curvesHolder1.get(i).get(j).get(0) + translateX);
				coords.add(curvesHolder1.get(i).get(j).get(1) + translateY);
				
				curve.add(coords);
			}
			curvesHolder2.add(curve);
		}
		
		curvesHolder1 = new ArrayList<ArrayList<ArrayList<Float>>>();
		curvesHolder1.addAll(curvesHolder2);
		curvesHolder2 = new ArrayList<ArrayList<ArrayList<Float>>>();
		
		for(int i = 0; i < linesHolder1.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			
			coords.add(linesHolder1.get(i).get(0) + translateX);
			coords.add(linesHolder1.get(i).get(1) + translateY);
			coords.add(linesHolder1.get(i).get(2) + translateX);
			coords.add(linesHolder1.get(i).get(3) + translateY);
			
			linesHolder2.add(coords);
		}
		
		linesHolder1 = new ArrayList<ArrayList<Float>>();
		linesHolder1.addAll(linesHolder2);
		linesHolder2 = new ArrayList<ArrayList<Float>>();
		
		curveApproxLines = new ArrayList<ArrayList<ArrayList<Float>>>();
		processedLines = new ArrayList<ArrayList<Float>>();
		
		curveApproxLines.addAll(curvesHolder1);
		processedLines.addAll(linesHolder1);
	}
	
	public static int clipVal(float x, float y) {
		int code = 0;
		
		if(x < WWLowX) code += 1;
		if(x > WWHighX) code += 2;
		if(y < WWLowY) code += 4;
		if(y > WWHighY) code += 8;
		
		return code;
	}
	
	public static void clipLines() {
		for(int i = 0; i < processedLines.size(); i++) {
			for(int j = 0; j < processedLines.get(i).size(); j++) {
				float xStart = processedLines.get(i).get(0);
				float yStart = processedLines.get(i).get(1);
				float xEnd = processedLines.get(i).get(2);
				float yEnd = processedLines.get(i).get(3);
				
				int val1 = clipVal(xStart, yStart);
				int val2 = clipVal(xEnd, yEnd);
				
				boolean inside = false, stop = false;
				
				while(!stop) {
					if((val1 | val2) == 0) {
						inside = true;
						stop = true;
					} else if((val1 & val2) != 0) {
						stop = true;
					} else {
						float x = 0.0f, y = 0.0f;
						int outside;
						
						if(val1 >= 1) outside = val1;
						else outside = val2;
						
						if((outside & 1) > 0) {
							x = WWLowX;
							y = yStart + (yEnd - yStart) * (WWLowX - xStart) / (xEnd - xStart);
						} else if((outside & 2) > 0) {
							x = WWHighX;
							y = yStart + (yEnd - yStart) * (WWHighX - xStart) / (xEnd - xStart);
						} else if((outside & 4) > 0) {
							x = xStart + (xEnd - xStart) * (WWLowY - yStart) / (yEnd - yStart);
							y = WWLowY;
						} else if((outside & 8) > 0) {
							x = xStart + (xEnd - xStart) * (WWHighY - yStart) / (yEnd - yStart);
							y = WWHighY;
						}
						
						if(outside == val1) {
							xStart = x;
							yStart = y;
							val1 = clipVal(xStart, yStart);
						} else {
							xEnd = x;
							yEnd = y;
							val2 = clipVal(xEnd, yEnd);
						}
					}
				}
				
				if(inside) {
					ArrayList<Float> coords = new ArrayList<Float>();
					coords.add(xStart);
					coords.add(yStart);
					coords.add(xEnd);
					coords.add(yEnd);
					
					drawLines.add(coords);
				}
			}
		}
		
		clipCurves();
	}
	
	public static void clipCurves() {
		for(int i = 0; i < curveApproxLines.size(); i++) {
			for(int j = 0; j < curveApproxLines.get(i).size() - 1; j++) {
				float xStart = curveApproxLines.get(i).get(j).get(0);
				float yStart = curveApproxLines.get(i).get(j).get(1);
				float xEnd = curveApproxLines.get(i).get(j + 1).get(0);
				float yEnd = curveApproxLines.get(i).get(j + 1).get(1);
				
				int val1 = clipVal(xStart, yStart);
				int val2 = clipVal(xEnd, yEnd);
				
				boolean inside = false;
				
				while(true) {
					if((val1 | val2) == 0) {
						inside = true;
						break;
					} else if((val1 & val2) != 0) {
						break;
					} else {
						float x = 0.0f, y = 0.0f;
						int outside;
						
						if(val1 > 0) outside = val1;
						else outside = val2;
						
						if((outside & 1) > 0) {
							x = WWLowX;
							y = yStart + (yEnd - yStart) * (WWLowX - xStart) / (xEnd - xStart);
						} else if((outside & 2) > 0) {
							x = WWHighX;
							y = yStart + (yEnd - yStart) * (WWHighX - xStart) / (xEnd - xStart);
						} else if((outside & 4) > 0) {
							x = xStart + (xEnd - xStart) * (WWLowY - yStart) / (yEnd - yStart);
							y = WWLowY;
						} else if((outside & 8) > 0) {
							x = xStart + (xEnd - xStart) * (WWHighY - yStart) / (yEnd - yStart);
							y = WWHighY;
						}
						
						if(outside == val1) {
							xStart = x;
							yStart = y;
							val1 = clipVal(xStart, yStart);
						} else {
							xEnd = x;
							yEnd = y;
							val2 = clipVal(xEnd, yEnd);
						}
					}
				}
				
				if(inside) {
					ArrayList<Float> coords = new ArrayList<Float>();
					coords.add(xStart);
					coords.add(yStart);
					coords.add(xEnd);
					coords.add(yEnd);

					drawLines.add(coords);
				}
			}
		}
	}
	
	public static void scaleToView() {
		ArrayList<ArrayList<Float>> linesHolder = new ArrayList<ArrayList<Float>>();
		for(int i = 0; i < drawLines.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			
			coords.add(drawLines.get(i).get(0) - WWLowX);
			coords.add(drawLines.get(i).get(1) - WWLowY);
			coords.add(drawLines.get(i).get(2) - WWLowX);
			coords.add(drawLines.get(i).get(3) - WWLowY);
			
			linesHolder.add(coords);
		}
		
		drawLines = new ArrayList<ArrayList<Float>>();
		drawLines.addAll(linesHolder);
		linesHolder = new ArrayList<ArrayList<Float>>();
		
		for(int i = 0; i < drawLines.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			coords.add(drawLines.get(i).get(0) * ((float) (VPHighX - VPLowX) / (float) (WWHighX - WWLowX)));
			coords.add(drawLines.get(i).get(1) * ((float) (VPHighY - VPLowY) / (float) (WWHighY - WWLowY)));
			coords.add(drawLines.get(i).get(2) * ((float) (VPHighX - VPLowX) / (float) (WWHighX - WWLowX)));
			coords.add(drawLines.get(i).get(3) * ((float) (VPHighY - VPLowY) / (float) (WWHighY - WWLowY)));
			
			linesHolder.add(coords);
		}
		
		drawLines = new ArrayList<ArrayList<Float>>();
		drawLines.addAll(linesHolder);
		linesHolder = new ArrayList<ArrayList<Float>>();
		
		for(int i = 0; i < drawLines.size(); i++) {
			ArrayList<Float> coords = new ArrayList<Float>();
			
			coords.add(drawLines.get(i).get(0) + VPLowX);
			coords.add(drawLines.get(i).get(1) + VPLowY);
			coords.add(drawLines.get(i).get(2) + VPLowX);
			coords.add(drawLines.get(i).get(3) + VPLowY);
			
			linesHolder.add(coords);
		}

		drawLines = new ArrayList<ArrayList<Float>>();
		drawLines.addAll(linesHolder);
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
	
	public static void drawLines() {
		for(int i = 0; i < drawLines.size(); i++) {
			int startX, startY, endX, endY;
			startX = Math.round(drawLines.get(i).get(0));
			startY = Math.round(drawLines.get(i).get(1));
			endX = Math.round(drawLines.get(i).get(2));
			endY = Math.round(drawLines.get(i).get(3));
			drawLine(startX, startY, endX, endY);
		}
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
}

