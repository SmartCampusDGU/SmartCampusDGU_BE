package org.smartcampus.smartcampus_be.domain.outlier.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.xwpf.usermodel.*;
import org.smartcampus.smartcampus_be.domain.outlier.dto.response.PeriodStatisticsResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReportGenerationService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public byte[] generatePeriodStatisticsReport(PeriodStatisticsResponse data) throws IOException {
        XWPFDocument document = new XWPFDocument();

        createReportTitle(document, "요약 개요");
        createSummaryTable(document, data);

        createSectionTitle(document, "센서별 이상 발생 요약표");
        createSensorSummaryTable(document, data);

        createSectionTitle(document, "환경 지표 통계");
        createEnvironmentStatisticsTable(document, data);

        createSectionTitle(document, "운영 이력");
        createActionRecordsTable(document, data);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.write(out);
        document.close();

        return out.toByteArray();
    }

    private void createReportTitle(XWPFDocument document, String title) {
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setText(title);
        titleRun.setBold(true);
        titleRun.setFontSize(16);
        titleRun.setFontFamily("맑은 고딕");
    }

    private void createSectionTitle(XWPFDocument document, String title) {
        XWPFParagraph paragraph = document.createParagraph();
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(title);
        run.setBold(true);
        run.setFontSize(14);
        run.setFontFamily("맑은 고딕");
    }

    private void createSummaryTable(XWPFDocument document, PeriodStatisticsResponse data) {
        XWPFTable table = document.createTable(5, 2);
        table.setWidth("100%");

        setTableHeaders(table, 0, "항목", "내용");

        setTableCell(table, 1, 0, "전체 이상 건수");
        setTableCell(table, 1, 1, String.valueOf(data.getTotalOutlierCount()));

        setTableCell(table, 2, 0, "주요 이상 항목");
        String majorOutliers = data.getMajorOutlierTypes().isEmpty() ? "없음" :
                data.getMajorOutlierTypes().get(0).getDataTypeName() + " (" +
                String.format("%.1f%%", data.getMajorOutlierTypes().get(0).getPercentage()) + ")";
        setTableCell(table, 2, 1, majorOutliers);

        setTableCell(table, 3, 0, "관찰 기간");
        String period = data.getObservationPeriod().getStartDate().format(DATE_FORMATTER) +
                " ~ " + data.getObservationPeriod().getEndDate().format(DATE_FORMATTER);
        setTableCell(table, 3, 1, period);

        setTableCell(table, 4, 0, "보고서 생성 일자");
        setTableCell(table, 4, 1, java.time.LocalDateTime.now().format(DATE_FORMATTER));

        addTableBorders(table);
    }

    private void createSensorSummaryTable(XWPFDocument document, PeriodStatisticsResponse data) {
        int rowCount = Math.max(data.getSensorSummaries().size(), 1) + 1; // 최소 1행 + 헤더
        XWPFTable table = document.createTable(rowCount, 4);
        table.setWidth("100%");

        setTableHeaders(table, 0, "센서 ID", "설치 위치", "이상 발생 횟수", "평균 지속 시간");

        for (int i = 0; i < data.getSensorSummaries().size(); i++) {
            PeriodStatisticsResponse.SensorOutlierSummary summary = data.getSensorSummaries().get(i);
            setTableCell(table, i + 1, 0, summary.getMacAddress());
            setTableCell(table, i + 1, 1, summary.getRoomNumber());
            setTableCell(table, i + 1, 2, String.valueOf(summary.getOutlierCount()));
            setTableCell(table, i + 1, 3, String.format("%.1f분", summary.getAverageDurationMinutes()));
        }

        // 빈 행 채우기
        for (int i = data.getSensorSummaries().size(); i < rowCount - 1; i++) {
            setTableCell(table, i + 1, 0, "");
            setTableCell(table, i + 1, 1, "");
            setTableCell(table, i + 1, 2, "");
            setTableCell(table, i + 1, 3, "");
        }

        addTableBorders(table);
    }

    private void createEnvironmentStatisticsTable(XWPFDocument document, PeriodStatisticsResponse data) {
        int rowCount = Math.max(data.getEnvironmentStatistics().size(), 1) + 1; // 최소 1행 + 헤더
        XWPFTable table = document.createTable(rowCount, 4);
        table.setWidth("100%");

        setTableHeaders(table, 0, "지표", "평균값", "최고값", "최저값");

        for (int i = 0; i < data.getEnvironmentStatistics().size(); i++) {
            PeriodStatisticsResponse.EnvironmentStatistics stats = data.getEnvironmentStatistics().get(i);
            setTableCell(table, i + 1, 0, stats.getIndicator());
            setTableCell(table, i + 1, 1, String.format("%.2f %s", stats.getAverage(), stats.getUnit()));
            setTableCell(table, i + 1, 2, String.format("%.2f %s", stats.getMaximum(), stats.getUnit()));
            setTableCell(table, i + 1, 3, String.format("%.2f %s", stats.getMinimum(), stats.getUnit()));
        }

        // 빈 행 채우기
        for (int i = data.getEnvironmentStatistics().size(); i < rowCount - 1; i++) {
            setTableCell(table, i + 1, 0, "");
            setTableCell(table, i + 1, 1, "");
            setTableCell(table, i + 1, 2, "");
            setTableCell(table, i + 1, 3, "");
        }

        addTableBorders(table);
    }

    private void createActionRecordsTable(XWPFDocument document, PeriodStatisticsResponse data) {
        int rowCount = Math.max(data.getActionRecords().size(), 1) + 1; // 최소 1행 + 헤더
        XWPFTable table = document.createTable(rowCount, 3);
        table.setWidth("100%");

        setTableHeaders(table, 0, "일시", "내용", "조치자");

        for (int i = 0; i < data.getActionRecords().size(); i++) {
            PeriodStatisticsResponse.ActionRecord record = data.getActionRecords().get(i);
            setTableCell(table, i + 1, 0, record.getActionDateTime().format(DATE_FORMATTER));
            String content = record.getRoomNumber() + "호 " + record.getDataTypeName() + " " + record.getActionStatus();
            setTableCell(table, i + 1, 1, content);
            setTableCell(table, i + 1, 2, record.getMemberName());
        }

        // 빈 행 채우기
        for (int i = data.getActionRecords().size(); i < rowCount - 1; i++) {
            setTableCell(table, i + 1, 0, "");
            setTableCell(table, i + 1, 1, "");
            setTableCell(table, i + 1, 2, "");
        }

        addTableBorders(table);
    }

    private void setTableHeaders(XWPFTable table, int rowIndex, String... headers) {
        XWPFTableRow row = table.getRow(rowIndex);
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = row.getCell(i);
            cell.setColor("E0E0E0");
            XWPFParagraph paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText(headers[i]);
            run.setBold(true);
            run.setFontFamily("맑은 고딕");
        }
    }

    private void setTableCell(XWPFTable table, int rowIndex, int colIndex, String text) {
        XWPFTableRow row = table.getRow(rowIndex);
        XWPFTableCell cell = row.getCell(colIndex);
        XWPFParagraph paragraph = cell.getParagraphArray(0);
        paragraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun run = paragraph.createRun();
        run.setText(text);
        run.setFontFamily("맑은 고딕");
    }

    private void addTableBorders(XWPFTable table) {
        table.getCTTbl().addNewTblPr().addNewTblBorders();
        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                cell.getCTTc().addNewTcPr().addNewTcBorders();
            }
        }
    }
}