package babynames;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task0_Step4_FilterAndDomGenerator {

    // Задаємо параметри для пошуку
    private static final String TARGET_ETHNICITY = "ASIAN AND PACIFIC ISLANDER";
    private static final int MAX_RECORDS = 15; // Кількість імен, які ми хочемо вибрати

    public static void main(String[] args) {
        System.out.println("--- Завдання №0 (Крок 4): Фільтрація, Сортування та DOM-генерація ---\n");

        String inputFilePath = "data/Popular_Baby_Names_NY.xml";
        String outputFilePath = "data/Filtered_Baby_Names.xml";
        File inputFile = new File(inputFilePath);

        if (!inputFile.exists()) {
            System.err.println("Помилка: Вхідний файл не знайдено!");
            return;
        }

        try {
            // 1. Читаємо дані за допомогою SAX та створюємо Java-об'єкти
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxFactory.newSAXParser();
            DataFilterHandler handler = new DataFilterHandler();

            System.out.println("Шукаємо топ-" + MAX_RECORDS + " імен для групи: " + TARGET_ETHNICITY + "...");
            saxParser.parse(inputFile, handler);

            List<BabyName> filteredNames = handler.getFilteredNames();

            // 2. Сортуємо список (спрацює метод compareTo з класу BabyName)
            Collections.sort(filteredNames);

            System.out.println("Знайдено та відсортовано записів: " + filteredNames.size() + "\n");

            // 3. Зберігаємо результати у новий XML за допомогою DOM
            generateNewXml(filteredNames, outputFilePath);

        } catch (Exception e) {
            System.err.println("Сталася помилка: " + e.getMessage());
        }
    }

    // Метод для створення нового XML-документа (DOM)
    private static void generateNewXml(List<BabyName> names, String outputFilePath) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // Створюємо кореневий елемент
        Document doc = docBuilder.newDocument();
        Element rootElement = doc.createElement("popular_names");
        rootElement.setAttribute("ethnicity", TARGET_ETHNICITY);
        doc.appendChild(rootElement);

        for (BabyName bn : names) {
            Element nameElement = doc.createElement("name_record");

            Element nm = doc.createElement("nm");
            nm.appendChild(doc.createTextNode(bn.getName()));
            nameElement.appendChild(nm);

            Element gndr = doc.createElement("gndr");
            gndr.appendChild(doc.createTextNode(bn.getGender()));
            nameElement.appendChild(gndr);

            Element cnt = doc.createElement("cnt");
            cnt.appendChild(doc.createTextNode(String.valueOf(bn.getCount())));
            nameElement.appendChild(cnt);

            Element rnk = doc.createElement("rnk");
            rnk.appendChild(doc.createTextNode(String.valueOf(bn.getRank())));
            nameElement.appendChild(rnk);

            rootElement.appendChild(nameElement);
        }

        // Записуємо структуру у файл
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        // Налаштування для красивого форматування (відступи)
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputFilePath));

        transformer.transform(source, result);

        System.out.println("Новий XML-документ успішно згенеровано: " + outputFilePath);
    }

    // Внутрішній клас-обробник для SAX парсера
    private static class DataFilterHandler extends DefaultHandler {
        private List<BabyName> filteredNames = new ArrayList<>();
        private String currentTag = "";

        private String tempNm, tempGndr, tempEthcty, tempCnt, tempRnk;

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            currentTag = qName;
            if (qName.equals("row")) {
                // Очищаємо тимчасові змінні при початку нового запису
                tempNm = tempGndr = tempEthcty = tempCnt = tempRnk = "";
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) {
            String text = new String(ch, start, length).trim();
            if (text.isEmpty()) return;

            switch (currentTag) {
                case "nm": tempNm = text; break;
                case "gndr": tempGndr = text; break;
                case "ethcty": tempEthcty = text; break;
                case "cnt": tempCnt = text; break;
                case "rnk": tempRnk = text; break;
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            currentTag = "";
            if (qName.equals("row")) {
                // Якщо це потрібна етнічна група і ми ще не зібрали потрібну кількість
                if (TARGET_ETHNICITY.equals(tempEthcty) && filteredNames.size() < MAX_RECORDS) {
                    try {
                        int count = Integer.parseInt(tempCnt);
                        int rank = Integer.parseInt(tempRnk);

                        // Перевіряємо, чи немає вже такого імені в списку (щоб уникнути дублікатів з різних років)
                        boolean exists = filteredNames.stream().anyMatch(bn -> bn.getName().equals(tempNm));
                        if (!exists) {
                            filteredNames.add(new BabyName(tempNm, tempGndr, count, rank));
                        }
                    } catch (NumberFormatException ignored) {
                        // Ігноруємо записи з некоректними числами
                    }
                }
            }
        }

        public List<BabyName> getFilteredNames() {
            return filteredNames;
        }
    }
}