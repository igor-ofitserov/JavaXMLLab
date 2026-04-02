package babynames;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Task0_Step5_DomReader {

    public static void main(String[] args) {
        System.out.println("--- Завдання №0 (Крок 5): Читання згенерованого XML (DOM) ---\n");

        File xmlFile = new File("data/Filtered_Baby_Names.xml");

        if (!xmlFile.exists()) {
            System.err.println("Помилка: Файл " + xmlFile.getPath() + " не знайдено!");
            return;
        }

        try {
            // Налаштовуємо DOM-парсер
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Завантажуємо XML-документ у пам'ять (будуємо дерево)
            Document document = builder.parse(xmlFile);
            document.getDocumentElement().normalize();

            // Отримуємо кореневий елемент та його атрибут
            Element root = document.getDocumentElement();
            String ethnicity = root.getAttribute("ethnicity");
            System.out.println("Читання даних для етнічної групи: " + ethnicity);
            System.out.println("---------------------------------------------------------");

            // Отримуємо список усіх записів про імена
            NodeList nodeList = document.getElementsByTagName("name_record");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    // Зчитуємо текст із вкладених тегів
                    String name = element.getElementsByTagName("nm").item(0).getTextContent();
                    String gender = element.getElementsByTagName("gndr").item(0).getTextContent();
                    String count = element.getElementsByTagName("cnt").item(0).getTextContent();
                    String rank = element.getElementsByTagName("rnk").item(0).getTextContent();

                    // Форматований вивід на екран
                    System.out.printf("Рейтинг: %-3s | Ім'я: %-10s | Стать: %-6s | Кількість: %s%n",
                            rank, name, gender, count);
                }
            }

            System.out.println("---------------------------------------------------------");
            System.out.println("Крок 5 успішно завершено! Всі завдання виконано.");

        } catch (Exception e) {
            System.err.println("Помилка під час читання XML: " + e.getMessage());
        }
    }
}