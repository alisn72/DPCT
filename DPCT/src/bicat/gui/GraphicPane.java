package bicat.gui;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.ui.Layer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Vector;

/**
 * <p>
 * Title: BicAT Tool
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * 
 * @author D. Frick, A. Prelic
 * @version 1.0
 * 
 * 
 * Expression View tab
 */

public class GraphicPane extends JPanel // extends ChartPanel
		implements ActionListener {

	private static final int MAXIMUM_SIZE = 25;

	/** Hook to the governing <code>BicaGUI</code> object. */
	BicatGui owner;

	/** List of genes that have been selected. */
	Vector geneList;

	/** List of chips that have been selected. */
	Vector chipList;

	/** List of arrays containing expression values for graphs. */
	Vector graphDataList;

	/** Bounding rectangle of area that has been drawn to. */
	Rectangle filledRect;

	/** Step size of values in x (horizontal) direction. */
	int xStep;

	/** Step size of values in y (vertical) direction. */
	int yStep;

	/** Array of preset colors to differentiate expression graphs. */
	Color[] colorWheel;

	// public ChartPanel() {}

	static int DEFAULT_X_TABLE = 3;

	// ===========================================================================
	public void visualizeAll(LinkedList list, float[][] data, int x_table) { // ,
		// int
		// y_table)
		// {

		// create table view of the GraphicPane
		// for all (B)Cs, visualize it in the next window ... (SIMPLE graphik,
		// ohne legends)

		// ....

		JPanel contentPane = new JPanel(new GridLayout(0, x_table));

		try {

			int row_cnt = 0;
			int col_cnt = 0;
			//
			for (int i = 0; i < list.size(); i++) {

				// get (b)c,
				bicat.biclustering.Bicluster bc = (bicat.biclustering.Bicluster) list
						.get(i);
				Vector genes = new Vector();
				Vector bcData = new Vector();
				//
				for (int g = 0; g < bc.getGenes().length; g++) {
					genes.add(new Integer(bc.getGenes()[g]));
					bcData.add(data[bc.getGenes()[g]]);
				}
				Vector chips = new Vector();
				for (int c = 0; c < bc.getChips().length; c++)
					chips.add(new Integer(bc.getChips()[c]));

				// get (corresponding) data
				setGraphDataList(bcData, genes, chips);

				// visualize the graph(ik) in the corresponding cell
				chart = newSimpleGraphic(Double.NaN, false);

				// proba:
				org.jfree.chart.ChartPanel cP = new org.jfree.chart.ChartPanel(
						chart);
				contentPane.add(cP);

				// old: cP.setPreferredSize(new Dimension(100, 10));
				// old: cP.setMaximumDrawHeight(2*xStep);
				// old: cP.setMaximumDrawWidth(6*xStep);

				// ... management stuff:
				col_cnt++;
				if (col_cnt == 3) {
					col_cnt = 0;
					row_cnt++;
				}
			}

			this.removeAll();
			this.add(contentPane);
			this.setVisible(true);
		} catch (Exception ee) {
			ee.printStackTrace();
		}
	}

	// ===========================================================================
	public JFreeChart newSimpleGraphic(
			double discretizationThr, boolean two_sided) {

		// this.remove(chartPanel);

		// System.out.println("GraphicPane.newSimpleGraphik(a,b) ...");

		org.jfree.data.xy.XYSeries[] series = new org.jfree.data.xy.XYSeries[graphDataList
				.size()];
		gProfiles = new org.jfree.data.xy.DefaultTableXYDataset();

		// System.out.println("Created empty gene
		// expressions...."+graphDataList.size());
		for (int j = 0; j < graphDataList.size(); j++) {

			series[j] = new org.jfree.data.xy.XYSeries(
					"Gene Profile " + j + "", true, false);

			float[] expressions = (float[]) graphDataList.get(j);
			for (int i = 0; i < expressions.length; i++)
				series[j].add(i, expressions[i]);

			gProfiles.addSeries(series[j]);
			expressions = null;
		}

		chart = null;
		double[] chips = new double[chipList.size()];
		for (int p = 0; p < chips.length; p++)
			chips[p] = ((Integer) chipList.get(p)).doubleValue();

		chart = myCreateSimpleXYLineChart(null, null, null, gProfiles, chips,
				org.jfree.chart.plot.PlotOrientation.VERTICAL, // HORIZONTAL,
				true, true, false);

		XYPlot xyPlot = (XYPlot) chart
				.getPlot();

		// ADD THE DISCRETIZATION THRESHOLD MARKS
		ValueMarker up_thr_marker = new ValueMarker(
				discretizationThr);
		up_thr_marker.setPaint(Color.RED);
		xyPlot.addRangeMarker(up_thr_marker);
		if (two_sided) {
			ValueMarker down_thr_marker = new ValueMarker(
					0 - discretizationThr);
			down_thr_marker.setPaint(Color.RED);
			xyPlot.addRangeMarker(down_thr_marker);
		}

		// xyPlot.setDomainCrosshairVisible(false);
		// xyPlot.setRangeCrosshairVisible(false);

		/*
		 * xyPlot.zoomHorizontalAxes(2.0); xyPlot.zoomVerticalAxes(2.0);
		 */

		return chart;
	}

	// ===========================================================================
	/**
	 * Default constructor, initializes soem values.
	 * 
	 */
	public GraphicPane() {

		geneList = null;
		graphDataList = null;
		filledRect = new Rectangle(0, 0);
		xStep = 10;
		yStep = 160; // sta ovo tacno znaci??? (size of the cells of the
		// visualized matrix?)

		colorWheel = new Color[8]; // samo 8?
		colorWheel[0] = Color.BLUE;
		colorWheel[1] = Color.CYAN;
		colorWheel[2] = Color.GREEN;
		colorWheel[3] = Color.MAGENTA;
		colorWheel[4] = Color.ORANGE;
		colorWheel[5] = Color.PINK;
		colorWheel[6] = Color.RED;
		colorWheel[7] = Color.YELLOW;

		//

		/*
		 * DefaultPieDataset dpd = new DefaultPieDataset();
		 * dpd.setValue("Category 1",50); dpd.setValue("Category 2",50);
		 */

		// create a dataset...
		/*
		 * DefaultPieDataset data = new DefaultPieDataset();
		 * data.setValue("Java", new Double(43.2)); data.setValue("Visual
		 * Basic", new Double(0.0)); data.setValue("C/C++", new Double(17.5));
		 * 
		 * JFreeChart chart = ChartFactory.createPieChart("Sample",data,
		 * true,true,false); ChartFrame frame = new ChartFrame("See", chart);
		 * frame.pack(); // this.add(frame); //frame.pack();
		 * frame.setVisible(true);
		 */

		/*
		 * chart = ChartFactory.createXYLineChart("Expression Profiles of a
		 * Bicluster","genes","conditions", new
		 * org.jfree.data.xy.DefaultTableXYDataset(),
		 * org.jfree.chart.plot.PlotOrientation.HORIZONTAL, false,false,false);
		 * 
		 * //ChartPanel cp = new ChartPanel(chart_1); chartPanel = new
		 * ChartPanel(chart); this.add(chartPanel); //cp);
		 * //,BorderLayout.WEST); this.setVisible(true);
		 * //this.add("Proba",chart_1); //ChartFrame frame_1 = new
		 * ChartFrame("XY",chart_1); //frame_1.pack();
		 * //frame_1.setVisible(true);
		 * 
		 * 
		 */

	}

	JFreeChart chart;

	org.jfree.chart.ChartPanel chartPanel; // ChartPanel chartPanel;

	// org.jfree.data.xy.XYDataset[] datasets;
	org.jfree.data.xy.DefaultTableXYDataset gProfiles;

	// ===========================================================================
	/**
	 * Hands the governing <code>BicaGUI</code> to this
	 * <code>GraphPane</code.>
	 */
	public void setOwner(BicatGui o) {
		owner = o;
	}

	// ===========================================================================
	/**
	 * For <code>ActionListener</code> interface, could be used to react to
	 * user input.
	 * 
	 */
	public void actionPerformed(ActionEvent e) {

		/*
		 * if (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_25)) {
		 * xStep = 3; yStep = 40; } else if
		 * (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_50)) {
		 * xStep = 5; yStep = 80; } else if
		 * (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_75)) {
		 * xStep = 8; yStep = 120; } else if
		 * (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_100)) {
		 * xStep = 10; yStep = 160; } else if
		 * (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_150)) {
		 * xStep = 15; yStep = 240; } else if
		 * (e.getActionCommand().equals(ActionManager.GRAPHPANE_ZOOM_200)) {
		 * xStep = 30; yStep = 480; }
		 */
		owner.graphScrollPane.repaint();
	}

	// ===========================================================================
	public void updateGraphic() {

		// this.remove(chartPanel);

		org.jfree.data.xy.XYSeries[] series = new org.jfree.data.xy.XYSeries[graphDataList
				.size()]; // = new org.jfree.data.xy.XYSeries("Gene
		// Profile",true,false);
		gProfiles = new org.jfree.data.xy.DefaultTableXYDataset();

		System.out.println("Created empty gene expressions...."
				+ graphDataList.size());
		//
		for (int j = 0; j < graphDataList.size(); j++) {

			if (owner.debug)
				System.out.println("\n\nNew profile: ");
			series[j] = new org.jfree.data.xy.XYSeries(
					"Gene Profile " + j + "", true, false);

			float[] expressions = (float[]) graphDataList.get(j);
			for (int i = 0; i < expressions.length; i++) {
				/*
				 * if(chipList.contains(new Integer(i))) series[j].add(i,
				 * expressions[i] - 1); //-owner.CONTRAST_VALUE); else
				 */
				series[j].add(i, expressions[i]);
				if (owner.debug)
					System.out.print(expressions[i] + ", ");
			}

			gProfiles.addSeries(series[j]);

			// series.clear();
			expressions = null;
		}

		// System.out.println("Added gene profiles");

		chart = null;
		double[] chips = new double[chipList.size()];
		for (int p = 0; p < chips.length; p++)
			chips[p] = ((Integer) chipList.get(p)).doubleValue();
		chart = myCreateXYLineChart("Expression profiles of Biclusters",
				"conditions", "gene expression", gProfiles, chips,
				org.jfree.chart.plot.PlotOrientation.VERTICAL, // HORIZONTAL,
				true, true, false);

		// chart.draw();
		// chart.draw();

		XYPlot xyPlot = (XYPlot) chart
				.getPlot();
		xyPlot.addRangeMarker(new ValueMarker(10.0));
		// chart.plotChanged(null);

		chartPanel = new org.jfree.chart.ChartPanel(chart); // ChartPanel(chart);
		this.removeAll();
		this.add(chartPanel);
		this.setVisible(true);

		owner.graphScrollPane.repaint();
	}

	// ===========================================================================
	public void updateGraphic(double discretizationThr, boolean two_sided) {

		org.jfree.data.xy.XYSeries[] series = new org.jfree.data.xy.XYSeries[graphDataList
				.size()]; // =
		// new
		// org.jfree.data.xy.XYSeries("Gene
		// Profile",true,false);
		gProfiles = new org.jfree.data.xy.DefaultTableXYDataset();

		if (BicatGui.debug) {
			System.out.println("2. Created empty gene expressions...."
					+ graphDataList.size());
		}
		int usedSize = 0;
		if (graphDataList.size() > MAXIMUM_SIZE)
			usedSize = MAXIMUM_SIZE;
		else
			usedSize = graphDataList.size();
		for (int j = 0; j < usedSize; j++) {

			// if(owner.debug) System.out.println("\n\nNew profile: ");
			Object currGene = geneList.get(j);
			Integer currentGeneInt = (Integer) currGene;
			String currentGeneName = BicatGui.currentDataset
					.getGeneName(currentGeneInt.intValue());
			series[j] = new org.jfree.data.xy.XYSeries("Gene "
					+ currentGeneName + "", true, false);

			float[] expressions = (float[]) graphDataList.get(j);
			for (int i = 0; i < expressions.length; i++) {
				/*
				 * if(chipList.contains(new Integer(i))) series[j].add(i,
				 * expressions[i] - 1); //-owner.CONTRAST_VALUE); else
				 */
				series[j].add(i, expressions[i]);
				// if(owner.debug) System.out.print(expressions[i]+", ");
			}

			gProfiles.addSeries(series[j]);

			// series.clear();
			expressions = null;
		}

		chart = null;
		double[] chips = new double[chipList.size()];
		for (int p = 0; p < chips.length; p++)
			chips[p] = ((Integer) chipList.get(p)).doubleValue();
		chart = myCreateXYLineChart("Expression profiles of Bicluster",
				"conditions", "gene expression", gProfiles, chips,
				org.jfree.chart.plot.PlotOrientation.VERTICAL, // HORIZONTAL,
				true, true, false);

		XYPlot xyPlot = (XYPlot) chart
				.getPlot();
		// xyPlot.addRangeMarker(new org.jfree.chart.plot.ValueMarker(10.0));

		// ADD THE DISCRETIZATION THRESHOLD MARKS!
		ValueMarker up_thr_marker = new ValueMarker(
				discretizationThr);
		up_thr_marker.setPaint(Color.RED);
		xyPlot.addRangeMarker(up_thr_marker);

		if (two_sided) {
			ValueMarker down_thr_marker = new ValueMarker(
					0 - discretizationThr);
			down_thr_marker.setPaint(Color.RED);
			xyPlot.addRangeMarker(down_thr_marker);
		}

		// chart.plotChanged(null)

		//owner.graphScrollPane.repaint();

		chartPanel = new org.jfree.chart.ChartPanel(chart); // ChartPanel(chart);
		this.removeAll();
		this.add(chartPanel);
		this.setVisible(true);
		chartPanel.revalidate();
		
		/*
		 * owner.graphViewPane.removeAll();
		 * owner.graphViewPane.add(owner.graphInfoLabel);
		 * owner.graphViewPane.add(this);
		 */
	}

	/**
	 * Creates a line chart (based on an <code>{link XYDataset}</code>) with
	 * default settings.
	 * 
	 * @param title
	 *            the chart title (<code>null</code> permitted).
	 * @param xAxisLabel
	 *            a label for the X-axis (<code>null</code> permitted).
	 * @param yAxisLabel
	 *            a label for the Y-axis (<code>null</code> permitted).
	 * @param dataset
	 *            the dataset for the chart (<code>null</code> permitted).
	 * @param orientation
	 *            the plot orientation (horizontal or vertical) (<code>null</code>
	 *            NOT permitted).
	 * @param legend
	 *            a flag specifying whether or not a legend is required.
	 * @param tooltips
	 *            configure chart to generate tool tips?
	 * @param urls
	 *            configure chart to generate URLs?
	 * 
	 * @return The chart.
	 */
	public static JFreeChart myCreateXYLineChart(String title,
			String xAxisLabel, String yAxisLabel,
			org.jfree.data.xy.XYDataset dataset, double marker,
			org.jfree.chart.plot.PlotOrientation orientation, boolean legend,
			boolean tooltips, boolean urls) {

		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		ValueMarker vm = new ValueMarker(marker);
		vm.setPaint(Color.GRAY);
		// plot.addRangeMarker(vm, Layer.FOREGROUND); //.setPaint(Color.PINK));
		plot.addDomainMarker(vm, Layer.FOREGROUND);

		plot.setOrientation(orientation);
		if (tooltips) {
			renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
		}
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, legend);

		return chart;

	}

	// this one is used (mit mehrere chips markers to add....)
	public static JFreeChart myCreateXYLineChart(String title,
			String xAxisLabel, String yAxisLabel,
			org.jfree.data.xy.XYDataset dataset, double[] marker,
			org.jfree.chart.plot.PlotOrientation orientation, boolean legend,
			boolean tooltips, boolean urls) {

		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		yAxis.setAutoRangeIncludesZero(false);
		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);
		for (int i = 0; i < marker.length; i++) {
			ValueMarker vm = new ValueMarker(marker[i]);
			vm.setPaint(Color.GRAY.brighter());
			// plot.addRangeMarker(vm, Layer.FOREGROUND);
			// //.setPaint(Color.PINK));
			plot.addDomainMarker(vm, Layer.FOREGROUND);
		}

		plot.setOrientation(orientation);
		if (tooltips) {
			renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
		}
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, legend);

		return chart;

	}

	// this one is used (mit mehrere chips markers to add....)
	public static JFreeChart myCreateSimpleXYLineChart(String title,
			String xAxisLabel, String yAxisLabel,
			org.jfree.data.xy.XYDataset dataset, double[] marker,
			org.jfree.chart.plot.PlotOrientation orientation, boolean legend,
			boolean tooltips, boolean urls) {

		if (orientation == null) {
			throw new IllegalArgumentException("Null 'orientation' argument.");
		}
		NumberAxis xAxis = new NumberAxis(xAxisLabel);
		xAxis.setAutoRangeIncludesZero(false);
		// xAxis.setAxisLineVisible(false);
		NumberAxis yAxis = new NumberAxis(yAxisLabel);
		yAxis.setAutoRangeIncludesZero(false);
		// yAxis.setAxisLineVisible(false); // new?

		// xAxis.setAxisLineVisible(false);
		// yAxis.setAxisLineVisible(false);

		XYItemRenderer renderer = new StandardXYItemRenderer(
				StandardXYItemRenderer.LINES);

		XYPlot plot = new XYPlot(dataset, xAxis, yAxis, renderer);

		/*
		 * for(int i = 0; i < marker.length; i++) { ValueMarker vm = new
		 * ValueMarker(marker[i]); vm.setPaint(Color.GRAY.brighter());
		 * //plot.addRangeMarker(vm, Layer.FOREGROUND);
		 * //.setPaint(Color.PINK)); plot.addDomainMarker(vm, Layer.FOREGROUND); }
		 */

		plot.setOrientation(orientation);
		if (tooltips) {
			renderer.setToolTipGenerator(new StandardXYToolTipGenerator());
		}
		if (urls) {
			renderer.setURLGenerator(new StandardXYURLGenerator());
		}

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, false);

		return chart;

	}

	// ===========================================================================
	/**
	 * Gets the size of the area that has been drawn to. Currently a pointless
	 * placeholder. //?? sta ovo znaci?
	 * 
	 */
	public Dimension getPictureSize() {
		if (0 != graphDataList.size())
			return new Dimension(0, 0);
		else
			return null;
	}

	// ===========================================================================
	/**
	 * Hands a list of selected genes and their respective expression value
	 * arrays to this <code>PicturePane</code>.
	 * 
	 */
	public void setGraphDataList(Vector graphs, Vector genes, Vector chips) {
		graphDataList = new Vector(graphs);
		geneList = new Vector(genes);
		chipList = new Vector(chips);
	}

}
