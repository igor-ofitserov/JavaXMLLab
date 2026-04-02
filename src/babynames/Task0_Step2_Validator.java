package babynames;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;

public class Task0_Step2_Validator {
    public static void main(String[] args) {
        System.out.println("--- Завдання №0 (Крок 2): Валідація XML за XSD-схемою ---\n");

        File schemaFile = new File("data/schema.xsd");
        File xmlFile = new File("data/fragment.xml");

        if (!schemaFile.exists() || !xmlFile.exists()) {
            System.err.println("Помилка: Не знайдено файл fragment.xml або schema.xsd у папці data!");
            return;
        }

        try {
            // Створюємо фабрику для XSD (стандарт W3C)
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // Завантажуємо нашу схему з правилами
            Schema schema = factory.newSchema(schemaFile);

            // Створюємо валідатор
            Validator validator = schema.newValidator();

            System.out.println("Перевірка файлу: " + xmlFile.getName());
            System.out.println("За правилами схеми: " + schemaFile.getName() + "\n");

            // Виконуємо перевірку
            validator.validate(new StreamSource(xmlFile));

            System.out.println("✅ УСПІХ! Структура XML-документа повністю відповідає XSD-схемі.");

        } catch (Exception e) {
            System.err.println("❌ ПОМИЛКА ВАЛІДАЦІЇ! Документ не відповідає схемі:");
            System.err.println(e.getMessage());
        }
    }
}