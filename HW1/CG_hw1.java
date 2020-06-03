import java.io.*;
import java.util.Scanner;

public class CG_hw1 {
	public static String[] fileLines = new String[8];
	public static String[] pixelMap;

	public static void main(String[] args) throws Exception {
		String file = "hw1.ps";
		double scale = 1.0;
		int rotation = 0, translateX = 0, translateY = 0;
		int lowerX = 0, lowerY = 0, upperX = 499, upperY = 499;
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
		
		String filePath = new File(".").getAbsolutePath();
		filePath += "/" + file;
		File inputFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		interpretInput(br, scale, rotation, translateX, translateY, lowerX, lowerY, upperX, upperY);
		
		System.out.println(generateXPM());
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
		String fileLine = "";
		boolean parseLines = false;
		while ((fileLine = br.readLine()) != null) {
			if(fileLine.equals("%%%BEGIN")) parseLines = true;
			else if(parseLines && !fileLine.equals("%%%END")) {
				Scanner parseLine = new Scanner(fileLine);
				
				int coords[] = new int[4];
				for(int i = 0; i < 4; i++) {
					coords[i] = parseLine.nextInt();
				}
				String type = parseLine.next();
				
				if(type.equals("Line")) drawLine(coords[0], coords[1], coords[2], coords[3], scale, rotation, translateX, translateY, lowerXBound, lowerYBound, upperXBound, upperYBound);
			}
			else parseLines = false;
		}
	}
	
