package vn.mavn.patientservice.controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.mavn.patientservice.entity.MedicalRecord;
import vn.mavn.patientservice.repository.MedicalRecordRepository;

/**
 * Created by TaiND on 2020-02-25.
 **/
@RestController
public class ExportController {

  private static String[] columns = {"Ngày tư vấn", "Mã nhân viên tư vấn", "Loại bệnh", "Bệnh nhân",
      "Tuổi", "Địa chỉ", "Số điện thoại", "Nguồn quảng cáo", "Tình trạng bệnh", "Ghi chú",
      "Số Zalo", "Số khác", "Ngày khám", "Phòng khám", "Lần khám", "Số thang", "Loại thuốc",
      "Bài thuốc", "Tổng tiền mặt", "Chuyển khoản", "CoD", "Ghi chú"};


  @Autowired
  private MedicalRecordRepository medicalRecordRepository;

  @GetMapping("/downloadFile")
  public void downloadFile(HttpServletResponse response) throws IOException {
    // Load file as Resource
    List<MedicalRecord> medicalRecords = medicalRecordRepository.findAll();

    // Create a Workbook
    Workbook workbook = new XSSFWorkbook();

    // Create a Sheet
    Sheet sheet = workbook.createSheet("Test");

    Font headerFont = workbook.createFont();
    headerFont.setBold(true);
    headerFont.setFontHeightInPoints((short) 14);
    headerFont.setColor(IndexedColors.RED.getIndex());

    // Create a CellStyle with the font
    CellStyle headerCellStyle = workbook.createCellStyle();
    headerCellStyle.setFont(headerFont);

    /* CreationHelper helps us create instances of various things like DataFormat,
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
    CreationHelper createHelper = workbook.getCreationHelper();

    // Create Cell Style for formatting Date
    CellStyle dateCellStyle = workbook.createCellStyle();
    dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

    // Create a Row
    Row headerRow = sheet.createRow(0);

    // Create cells
    for (int i = 0; i < columns.length; i++) {
      Cell cell = headerRow.createCell(i);
      cell.setCellValue(columns[i]);
      cell.setCellStyle(headerCellStyle);
    }

    // Create Other rows and cells with employees data
    int rowNum = 1;
    for (MedicalRecord medicalRecord : medicalRecords) {
      Row row = sheet.createRow(rowNum++);

      // Ngay tu van
      Cell advisoryDateCell = row.createCell(0);
      advisoryDateCell.setCellValue(medicalRecord.getAdvisoryDate());
      advisoryDateCell.setCellStyle(dateCellStyle);

      // Ma nhan vien tu van
      row.createCell(1)
          .setCellValue(medicalRecord.getUserCode());

      // Loai benh
      row.createCell(2)
          .setCellValue(medicalRecord.getDiseaseId() != null ? medicalRecord.getDiseaseId() : 0);

      // Ten benh nhan
      row.createCell(3)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // Tuoi benh nhan
      row.createCell(4, CellType.NUMERIC)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // Dia chi
      row.createCell(5)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // SDT
      row.createCell(6)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // Nguon QC
      row.createCell(7).setCellValue(medicalRecord.getConsultingStatusCode());

      // Tinh trang benh
      row.createCell(8).setCellValue(medicalRecord.getDiseaseStatus());

      // Tinh trang tu van
      row.createCell(9).setCellValue(medicalRecord.getConsultingStatusCode());

      // Ghi chu
      row.createCell(10).setCellValue(medicalRecord.getNote());

      // SDT Zalo
      row.createCell(11)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // SDT khac
      row.createCell(12)
          .setCellValue(medicalRecord.getPatientId() != null ? medicalRecord.getPatientId() : 0);

      // Ngay kham
      Cell examDateCell = row.createCell(13);
      examDateCell.setCellValue(medicalRecord.getExaminationDate());
      examDateCell.setCellStyle(dateCellStyle);

      // Co so kham
      row.createCell(14)
          .setCellValue(medicalRecord.getClinicId() != null ? medicalRecord.getClinicId() : 0);

      // Lan kham
      row.createCell(15).setCellValue(
          medicalRecord.getExaminationTimes() != null ? medicalRecord.getExaminationTimes() : 1);

      // So thang
      row.createCell(16, CellType.NUMERIC).setCellValue(
          medicalRecord.getRemedyAmount() != null ? medicalRecord.getRemedyAmount() : 0);

      // Loai thuoc
      row.createCell(17).setCellValue(medicalRecord.getRemedyType());

      // Bai thuoc
      row.createCell(18).setCellValue(medicalRecord.getRemedies());

      // Tong tien mat
      row.createCell(19, CellType.NUMERIC)
          .setCellValue(
              medicalRecord.getTotalAmount() != null ? medicalRecord.getTotalAmount().doubleValue()
                  : 0);

      // Chuyen khoan
      row.createCell(20, CellType.NUMERIC)
          .setCellValue(
              medicalRecord.getTransferAmount() != null ? medicalRecord.getTransferAmount()
                  .doubleValue() : 0);

      // CoD
      row.createCell(21, CellType.NUMERIC)
          .setCellValue(
              medicalRecord.getCodAmount() != null ? medicalRecord.getCodAmount().doubleValue()
                  : 0);

      // Ghi chu mo rong
      row.createCell(22).setCellValue(medicalRecord.getExtraNote());
    }

    // Try to determine file's content type
    String contentType = "application/octet-stream";

    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    response.setHeader("Content-Disposition", "attachment; filename=\"file.xlsx\"");

    workbook.write(response.getOutputStream());
    // Closing the workbook
    workbook.close();
  }
}
