package babynames;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class Task0_Step1_StructureAnalyzer {

    public static void main(String[] args) {
        System.out.println("--- Завдання №0 (Крок 1): Аналіз структури та збір тегів (SAX) ---\n");

        // Вказуємо шлях до нашого збереженого файлу
        String filePath = "data/Popular_Baby_Names_NY.xml";
        File xmlFile = new File(filePath);

        if (!xmlFile.exists()) {
            System.err.println("Помилка: Файл не знайдено за шляхом: " + xmlFile.getAbsolutePath());
            return;
        }

        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            // Створюємо наш власний обробник
            StructureHandler handler = new StructureHandler();

            System.out.println("[ЧАСТИНА ДОКУМЕНТА]:");
            // Запускаємо парсинг
            saxParser.parse(xmlFile, handler);

            // Отримуємо зібрані теги
            Set<String> tags = handler.getUniqueTags();

            System.out.println("\n[ПЕРЕЛІК УНІКАЛЬНИХ ТЕГІВ]:");
            for (String tag : tags) {
                System.out.println(" - <" + tag + ">");
            }

            System.out.println("\nКрок 1 успішно завершено!");

        } catch (Exception e) {
            System.err.println("Помилка під час обробки XML: " + e.getMessage());
        }
    }

    // Внутрішній статичний клас для обробки подій SAX
    private static class StructureHandler extends DefaultHandler {
        private Set<String> uniqueTags = new HashSet<>();
        private int recordsPrinted = 0;
        private boolean printEnabled = true;
        private StringBuilder currentText = new StringBuilder();

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            // Додаємо назву тегу до множини унікальних тегів
            uniqueTags.add(qName);

            if (printEnabled) {
                System.out.print("<" + qName);
                for (int i = 0; i < attributes.getLength(); i++) {
                    System.out.print(" " + attributes.getQName(i) + "=\"" + attributes.getValue(i) + "\"");
                }
                System.out.print(">");
            }
            currentText.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            if (printEnabled) {
                String text = new String(ch, start, length).trim();
                if (!text.isEmpty()) {
                    System.out.print(text);
                }
            }
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (printEnabled) {
                System.out.println("</" + qName + ">");
            }

            // Рахуємо закриті теги <row>, щоб вивести лише 4 записи (щоб не засмічувати консоль)
            if (qName.equals("row")) {
                recordsPrinted++;
                if (recordsPrinted >= 4) {
                    printEnabled = false;
                }
            }
        }

        // Метод для отримання множини тегів після завершення парсингу
        public Set<String> getUniqueTags() {
            return uniqueTags;
        }
    }
}