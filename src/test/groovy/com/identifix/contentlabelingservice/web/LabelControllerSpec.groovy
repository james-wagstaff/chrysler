//package com.identifix.contentlabelingservice.web
//
//import com.identifix.contentlabelingservice.model.LabelRequest
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.HttpStatus
//import org.springframework.http.ResponseEntity
//import spock.lang.Specification
//
////  NOTE: Copy and paste the results from kraken-client buildCsvFile into documentTable.csv (input file)
//// uncomment the appropriate test method below, run that test, outputs to documentTable_finished.csv
//@SpringBootTest('properties = "spring.cloud.config.enabled=true"')
//@SuppressWarnings("Println")
//class LabelControllerSpec extends Specification {
//    @Autowired
//    LabelController controller
////
////    def 'Apply labels to PCED spreadsheet functional test' () {
////        when:
////            int totalDocs = 0
////            int totalLabelsFound = 0
////
////            StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
////            String originalCSV = new File("src/test/resources/documentTable.csv").text
////            originalCSV.split("\\r?\\n").drop(1).each {
////                totalDocs++
////                Document document = createDocument(it)
////                LabelRequest request = new LabelRequest()
////                request.publisher = "Ford"
////                request.manualType = "PCED" //document.category
////                request.title = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[3]
////                request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[2]
////                ResponseEntity responseEntity = controller.createLabel(request)
////                document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"
////
////                if (responseEntity.statusCode == HttpStatus.OK) {
////                    totalLabelsFound++
////                }
////
////                labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
////            }
////
////            println ("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")
////
////            File file = new File("src/test/resources/documentTable_finished.csv")
////            PrintWriter writer = new PrintWriter(file)
////            writer.print("")
////            file.append(labelSpreadsheet.toString().bytes)
////            file.createNewFile()
////
////        then:
////            true
////    }
////
////    def 'Apply labels to Workshop spreadsheet functional test' () {
////        when:
////        int totalDocs = 0
////        int totalLabelsFound = 0
////
////        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
////        String originalCSV = new File("src/test/resources/documentTable.csv").text
////        originalCSV.split("\\r?\\n").drop(1).each {
////            totalDocs++
////            Document document = createDocument(it)
////            LabelRequest request = new LabelRequest()
////            request.publisher = "Ford"
////            request.manualType = "Workshop" //document.category
////            request.title = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ").last()
////            request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[4]
////            ResponseEntity responseEntity = controller.createLabel(request)
////            document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"
////
////            if (responseEntity.statusCode == HttpStatus.OK) {
////                totalLabelsFound++
////            }
////
////            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
////        }
////
////        println ("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")
////
////        File file = new File("src/test/resources/documentTable_finished.csv")
////        PrintWriter writer = new PrintWriter(file)
////        writer.print("")
////        file.append(labelSpreadsheet.toString().bytes)
////        file.createNewFile()
////
////        then:
////        true
////    }
//    def 'Apply labels to Repair Manual spreadsheet functional test' () {
//        when:
//        int totalDocs = 0
//        int totalLabelsFound = 0
//
//        StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
//        String originalCSV = new File("src/test/resources/documentTable.csv").text
//        originalCSV.split("\\r?\\n").drop(1).each {
//            totalDocs++
//            Document document = createDocument(it)
//            LabelRequest request = new LabelRequest()
//            request.publisher = "Toyota"
//            request.manualType = "Repair Manual" //document.category
//            request.title = document.title
//            print(document.tocpath)
//            try {
//                request.header = document.tocpath.split(" > ")[2]
//            } catch(Exception e) {
//                println e.message
//            }
//            ResponseEntity responseEntity = controller.createLabel(request)
//            document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"
//
//            if (responseEntity.statusCode == HttpStatus.OK) {
//                totalLabelsFound++
//            }
//
//            labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
//        }
//
//        println ("${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled")
//
//        File file = new File("src/test/resources/documentTable_finished.csv")
//        PrintWriter writer = new PrintWriter(file)
//        writer.print("")
//        file.append(labelSpreadsheet.toString().bytes)
//        file.createNewFile()
//
//        then:
//        true
//    }
////
////    def 'Apply labels to Wiring spreadsheet functional test' () {
////        when:
////            int totalDocs = 0
////            int totalLabelsFound = 0
////
////            StringBuilder labelSpreadsheet = new StringBuilder("Label,title,tocpath,category,linkToPage,nuxeoId\r\n")
////            String originalCSV = new File("src/test/resources/documentTable.csv").text
////            originalCSV.split("\\r?\\n").drop(1).each {
////                totalDocs++
////                Document document = createDocument(it)
////                LabelRequest request = new LabelRequest()
////                request.publisher = "Ford"
////                request.manualType = "Wiring" //document.category
////                request.title = document.title
////                request.header = document.tocpath.replaceAll("\\[", "").replaceAll("]", "").split(" > ")[1]
////                ResponseEntity responseEntity = controller.createLabel(request)
////                document.label = responseEntity.statusCode == HttpStatus.OK ? responseEntity.body.toString() : "Not Found"
////
////                if (responseEntity.statusCode == HttpStatus.OK) {
////                    totalLabelsFound++
////                }
////
////                labelSpreadsheet.append("${document.label},${document.title},${document.tocpath},${document.category},${document.linkToPage},${document.nuxeoId}\r\n")
////            }
////
////            println "${((totalLabelsFound / totalDocs) * 100).round(2)} % labeled"
////
////            File file = new File("src/test/resources/documentTable_finished.csv")
////            PrintWriter writer = new PrintWriter(file)
////            writer.print("")
////            file.append(labelSpreadsheet.toString().bytes)
////            file.createNewFile()
////
////        then:
////            true
////    }
////
////    @SuppressWarnings('FactoryMethodName')
////    private static Document createDocument(String docValues) {
////        String[] docValueArray = docValues.split(",")
////        if (docValueArray.size() == 7) {
////            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4] + docValueArray[5], docValueArray[6])
////        } else {
////            new Document(docValueArray[0], docValueArray[1], docValueArray[2], docValueArray[3], docValueArray[4], docValueArray[5])
////        }
////    }
////
////    def 'handles extra comma in URL string'() {
////        given:
////        String extraCommaIn = ",Parking Rear And License Lamps (092-1),[092 > Parking Rear and License Lamps > 1],Wiring,\"www.fordtechservice.dealerconnection.com/renderers/wiring/svg/https://www.fordtechservice.dealerconnection.com/wiring/page/?book=EJY&vehicleId=6320&vin=&cell=092&page=1&sortby=&country=US&bookType=svg&language=EN&title=Parking,%20Rear%20and%20License%20Lamps\",ace55ddd-fca5-3b95-aa30-7ac36db88189"
////        String extraCommaOut = "com.identifix.contentlabelingservice.web.Document(, Parking Rear And License Lamps (092-1), [092 > Parking Rear and License Lamps > 1], Wiring, \"www.fordtechservice.dealerconnection.com/renderers/wiring/svg/https://www.fordtechservice.dealerconnection.com/wiring/page/?book=EJY&vehicleId=6320&vin=&cell=092&page=1&sortby=&country=US&bookType=svg&language=EN&title=Parking%20Rear%20and%20License%20Lamps\", ace55ddd-fca5-3b95-aa30-7ac36db88189)"
////        expect:
////        createDocument(extraCommaIn).toString() == extraCommaOut
////    }
////
////    def 'handles normal table row string'() {
////        given:
////        String normalTableRowIn = ",Reversing Lamps (093-1),[093 > Reversing Lamps > 1],Wiring,\"www.fordtechservice.dealerconnection.com/renderers/wiring/svg/https://www.fordtechservice.dealerconnection.com/wiring/page/?book=EJY&vehicleId=6320&vin=&cell=093&page=1&sortby=&country=US&bookType=svg&language=EN&title=Reversing%20Lamps\",c931256f-ba5b-3227-abd8-f7ec97100ee2"
////        String normalTableRowOut = "com.identifix.contentlabelingservice.web.Document(, Reversing Lamps (093-1), [093 > Reversing Lamps > 1], Wiring, \"www.fordtechservice.dealerconnection.com/renderers/wiring/svg/https://www.fordtechservice.dealerconnection.com/wiring/page/?book=EJY&vehicleId=6320&vin=&cell=093&page=1&sortby=&country=US&bookType=svg&language=EN&title=Reversing%20Lamps\", c931256f-ba5b-3227-abd8-f7ec97100ee2)"
////        expect:
////        createDocument(normalTableRowIn).toString() == normalTableRowOut
////    }
//
//}
