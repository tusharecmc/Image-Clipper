import java.io.*;
import javafx.scene.shape.Rectangle;
import java.io.File;
import java.io.IOException;
import java.io.BufferedWriter;

/**
 *
 * @author Tushar
 */
public class getData
{

    /**
     *
     * @param rect
     * @param pFile
     * @param p
     */
    public static void saveToFile(Rectangle rect, File pFile, int p)
	{                     
		double cordX = rect.getX();
		double cordY = rect.getY();
		double height = rect.getHeight();
		double width = rect.getWidth();
		String path = pFile.getPath();
 		
			
		String rslt = convertToString(cordX, cordY, height, width, path,p);
		fileCheck(rslt);


	}

    /**
     *
     * @param cordX
     * @param cordY
     * @param height
     * @param width
     * @param path
     * @param j
     * @return
     */
    public static String convertToString(double cordX, double cordY, double height, double width, String path, int j)
	{
		String cordXAsString = String.valueOf(cordX);
		String cordYAsString = String.valueOf(cordY);
		String heightAsString = String.valueOf(height);
		String widthAsString = String.valueOf(width);
		String obj = "1";
		String result = new StringBuilder(100).append("img").append(j).append(".jpg").append("  ").append(obj).append(" ").append(cordXAsString).append(" ").append(cordYAsString).append(" ").append(height).append(" ").append(width).append("\n").toString();
		//String result= path.concat(" ").concat(obj).concat(" ").concat(cordXAsString).concat(" ").concat(cordYAsString).concat(" ").concat(height).concat(" ").concat(width).concat("\n");

		return result;
	}

    /**
     *
     * @param result
     */
    public static void fileCheck(String result)
	{
		File f = new File("./info.dat");
		if(f.exists())
		{
    		writeFile(result);
		}
		else
		{
		    createFile(result);
		}
	}
 
    /**
     *
     * @param result
     */
    public static void createFile(String result) 
	{
		File file = new File("info.dat");
		try {
			boolean createNewFile = file.createNewFile();
			System.out.println("File Created = "+createNewFile);
			writeFile(result);
		} catch (IOException e) {
                    // TODO Auto-generated catch block

		}
		
	}

    /**
     *
     * @param result
     */
    public static void writeFile(String result)
	{
		try
		{
    		String filename= "info.dat";
    		FileWriter fw =new FileWriter(filename,true); //the true will append the new data
                    //BufferedWriter fw = new BufferedWriter(new Filewriter(filename, true), "UTF-8"));
                    try (BufferedWriter bw = new BufferedWriter(fw)) {
                        //BufferedWriter fw = new BufferedWriter(new Filewriter(filename, true), "UTF-8"));
                        bw.write(result);//appends the string to the file
                    } //appends the string to the file
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
	}
//(new OutputStreamWriter(new FileOutputStream(filename, true), "UTF-8")
}
