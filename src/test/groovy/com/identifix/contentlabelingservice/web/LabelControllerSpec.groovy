package com.identifix.contentlabelingservice.web

import com.identifix.contentlabelingservice.model.LabelRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class LabelControllerSpec extends Specification {
    @Autowired
    LabelController controller

    def 'Create Excel Labels' () {
        boolean test = true
        when:
            StringBuilder baseRulesWithLabels = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
            String originalCSV = new File("src/test/resources/2016F150.csv").text
            Arrays.stream(originalCSV.split("\\r?\\n"))
                    .filter({ value -> !value.equalsIgnoreCase("Label,title,tocpath,category,linkToPage,nuxeoId") })
                    .forEach({ value ->
                        Document document = createDocument(value)
                        LabelRequest request = new LabelRequest()
                        request.setPublisher("Ford")
                        request.setManualType(document.category)
                        request.setTitle(document.title)
                        request.setTocPath(document.tocpath)
                        document.setLabel(controller.createLabel(request).getBody().toString())

                        baseRulesWithLabels.append(document.getLabel())
                        baseRulesWithLabels.append(",")
                        baseRulesWithLabels.append(document.getTitle())
                        baseRulesWithLabels.append(",")
                        baseRulesWithLabels.append(document.getTocpath())
                        baseRulesWithLabels.append(",")
                        baseRulesWithLabels.append(document.getCategory())
                        baseRulesWithLabels.append(",")
                        baseRulesWithLabels.append(document.getLinkToPage())
                        baseRulesWithLabels.append(",")
                        baseRulesWithLabels.append(document.getNuxeoId())
                        baseRulesWithLabels.append("\r\n")
                    })

            File file = new File("src/test/resources/2016F150_finished.csv")
            PrintWriter writer = new PrintWriter(file)
            writer.print("")
            file.append(baseRulesWithLabels.toString().getBytes())
            file.createNewFile()

        then:
        test
    }

    private static Document createDocument(String docValues) {
        String[] docValueArray = docValues.split(",")
        new Document(docValueArray[0],docValueArray[1],docValueArray[2],docValueArray[3],docValueArray[4],docValueArray[5])
    }
}
