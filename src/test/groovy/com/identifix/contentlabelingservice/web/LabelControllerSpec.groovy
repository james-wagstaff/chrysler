package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import spock.lang.Specification

@SpringBootTest
@SuppressWarnings("Println")
class LabelControllerSpec extends Specification {
    @Autowired
    LabelController controller

    def 'Apply labels to PCED spreadsheet functional test' () {
        when:
            int totalDocs = 0
            int totalLabelsFound = 0

            StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
            String originalCSV = new File("src/test/resources/documentTable.csv").text
            originalCSV.split("\\r?\\n").drop(1).each {
                totalDocs++
                Document document = createDocument(it)
                LabelRequest request = new LabelRequest()
                request.publisher = "Ford"
                request.manualType = "PCED" //document.category
                request.title = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[3]
                request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[2]
                ResponseEntity responseEntity = controller.createLabel(request)
                document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"

                if (responseEntity.statusCode == HttpStatus.OK) {
                    totalLabelsFound++
                }

                labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
            }

            println ("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

            File file = new File("src/test/resources/documentTable_finished.csv")
            PrintWriter writer = new PrintWriter(file)
            writer.print("")
            file.append(labelSpreadsheet.toString().bytes)
            file.createNewFile()

        then:
            true
    }

    def 'Apply labels to Workshop spreadsheet functional test' () {
        when:
        int totalDocs = 0
        int totalLabelsFound = 0

        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
        String originalCSV = new File("src/test/resources/documentTable.csv").text
        originalCSV.split("\\r?\\n").drop(1).each {
            totalDocs++
            Document document = createDocument(it)
            LabelRequest request = new LabelRequest()
            request.publisher = "Ford"
            request.manualType = "Workshop" //document.category
            request.title = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[5]
            request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[4]
            ResponseEntity responseEntity = controller.createLabel(request)
            document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"

            if (responseEntity.statusCode == HttpStatus.OK) {
                totalLabelsFound++
            }

            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
        }

        println ("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")

        File file = new File("src/test/resources/documentTable_finished.csv")
        PrintWriter writer = new PrintWriter(file)
        writer.print("")
        file.append(labelSpreadsheet.toString().bytes)
        file.createNewFile()

        then:
        true
    }

    @SuppressWarnings('FactoryMethodName')
    private static Document createDocument(String docValues) {
        String[] docValueArray = docValues.split(",")
        new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4], docValueArray[5])
    }
}
