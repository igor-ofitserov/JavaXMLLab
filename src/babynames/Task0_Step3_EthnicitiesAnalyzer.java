package babynames;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Task0_Step3_EthnicitiesAnalyzer {

    public static void main(String[] args) {
        System.out.println("--- Завдання №0 (Крок 3): Отримання переліку етнічних груп (SAX) ---\n");

        String filePath = "data/Popular_Baby_Names_NY.xml";
        File xmlFile = new File(filePath);

        if (!xmlFile.exists()) {
            System.err.println("Помилка: Файл не знайдено за шляхом: " + xmlFile.getAbsolutePath());
            return;
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            EthnicityHandler handler = new EthnicityHandler();

            System.out.println("Читання документа...\n");
            saxParser.parse(xmlFile, handler);

            Set<String> ethnicities = handler.getEthnicities();

            System.out.println("[ПРЕДСТАВЛЕНІ ЕТНІЧНІ ГРУПИ]:");
            for (String ethnicity : ethnicities) {
                System.out.println(" - " + ethnicity);
            }

            System.out.println("\nКрок 3 успішно завершено!");

        } catch (Exception e) {
            System.err.println("Помилка під час обробки XML: " + e.getMessage());
        }
    }

    private static class EthnicityHandler extends DefaultHandler {
        private Set<String> ethnicities = new HashSet<>();
        private boolean isEthctyTag = false;
        private StringBuilder currentText = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            if (qName.equalsIgnoreCase("ethcty")) {
                isEthctyTag = true;
                currentText.setLength(0); // Очищаємо буфер перед зчитуванням нового тексту
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (isEthctyTag) {
                currentText.append(ch, start, length);
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (qName.equalsIgnoreCase("ethcty")) {
                // Додаємо очищений від пробілів текст до множини
                ethnicities.add(currentText.toString().trim());
                isEthctyTag = false;
            }
        }

        public Set<String> getEthnicities() {
            return ethnicities;
        }
    }
}