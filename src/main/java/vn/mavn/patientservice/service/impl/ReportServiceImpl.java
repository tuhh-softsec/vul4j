package vn.mavn.patientservice.service.impl;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
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
import org.springframework.stereotype.Service;
import vn.mavn.patientservice.dto.MedicalRecordDto;
import vn.mavn.patientservice.dto.qobject.QueryMedicalRecordDto;
import vn.mavn.patientservice.service.MedicalRecordService;
import vn.mavn.patientservice.service.ReportService;

/**
 * Created by TaiND on 2020-02-26.
 **/
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

  private static String[] columns = {"Ngày tư vấn", "Mã nhân viên tư vấn", "Loại bệnh", "Bệnh nhân",
      "Tuổi", "Địa chỉ", "Số điện thoại", "Nguồn quảng cáo", "Tình trạng bệnh", "Ghi chú",
      "Số Zalo", "Số khác", "Ngày khám", "Phòng khám", "Lần khám", "Số thang", "Loại thuốc",
      "Bài thuốc", "Tổng tiền mặt", "Chuyển khoản", "CoD", "Ghi chú"};

  @Autowired
  private MedicalRecordService medicalRecordService;

  @Override
  public void exportReport(QueryMedicalRecordDto queryMedicalRecordDto,
      HttpServletResponse httpServletResponse) throws IOException {
    List<MedicalRecordDto> medicalRecordDtos = medicalRecordService.findAll(queryMedicalRecordDto);

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
    for (MedicalRecordDto medicalRecord : medicalRecordDtos) {
      Row row = sheet.createRow(rowNum++);

      // Ngay tu van
      Cell advisoryDateCell = row.createCell(0);
      advisoryDateCell.setCellValue(medicalRecord.getAdvisoryDate());
      advisoryDateCell.setCellStyle(dateCellStyle);

      // Ma nhan vien tu van
      row.createCell(1)
          .setCellValue(medicalRecord.getUserCode());

      // Loai benh
      row.createCell(2).setCellValue(
          medicalRecord.getDiseaseDto() != null ? medicalRecord.getDiseaseDto().getName() : "");

      // Ten benh nhan
      row.createCell(3).setCellValue(
          medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getName() : "");

      // Tuoi benh nhan
      row.createCell(4, CellType.NUMERIC)
          .setCellValue(
              medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getAge() : 0);

      // Dia chi
      row.createCell(5)
          .setCellValue(
              medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getAddress()
                  : "");

      // SDT
      row.createCell(6)
          .setCellValue(
              medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getPhone()
                  : "");

      // Nguon QC
      row.createCell(7).setCellValue(
          medicalRecord.getAdvertisingSourceDto() != null ? medicalRecord.getAdvertisingSourceDto()
              .getName() : "");

      // Tinh trang benh
      row.createCell(8).setCellValue(medicalRecord.getDiseaseStatus());

      // Tinh trang tu van
      row.createCell(9).setCellValue(
          medicalRecord.getConsultingStatusDto() != null ? medicalRecord.getConsultingStatusDto()
              .getName() : "");

      // Ghi chu
      row.createCell(10).setCellValue(medicalRecord.getNote());

      // SDT Zalo
      row.createCell(11)
          .setCellValue(
              medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getZaloPhone()
                  : "");

      // SDT khac
      row.createCell(12)
          .setCellValue(
              medicalRecord.getPatientDto() != null ? medicalRecord.getPatientDto().getOtherPhone()
                  : "");

      // Ngay kham
      Cell examDateCell = row.createCell(13);
      examDateCell.setCellValue(medicalRecord.getExaminationDate());
      examDateCell.setCellStyle(dateCellStyle);

      // Co so kham
      row.createCell(14)
          .setCellValue(
              medicalRecord.getClinicDto() != null ? medicalRecord.getClinicDto().getName() : "");

      // Lan kham
      row.createCell(15).setCellValue(
          medicalRecord.getExaminationTimes() != null ? medicalRecord.getExaminationTimes() : 0);

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

    httpServletResponse
        .setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
    httpServletResponse.setHeader("Content-Disposition", "attachment; filename=\"file.xlsx\"");

    workbook.write(httpServletResponse.getOutputStream());
    // Closing the workbook
    workbook.close();
  }
}
