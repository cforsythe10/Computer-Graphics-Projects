import java.io.*;
import java.util.*;

public class CG_hw4 {
	private static String[] fileLines = new String[8];
	private static String[] pixelMap = new String[501];
	private static int VPLowX = 0, VPHighX = 500, VPLowY = 0, VPHighY = 500;
	private static float WWLowX, WWHighX, WWLowY, WWHighY;
	private static float PRPX = 0.0f, PRPY = 0.0f, PRPZ = 1.0f;
	private static float VRPX = 0.0f, VRPY = 0.0f, VRPZ = 0.0f;
	private static float VPNX = 0.0f, VPNY = 0.0f, VPNZ = -1.0f;
	private static float VUPX = 0.0f, VUPY = 1.0f, VUPZ = 0.0f;
	private static float uMin = -0.7f, uMax = 0.7f, vMin = -0.7f, vMax = 0.7f;
	private static boolean parallelProjection = false, backfaceCulling = false;
	private static float frontFace = 0.6f, backFace = -0.6f;
	private static ArrayList<ArrayList<Float>> vertices = new ArrayList<ArrayList<Float>>();
	private static ArrayList<ArrayList<Integer>> faces = new ArrayList<ArrayList<Integer>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> drawCoords = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> polygons = new ArrayList<ArrayList<ArrayList<Float>>>();
	private static ArrayList<ArrayList<ArrayList<Float>>> polygonsTempHolder = new ArrayList<ArrayList<ArrayList<Float>>>();
	
	public static void main(String args[]) throws Exception{
		String file = "bound-lo-sphere.smf";
		for(int i = 0; i < args.length; i++) {
			String option = args[i];
			switch(option) {
				case "-f":
					file = args[++i];
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
		fileLines[3] = "\"501 501 2 1\",";
		fileLines[4] = "/* pixels */";
		fileLines[5] = "\"@ c #000000\",";
		fileLines[6] = "\"  c #FFFFFF\",";
		fileLines[7] = "};";
		
		initializeMap();
		
		String filePath = new File(".").getCanonicalPath();
		filePath += "/" + file;
		File inputFile = new File(filePath);
		BufferedReader br = new BufferedReader(new FileReader(inputFile));
		
		interperetInput(br);

		transformVertices();

		if(backfaceCulling) {
			backfaceCulling();
			projectionWithCulling();
		} else {
			project();
			definePolygons();
		}
		clipPolygons();
		scaleToWindow();
		drawLines();
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
	
	public static void interperetInput(BufferedReader br) throws IOException {
		String fileLine = "";
		while((fileLine = br.readLine()) != null) {
			Scanner lineReader = new Scanner(fileLine);
			String type = lineReader.next();
			
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

				ArrayList<Float> temp = new ArrayList<Float>();
				temp.add(vert.get(0) / ZOffset);
				temp.add(vert.get(1) / ZOffset);

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
				if(backfaceCulling) newVertex.add(vert[2][0]);
				
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
	
	public static void printMatrix(float[][] f) {
		for(int i = 0; i < f.length; i++) {
			for(int j = 0; j < f[0].length; j++) {
				System.out.print(f[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
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
	
	public static void drawLines() {
		for(int i = 0; i < drawCoords.size(); i++) {
			for(int j = 0; j < drawCoords.get(i).size() - 1; j++) {
				int startX, startY, endX, endY;
				startX = Math.round(drawCoords.get(i).get(j).get(0));
				startY = Math.round(drawCoords.get(i).get(j).get(1));
				endX = Math.round(drawCoords.get(i).get(j + 1).get(0));
				endY = Math.round(drawCoords.get(i).get(j + 1).get(1));
				drawLine(startX, startY, endX, endY);
			}
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