	public static void drawLine(int startX, int startY, int endX, int endY, double scale, int rotation, 
			int translateX, int translateY, int lowerXBound, int lowerYBound, int upperXBound, int upperYBound) {
		int transformStartX, transformStartY, transformEndX, transformEndY;
		transformStartX = startX + translateX;
		transformStartY = startY + translateY;
		double finalStartX = (double) transformStartX * scale;
		double finalStartY = (double) transformStartY * scale;
		double rads = ((double) rotation * Math.PI) / (double) 180;
		double tempX = finalStartX;
		finalStartX = (finalStartX * Math.cos(rads)) - (finalStartY * Math.sin(rads));
		finalStartY = (tempX * Math.sin(rads)) + (finalStartY * Math.cos(rads));
		
		transformEndX = endX + translateX;
		transformEndY = endY + translateY;
		double finalEndX = (double) transformEndX * scale;
		double finalEndY = (double) transformEndY * scale;
		double radsEnd = ((double) rotation * Math.PI) / (double) 180;
		double tempXEnd = finalEndX;
		finalEndX = (finalEndX * Math.cos(radsEnd)) - (finalEndY * Math.sin(radsEnd));
		finalEndY = (tempXEnd * Math.sin(radsEnd)) + (finalEndY * Math.cos(radsEnd));
		
		int dx, dy, D, sx, sy, ex, ey;
		sx = (int)Math.round(finalStartX);
		sy = (int)Math.round(finalStartY);
		ex = (int)Math.round(finalEndX);
		ey = (int)Math.round(finalEndY);
		
		int drawStartX = 0, drawStartY = 0, drawEndX = 0, drawEndY = 0;
		boolean inFrame = true;
		if(sx >= lowerXBound && sx <= upperXBound &&
				sy >= lowerYBound && sy <= upperYBound &&
				ex >= lowerXBound && ex <= upperXBound &&
				ey >= lowerYBound && ey <= upperYBound) {
			drawStartX = sx;
			drawStartY = sy;
			drawEndX = ex;
			drawEndY = ey;
		} else if((sx < lowerXBound && ex < lowerXBound) ||
				(sy < lowerYBound && ey < lowerYBound) ||
				(sx > upperXBound && ex > upperXBound) ||
				(sy > upperYBound && ey > upperYBound)) {
			inFrame = false;
		} else {
			boolean done = false;
			while(!done) {
				boolean xAboveUp = sx > upperXBound, yAboveUp = sy > upperYBound, 
						xBelowLow = sx < lowerXBound, yBelowLow = sy < lowerYBound;
				boolean exAboveUp = ex > upperXBound, eyAboveUp = ey > upperYBound, 
						exBelowLow = ex < lowerXBound, eyBelowLow = ey < lowerYBound;
				if ((xAboveUp && exAboveUp) || (xBelowLow && exBelowLow) ||
						(yAboveUp && eyAboveUp) || (yBelowLow && eyBelowLow)) {
					inFrame = false;
					done = true;
				}
				
				if(xAboveUp) {
					//right
					sy = sy + (ey - sy) * (upperXBound - sx) / (ex - sx);
					sx = upperXBound;
				} else if(yAboveUp) {
					//above
					sx = sx + (ex - sx) * (upperYBound - sy) / (ey - sy);
					sy = upperYBound;
				} else if(xBelowLow) {
					//left
					sy = sy + (ey - sy) * (lowerXBound - sx) / (ex - sx);
					sx = lowerXBound;
				} else if(yBelowLow) {
					//below
					sx = sx + (ex - sx) * (lowerYBound - sy) / (ey - sy);
					sy = lowerYBound;
				} else done = true;
			}
			
			done = false;
			while(!done) {
				boolean xAboveUp = ex > upperXBound, yAboveUp = ey > upperYBound, 
						xBelowLow = ex < lowerXBound, yBelowLow = ey < lowerYBound;
				boolean exAboveUp = ex > upperXBound, eyAboveUp = ey > upperYBound, 
						exBelowLow = ex < lowerXBound, eyBelowLow = ey < lowerYBound;
				if ((xAboveUp && exAboveUp) || (xBelowLow && exBelowLow) ||
						(yAboveUp && eyAboveUp) || (yBelowLow && eyBelowLow)) {
					inFrame = false;
					done = true;
				}
				if(xAboveUp) {
					//right
					ey = ey + (sy - ey) * (upperXBound - ex) / (sx - ex);
					ex = upperXBound;
				} else if(yAboveUp) {
					//above
					ex = ex + (sx - ex) * (upperYBound - ey) / (sy - ey);
					ey = upperYBound;
				} else if(xBelowLow) {
					//left
					ey = ey + (sy - ey) * (lowerXBound - ex) / (sx - ex);
					ex = lowerXBound;
				} else if(yBelowLow) {
					//below
					ex = ex + (sx - ex) * (lowerYBound - ey) / (sy - ey);
					ey = lowerYBound;
				} else done = true;
			}
			
			drawStartX = sx;
			drawStartY = sy;
			drawEndX = ex;
			drawEndY = ey;
		}
		
		if(inFrame) {
			if(drawStartX > drawEndX) {
				int tempSX = drawStartX, tempSY = drawStartY;
				drawStartX = drawEndX;
				drawStartY = drawEndY;
				drawEndX = tempSX;
				drawEndY = tempSY;
			}
			System.out.println("Drawing Line (" + drawStartX + "," + drawStartY + ") to (" + drawEndX + ", " + drawEndY + ")");
			dx = (int)(drawEndX - drawStartX);
			dy = (int)(drawEndY - drawStartY);
			double m = (double) dy / (double) dx;
			if(m > -1 && m <= 1) {
				if(drawStartY > drawEndY) D = 2*dx - dy;
				else D = (2 * dy) - dx;
				if(dx != 0) {
					int y = drawStartY;
					for(int x = drawStartX; x <= drawEndX; x++) {
						writePixel(x, y);
						if(D <= 0) {
							if(drawStartY >= drawEndY) {
								D -= 2*dy;
							} else {
								D += 2*dy;
							}
							
						}
						else {
							if(drawStartY >= drawEndY) D -= 2*(dx - dy);
							else {
								D -= 2 * (dy - dx);
							}
							if(drawStartY >= drawEndY) {
								y--;
							}
							else {
								y++;
							}
						}
					}
				} else {
					if(drawStartY > drawEndY) {
						for (int y = drawStartY; y >= drawEndY; y--) {
							writePixel(drawStartX, y);
						}
					} else {
						for (int y = drawStartY; y <= drawEndY; y++) {
							writePixel(drawStartX, y);
						}
					}
				}
			} else if(m == -1.0) {
				int y = drawStartY;
				for(int x = drawStartX; x <= drawEndX; x++) {
					writePixel(x,y);
					y--;
				}
			} else if((float)m == Float.NEGATIVE_INFINITY) {
				for(int y = drawStartY; y >= drawEndY; y--) {
					writePixel(drawStartX, y);
				}
			} else {
				if(drawStartX > drawEndX) D = 2*dy - dx;
				else D = (2 * dx) - dy;
				if(dy != 0) {
					int x = drawStartX;
					for(int y = drawStartY; y <= drawEndY; y++) {
						writePixel(x, y);
						if(D <= 0) {
							if(drawStartX >= drawEndX) {
								D -= 2*dx;
							} else {
								D += 2*dx;
							}
							
						}
						else {
							if(drawStartX >= drawEndX) D += 2*(dy - dx);
							else {
								D += 2 * (dx - dy);
							}
							if(drawStartX >= drawEndX) {
								x--;
							}
							else {
								x++;
							}
						}
					}
				} else {
					if(drawStartX >= drawEndX) {
						for (int x = drawStartX; x >= drawEndX; x--) {
							writePixel(x, drawStartY);
						}
					} else {
						for (int x = drawStartX; x <= drawEndX; x++) {
							writePixel(x, drawStartY);
						}
					}
				}
			}
		}
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
	
	public static String generateXPM() {
		String file = "";
		for(int i = 0; i < 7; i++) {
				file += fileLines[i] + "\n";
		}
		for(int i = pixelMap.length - 1; i >= 0; i--) {
				file += "\"" + pixelMap[i] + "\",\n";
		}
		file += fileLines[7] + "\n";
		return file;
	}
}