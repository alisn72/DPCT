//package smadeira.utils;
//
//import com.meterware.httpunit.*;
//
//// c�digo do professor
//
///**
// * <p>Title: </p>
// *
// * <p>Description: </p>
// *
// * <p>Copyright:   Copyright (C) 2007  Joana P. Gon�alves, Sara C. Madeira
// *                 This program is free software; you can redistribute
// *                 it and/or modify it under the terms of the GNU General
// *                 Public License as published by the Free Software
// *                 Foundation; either version 3 of the License, or
// *                 any later version.
// *
// *                 This program is distributed in the hope that it will
// *                 be useful, but WITHOUT ANY WARRANTY; without even the
// *                 implied warranty of MERCHANTABILITY or FITNESS FOR A
// *                 PARTICULAR PURPOSE.  See the
// *                 <a href="http://www.gnu.org/licenses/gpl.html">
// *                 GNU General Public License</a> for more details.
// * </p>
// *
// * @author Arlindo L. Oliveira
// * @version 1.0
// */
//public class PTest {
//
//  //  Accesses Yeastract and converts list of ORF names to gene names
//
//  static String convertToGeneNames(String listOfOrfs[]) {
//    String result = new String();
//    try {
//      System.out.println("Converting ORF names to gene names");
//
//      // create the conversation object which will maintain state for us
//      WebConversation wc = new WebConversation();
//
//      WebRequest request = new GetMethodWebRequest(
//          "http://www.yeastract.com/formorftogene.php");
//
//      System.out.print("REQUEST SUCCEEDED");
//
//      WebResponse response = wc.getResponse(request);
//
//      System.out.print("RESPONSE SUCCEEDED");
//
//      String list = new String("");
//      for (int i = 0; i < listOfOrfs.length; i++) {
//        list = list + "\n" + listOfOrfs[i];
//        System.out.print(list);
//      }
//
//      WebForm form = response.getForms()[1];
//
//      form.setParameter("orfs", list);
//      form.submit();
//
//      response = wc.getCurrentPage();
//
//      WebTable table = response.getFirstMatchingTable(WebTable.
//          MATCH_FIRST_NONBLANK_CELL, "ORF Name");
//
//      result = table.getCellAsText(1, 1);
//    }
//    catch (Exception e) {
//      System.err.println("Problem inside converToGeneNames: " + e);
//    }
//    return (result);
//
//  }
//
//  // Access GOToolBox
//  //
//  // Second argument should be null or the file code for a file stored in the GOToolBox
//  // If null, the genome will be used as a reference
//  //
//  // Third argument can be "none", "bonferroni", "fdr" ou outros. Ver source da terceira p�gina no GOToolBox
//
//  static WebTable getTableFromGoToolBox(String listOfGenes, String userFile,
//                                        String correction) {
//    WebTable resTable = null;
//    try {
//      System.out.println("Acessing GOToolBox");
//      WebConversation wc = new WebConversation();
//
//      // First page
//      // Fill in standard values
//
//      /*      WebRequest request = new GetMethodWebRequest(
//                "http://139.124.62.227/GOToolBox/index.php?page=dataset"); // N�O EST� A FUNCIONAR !!!
//            WebResponse response = wc.getResponse(request);
//       */
//      WebRequest request = new GetMethodWebRequest(
//          "http://crfb.univ-mrs.fr/GOToolBox/index.php?page=dataset"); // P�GINA ALTERADA !
//      WebResponse response = wc.getResponse(request);
//
//      WebForm form = response.getForms()[0];
//
//      // Fill in species and list of genes
//      form.setParameter("specie", "sc");
//      form.setParameter("list", listOfGenes);
//
//      // Fill in reference entries
//      if (userFile == null) {
//        form.setParameter("ref", "genome");
//      }
//      else {
//        form.setParameter("ref", "code");
//        form.setParameter("ref_code", userFile);
//      }
//
//      // Check all three ontologies
//      String ontos[] = {
//          "BP", "MF", "CC"};
//      form.setParameter("onto", ontos);
//
//      form.submit();
//
//      // Second page
//      // Follow link to GO-Stats
//      response = wc.getCurrentPage();
//
//      WebLink allLinks[] = response.getLinks();
//
//      WebLink link = response.getLinkWith("GO-Stats");
//
//      link.click();
//      response = wc.getCurrentPage();
//
//      // Third page
//      // Fill in dataset code and submit form
//      // Somehow, following directly the right link doesn't work
//      // so I had to create this work around.
//
//      form = response.getForms()[0];
//      form.setParameter("i_code", allLinks[6].asText());
//      //Value must be one of: { none, bonferroni, holm, hochberg, hommel, fdr }
//      form.setParameter("mt", correction);
//
//      form.submit();
//
//      // Fourth page
//      // Get the table and return it
//
//      response = wc.getCurrentPage();
//
//      resTable = response.getTables()[1];
//
//      return (resTable);
//
//    }
//    catch (Exception e) {
//      System.err.println("Problem inside getTableFromGoToolBox: " + e);
//    }
//    return (resTable);
//  }
//
//  public static void main(String[] params) {
//    try {
//
//      String listOfOrfs[] = {
//          "FMP51", "??", "TSC10", "YDR011W", "LEU3", "YLR102C", "GIM3",
//          "YPL088W", "ADH4", "YBL005W"};
//
//      String listOfGenes;
//
//      listOfGenes = convertToGeneNames(listOfOrfs);
//
//      System.out.println("Converted list of genes \n" + listOfGenes);
//
//      WebTable resultTable = getTableFromGoToolBox(listOfGenes, null, "none");
//      // Exemplo com uma tabela de refer�ncia e correc��o de Bonferroni
//      // WebTable resultTable = getTableFromGoToolBox(listOfGenes,"11063324May2005.REF","bonferroni");
//
//      for (int i = 0; i <= 10; i++) {
//        System.out.println(resultTable.getCellAsText(i, 2) + " \"" +
//                           resultTable.getCellAsText(i, 3) + "\" " +
//                           resultTable.getCellAsText(i, 6) + " " +
//                           resultTable.getCellAsText(i, 8));
//      }
//
//    }
//    catch (Exception e) {
//      System.err.println("Exception: " + e);
//    }
//  }
//}
