package com.example.calc;

import android.annotation.SuppressLint;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Courses {


    public double[] getCourses(String urlString) {
        double eur = 73.0448;
        double usd = 64.5158;
        double jpy = 59.3877;

        try {
            URL url = new URL(urlString);
            // Получение фабрики, чтобы после получить билдер документов.
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Получили из фабрики билдер, который парсит XML, создает структуру Document в виде иерархического дерева.
            DocumentBuilder db = dbf.newDocumentBuilder();
            // Запарсили XML, создав структуру Document. Теперь у нас есть доступ ко всем элементам, каким нам нужно.
            Document doc = db.parse(new InputSource(url.openStream()));
            doc.getDocumentElement().normalize();
            Node root = doc.getDocumentElement();
            NodeList courses = root.getChildNodes();
            for (int i = 0; i < courses.getLength(); i++) {
                Node course = courses.item(i);
                NodeList courseProps = course.getChildNodes();
                Node courseProp = courseProps.item(1);
                if (courseProp.getTextContent().equals("EUR")) {
                    String eurStr = courseProps.item(4).getTextContent();
                    eur = Double.parseDouble(eurStr.replace(',', '.'));
                    //System.out.println("Eur = " + eur);
                } else if (courseProp.getTextContent().equals("USD")) {
                    String usdStr = courseProps.item(4).getTextContent();
                    usd = Double.parseDouble(usdStr.replace(',', '.'));
                    //System.out.println("Usd = " + usd);
                } else if (courseProp.getTextContent().equals("JPY")) {
                    String jpyStr = courseProps.item(4).getTextContent();
                    jpy = Double.parseDouble(jpyStr.replace(',', '.'));
                    //System.out.println("Jpy = " + jpy);
                }
            }

        } catch (ParserConfigurationException ex) {
            Log.d("error",ex.toString());
        } catch (org.xml.sax.SAXException e) {
            Log.d("error",e.toString());
        } catch (IOException e) {
            Log.d("error",e.toString());
        }

        return new double[] {eur, usd, jpy};
    }


}
