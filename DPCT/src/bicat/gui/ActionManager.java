package bicat.gui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

/**
 * <p>Title: BicAT Tool </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @author Amela Prelic
 * @version 1.0
 *
 **/

public final class ActionManager {

  private HashMap actions = new HashMap();
  private ActionListener listener;
 
  // ===========================================================================
  public ActionManager() { }

  // ===========================================================================
  public ActionListener getListener() { return listener; }

  // ===========================================================================
  public Action getAction(String name) {
    return (Action)actions.get(name);
  }

  // ===========================================================================
  public void forwardAction(ActionEvent ae) {
    listener.actionPerformed(ae);
  }

  // ===========================================================================

  // BicaGUI:
  public static final String MAIN_SET_FILE_OFFSET = "set_file_offset";

  public static final String MAIN_QUIT = "quit";

  public static final String MAIN_LOAD_TWO_INPUT_FILES = "load_data_ctrl";
  public static final String MAIN_LOAD_ONE_INPUT_FILE = "load_data";
  public static final String MAIN_LOAD_BICLUSTERS = "load_bcs";

  public static final String MAIN_SAVE_PREPROCESSED = "save_preprocessed";
  public static final String MAIN_SAVE_ALL_BICLUSTERS = "save_all_bcs";
  public static final String MAIN_SAVE_SELECTED_BICLUSTER = "save_selected_bc";
  public static final String MAIN_SAVE_SEARCH_RESULT_BICLUSTERS = "save_search_bcs";
  public static final String MAIN_EXPORT = "export...";
  public static final String MAIN_EXPORT_ALL_BICLUSTERS = "save_all_bcs_human";
  public static final String MAIN_EXPORT_ALL_SEARCH_RESULT_BICLUSTERS = "save_search_bc_human";

  public static final String MAIN_SET_DISCRETIZATION_THRESHOLD = "set_discretization_threshold";
  public static final String MAIN_SET_LOGARITHM_BASE = "set_logarithm_base";

  public static final String MAIN_PREPROCESS = "preprocess";
  public static final String MAIN_SET_PREPROCESSED = "set_preprocessing";
  public static final String MAIN_SET_EXTENDED = "set_extended";
  public static final String MAIN_COMPUTE_RATIO = "compute_ratio";
  public static final String MAIN_MAKE_OTHER_LOG = "make_log_other";

  public static final String MAIN_NORMALIZATION_STANDARD = "normalization_std";
  public static final String MAIN_NORMALIZATION_MIXTURE_MODEL = "normalization_anja";
  public static final String MAIN_NORMALIZATION_PHZ = "normalization_phz";
  public static final String MAIN_NORMALIZATION_HUBER = "normalization_huber";

  public static final String MAIN_DISCRETIZE_MATRIX = "discretize_matrix";

  public static final String MAIN_RUN_BENDORFRECHRELOADED_BICLUSTERING = "bendorfrechreloaded";

  public static final String MAIN_RUN_BIMAX = "bicluster_bimax";   // changed 22.02.2005
  public static final String MAIN_RUN_CC = "bicluster_cc";         // added 22.02.2005
  public static final String MAIN_RUN_OPSM = "bicluster_opsm";
  public static final String MAIN_RUN_ISA = "bicluster_isa";
  public static final String MAIN_RUN_XMOTIF = "bicluster_xmotif";
  public static final String MAIN_RUN_HCL = "cluster_hcl";
  public static final String MAIN_RUN_KMEANS = "cluster_kmeans";

  public static final String MAIN_RUN_BICLUSTERING_PATTERN = "extended_bicluster";

  public static final String MAIN_SEARCH_BICLUSTERS = "search";
  public static final String MAIN_FILTER_BICLUSTERS = "filter_biclusters";
  public static final String MAIN_EXTEND_BICLUSTERS = "extend_biclusters";
  public static final String MAIN_GET_BICLUSTER_INFORMATION = "info_bc";
  public static final String MAIN_SORT_BICLUSTERS_BY_SIZE = "sort_by_size";
 
  public static final String MAIN_GENE_PAIR_ANALYSIS = "do_gpa_analysis";
  public static final String MAIN_GENE_ANNOTATION_ANALYSIS = "do_annotation_analysis";
  public static final String MAIN_GENE_NETWORK_ANALYSIS = "do_network_analysis";

  public static final String MAIN_INFO_HELP = "info_help";
  public static final String MAIN_INFO_LICENSE = "info_license";
  public static final String MAIN_INFO_ABOUT = "info_about";

  // GraphicPane
  public static final String GRAPHPANE_ZOOM_25 = "zoom_25";
  public static final String GRAPHPANE_ZOOM_50 = "zoom_50";
  public static final String GRAPHPANE_ZOOM_75 = "zoom_75";
  public static final String GRAPHPANE_ZOOM_100 = "zoom_100";
  public static final String GRAPHPANE_ZOOM_150 = "zoom_150";
  public static final String GRAPHPANE_ZOOM_200 = "zoom_200";

  // PicturePane
  public static final String PICTUREPANE_ZOOM_25 = "zoom_25";
  public static final String PICTUREPANE_ZOOM_50 = "zoom_50";
  public static final String PICTUREPANE_ZOOM_75 = "zoom_75";
  public static final String PICTUREPANE_ZOOM_100 = "zoom_100";
  public static final String PICTUREPANE_ZOOM_150 = "zoom_150";
  public static final String PICTUREPANE_ZOOM_200 = "zoom_200";

  public static final String PICTUREPANE_LIMIT_100 = "limit_100";
  public static final String PICTUREPANE_LIMIT_500 = "limit_500";
  public static final String PICTUREPANE_LIMIT_1000 = "limit_1000";
  public static final String PICTUREPANE_LIMIT_5000 = "limit_5000";
  public static final String PICTUREPANE_LIMIT_TOGGLE = "limit_toggle";
  public static final String PICTUREPANE_FIT_2_FRAME = "limit_fit2pp";

 
}