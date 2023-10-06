import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import java.io.*;
import java.util.Scanner;

public class salesD {
    public static void main(String[] args) {
        try {
            // Cargar el documento XML
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File("sales.xml"));
            doc.getDocumentElement().normalize();

            // Solicitar el departamento
            Scanner scanner = new Scanner(System.in);
            System.out.print("Ingrese el departamento: ");
            String departamento = scanner.nextLine().trim();

            // Mostrar el departamento ingresado
            System.out.println("Departamento ingresado: " + departamento);

            // Solicitar el incremento
            System.out.print("Ingrese el porcentaje de incremento (entre 5% y 15%): ");
            double porcentaje = scanner.nextDouble();

            if (porcentaje < 5 || porcentaje > 15) {
                System.out.println("El porcentaje debe estar entre 5% y 15%.");
            } else {
                // XML
                DocumentBuilderFactory newDbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder newDb = newDbf.newDocumentBuilder();
                Document newDoc = newDb.newDocument();

                Element salesDoc = newDoc.createElement("sales_doc");
                newDoc.appendChild(salesDoc);

                // Procesar el documento XML y aplicar el incremento solo a los registros del departamento especificado
                NodeList saleRecords = doc.getElementsByTagName("sale_record");
                boolean departamentoEncontrado = false;
                for (int i = 0; i < saleRecords.getLength(); i++) {
                    Element saleRecord = (Element) saleRecords.item(i);
                    String departamentoActual = saleRecord.getElementsByTagName("department").item(0).getTextContent();
                    if (departamentoActual.equals(departamento)) {
                        departamentoEncontrado = true;
                        Element newSaleRecord = newDoc.createElement("sale_record");

                        // Clonar y agregar los elementos a el nuevo registro
                        newSaleRecord.appendChild(cloneElement(newDoc, saleRecord, "id"));
                        newSaleRecord.appendChild(cloneElement(newDoc, saleRecord, "first_name"));
                        newSaleRecord.appendChild(cloneElement(newDoc, saleRecord, "last_name"));

                        // Aplicar incremento
                        double ventasOriginales = Double.parseDouble(saleRecord.getElementsByTagName("sales").item(0).getTextContent());
                        double ventasIncrementadas = ventasOriginales * (1 + (porcentaje / 100));
                        Element salesElement = newDoc.createElement("sales");
                        salesElement.appendChild(newDoc.createTextNode(String.valueOf(ventasIncrementadas)));
                        newSaleRecord.appendChild(salesElement);
                        newSaleRecord.appendChild(cloneElement(newDoc, saleRecord, "state"));
                        newSaleRecord.appendChild(cloneElement(newDoc, saleRecord, "department"));
                        salesDoc.appendChild(newSaleRecord);
                    }
                }

                if (departamentoEncontrado) {
                    // Guardar el nuevo documento XML con formato e indentación
                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    transformer.setOutputProperty(OutputKeys.INDENT, "yes"); // Configurar la indentación
                    DOMSource source = new DOMSource(newDoc);
                    StreamResult result = new StreamResult(new File("new_sales.xml"));
                    transformer.transform(source, result);

                    System.out.println("Se ha generado el nuevo archivo XML 'new_sales.xml' con las actualizaciones.");
                } else {
                    System.out.println("No se encontraron ventas para el departamento ingresado.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para clonar
    private static Element cloneElement(Document doc, Element element, String tagName) {
        Element newElement = doc.createElement(tagName);
        newElement.appendChild(doc.createTextNode(element.getElementsByTagName(tagName).item(0).getTextContent()));
        return newElement;
    }
}////JDSD

