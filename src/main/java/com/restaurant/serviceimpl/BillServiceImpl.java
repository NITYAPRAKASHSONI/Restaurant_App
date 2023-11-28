package com.restaurant.serviceimpl;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.restaurant.constants.CafeConstants;
import com.restaurant.dao.BillDao;
import com.restaurant.jwt.JwtFilter;
import com.restaurant.pojo.Bill;
import com.restaurant.service.BillService;
import com.restaurant.utils.CafeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.io.IOUtils;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
public class BillServiceImpl implements BillService {

    @Autowired
   private JwtFilter jwtFilter;

    @Autowired
    private BillDao billDao;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        log.info("inside generate report ");
        try {
            String fileName;
            if(validateRequestMap(requestMap)){
                if(requestMap.containsKey("isGenerate") && !(Boolean) requestMap.get("isGenerate")){
                    fileName=(String) requestMap.get("uuid");
                }else{
                 fileName=CafeUtils.getUUId();
                 requestMap.put("uuid",fileName);
                 insertBill(requestMap);
                }
                String data="Name: "+requestMap.get("name") +"\n" +"Contact Number: "+requestMap.get("contactNumber") +"\n"
                        +"Email: "+requestMap.get("email") +"\n" +"Payment Method: "+requestMap.get("paymentMethod");


                Document document=new Document();
                PdfWriter.getInstance(document,new FileOutputStream(CafeConstants.STORE_LOCATION+"\\"+fileName+".pdf"));
                 document.open();
                 setRectangleInPdf(document);
                Paragraph  paragraph=new Paragraph("Restaurant Management System",getFont("Header"));
                 paragraph.setAlignment(Element.ALIGN_CENTER);
                 document.add(paragraph);
                 Paragraph paragraph1=new Paragraph(data +"\n \n"+getFont("Data"));
                 document.add(paragraph1);

                PdfPTable pdfPTable=new PdfPTable(5);
                pdfPTable.setWidthPercentage(100);
                addTableHeader(pdfPTable);
                JSONArray jsonArray=CafeUtils.getJsonFromArrayString((String) requestMap.get("productDetails"));
                for (int i=0;i<jsonArray.length();i++){
                  addRows(pdfPTable,CafeUtils.getMapFromJson(jsonArray.getString(i)));
                }
                document.add(pdfPTable);
                Paragraph footer=new Paragraph("Total: "+requestMap.get("totalAmount") +"\n" +"Thank you for visiting.Please Visit again!"
                        ,getFont("Data"));
                document.add(footer);
                document.close();
                return new ResponseEntity<>("{\"uuid\":"+fileName+"\"}",HttpStatus.OK);
            }



            return CafeUtils.getResponseEntity("Required Data Not Found",HttpStatus.BAD_REQUEST);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<List<Bill>> getBills() {
        try {
            List<Bill> bills=new ArrayList<>();
            if(jwtFilter.isAdmin()){
                bills=billDao.getAllBills();
            }else{
            bills=billDao.getBillByUserName(jwtFilter.getCurrentUser());
            }

            return new ResponseEntity<>(bills,HttpStatus.OK);

        }catch (Exception ex){
            ex.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(),HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<byte[]> getPdf(Map<String, Object> requestMap) {
        log.info("inside get pdf: requestMap {} ",requestMap);
        try{
        byte[] bytes=new byte[0];
        if(!requestMap.containsKey("uuid") &&   validateRequestMap(requestMap)){
         return new ResponseEntity<>(bytes,HttpStatus.BAD_REQUEST);
        }
        String filePath=CafeConstants.STORE_LOCATION+"\\"+(String) requestMap.get("uuid")+".pdf";
       if(CafeUtils.isFileExist(filePath)){
           bytes=getByteArray(filePath);
           return new ResponseEntity<>(bytes,HttpStatus.OK);
       }else {
           requestMap.put("isGenerate",false);
           generateReport(requestMap);
           bytes=getByteArray(filePath);
         return   new ResponseEntity<>(bytes,HttpStatus.OK);
       }
        }catch (Exception ex){
            ex.printStackTrace();
            
        }
        return null;
    }

    @Override
    public ResponseEntity<String> deleteBill(Integer id) {
        try{
            Optional optional=billDao.findById(id);
            if(!optional.isEmpty()){
             billDao.deleteById(id);
             return CafeUtils.getResponseEntity("Bill Deleted Successfully",HttpStatus.OK);
            }
            return CafeUtils.getResponseEntity("Bill Id doesn't exist",HttpStatus.OK);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private byte[] getByteArray(String filePath) throws Exception {
        File file=new File(filePath);
        InputStream inputStream=new FileInputStream(file);
        byte[] byteArray= IOUtils.toByteArray(inputStream);
        inputStream.close();
        return byteArray;
    }

    private void addRows(PdfPTable pdfPTable, Map<String, Object> data) {
        log.info("Inside rows ");
        pdfPTable.addCell((String)data.get("name"));
        pdfPTable.addCell((String) data.get("category"));
        pdfPTable.addCell((String) data.get("quantity"));
        pdfPTable.addCell(Double.toString((Double) data.get("price")));
        pdfPTable.addCell(Double.toString((Double) data.get("total")));

    }

    private void addTableHeader(PdfPTable pdfPTable) {
        log.info("Inside table header");
        Stream.of("Name","Category","Quantity","Price","Sub Total")
                .forEach(columnTitle->
                {
                    PdfPCell pdfPCell=new PdfPCell();
                    pdfPCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    pdfPCell.setBorderWidth(2);
                    pdfPCell.setPhrase(new Phrase(columnTitle));
                    pdfPCell.setBackgroundColor(BaseColor.YELLOW);
                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    pdfPCell.setVerticalAlignment(Element.ALIGN_CENTER);
                    pdfPTable.addCell(pdfPCell);
                });
    }

    private Font getFont(String type) {
         log.info("Inside font header");

         switch (type){
             case "Header":
                 Font headerFont=FontFactory.getFont(FontFactory.HELVETICA_BOLDOBLIQUE,18,BaseColor.BLACK);
                 headerFont.setStyle(Font.BOLD);
                 return headerFont;


             case "Data":
                 Font dataFont=FontFactory.getFont(FontFactory.TIMES_ROMAN,11,BaseColor.BLACK);
                 dataFont.setStyle(Font.BOLD);
                 return dataFont;

             default:
                 return new Font();

         }
    }

    private void setRectangleInPdf(Document document) throws DocumentException  {
        log.info("inside rectangle pdf");
        Rectangle rectangle=new Rectangle(577,825,18,15);
        rectangle.enableBorderSide(1);
        rectangle.enableBorderSide(2);
        rectangle.enableBorderSide(4);
        rectangle.enableBorderSide(8);
        rectangle.setBorderColor(BaseColor.BLACK);
        rectangle.setBorderWidth(1);
        document.add(rectangle);

    }

    private void insertBill(Map<String, Object> requestMap) {
        try{
            Bill bill=new Bill();
            bill.setUuid((String) requestMap.get("uuid"));
            bill.setName((String) requestMap.get("name"));
            bill.setEmail((String) requestMap.get("email"));
            bill.setContactNumber((String) requestMap.get("contactNumber"));
            bill.setTotal(Integer.parseInt((String) requestMap.get("totalAmount")));
            bill.setProductDetails((String) requestMap.get("productDetails"));
            bill.setCreatedBy(jwtFilter.getCurrentUser());
            billDao.save(bill);

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private boolean validateRequestMap(Map<String, Object> requestMap) {
return requestMap.containsKey("name") && requestMap.containsKey("email")
        && requestMap.containsKey("contactNumber") && requestMap.containsKey("paymentMethod")
        && requestMap.containsKey("productDetails") && requestMap.containsKey("totalAmount");

    }
}
