/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import model.SharableContentObject;
import org.xml.sax.SAXException;
import utiility.JFolderChooser;
import view.Index;

/**
 *
 * @author hangarita
 */
public class MetadataParser {

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {

                System.out.println("Program Arguments:");
                for (String arg : args) {
                    System.out.println("\t" + arg);
                }

                ArrayList<String> listFilesForFolder = JFolderChooser.listFilesForFolder(new File(args[0]));

                ArrayList<SharableContentObject> arrScos = new ArrayList<>();

                //Creating SCOs
                SharableContentObject scoObjeto = null;
                SharableContentObject scoLeccion = null;

                for (String strNameFolder : listFilesForFolder) {
                    if (strNameFolder.endsWith(".zip")) {
                        try {
                            SharableContentObject scoData = new SharableContentObject(new ZipReader(args[0], strNameFolder));
                            switch (scoData.getStrType()) {
                                case "LECCION":
                                    scoLeccion = scoData;
                                    break;
                                case "OBJETO":
                                    scoObjeto = scoData;
                                    break;
                                case "RECURSO":
                                    arrScos.add(scoData);
                                    break;
                            }
                        } catch (IOException | SAXException | ParserConfigurationException | NullPointerException ex) {
                            JOptionPane.showMessageDialog(null, ex);
                            Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                            System.exit(8);
                        }
                    }
                }

                try {
                    SharableContentObject scoData;
                    scoData = new SharableContentObject(new XMLReader(args[1], ""));
                    switch (scoData.getStrType()) {
                        case "LECCION":
                            scoLeccion = scoData;
                            break;
                        case "OBJETO":
                            scoObjeto = scoData;
                            break;
                        case "RECURSO":
                            arrScos.add(scoData);
                            break;
                    }
                } catch (IOException | SAXException | ParserConfigurationException | NullPointerException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(8);
                }

                try {
                    for (SharableContentObject scoData : arrScos) {
                        scoData.SetRelation(scoObjeto, "Es parte de");
                    }
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(9);
                }

                try {
                    scoObjeto.SetRelation(scoLeccion, "Es parte de");
                } catch (NullPointerException ex) {
                    JOptionPane.showMessageDialog(null, ex);
                    Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(9);
                }
                
                for (SharableContentObject scoData : arrScos) {
                    scoObjeto.SetRelation(scoData, "Está compuesto por");
                }

                for (SharableContentObject scoData : arrScos) {
                    try {
                        scoData.SaveChanges();
                    } catch (IOException | TransformerException ex) {
                        Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                try {
                    scoObjeto.SaveChanges();
                } catch (IOException | TransformerException ex) {
                    Logger.getLogger(MetadataParser.class.getName()).log(Level.SEVERE, null, ex);
                }

                JOptionPane.showMessageDialog(null, "Terminado exitosamente", "Mensaje", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);

            }
        });

    }

}
