package graph.program;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Save {
	
	private File file;
	private String defaultLoc;
	private FileWriter fileW;
	private Scanner sc;
	private int currentIndex;
	private int currentRow;
	
	/**
	 * Sets defaultLoc to the given String.
	 * @param defaultLoc
	 */
	public void setDefaultLoc(String defaultLoc){
		this.defaultLoc=defaultLoc;
	}
	
	/**
	 * Returns the default location.
	 * @return defaultLoc
	 */
	public String getDefaultLoc(){
		return defaultLoc;
	}
	
	/**
	 * Returns the index at which the Scanner is looking.
	 * @return file
	 */
	public int getCurrentIndex(){
		if(sc!=null){
			return currentIndex;
		}else{
			throw new RuntimeException("No scanner exists!");
		}
	}
	
	/**
	 * Returns the row at which the Scanner is looking.
	 * @return file
	 */
	public int getCurrentRow(){
		if(sc!=null){
			return currentRow;
		}else{
			throw new RuntimeException("No scanner exists!");
		}
	}
	
	/**
	 * Sets the index at which the Scanner is looking.
	 * @return file
	 */
	public void setCurrentIndex(int currentIndex){
		this.currentIndex=currentIndex;
	}
	
	/**
	 * Sets the row at which the Scanner is looking.
	 * @return file
	 */
	public void setCurrentRow(int currentRow){
		this.currentRow=currentRow;
	}
	
	/**
	 * Returns the file.
	 * @return file
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * Returns the used Scanner.
	 * If the openFile()-method haven't been called yet, this method will return null.
	 * @return sc
	 */
	public Scanner getScanner(){
		return sc;
	}
	
	/**
	 * Returns the used Formatter.
	 * If the createFile()-method haven't been called yet, this method will return null.
	 * @return fmat
	 */
	public FileWriter getFileWriter(){
		return fileW;
	}
	
	
	/**
	 * Creates a new Save object.
	 * @param defaultLoc
	 */
	public Save(String defaultLoc){
		setDefaultLoc(defaultLoc);
		file = new File(defaultLoc);
		setCurrentIndex(0);
		setCurrentRow(0);
	}
	
	/**
	 * Creates the file at the default location. Doesn't overwrite file.
	 */
	public void createFile(){
		if(!file.exists()){
			try{
				fileW = new FileWriter(file);
				closeFileWriter();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException("Couldn't create file!");
			}
		}
	}
	
	/**
	 * Empties the file.
	 */
	public void emptyFile(){
		if(file.exists()){
			try{
				fileW = new FileWriter(file);
				closeFileWriter();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException("Couldn't empty file!");
			}
		}
	}
	
	/**
	 * Deletes the file.
	 */
	public void deleteFile(){
		if(file.exists()){
			try{
				file.delete();
			}catch(Exception e){
				e.printStackTrace();
				throw new RuntimeException("Couldn't delete file!");
			}
		}
	}
	
	/**
	 * Opens a stream to write into the file.
	 */
	public void openFileWriter(){
		try{
			try{
				openScanner();
				String s=read();
				reset();
				closeScanner();
				fileW = new FileWriter(file);
				write("",s);
			}catch(IOException ioe){
				ioe.printStackTrace();
				throw new RuntimeException("Couldn't rewrite file!");
			}catch(Exception e){
				fileW = new FileWriter(file);
			}
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException("Couldn't open FileWriter!");
		}
	}
	
	/**
	 * Writes the given Strings seperated by the String seperator into the file.
	 * @param obj
	 */
	public void write(String seperator,String... s1){
		if(file.canWrite()){
			String sAll="";
			boolean firstTime=true;;
			for(String s:s1){
				if(firstTime){
					firstTime=false;
					sAll=s;
				}else{
				sAll=sAll+seperator+s;
				}
			}
			try{
				fileW.write(sAll);
			} catch (IOException e) {		
				e.printStackTrace();
			}
		}else{
			throw new RuntimeException("Couldn't write into file! (file is not writeable)");
		}
	}
	
	/**
	 * Opens a stream to read the file.
	 */
	public void openScanner(){
		try{
			sc = new Scanner(file);
			
			setScannerDelimiter("%s");
		}catch(IOException e){
			e.printStackTrace();
			throw new RuntimeException("Couldn't open file!");
		}
	}
	
	/**
	 * Sets the scanner delimiter.
	 * @param pattern
	 */
	public void setScannerDelimiter(String pattern){
		sc.useDelimiter(pattern);
	}
	
	/**
	 * Returns the scanner delimiter.
	 * @return
	 */
	public String getScannerDelimiter(){
		return sc.delimiter().toString();
	}
	
	/**
	 * Sets the scanner back to the start of the file.
	 */
	public void toStart(){
		closeScanner();
		openScanner();
	}
	
	/**
	 * Resets the scanner positions.
	 */
	public void reset(){
		setCurrentIndex(0);
		setCurrentRow(0);
	}
	
	/**
	 * Resets the scanner index position.
	 */
	public void resetIndex(){
		setCurrentIndex(0);
	}
	
	/**
	 * Looks for the pattern in the file.
	 * @param pattern
	 * @return the pattern if found
	 */
	public String lookFor(String pattern){
		String s;
		try{
			s=sc.findInLine(pattern);
			return s;
		}catch(Exception e){
			throw new RuntimeException("Couldn't find "+pattern+" in file!");
		}	
	}
	
	/**
	 * Goes to the give row (relative to the current row).
	 * @param row
	 */
	public void goToRow(int row, boolean indexAtZero){
		if(file.canRead()){
			for(int g=0; g<row;g++){
				try{
					sc.nextLine();
					currentRow++;
				}catch(Exception e){
					throw new RuntimeException("Row doesn't exists!");
				}
			}
			if(indexAtZero){
			resetIndex();
			}
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns a String with all characters found from the character at startIndex to the character at endIndex (included) in the given row.	 
	 * @param startIndex : index of the first character
	 * @param endIndex : index of the last character
	 * @param row : 0 is the current row the scanner is looking at, 1 is the next row
	 * @return 
	 */
	public String read(int startIndex, int endIndex,  int row){
		if(file.canRead()){
			String c="";
			try{
				currentRow+=row;
				c = c+read(startIndex);
				for(int i=startIndex; i<=endIndex; i++){
						c = c+read(1);
				}
				return c;
			}catch(Exception e){
				if(c!=""){
					return c;
				}			
				throw new RuntimeException("Couldn't find characters!");		
			}
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns the character in the file at the given index and row.	 
	 * @param index : 0 is the current character the scanner is looking at, 1 is the next character
	 * @param row : 0 is the current row the scanner is looking at, 1 is the next row
	 * @return 
	 * @throws Exception 
	 */
	public char read(int index, int row){
		if(file.canRead()){
			currentRow+=row;
			
			try{	
				char c = read(index);
				return c;
			}catch(Exception e){
				throw new RuntimeException("Couldn't find character at index "+index+"!");
			}
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns the character in the file at the given index.	 
	 * @param index : 0 is the current character the scanner is looking at, 1 is the next character
	 * @return 
	 */
	public char read(int index){
		if(file.canRead()){
			try{
				
				toStart();
				try{
					int row=currentRow;
					goToRow(row,false);
					setCurrentRow(row);
				}catch(RuntimeException e){
					throw e;
				}
				
				currentIndex+=index;
				char c=readLine(0).charAt(currentIndex);
				return c;
			}catch(Exception e){
				throw new RuntimeException("Couldn't find character at index "+index+"!");
			}
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns a String which includes every String from the file.
	 * @return
	 */
	public String read(){
		if(file.canRead()){
			String s = "";
			if(sc.hasNext()){
				s = sc.next();
				toStart();
			}else{
				throw new RuntimeException("File is empty!");
			}
			return s;
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns the given line.
	 * @return
	 */
	public String readLine(int row){
		if(file.canRead()){
			String s = "";
			goToRow(row,false);
			if(sc.hasNextLine()){
				s = sc.nextLine();
				toStart();
			}else{
				throw new RuntimeException("File is empty!");
			}
			return s;
		}else{
			throw new RuntimeException("Couldn't read file! (file is not readable)");
		}
	}
	
	/**
	 * Returns the next integer
	 * @return
	 */
	public int readInt(int index){
		String i = read(index)+"";
		
		try{
			Integer.parseInt(i);
		}catch(Exception e){
			throw new RuntimeException("No integer found!");
		}
		
		int intTest;
		while(true){
			try{
				intTest = Integer.parseInt(read(1)+"");
				i+=intTest;
			}catch(Exception e){
				break;
			}
		}
		return Integer.parseInt(i);
	}
	
	/**
	 * Returns the next double
	 * @return
	 */
	public double readDouble(int index){
		String i = read(index)+"";
		
		try{
			Integer.parseInt(i);
		}catch(Exception e){
			throw new RuntimeException("No double found!");
		}
		
		int intTest;
		boolean foundDot=false;
		while(true){
			try{
				char chara=read(1);
				if(chara=='.' && !foundDot){
					foundDot=true;
					i+=".";
				}else{
					intTest = Integer.parseInt(chara+"");
					i+=intTest;
				}
			}catch(Exception e){
				break;
			}
		}
		return Double.parseDouble(i);
	}
	
	/**
	 * Closes the Scanner. When done with editing a file, this function should be called.
	 */
	public void closeScanner(){
		sc.close();
	}
	
	/**
	 * Closes the FileWriter. When done with editing a file, this function should be called.
	 */
	public void closeFileWriter(){
		try {
			fileW.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
