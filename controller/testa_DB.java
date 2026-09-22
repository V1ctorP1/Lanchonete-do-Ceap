/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;
import static view.menu_GUI.*;

/**
 *
 * @author Lincoln
 */
public class testa_DB {
     public static  void carregaDriver(){
       try { // Conecttando o driver
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            System.out.println("Driver carregado com sucesso!");




       } catch (Exception ex) { // Erro driver não encontrado
            System.out.println("Driver nao pode ser carregado!");
        }

    }
     

}
