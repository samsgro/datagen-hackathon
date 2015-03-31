package com.thomsonreuters.graph;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.graphstream.algorithm.generator.BaseGenerator;
import org.graphstream.algorithm.generator.Generator;
import org.graphstream.algorithm.generator.RandomGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Resolution;
import org.graphstream.stream.file.FileSinkImages.Resolutions;
import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import com.thomsonreuters.graph.generator.TwoWayExclusiveRandomGenerator;

public class RelationshipBuilderMain {

	
	static String ABATTR = "distribution";
	static String IDENTIFIERATTR = "identifier";
	
	@Option(name="-pic",usage="recursively run something")
    private boolean outputPic;
	
	@Option(name="-c",usage="connectedness")
    private double connectedness = 1.2;
	
	@Option(name="-n",usage="repeat <n> cycles")
    private int num = 0;
	
	@Option(name="-setA",usage="filename of set A identifiers")
    private String setAFile;
	
	@Option(name="-setB",usage="filename of set B identifiers")
    private String setBFile;
	
	 	@Argument
	    private List<String> arguments = new ArrayList<String>();

	    public static void main(String[] args) throws IOException {
	        new RelationshipBuilderMain().doMain(args);
	    }
	

	public void doMain(String[] args) throws IOException {
		CmdLineParser parser = new CmdLineParser(this);
        
        // if you have a wider console, you could increase the value;
        // here 80 is also the default
        parser.setUsageWidth(80);
		
		try {
            // parse the arguments.
            parser.parseArgument(args);

            // you can parse additional arguments if you want.
            // parser.parseArgument("more","args");

            // after parsing arguments, you should check
            // if enough arguments are given.
            //if( arguments.isEmpty() )
           //     throw new CmdLineException(parser,"No argument is given");

        } catch( CmdLineException e ) {
            // if there's a problem in the command line,
            // you'll get this exception. this will report
            // an error message.
            System.err.println(e.getMessage());
            System.err.println("java SampleMain [options...] arguments...");
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();


            return;
        }
		


		System.out.println("-c was "+connectedness);
		if(StringUtils.isEmpty(setAFile)) { System.out.println("must provide set A filename of identifiers"); return; }
		if(StringUtils.isEmpty(setBFile)) { System.out.println("must provide set B filename of identifiers"); return; }
		
		Graph graph = new MultiGraph("Random");
		String[] setAIdentifierArray = convertFileArrayToString(setAFile);
		String[] setBIdentifierArray = convertFileArrayToString(setBFile);
		
		if (num == 0) num = (setAIdentifierArray.length + setBIdentifierArray.length) * 2;
		
		System.out.println("-n was "+num);
		BaseGenerator gen = new TwoWayExclusiveRandomGenerator(connectedness, true, false, "distance", false, false, 
				new Double ("0.50"), setAIdentifierArray, setBIdentifierArray);
		gen.addSink(graph);
		gen.setNodeAttributesRange(0, 1);
		gen.begin();
		for(int i=0; i<num; i++)
		    gen.nextEvents();
		gen.end();
		
		
		outputCSVFile(graph, "output.csv");
		if(outputPic) outputScreenshot(graph, "screenshot.png");

	}
	
	
	private void outputScreenshot(Graph graph, String output) throws IOException {

		graph.addAttribute("ui.stylesheet", 
				"edge { size: 1px; shape: line; fill-mode: dyn-plain; fill-color: #E6E6E6; arrow-size: 1px, 1px; z-index: 0;} "
				+ "node { size: 5px; fill-color: #777;text-mode: normal; z-index: 5;} "
				+ "node.isSetB { size: 5px; fill-color:red; z-index: 5;}");
		graph.addAttribute("ui.quality");
		graph.addAttribute("ui.antialias");
		
		OutputType type = OutputType.PNG;
		Resolution resolution = outputResolution.FourK;
		FileSinkImages fsi = new FileSinkImages(type, resolution);
		fsi.setLayoutPolicy(LayoutPolicy.COMPUTED_ONCE_AT_NEW_IMAGE);
		fsi.writeAll(graph, output);
		
	}


	private void outputCSVFile(Graph graph, String output) throws IOException {
		File filename = new File(output);
		
		if (!filename.exists()) {
			filename.createNewFile();
		}
		FileWriter fw = new FileWriter(filename);
		BufferedWriter bw = new BufferedWriter(fw);
		
		for(Edge edge: graph.getEachEdge()) {
			bw.write(edge.getNode0().getAttribute(IDENTIFIERATTR).toString()
					+","
					+edge.getNode1().getAttribute(IDENTIFIERATTR).toString());
			bw.newLine();
			
		}
		bw.flush(); bw.close(); fw.close();
		
	}
	
	public static enum outputResolution implements Resolution {
		FourK(4096,4096)
		;
		
		outputResolution(int width, int height) {
			this.width = width;
			this.height = height;
		}
		
		final int width, height;
		
		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
		
		@Override
		public String toString() {
			return String.format("%s (%dx%d)", name(), width, height);
		}
		
	}


	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private String[] convertFileArrayToString(String filename) throws FileNotFoundException, IOException {
		 	
		String token1 = "";
		@SuppressWarnings("resource")
		BufferedReader in = new BufferedReader(new FileReader(new File(filename)));

		    List<String> temps = new ArrayList<String>();
		    for (String x = in.readLine(); x != null; x = in.readLine()) {
		        temps.add(x);
		      }
		    in.close();

		return temps.toArray(new String[0]);

		
	}
	
	
}
