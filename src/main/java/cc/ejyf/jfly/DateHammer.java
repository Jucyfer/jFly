package cc.ejyf.jfly;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.text.DecimalFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 这个类处理所有跟日期相关的内容
 */
public class DateHammer {
    private static final String[] patternStrings = {
            "yyyy-MM-dd", "y-M-d", "yyyy.MM.dd", "y.M.d", "yyyy/MM/dd",
            "y/M/d", "yyyyMMdd", "yyyy-MMdd", "yyyy.MMdd", "yyyy/MMdd",
            "y年M月d日", "yyyy年MM月dd日"
    };
    //动态添加DateTimeFormatter对象
    private static final ArrayList<DateTimeFormatter> formatters = Arrays.stream(patternStrings)
            .map(DateTimeFormatter::ofPattern)
            .collect(Collectors.toCollection(ArrayList::new));
    private ZoneId zoneId;
    private DateTimeFormatter systemDateFormatter;
    private long excelNowDayNum;
    private DecimalFormat decimalFormat0 = new DecimalFormat("0");

    public DateHammer() {
        updateExcelNowDayNum();
        this.zoneId = ZoneId.of("Asia/Shanghai");
    }

    public void setZoneId(ZoneId zoneId) {
        this.zoneId = zoneId;
    }

    public void setSystemDateFormatter(DateTimeFormatter systemDateFormatter) {
        this.systemDateFormatter = systemDateFormatter;
    }

    private void updateExcelNowDayNum() {
        this.excelNowDayNum = LocalDate.of(1900, 1, 1).until(LocalDate.now(), ChronoUnit.DAYS);
    }

    /**
     * 这个方法把所有非标日期给干成yyyy-MM-dd字符串。POI的Cell参数版。
     *
     * @param cell
     * @return
     */
    public String smashAllTheFuckingDateToString(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            //非文本类型，先直接取
            double cellNumValue = cell.getNumericCellValue();
            if (cellNumValue >= 32874 && cellNumValue <= excelNowDayNum) {
                return cell.getDateCellValue().toInstant().atZone(zoneId).format(systemDateFormatter);
            }
            if (cellNumValue >= 19900101) {
                return smashAllTheFuckingDateToString(decimalFormat0.format(cellNumValue));
            }
            throw new IllegalArgumentException("非法日期，或日期超出范围");
        } catch (IllegalStateException | NumberFormatException e) {
            //取失败了，说明是文本类型
            //文本类型，干死他
            return smashAllTheFuckingDateToString(cell.getStringCellValue());
        }
    }

    /**
     * 这个方法把所有非标日期字符串给干成yyyy-MM-dd字符串
     *
     * @param dateString
     * @return
     */
    public String smashAllTheFuckingDateToString(String dateString) {
        return formatters.parallelStream().map(formatter -> {
            try {
                return systemDateFormatter.format(formatter.parse(dateString.strip()));
            } catch (DateTimeException transformException) {
                return null;
            }
        }).filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DateTimeException("日期转换失败"));
    }

    public String date2Str(Date date) {
        return systemDateFormatter.format(date.toInstant().atZone(zoneId));
    }

    public LocalDateTime date2LocalDateTime(Date date) {
        return LocalDateTime.from(date.toInstant().atZone(zoneId));
    }

    public LocalDateTime str2LocalDateTime(String dateStr) {
        return formatters.parallelStream()
                .map(formatter -> {
                    try {
                        return LocalDateTime.parse(dateStr, formatter);
                    } catch (DateTimeException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new DateTimeException("日期转换失败"));
    }

    public Date str2date(String dateStr) {
        return localDateTime2date(str2LocalDateTime(dateStr));
    }

    public String localDateTime2str(LocalDateTime localDateTime) {
        return systemDateFormatter.format(localDateTime.atZone(zoneId));
    }

    public Date localDateTime2date(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }


}
